package co.edu.poli.all10.modelo;

import java.time.LocalDateTime;

/** Una fila del ranking por velocidad: jugador, tiempo y fecha en que logró resolver el juego. */
public class RegistroVelocidad {

    private final String nombre;
    private final int tiempo; // segundos
    private final LocalDateTime fecha;

    public RegistroVelocidad(String nombre, int tiempo, LocalDateTime fecha) {
        this.nombre = nombre;
        this.tiempo = tiempo;
        this.fecha = fecha;
    }

    public String getNombre() { return nombre; }
    public int getTiempo() { return tiempo; }
    public LocalDateTime getFecha() { return fecha; }
}