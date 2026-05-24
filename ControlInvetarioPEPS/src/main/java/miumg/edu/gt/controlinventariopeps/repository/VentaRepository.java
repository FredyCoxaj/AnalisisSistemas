package com.inventario.repository;

import com.inventario.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    
    List<Venta> findByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    List<Venta> findByIdVendedor(Integer idVendedor);
    
    List<Venta> findByAnuladaFalse();
    
    List<Venta> findByAnuladaTrue();
    
    @Query("SELECT SUM(v.montoTotal) FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin AND v.anulada = false")
    BigDecimal sumVentasPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin AND v.anulada = false")
    Long countVentasPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT v FROM Venta v WHERE v.fechaVenta >= :fecha ORDER BY v.fechaVenta DESC")
    List<Venta> findVentasRecientes(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT COALESCE(SUM(v.montoTotal), 0) FROM Venta v WHERE v.anulada = false")
    BigDecimal getTotalVentasGenerales();
}