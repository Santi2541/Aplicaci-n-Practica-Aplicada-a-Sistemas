package co.edu.poli.all10.modelo;

/** Guarda al jugador que inició sesión mientras la aplicación esté abierta. */
public final class SesionActual {

    private static Jugador jugador;

    private SesionActual() { }

    /** Inicia la sesión del jugador dado. */
    public static void iniciar(Jugador j) { jugador = j; }

    /** Cierra la sesión actual. */
    public static void cerrar() { jugador = null; }

    /** @return true si hay un jugador con sesión iniciada. */
    public static boolean haySesion() { return jugador != null; }

    /** @return El jugador con sesión iniciada, o null si no hay. */
    public static Jugador getJugador() { return jugador; }
}