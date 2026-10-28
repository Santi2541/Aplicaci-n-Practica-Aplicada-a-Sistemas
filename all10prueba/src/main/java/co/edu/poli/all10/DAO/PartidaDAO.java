package co.edu.poli.all10.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Guarda cada partida jugada en la tabla partida. */
public class PartidaDAO {

    private final ConexionDB conexion;

    /** Crea el DAO. */
    public PartidaDAO() {
        this.conexion = new ConexionDB();
    }

    /**
     * Guarda una partida del jugador. El jugador debe existir en la tabla jugador.
     *
     * @param nombre    Nombre del jugador.
     * @param puntaje   Puntaje obtenido en la partida.
     * @param objetivos Objetivos completados (0 a 10).
     * @param segundos  Tiempo de la partida en segundos.
     * @throws SQLException si falla la operación.
     */
    public void guardar(String nombre, int puntaje, int objetivos, int segundos) throws SQLException {
        String sql = "INSERT INTO partida (jugador_id, puntaje, objetivos_completados, tiempo) "
                + "SELECT id, ?, ?, ? FROM jugador WHERE nombre = ?";
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, puntaje);
            ps.setInt(2, objetivos);
            ps.setInt(3, segundos);
            ps.setString(4, nombre);
            ps.executeUpdate();
        }
    }
}