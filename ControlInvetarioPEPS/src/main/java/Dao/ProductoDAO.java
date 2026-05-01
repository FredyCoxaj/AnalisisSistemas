/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;
import Modelo.ConexionSQLServer;
import Modelo.Producto;
import modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
/**
 *
 * @author Fredy
 */
public class ProductoDAO {
    
    public List<Producto> listarProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "{call sp_ListarProductos}";
        
        try (Connection conn = ConexionSQLServer.getInstancia().obtenerConexion();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
            
            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("id_producto"));
                p.setNombreProducto(rs.getString("nombre_producto"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecioUnitario(rs.getDouble("precio_unitario"));
                p.setStockTotal(rs.getInt("stock_total"));
                p.setActivo(rs.getBoolean("activo"));
                productos.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }
    
    public Producto obtenerProducto(int idProducto) {
        Producto producto = null;
        String sql = "{call sp_ObtenerProducto(?)}";
        
        try (Connection conn = ConexionSQLServer.getInstancia().obtenerConexion();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, idProducto);
            
            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    producto = new Producto();
                    producto.setIdProducto(rs.getInt("id_producto"));
                    producto.setNombreProducto(rs.getString("nombre_producto"));
                    producto.setDescripcion(rs.getString("descripcion"));
                    producto.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    producto.setStockTotal(rs.getInt("stock_total"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return producto;
    }
    
    public boolean insertarProducto(Producto producto) {
        String sql = "{call sp_InsertarProducto(?, ?, ?)}";
        
        try (Connection conn = ConexionSQLServer.getInstancia().obtenerConexion();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, producto.getNombreProducto());
            cstmt.setString(2, producto.getDescripcion());
            cstmt.setDouble(3, producto.getPrecioUnitario());
            
            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                producto.setIdProducto(rs.getInt("id_producto"));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al insertar producto: " + e.getMessage());
        }
        return false;
    }
    
    public boolean actualizarProducto(Producto producto) {
        String sql = "{call sp_ActualizarProducto(?, ?, ?, ?)}";
        
        try (Connection conn = ConexionSQLServer.getInstancia().obtenerConexion();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, producto.getIdProducto());
            cstmt.setString(2, producto.getNombreProducto());
            cstmt.setString(3, producto.getDescripcion());
            cstmt.setDouble(4, producto.getPrecioUnitario());
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean eliminarProducto(int idProducto) {
        String sql = "{call sp_EliminarProducto(?)}";
        
        try (Connection conn = ConexionSQLServer.getInstancia().obtenerConexion();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, idProducto);
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
