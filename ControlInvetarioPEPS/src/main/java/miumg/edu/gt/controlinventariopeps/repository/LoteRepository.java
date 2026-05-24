package com.inventario.repository;

import com.inventario.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {
    
    // Para PEPS: lotes disponibles por producto ordenados por fecha de ingreso (más antiguos primero)
    List<Lote> findByProductoIdProductoAndCantidadDisponibleGreaterThanAndActivoTrueOrderByFechaIngresoAsc(
        Integer idProducto, Integer cantidad
    );
    
    // Para ofertas automáticas (lotes que caducan antes de la fecha límite y no están en oferta)
    List<Lote> findByFechaCaducidadBeforeAndEnOfertaFalse(LocalDate fechaLimite);
    
    // Para ver lotes próximos a vencerse (3 meses)
    @Query("SELECT l FROM Lote l WHERE l.fechaCaducidad BETWEEN CURRENT_DATE AND :fechaLimite AND l.cantidadDisponible > 0 AND l.activo = true")
    List<Lote> findLotesProximosAVencer(@Param("fechaLimite") LocalDate fechaLimite);
    
    // Para productos con stock bajo (<50 unidades disponibles)
    @Query("SELECT l FROM Lote l WHERE l.cantidadDisponible < 50 AND l.cantidadDisponible > 0 AND l.activo = true ORDER BY l.cantidadDisponible ASC")
    List<Lote> findLotesConStockBajo();
    
    // Buscar lotes por producto
    List<Lote> findByProductoIdProducto(Integer idProducto);
    
    // Buscar lotes en oferta
    List<Lote> findByEnOfertaTrueAndActivoTrue();
    
    // Buscar lotes por proveedor
    List<Lote> findByProveedorIdProveedor(Integer idProveedor);

    // limpiar ofertas caducadas
    List<Lote> findByFechaCaducidadBeforeAndEnOfertaTrue(LocalDate fecha);
    
    // Sumar stock disponible por producto
    @Query("SELECT SUM(l.cantidadDisponible) FROM Lote l WHERE l.producto.idProducto = :idProducto AND l.activo = true")
    Integer sumCantidadDisponibleByProducto(@Param("idProducto") Integer idProducto);
}