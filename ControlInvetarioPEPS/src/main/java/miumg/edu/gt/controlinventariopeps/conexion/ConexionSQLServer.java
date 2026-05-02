package miumg.edu.gt.controlinventariopeps.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionSQLServer {
    private static ConexionSQLServer instancia;
    private Connection conexion;
    private final String usuario = "sa";
    private final String contrasena = "2026"; // Cambia por la contraseña real de tu sa
    private final String db = "InventariosPEPS"; // 
    private final String servidor = "FERNANDA\\SQLEXPRESS"; // Tu servidor con instancia

    private ConexionSQLServer() {}

    public static ConexionSQLServer getInstancia() {
        if (instancia == null) {
            instancia = new ConexionSQLServer();
        }
        return instancia;
    }

    public Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            String cadena = "jdbc:sqlserver://" + servidor + ";databaseName=" + db
                    + ";encrypt=true;trustServerCertificate=true;";
            conexion = DriverManager.getConnection(cadena, usuario, contrasena);
        }
        return conexion;
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}