/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;
import Modelo.ConexionSQLServer;
import Modelo.Lote;
import modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Fredy
 */
public class LoteDAO {
    
    public List<Lote> listarLotesPorProducto(int idProducto) {
        List<Lote> lotes = new ArrayList<>();
        String sql = "{call sp_ListarLotesPorProducto(?)}";
        
        try (Connection conn = ConexionSQLServer.getInstancia().obtenerConexion();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, idProducto);
            
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    Lote l = new Lote();
                    l.setIdLote(rs.getInt("id_lote"));
                    l.setIdProducto(rs.getInt("id_producto"));
                    l.setCodigoLote(rs.getString("codigo_lote"));
                    l.setFechaIngreso(rs.getDate("fecha_ingreso"));
                    l.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                    l.setCantidadInicial(rs.getInt("cantidad_inicial"));
                    l.setStockActual(rs.getInt("stock_actual"));
                    l.setCostoUnitario(rs.getDouble("costo_unitario"));
                    l.setActivo(rs.getBoolean("activo"));
                    lotes.add(l);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lotes;
    }
    
    public boolean insertarLote(Lote lote) {
        String sql = "{call sp_InsertarLote(?, ?, ?, ?, ?, ?)}";
        
        try (Connection conn = ConexionSQLServer.getInstancia().obtenerConexion();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, lote.getIdProducto());
            cstmt.setString(2, lote.getCodigoLote());
            cstmt.setDate(3, lote.getFechaIngreso());
            cstmt.setDate(4, lote.getFechaVencimiento());
            cstmt.setInt(5, lote.getCantidadInicial());
            cstmt.setDouble(6, lote.getCostoUnitario());
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
