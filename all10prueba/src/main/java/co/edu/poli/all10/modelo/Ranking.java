package co.edu.poli.all10.modelo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tabla de posiciones de AllTen.
 * <p>
 * Contiene a los {@link Jugador} (composición): los crea y los conserva en un
 * {@link HashSet}, que evita jugadores repetidos. El orden se calcula al
 * consultar: más objetivos primero y, a igualdad, menor tiempo.
 */
public class Ranking {

    private final Set<Jugador> jugadores;

    /** Crea un ranking vacío. */
    public Ranking() {
        this.jugadores = new HashSet<>();
    }

    /**
     * Busca un jugador por nombre, sin distinguir mayúsculas.
     *
     * @param nombre Nombre a buscar.
     * @return El jugador encontrado, o {@code null} si no existe.
     */
    public Jugador buscar(String nombre) {
        if (nombre == null) return null;
        for (Jugador j : jugadores) {
            if (j.getNombre().equalsIgnoreCase(nombre.trim())) return j;
        }
        return null;
    }
    
    /**
     * Agrega un jugador ya existente en la base de datos.
     *
     * @param jugador Jugador a agregar.
     * @return {@code true} si se agregó; {@code false} si ya había uno con ese nombre.
     */
    public boolean agregar(Jugador jugador) {
        return jugadores.add(jugador);
    }

    /**
     * Registra el resultado de una partida. Si el jugador no existe lo crea;
     * si existe, conserva su mejor resultado.
     *
     * @param nombre    Nombre del jugador.
     * @param objetivos Objetivos completados (0 a 10).
     * @param segundos  Tiempo de la partida en segundos.
     * @return El jugador con su resultado actualizado.
     */
    public Jugador registrarPartida(String nombre, int objetivos, int segundos) {
        Jugador jugador = buscar(nombre);
        if (jugador == null) {
            jugador = new Jugador(nombre);
            jugadores.add(jugador);
        }
        jugador.registrarPartida(objetivos, segundos);
        return jugador;
    }

    /**
     * Devuelve los jugadores ordenados por posición: más objetivos primero y,
     * a igualdad, menor tiempo.
     *
     * @return Lista nueva y ordenada; modificarla no altera el ranking.
     */
    public List<Jugador> obtenerOrdenado() {
        List<Jugador> lista = new ArrayList<>(jugadores);
        lista.sort(Comparator.comparingInt(Jugador::getObjetivosCompletados).reversed()
                .thenComparingInt(Jugador::getMejorTiempo));
        return lista;
    }

    /** @return Cantidad de jugadores registrados. */
    public int getCantidadJugadores() {
        return jugadores.size();
    }
}