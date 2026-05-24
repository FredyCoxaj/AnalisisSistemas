package com.inventario.repository;

import com.inventario.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<MovimientoInventario, Integer> {
    
    List<MovimientoInventario> findByFechaMovimientoBetween(LocalDateTime inicio, LocalDateTime fin);
    
    List<MovimientoInventario> findByLoteIdLote(Integer idLote);
    
    List<MovimientoInventario> findByTipoMovimiento(String tipoMovimiento);
    
    List<MovimientoInventario> findByUsuarioResponsableId(Integer usuarioResponsableId);

    List<MovimientoInventario> findAllByOrderByFechaMovimientoDesc();
    
    @Query("SELECT m FROM MovimientoInventario m WHERE m.fechaMovimiento BETWEEN :inicio AND :fin AND m.tipoMovimiento = :tipo")
    List<MovimientoInventario> findMovimientosPorTipoYFecha(
        @Param("tipo") String tipo,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    @Query("SELECT m FROM MovimientoInventario m WHERE m.referencia LIKE %:referencia%")
    List<MovimientoInventario> findByReferenciaConteniendo(@Param("referencia") String referencia);
}