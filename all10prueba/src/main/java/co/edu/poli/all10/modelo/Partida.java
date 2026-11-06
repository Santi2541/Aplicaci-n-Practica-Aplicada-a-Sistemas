package co.edu.poli.all10.modelo;

import java.time.LocalDateTime;

/** Una partida jugada, tal como se guarda en la tabla partida. */
public class Partida {

    private final int puntaje;
    private final int objetivosCompletados;
    private final int tiempo; // segundos
    private final LocalDateTime fecha;

    public Partida(int puntaje, int objetivosCompletados, int tiempo, LocalDateTime fecha) {
        this.puntaje = puntaje;
        this.objetivosCompletados = objetivosCompletados;
        this.tiempo = tiempo;
        this.fecha = fecha;
    }

    public int getPuntaje() { return puntaje; }
    public int getObjetivosCompletados() { return objetivosCompletados; }
    public int getTiempo() { return tiempo; }
    public LocalDateTime getFecha() { return fecha; }
}