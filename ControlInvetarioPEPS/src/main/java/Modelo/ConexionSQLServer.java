/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
// modelo/ConexionSQLServer.java
import java.sql.*;
import javax.swing.JOptionPane;
/**
 *
 * @author Fredy
 */
public class ConexionSQLServer {
    private static ConexionSQLServer instancia;  // <- Agregar esto
    private Connection conexion = null;
    
    String usuario = "sa";
    String contrasena = "231124";
    String db = "InventariosPEPS";
    String ip = "localhost";
    String puerto = "1433";
    
    // Constructor privado para Singleton
    private ConexionSQLServer() {
        obtenerConexion();
    }
    
    // Método para obtener la instancia única
    public static ConexionSQLServer getInstancia() {
        if (instancia == null) {
            instancia = new ConexionSQLServer();
        }
        return instancia;
    }
    
    public Connection obtenerConexion(){
        try{
            if (conexion == null || conexion.isClosed()) {
                String cadena = "jdbc:sqlserver://" + ip + ":" + puerto + ";databaseName=" + db +
                        ";encrypt=true;trustServerCertificate=true;";
                conexion = DriverManager.getConnection(cadena, usuario, contrasena);
                JOptionPane.showMessageDialog(null, "Conexion exitosa");
            }
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString());
            e.printStackTrace();
        }
        return conexion;    
    }
    
    // Método para cerrar la conexión
    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}