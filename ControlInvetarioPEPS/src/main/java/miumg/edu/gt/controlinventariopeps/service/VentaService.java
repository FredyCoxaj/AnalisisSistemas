package com.inventario.service;

import com.inventario.dto.ItemVentaDTO;
import com.inventario.dto.VentaRequestDTO;
import com.inventario.exception.BusinessException;
import com.inventario.exception.InsufficientStockException;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.model.*;
import com.inventario.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {
    
    @Autowired
    private LoteRepository loteRepository;
    
    @Autowired
    private MovimientoRepository movimientoRepository;
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private DetalleVentaRepository detalleRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Transactional
    public Venta registrarVenta(VentaRequestDTO request, Integer vendedorId) {
        // Validar campos obligatorios
        validarCamposVenta(request);
        
        Venta venta = new Venta();
        venta.setNombreComprador(request.getNombreComprador());
        venta.setDireccionComprador(request.getDireccionComprador());
        venta.setTelefonoComprador(request.getTelefonoComprador());
        venta.setTipoPago(request.getTipoPago());
        venta.setIdVendedor(vendedorId);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setAnulada(false);
        
        BigDecimal totalVenta = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();
        List<MovimientoInventario> movimientosRegistrados = new ArrayList<>();
        
        for (ItemVentaDTO item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", item.getIdProducto()));
            
            // Verificar stock total disponible
            Integer stockTotal = loteRepository.sumCantidadDisponibleByProducto(item.getIdProducto());
            if (stockTotal == null || stockTotal < item.getCantidad()) {
                throw new InsufficientStockException(item.getIdProducto(), producto.getNombre(), 
                                                    item.getCantidad(), stockTotal == null ? 0 : stockTotal);
            }
            
            // Buscar lotes PEPS (más antiguos primero)
            List<Lote> lotesDisponibles = loteRepository.findByProductoIdProductoAndCantidadDisponibleGreaterThanAndActivoTrueOrderByFechaIngresoAsc(
                item.getIdProducto(), 0
            );
            
            int cantidadPorVender = item.getCantidad();
            
            while (cantidadPorVender > 0) {
                if (lotesDisponibles.isEmpty()) {
                    throw new InsufficientStockException(item.getIdProducto(), producto.getNombre(), 
                                                        cantidadPorVender, 0);
                }
                
                Lote lote = lotesDisponibles.get(0);
                int disponibleEnLote = lote.getCantidadDisponible();
                int aTomar = Math.min(cantidadPorVender, disponibleEnLote);
                
                // Calcular precio de venta (con oferta si aplica)
                BigDecimal precioUnitario = calcularPrecioVentaConOfertas(lote);
                
                DetalleVenta detalle = new DetalleVenta();
                detalle.setLote(lote);
                detalle.setCantidad(aTomar);
                detalle.setPrecioUnitarioVenta(precioUnitario);
                detalle.setSubtotal(precioUnitario.multiply(new BigDecimal(aTomar)));
                detalles.add(detalle);
                
                // Actualizar stock del lote
                lote.setCantidadDisponible(lote.getCantidadDisponible() - aTomar);
                loteRepository.save(lote);
                
                // Registrar salida en movimientos
                MovimientoInventario mov = new MovimientoInventario();
                mov.setLote(lote);
                mov.setTipoMovimiento("SALIDA");
                mov.setCantidad(aTomar);
                mov.setUsuarioResponsableId(vendedorId);
                mov.setReferencia("VENTA_PENDIENTE");
                movimientoRepository.save(mov);
                movimientosRegistrados.add(mov);
                
                cantidadPorVender -= aTomar;
                totalVenta = totalVenta.add(precioUnitario.multiply(new BigDecimal(aTomar)));
                
                // Remover lote si se agotó
                if (lote.getCantidadDisponible() == 0) {
                    lotesDisponibles.remove(0);
                }
            }
        }
        
        venta.setMontoTotal(totalVenta);
        Venta savedVenta = ventaRepository.save(venta);
        
        // Asignar referencia de venta a los detalles
        for (DetalleVenta detalle : detalles) {
            detalle.setVenta(savedVenta);
            detalleRepository.save(detalle);
        }
        
        // Actualizar referencias de movimientos
        for (MovimientoInventario mov : movimientosRegistrados) {
            mov.setReferencia("VENTA_ID_" + savedVenta.getIdVenta());
            movimientoRepository.save(mov);
        }
        
        return savedVenta;
    }
    
    private BigDecimal calcularPrecioVentaConOfertas(Lote lote) {
        Producto producto = lote.getProducto();
        BigDecimal precioBase = producto.getPrecioBaseCompra()
            .multiply(BigDecimal.ONE.add(producto.getMargenGananciaPorcentual().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)));
        
        // Regla: si fecha caducidad <= 3 meses (90 días), aplicar 5% descuento
        long diasParaCaducar = ChronoUnit.DAYS.between(LocalDate.now(), lote.getFechaCaducidad());
        if (diasParaCaducar <= 90 && diasParaCaducar > 0) {
            lote.setEnOferta(true);
            lote.setPorcentajeDescuentoOferta(new BigDecimal("5.00"));
            loteRepository.save(lote);
            return precioBase.multiply(new BigDecimal("0.95")); // 5% descuento
        }
        
        return precioBase;
    }
    
    private void validarCamposVenta(VentaRequestDTO request) {
        if (request.getNombreComprador() == null || request.getNombreComprador().trim().isEmpty()) {
            throw new BusinessException("El nombre del comprador es obligatorio");
        }
        if (request.getDireccionComprador() == null || request.getDireccionComprador().trim().isEmpty()) {
            throw new BusinessException("La dirección del comprador es obligatoria");
        }
        if (request.getTelefonoComprador() == null || request.getTelefonoComprador().trim().isEmpty()) {
            throw new BusinessException("El teléfono del comprador es obligatorio");
        }
        if (request.getTipoPago() == null || request.getTipoPago().trim().isEmpty()) {
            throw new BusinessException("El tipo de pago es obligatorio");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("Debe incluir al menos un producto en la venta");
        }
    }
    
    public List<Venta> obtenerHistorialVentas() {
        return ventaRepository.findAll();
    }
    
    public List<Venta> obtenerVentasPorVendedor(Integer vendedorId) {
        return ventaRepository.findByIdVendedor(vendedorId);
    }
    
    public Venta obtenerVentaPorId(Integer id) {
        return ventaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Venta", "id", id));
    }
    
    public BigDecimal getTotalVentasGenerales() {
        return ventaRepository.getTotalVentasGenerales();
    }
}