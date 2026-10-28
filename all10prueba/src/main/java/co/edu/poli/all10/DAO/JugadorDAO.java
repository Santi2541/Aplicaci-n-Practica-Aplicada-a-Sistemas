package co.edu.poli.all10.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.poli.all10.modelo.Jugador;

/** Guarda y consulta jugadores en la tabla jugador y en la vista ranking. */
public class JugadorDAO {

    private final ConexionDB conexion;

    /** Crea el DAO. */
    public JugadorDAO() {
        this.conexion = new ConexionDB();
    }

    /**
     * Inserta el jugador o actualiza su resultado si el nombre ya existe.
     *
     * @param jugador Jugador a guardar.
     * @throws SQLException si falla la operación.
     */
    public void guardar(Jugador jugador) throws SQLException {
        String sql = "INSERT INTO jugador (nombre, puntaje, objetivos_completados, mejor_tiempo) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE puntaje = VALUES(puntaje), "
                + "objetivos_completados = VALUES(objetivos_completados), "
                + "mejor_tiempo = VALUES(mejor_tiempo)";
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, jugador.getNombre());
            ps.setInt(2, jugador.getPuntaje());
            ps.setInt(3, jugador.getObjetivosCompletados());
            ps.setInt(4, jugador.getMejorTiempo());
            ps.executeUpdate();
        }
    }

    /**
     * Lista el Top 10 leyendo la vista ranking.
     *
     * @return Jugadores del mejor al peor.
     * @throws SQLException si falla la consulta.
     */
    public List<Jugador> listarTop10() throws SQLException {
        return consultar("SELECT nombre, puntaje, objetivos_completados, mejor_tiempo "
                + "FROM ranking ORDER BY posicion LIMIT 10");
    }

    /**
     * Lista todos los jugadores guardados.
     *
     * @return Todos los jugadores, del mejor al peor.
     * @throws SQLException si falla la consulta.
     */
    public List<Jugador> listarTodos() throws SQLException {
        return consultar("SELECT nombre, puntaje, objetivos_completados, mejor_tiempo "
                + "FROM ranking ORDER BY posicion");
    }

    private List<Jugador> consultar(String sql) throws SQLException {
        List<Jugador> lista = new ArrayList<>();
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Jugador(rs.getString("nombre"), rs.getInt("puntaje"),
                        rs.getInt("objetivos_completados"), rs.getInt("mejor_tiempo")));
            }
        }
        return lista;
    }
}