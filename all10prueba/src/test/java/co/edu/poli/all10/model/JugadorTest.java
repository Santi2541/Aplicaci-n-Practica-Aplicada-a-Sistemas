package co.edu.poli.all10.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import co.edu.poli.all10.modelo.Jugador;

/** Pruebas unitarias de {@link Jugador}. */
class JugadorTest {

    /** Un nombre vacío no es válido. */
    @Test
    void nombreVacioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Jugador("  "));
        assertThrows(IllegalArgumentException.class, () -> new Jugador(null));
    }

    /** Solo se conserva la mejor partida: más objetivos, o igual y menos tiempo. */
    @Test
    void registrarPartidaConservaLaMejor() {
        Jugador j = new Jugador("Ana");
        j.registrarPartida(5, 100);
        j.registrarPartida(5, 80);   // mismos objetivos, menos tiempo: mejora
        j.registrarPartida(5, 120);  // peor tiempo: se ignora
        j.registrarPartida(3, 10);   // menos objetivos: se ignora
        assertEquals(5, j.getObjetivosCompletados());
        assertEquals(80, j.getMejorTiempo());
    }

    /** Dos jugadores con el mismo nombre (sin importar mayúsculas) son iguales. */
    @Test
    void igualdadPorNombreSinMayusculas() {
        assertEquals(new Jugador("Ana"), new Jugador("ANA"));
        assertEquals(new Jugador("Ana").hashCode(), new Jugador("ANA").hashCode());
    }
}