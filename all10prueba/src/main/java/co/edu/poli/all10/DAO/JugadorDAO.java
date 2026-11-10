package co.edu.poli.all10.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.poli.all10.modelo.Jugador;
import co.edu.poli.all10.modelo.Partida;
import co.edu.poli.all10.modelo.RegistroVelocidad;

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
        String sql = "INSERT INTO jugador (nombre, hash, sal, puntaje, objetivos_completados, mejor_tiempo) "
                   + "VALUES (?, ?, ?, 0, 0, 0)";

        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, jugador.getNombre());
            ps.setString(2, jugador.getHash());
            ps.setString(3, jugador.getSal());

            int filasInsertadas = ps.executeUpdate();
            return filasInsertadas > 0;

        } catch (SQLException e) {
            // Error de duplicado (violación de restricción UNIQUE / PRIMARY KEY)
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                return false;
            }
            throw e; // Cualquier otro error SQL se propaga
        }
    }

    /**
     * Devuelve las credenciales guardadas de un jugador.
     *
     * @param nombre Nombre de usuario.
     * @return arreglo {hash, sal, codigo_hash, codigo_sal}, o null si el usuario no existe.
     * @throws SQLException si falla la consulta.
     */
    public String[] obtenerCredenciales(String nombre) throws SQLException {
        String sql = "SELECT hash, sal, codigo_hash, codigo_sal FROM jugador WHERE nombre = ?";
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[] {
                        rs.getString("hash"),
                        rs.getString("sal"),
                        rs.getString("codigo_hash"),
                        rs.getString("codigo_sal")
                    };
                }
            }
        }
        return null;
    }

    /**
     * Guarda (o renueva) el código de recuperación de un jugador, ya con hash y sal.
     *
     * @param nombre Nombre de usuario.
     * @param codigoHash Hash del código de recuperación.
     * @param codigoSal Sal usada para el hash del código.
     * @return true si se actualizó el jugador.
     * @throws SQLException si falla la operación.
     */
    public boolean guardarCodigoRecuperacion(String nombre, String codigoHash, String codigoSal)
            throws SQLException {
        String sql = "UPDATE jugador SET codigo_hash = ?, codigo_sal = ? WHERE nombre = ?";
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigoHash);
            ps.setString(2, codigoSal);
            ps.setString(3, nombre);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Cambia la contraseña (hash y sal) de un jugador.
     *
     * @param nombre Nombre de usuario.
     * @param hash Nuevo hash de la contraseña.
     * @param sal Nueva sal.
     * @return true si se actualizó el jugador.
     * @throws SQLException si falla la operación.
     */
    public boolean restablecerContrasena(String nombre, String hash, String sal) throws SQLException {
        String sql = "UPDATE jugador SET hash = ?, sal = ? WHERE nombre = ?";
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setString(2, sal);
            ps.setString(3, nombre);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Lee un jugador con su mejor resultado.
     *
     * @param nombre Nombre de usuario.
     * @return El jugador, o null si no existe.
     * @throws SQLException si falla la consulta.
     */
    public Jugador obtenerJugador(String nombre) throws SQLException {
        String sql = "SELECT nombre, puntaje, objetivos_completados, mejor_tiempo "
                   + "FROM jugador WHERE nombre = ?";
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Jugador(rs.getString("nombre"), rs.getInt("puntaje"),
                            rs.getInt("objetivos_completados"), rs.getInt("mejor_tiempo"));
                }
            }
        }
        return null;
    }

    /**
     * Historial de partidas de un jugador, de la más reciente a la más antigua.
     *
     * @param nombre Nombre de usuario.
     * @return Lista de partidas del jugador.
     * @throws SQLException si falla la consulta.
     */
    public List<Partida> listarPartidas(String nombre) throws SQLException {
        String sql = "SELECT p.puntaje, p.objetivos_completados, p.tiempo, p.fecha "
                   + "FROM partida p JOIN jugador j ON j.id = p.jugador_id "
                   + "WHERE j.nombre = ? ORDER BY p.fecha DESC, p.id DESC";
        List<Partida> lista = new ArrayList<>();
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Partida(rs.getInt("puntaje"), rs.getInt("objetivos_completados"),
                            rs.getInt("tiempo"), rs.getTimestamp("fecha").toLocalDateTime()));
                }
            }
        }
        return lista;
    }

    /**
     * Ranking por velocidad: partidas con los 10 objetivos resueltos, de menor a mayor tiempo.
     *
     * @return Hasta 50 registros con jugador, tiempo y fecha.
     * @throws SQLException si falla la consulta.
     */
    public List<RegistroVelocidad> listarRankingVelocidad() throws SQLException {
        String sql = "SELECT j.nombre, p.tiempo, p.fecha "
                   + "FROM partida p JOIN jugador j ON j.id = p.jugador_id "
                   + "WHERE p.objetivos_completados = 10 "
                   + "ORDER BY p.tiempo ASC, p.fecha ASC LIMIT 50";
        List<RegistroVelocidad> lista = new ArrayList<>();
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new RegistroVelocidad(rs.getString("nombre"), rs.getInt("tiempo"),
                        rs.getTimestamp("fecha").toLocalDateTime()));
            }
        }
        return lista;
    }

    /**
     * Posición del jugador en la vista ranking.
     *
     * @param nombre Nombre de usuario.
     * @return La posición, o 0 si no aparece.
     * @throws SQLException si falla la consulta.
     */
    public int obtenerPosicion(String nombre) throws SQLException {
        String sql = "SELECT posicion FROM ranking WHERE nombre = ?";
        try (Connection c = conexion.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("posicion") : 0;
            }
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