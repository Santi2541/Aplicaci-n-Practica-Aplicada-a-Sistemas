package co.edu.poli.all10.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import co.edu.poli.all10.modelo.Cronometro;
import co.edu.poli.all10.modelo.JuegoAllTen;

/**
 * Pruebas unitarias de {@link Cronometro} y del manejo del tiempo en {@link JuegoAllTen}.
 * No dependen de JavaFX ni de la interfaz gráfica.
 */
class CronometroTest {
	
	/** Un cronómetro nuevo marca 0 segundos y no está en curso. */

    @Test
    void empiezaEnCero() {
        Cronometro c = new Cronometro();
        assertEquals(0, c.obtenerSegundos());
        assertFalse(c.estaEnCurso());
    }
    
    /** Al iniciar, el cronómetro corre y los segundos avanzan con el tiempo. */

    @Test
    void iniciaYAvanza() throws InterruptedException {
        Cronometro c = new Cronometro();
        c.iniciar();
        assertTrue(c.estaEnCurso());
        Thread.sleep(2100);
        int s = c.obtenerSegundos();
        assertTrue(s >= 2 && s <= 3, "Segundos: " + s);
    }
    
    /** Al detenerlo, el tiempo queda fijo y no sigue contando. */

    @Test
    void seDetieneYNoSigueContando() throws InterruptedException {
        Cronometro c = new Cronometro();
        c.iniciar();
        Thread.sleep(1100);
        c.detener();
        int alDetener = c.obtenerSegundos();
        Thread.sleep(1500);
        assertEquals(alDetener, c.obtenerSegundos());
        assertFalse(c.estaEnCurso());
    }
    
    /** No hay pausa: detener e iniciar de nuevo no reanuda, empieza desde cero. */

    @Test
    void noTienePausa() throws InterruptedException {
        Cronometro c = new Cronometro();
        c.iniciar();
        Thread.sleep(1100);
        c.detener();
        c.iniciar();                         // no reanuda: empieza de nuevo
        assertEquals(0, c.obtenerSegundos());
    }
    
    /** Descartar deja el cronómetro detenido y en 0. */

    @Test
    void descartarDejaEnCero() throws InterruptedException {
        Cronometro c = new Cronometro();
        c.iniciar();
        Thread.sleep(1100);
        c.descartar();
        assertEquals(0, c.obtenerSegundos());
        assertFalse(c.estaEnCurso());
    }

    /** Juega el objetivo dado usando los 4 números: "objetivo + 0 + 0 + 0". */
    
    private void jugar(JuegoAllTen j, int objetivo) {
        j.limpiar();
        j.agregarSimbolo(String.valueOf(objetivo));
        j.usarNumero(0);
        for (int i = 1; i < 4; i++) {
            j.agregarSimbolo("+");
            j.agregarSimbolo("0");
            j.usarNumero(i);
        }
        j.calcular();
    }
    
    /** Al resolver los 10 objetivos, el juego guarda el tiempo final y este queda fijo. */

    @Test
    void elJuegoGuardaElTiempoAlResolverLos10() throws InterruptedException {
        JuegoAllTen j = new JuegoAllTen();
        j.iniciarPartida();
        for (int k = 1; k <= 9; k++) jugar(j, k);
        assertFalse(j.isPartidaTerminada());

        Thread.sleep(1100);
        jugar(j, 10);

        assertTrue(j.isPartidaTerminada());
        int t = j.getTiempoFinal();
        assertTrue(t >= 1);
        Thread.sleep(1100);
        assertEquals(t, j.getSegundos());    // el tiempo quedó fijo
    }
    
    /** Si el jugador abandona, no se registra tiempo y el reloj queda en 0. */

    @Test
    void abandonarNoRegistraTiempo() throws InterruptedException {
        JuegoAllTen j = new JuegoAllTen();
        j.iniciarPartida();
        Thread.sleep(1100);
        j.abandonarPartida();
        assertFalse(j.isPartidaTerminada());
        assertEquals(0, j.getSegundos());
    }
}
