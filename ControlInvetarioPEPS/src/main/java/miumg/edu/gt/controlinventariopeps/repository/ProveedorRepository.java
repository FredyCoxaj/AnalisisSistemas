package com.inventario.repository;

import com.inventario.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    
    Optional<Proveedor> findByNombreEmpresa(String nombreEmpresa);
    
    @Query("SELECT p FROM Proveedor p WHERE p.nombreEmpresa LIKE %:nombre%")
    List<Proveedor> buscarPorNombre(@Param("nombre") String nombre);
}