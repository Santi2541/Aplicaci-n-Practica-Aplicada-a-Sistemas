package co.edu.poli.all10.modelo;

import java.util.Objects;

/**
 * Representa a un jugador de AllTen con su mejor resultado.
 * <p>
 * Dos jugadores son iguales si tienen el mismo nombre (sin distinguir
 * mayúsculas), lo que permite guardarlos en un {@link java.util.HashSet}
 * sin duplicados.
 */
public class Jugador {

    /** Puntos que otorga cada objetivo completado. */
    public static final int PUNTOS_POR_OBJETIVO = 10;

    private final String nombre;
    private int puntaje;
    private int objetivosCompletados;
    private int mejorTiempo; // en segundos

    /**
     * Crea un jugador nuevo sin resultados.
     *
     * @param nombre Nombre del jugador; no puede ser nulo ni vacío.
     * @throws IllegalArgumentException si el nombre es nulo o está vacío.
     */
    public Jugador(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }

    /**
     * Crea un jugador con resultados ya guardados (por ejemplo, leídos de la BD).
     *
     * @param nombre               Nombre del jugador.
     * @param puntaje              Mejor puntaje.
     * @param objetivosCompletados Objetivos completados en su mejor partida.
     * @param mejorTiempo          Mejor tiempo en segundos.
     */
    public Jugador(String nombre, int puntaje, int objetivosCompletados, int mejorTiempo) {
        this(nombre);
        this.puntaje = puntaje;
        this.objetivosCompletados = objetivosCompletados;
        this.mejorTiempo = mejorTiempo;
    }

    /** @return Nombre del jugador. */
    public String getNombre() { return nombre; }

    /** @return Mejor puntaje del jugador. */
    public int getPuntaje() { return puntaje; }

    /** @return Cantidad de objetivos (0 a 10) completados en su mejor partida. */
    public int getObjetivosCompletados() { return objetivosCompletados; }

    /** @return Mejor tiempo del jugador, en segundos. */
    public int getMejorTiempo() { return mejorTiempo; }

    /**
     * Registra el resultado de una partida y conserva el mejor: más puntaje y,
     * a igualdad, menos tiempo.
     *
     * @param objetivos Objetivos completados en la partida (0 a 10).
     * @param segundos  Tiempo de la partida en segundos.
     */
    public void registrarPartida(int objetivos, int segundos) {
        int nuevoPuntaje = objetivos * PUNTOS_POR_OBJETIVO;
        if (nuevoPuntaje > puntaje
                || (nuevoPuntaje == puntaje && (mejorTiempo == 0 || segundos < mejorTiempo))) {
            puntaje = nuevoPuntaje;
            objetivosCompletados = objetivos;
            mejorTiempo = segundos;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Jugador)) return false;
        return nombre.equalsIgnoreCase(((Jugador) o).nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase());
    }
}