package co.edu.poli.all10.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Abre conexiones a la base de datos all10. */
public class ConexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/all10?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CLAVE = "081305_D4";

    /**
     * Abre una conexión nueva; quien la recibe debe cerrarla.
     *
     * @return Conexión abierta.
     * @throws SQLException si no se puede conectar.
     */
    public Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}