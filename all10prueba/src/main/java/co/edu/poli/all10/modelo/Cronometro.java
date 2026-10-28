package co.edu.poli.all10.modelo;
/**
 * Cronómetro de una partida de AllTen, medido en segundos enteros.
 * <p>
 * Guarda el instante de inicio con {@link System#nanoTime()} y calcula los
 * segundos transcurridos cuando se le pregunta, en lugar de sumar de uno en uno.
 * No depende de JavaFX, por lo que se puede probar sin interfaz gráfica.
 * <p>
 * No tiene pausa: solo se puede iniciar, detener o descartar. Si se detiene y
 * se vuelve a iniciar, cuenta desde cero.
 */
public class Cronometro {

    private long inicioNanos;
    private long finNanos;
    private boolean enCurso = false;

    /** Inicia el conteo desde cero. Si ya está corriendo, no hace nada. */
    public void iniciar() {
        if (enCurso) return;
        inicioNanos = System.nanoTime();
        finNanos = inicioNanos;
        enCurso = true;
    }

    /** Detiene el conteo y deja fijo el tiempo transcurrido. */
    public void detener() {
        if (!enCurso) return;
        finNanos = System.nanoTime();
        enCurso = false;
    }

    /**
     * Obtiene los segundos enteros transcurridos.
     * Si está corriendo, cuenta hasta este momento; si está detenido,
     * devuelve el tiempo fijo del momento en que se detuvo.
     *
     * @return Segundos transcurridos (0 si nunca se inició o se descartó).
     */
    public int obtenerSegundos() {
        long fin = enCurso ? System.nanoTime() : finNanos;
        return (int) ((fin - inicioNanos) / 1_000_000_000L);
    }
    
    /**
     * Indica si el cronómetro está corriendo.
     *
     * @return {@code true} si está en curso; {@code false} si está detenido o descartado.
     */
    public boolean estaEnCurso() { 
    	return enCurso; 
    }

    /**
     * Descarta el tiempo: detiene el cronómetro y lo deja en 0.
     * Se usa cuando el jugador abandona el tablero.
     */
    public void descartar() {
        enCurso = false;
        inicioNanos = 0;
        finNanos = 0;
    }
}