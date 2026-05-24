package com.inventario.service;

import com.inventario.exception.BusinessException;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.model.Lote;
import com.inventario.model.MovimientoInventario;
import com.inventario.repository.LoteRepository;
import com.inventario.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoService {
    
    @Autowired
    private MovimientoRepository movimientoRepository;
    
    @Autowired
    private LoteRepository loteRepository;
    
    /**
     * Registrar una entrada de inventario (ingreso de mercadería)
     */
    public MovimientoInventario registrarEntrada(Integer idLote, Integer cantidad, 
                                                  Integer usuarioResponsableId, String referencia) {
        Lote lote = loteRepository.findById(idLote)
            .orElseThrow(() -> new ResourceNotFoundException("Lote", "id", idLote));
        
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setLote(lote);
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setCantidad(cantidad);
        movimiento.setUsuarioResponsableId(usuarioResponsableId);
        movimiento.setReferencia(referencia != null ? referencia : "ENTRADA_MANUAL");
        
        // Actualizar stock del lote
        lote.setCantidadDisponible(lote.getCantidadDisponible() + cantidad);
        loteRepository.save(lote);
        
        return movimientoRepository.save(movimiento);
    }
    
    /**
     * Registrar una salida de inventario (egreso de mercadería)
     */
    public MovimientoInventario registrarSalida(Integer idLote, Integer cantidad, 
                                                 Integer usuarioResponsableId, String referencia) {
        Lote lote = loteRepository.findById(idLote)
            .orElseThrow(() -> new ResourceNotFoundException("Lote", "id", idLote));
        
        if (lote.getCantidadDisponible() < cantidad) {
            throw new BusinessException("No hay suficiente stock en el lote. " +
                "Disponible: " + lote.getCantidadDisponible() + ", Solicitado: " + cantidad);
        }
        
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setLote(lote);
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setCantidad(cantidad);
        movimiento.setUsuarioResponsableId(usuarioResponsableId);
        movimiento.setReferencia(referencia != null ? referencia : "SALIDA_MANUAL");
        
        // Actualizar stock del lote
        lote.setCantidadDisponible(lote.getCantidadDisponible() - cantidad);
        loteRepository.save(lote);
        
        return movimientoRepository.save(movimiento);
    }
    
    /**
     * Obtener todos los movimientos
     */
    public List<MovimientoInventario> listarTodos() {
        return movimientoRepository.findAll();
    }
    
    /**
     * Obtener movimiento por ID
     */
    public MovimientoInventario buscarPorId(Integer id) {
        return movimientoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movimiento", "id", id));
    }
    
    /**
     * Obtener movimientos por lote
     */
    public List<MovimientoInventario> buscarPorLote(Integer idLote) {
        return movimientoRepository.findByLoteIdLote(idLote);
    }
    
    /**
     * Obtener movimientos por tipo (ENTRADA o SALIDA)
     */
    public List<MovimientoInventario> buscarPorTipo(String tipo) {
        if (!tipo.equals("ENTRADA") && !tipo.equals("SALIDA")) {
            throw new BusinessException("Tipo de movimiento inválido. Debe ser 'ENTRADA' o 'SALIDA'");
        }
        return movimientoRepository.findByTipoMovimiento(tipo);
    }
    
    /**
     * Obtener movimientos por usuario responsable
     */
    public List<MovimientoInventario> buscarPorUsuario(Integer usuarioResponsableId) {
        return movimientoRepository.findByUsuarioResponsableId(usuarioResponsableId);
    }
    
    /**
     * Obtener movimientos por rango de fechas
     */
    public List<MovimientoInventario> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null) {
            throw new BusinessException("Debe proporcionar fecha de inicio y fecha de fin");
        }
        if (inicio.isAfter(fin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return movimientoRepository.findByFechaMovimientoBetween(inicio, fin);
    }
    
    /**
     * Obtener movimientos por tipo y rango de fechas
     */
    public List<MovimientoInventario> buscarPorTipoYFecha(String tipo, LocalDateTime inicio, LocalDateTime fin) {
        if (!tipo.equals("ENTRADA") && !tipo.equals("SALIDA")) {
            throw new BusinessException("Tipo de movimiento inválido. Debe ser 'ENTRADA' o 'SALIDA'");
        }
        if (inicio == null || fin == null) {
            throw new BusinessException("Debe proporcionar fecha de inicio y fecha de fin");
        }
        return movimientoRepository.findMovimientosPorTipoYFecha(tipo, inicio, fin);
    }
    
    /**
     * Obtener historial completo de movimientos (para reportes)
     */
    public List<MovimientoInventario> getHistorialMovimientos() {
        return movimientoRepository.findAllByOrderByFechaMovimientoDesc();
    }
    
    /**
     * Obtener movimientos por referencia (ej: VENTA_ID_xxx)
     */
    public List<MovimientoInventario> buscarPorReferencia(String referencia) {
        if (referencia == null || referencia.trim().isEmpty()) {
            throw new BusinessException("La referencia no puede estar vacía");
        }
        return movimientoRepository.findByReferenciaConteniendo(referencia);
    }
}