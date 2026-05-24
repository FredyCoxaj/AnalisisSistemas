package com.inventario.service;

import com.inventario.dto.LoteRequestDTO;
import com.inventario.exception.BusinessException;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.model.Lote;
import com.inventario.model.MovimientoInventario;
import com.inventario.model.Producto;
import com.inventario.model.Proveedor;
import com.inventario.repository.LoteRepository;
import com.inventario.repository.MovimientoRepository;
import com.inventario.repository.ProductoRepository;
import com.inventario.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoteService {
    
    @Autowired
    private LoteRepository loteRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ProveedorRepository proveedorRepository;
    
    @Autowired
    private MovimientoRepository movimientoRepository;
    
    @Transactional
    public Lote ingresarMercaderia(LoteRequestDTO request, Integer usuarioResponsableId) {
        Producto producto = productoRepository.findById(request.getIdProducto())
            .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getIdProducto()));
        
        Proveedor proveedor = null;
        if (request.getIdProveedor() != null) {
            proveedor = proveedorRepository.findById(request.getIdProveedor())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", request.getIdProveedor()));
        }
        
        Lote lote = new Lote();
        lote.setProducto(producto);
        lote.setProveedor(proveedor);
        lote.setCodigoLoteExterno(request.getCodigoLoteExterno());
        lote.setCantidadIngresada(request.getCantidad());
        lote.setCantidadDisponible(request.getCantidad());
        lote.setCostoUnitarioCompra(request.getCostoUnitario());
        lote.setFechaCaducidad(request.getFechaCaducidad());
        
        Lote savedLote = loteRepository.save(lote);
        
        // Registrar movimiento de entrada
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setLote(savedLote);
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setCantidad(request.getCantidad());
        movimiento.setUsuarioResponsableId(usuarioResponsableId);
        movimiento.setReferencia("COMPRA_DIRECTA");
        movimientoRepository.save(movimiento);
        
        return savedLote;
    }
    
    public List<Lote> verLotesExistentes() {
        return loteRepository.findAll();
    }
    
    public List<Lote> verLotesPorProducto(Integer idProducto) {
        return loteRepository.findByProductoIdProducto(idProducto);
    }
    
    public List<Lote> verLotesProximosVencer() {
        LocalDate fechaLimite = LocalDate.now().plusMonths(3);
        return loteRepository.findLotesProximosAVencer(fechaLimite);
    }
    
    public List<Lote> verProductosConStockBajo() {
        return loteRepository.findLotesConStockBajo();
    }
    
    public List<Lote> verLotesEnOferta() {
        return loteRepository.findByEnOfertaTrueAndActivoTrue();
    }
    
    public Lote buscarLotePorId(Integer id) {
        return loteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lote", "id", id));
    }
    
    @Transactional
    public void actualizarOfertasAutomaticas() {
        LocalDate fechaLimite = LocalDate.now().plusDays(90);
        List<Lote> lotesPorCaducar = loteRepository.findByFechaCaducidadBeforeAndEnOfertaFalse(fechaLimite);
        
        for (Lote lote : lotesPorCaducar) {
            lote.setEnOferta(true);
            lote.setPorcentajeDescuentoOferta(new java.math.BigDecimal("5.00"));
            loteRepository.save(lote);
        }
    }
}