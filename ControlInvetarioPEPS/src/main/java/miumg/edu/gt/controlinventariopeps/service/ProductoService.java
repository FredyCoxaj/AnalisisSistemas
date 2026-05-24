package com.inventario.service;

import com.inventario.dto.ProductoBusquedaDTO;
import com.inventario.exception.BusinessException;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.model.Producto;
import com.inventario.repository.LoteRepository;
import com.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private LoteRepository loteRepository;
    
    public Producto buscarPorId(Integer id) {
        return productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
    }
    
    public Producto buscarPorNombreExactoMayusculas(String nombreMayusculas) {
        // Buscar exactamente con el nombre en mayúsculas
        return productoRepository.findByNombre(nombreMayusculas)
            .orElseThrow(() -> new ResourceNotFoundException("Producto", "nombre", nombreMayusculas));
    }
    
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }
    
    public List<Producto> listarActivos() {
        return productoRepository.findByActivoTrue();
    }
    
    public ProductoBusquedaDTO buscarProductoConStock(Integer idProducto) {
        Producto producto = buscarPorId(idProducto);
        Integer stockDisponible = loteRepository.sumCantidadDisponibleByProducto(idProducto);
        if (stockDisponible == null) stockDisponible = 0;
        
        Boolean enOferta = loteRepository.findByProductoIdProducto(idProducto)
            .stream()
            .anyMatch(lote -> lote.getEnOferta() != null && lote.getEnOferta());
        
        return new ProductoBusquedaDTO(
            producto.getIdProducto(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getPrecioBaseCompra(),
            producto.getPrecioVentaSugerido(),
            stockDisponible,
            enOferta
        );
    }
    
    public Producto crearProducto(Producto producto) {
        if (producto.getMargenGananciaPorcentual() == null) {
            producto.setMargenGananciaPorcentual(new BigDecimal("15.00"));
        }
        return productoRepository.save(producto);
    }
    
    public Producto actualizarProducto(Integer id, Producto productoActualizado) {
        Producto producto = buscarPorId(id);
        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setPrecioBaseCompra(productoActualizado.getPrecioBaseCompra());
        producto.setMargenGananciaPorcentual(productoActualizado.getMargenGananciaPorcentual());
        return productoRepository.save(producto);
    }
    
    public void eliminarProducto(Integer id) {
        Producto producto = buscarPorId(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }
}