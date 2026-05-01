/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package miumg.edu.gt.controlinvetariopeps.Modelo;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 *
 * @author Fredy
 */
public class ConexionSQLServer {
    Connection conexion = null;
    
    String usuario = "sa";
    String contrasena = "231124";
    String db = "InventariosPEPS";
    String ip = "localhost";
    String puerto = "1433";
    
    public Connection ObtenerConexion(){
        try{
            String cadena = "jdbc:sqlserver://" + ip + ":" + puerto + ";databaseName =" + db +
                    ";encrypt=true;trustServerCertificate=true;";
            conexion = DriverManager.getConnection(cadena, usuario, contrasena);
            JOptionPane.showMessageDialog(null, "Conexion exitosa");
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString());
        }
        return conexion;    
    }
}