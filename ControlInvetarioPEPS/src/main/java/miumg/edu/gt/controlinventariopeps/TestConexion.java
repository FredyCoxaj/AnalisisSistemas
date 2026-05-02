package miumg.edu.gt.controlinventariopeps;

import miumg.edu.gt.controlinventariopeps.conexion.ConexionSQLServer;
import java.sql.Connection;

public class TestConexion {
    public static void main(String[] args) {
        try {
            Connection conn = ConexionSQLServer.getInstancia().obtenerConexion();
            System.out.println("Conectado: " + (conn != null && !conn.isClosed()));
            conn.close();
        } catch (Exception e) {
            System.out.println("ERROR DE CONEXIÓN:");
            System.out.println(e.getMessage());
            e.printStackTrace(System.out); // imprime en System.out para que Maven lo muestre
        }
    }
}