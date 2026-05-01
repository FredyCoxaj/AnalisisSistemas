/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;
import Modelo.ConexionSQLServer;
import Modelo.ItemVenta;
import modelo.*;
import java.sql.*;
import java.util.List;
import javax.swing.JOptionPane;
/**
 *
 * @author Fredy
 */
public class VentaDAO {
    
    public boolean realizarVenta(int idUsuario, List<ItemVenta> items, double descuento) {
        Connection conn = null;
        
        try {
            conn = ConexionSQLServer.getInstancia().obtenerConexion();
            
            conn.setAutoCommit(false);
            
            // Calcular total
            double total = 0;
            for (ItemVenta item : items) {
                total += item.getSubtotal();
            }
            total -= descuento;
            
            // Insertar cabecera de venta
            String sqlVenta = "INSERT INTO Ventas (id_usuario, total, descuento) VALUES (?, ?, ?)";
            PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            pstmtVenta.setInt(1, idUsuario);
            pstmtVenta.setDouble(2, total);
            pstmtVenta.setDouble(3, descuento);
            pstmtVenta.executeUpdate();
            
            ResultSet rsVenta = pstmtVenta.getGeneratedKeys();
            int idVenta = 0;
            if (rsVenta.next()) {
                idVenta = rsVenta.getInt(1);
            }
            
            // Insertar detalles y aplicar PEPS
            for (ItemVenta item : items) {
                // Insertar detalle de venta
                String sqlDetalle = "INSERT INTO DetalleVenta (id_venta, id_producto, cantidad, precio_unitario) " +
                                   "VALUES (?, ?, ?, ?)";
                PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle, Statement.RETURN_GENERATED_KEYS);
                pstmtDetalle.setInt(1, idVenta);
                pstmtDetalle.setInt(2, item.getIdProducto());
                pstmtDetalle.setInt(3, item.getCantidad());
                pstmtDetalle.setDouble(4, item.getPrecioUnitario());
                pstmtDetalle.executeUpdate();
                
                // Aplicar PEPS usando el SP
                String sqlPEPS = "{call sp_AplicarPEPS(?, ?, ?)}";
                CallableStatement cstmtPEPS = conn.prepareCall(sqlPEPS);
                cstmtPEPS.setInt(1, idVenta);
                cstmtPEPS.setInt(2, item.getIdProducto());
                cstmtPEPS.setInt(3, item.getCantidad());
                cstmtPEPS.execute();
                
                // Actualizar stock_total del producto
                String sqlUpdateStock = "UPDATE Productos SET stock_total = stock_total - ? WHERE id_producto = ?";
                PreparedStatement pstmtStock = conn.prepareStatement(sqlUpdateStock);
                pstmtStock.setInt(1, item.getCantidad());
                pstmtStock.setInt(2, item.getIdProducto());
                pstmtStock.executeUpdate();
            }
            
            conn.commit();
            JOptionPane.showMessageDialog(null, "Venta realizada con éxito. ID Venta: " + idVenta);
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al realizar venta: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}