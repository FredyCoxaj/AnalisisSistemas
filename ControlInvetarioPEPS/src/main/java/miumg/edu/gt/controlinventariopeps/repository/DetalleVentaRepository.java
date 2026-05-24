package com.inventario.repository;

import com.inventario.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
    
    List<DetalleVenta> findByVentaIdVenta(Integer idVenta);
    
    List<DetalleVenta> findByLoteIdLote(Integer idLote);
    
    @Query("SELECT d FROM DetalleVenta d WHERE d.venta.idVenta = :idVenta")
    List<DetalleVenta> findDetallesByVentaId(@Param("idVenta") Integer idVenta);
    
    @Query("SELECT SUM(d.cantidad) FROM DetalleVenta d WHERE d.lote.producto.idProducto = :idProducto")
    Integer sumCantidadVendidaByProducto(@Param("idProducto") Integer idProducto);
    
    @Query("SELECT d.lote.producto.nombre, SUM(d.cantidad) as totalVendido " +
           "FROM DetalleVenta d " +
           "GROUP BY d.lote.producto.nombre " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos();
}