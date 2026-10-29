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
     * Verifica si un nombre de usuario ya está registrado en la base de datos.
     *
     * @param nombre Nombre de usuario a buscar.
     * @return true si el usuario ya existe, false en caso contrario.
     * @throws SQLException si ocurre un error en la base de datos.
     */
    public boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT 1 FROM jugador WHERE nombre = ?";
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Si hay un resultado, el nombre ya existe
            }
        }
    }

    /**
     * Inserta un nuevo jugador en la base de datos con sus credenciales (hash y sal).
     *
     * @param jugador El nuevo jugador a registrar.
     * @return true si se insertó con éxito, false si no se pudo insertar.
     * @throws SQLException si ocurre un error en la base de datos.
     */
    public boolean insertar(Jugador jugador) throws SQLException {
        // Asegúrate de que los nombres de las columnas 'hash' y 'sal' coincidan con tu base de datos
        String sql = "INSERT INTO jugador (nombre, hash, sal, puntaje, objetivos_completados, mejor_tiempo) "
                   + "VALUES (?, ?, ?, 0, 0, 0)";
        
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
             
            ps.setString(1, jugador.getNombre());
            ps.setString(2, jugador.getHash()); // Tu clase Jugador debe tener este getter
            ps.setString(3, jugador.getSal());  // Tu clase Jugador debe tener este getter
            
            int filasInsertadas = ps.executeUpdate();
            return filasInsertadas > 0;
            
        } catch (SQLException e) {
            // Si ocurre un error de duplicado (Violación de restricción Unique/Primary Key)
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                return false;
            }
            throw e; // Lanza cualquier otro tipo de error SQL
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