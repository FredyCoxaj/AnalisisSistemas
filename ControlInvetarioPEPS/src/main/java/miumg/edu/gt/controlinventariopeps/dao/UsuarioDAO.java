package miumg.edu.gt.controlinventariopeps.dao;

import miumg.edu.gt.controlinventariopeps.modelo.Usuario;
import miumg.edu.gt.controlinventariopeps.conexion.ConexionSQLServer;
import java.sql.*;

public class UsuarioDAO {
    
    public Usuario login(String correo, String contrasenaHash) {
        Usuario usuario = null;
        String sql = "{call sp_Login(?, ?)}";
        
        try (Connection conn = ConexionSQLServer.getInstancia().obtenerConexion();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, correo);
            cstmt.setString(2, contrasenaHash);
            
            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNombreCompleto(rs.getString("nombre_completo"));
                    usuario.setRol(rs.getString("rol"));
                    usuario.setCorreo(correo);
                }
            }
        } catch (SQLException e) {
        }
        return usuario;
    }
    
    public boolean insertarUsuario(Usuario usuario, String contrasenaHash) {
        String sql = "{call sp_InsertarUsuario(?, ?, ?, ?)}";
        
        try (Connection conn = ConexionSQLServer.getInstancia().obtenerConexion();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, usuario.getNombreCompleto());
            cstmt.setString(2, usuario.getCorreo());
            cstmt.setString(3, contrasenaHash);
            cstmt.setString(4, usuario.getRol());
            
            return cstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}