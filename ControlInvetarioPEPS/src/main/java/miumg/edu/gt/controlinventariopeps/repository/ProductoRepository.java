
package com.inventario.repository;

import com.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
    Optional<Producto> findByNombre(String nombre);
    
    Optional<Producto> findByNombreIgnoreCase(String nombre);
    
    List<Producto> findByActivoTrue();
    
    List<Producto> findByActivoFalse();
    
    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %:nombre%")
    List<Producto> buscarPorNombreConteniendo(@Param("nombre") String nombre);
}