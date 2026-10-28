package co.edu.poli.all10.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import co.edu.poli.all10.modelo.Jugador;
import co.edu.poli.all10.modelo.Ranking;

/** Pruebas unitarias de {@link Ranking}. */
class RankingTest {

    private Ranking ranking;

    @BeforeEach
    void setUp() {
        ranking = new Ranking();
    }

    /** Registrar el mismo nombre dos veces no duplica jugadores (HashSet). */
    @Test
    void elMismoNombreNoDuplicaJugadores() {
        ranking.registrarPartida("Ana", 5, 100);
        ranking.registrarPartida("ana", 7, 90);
        assertEquals(1, ranking.getCantidadJugadores());
        assertEquals(7, ranking.buscar("ANA").getObjetivosCompletados());
    }

    /** Orden: más objetivos primero y, a igualdad, menor tiempo. */
    @Test
    void ordenaPorObjetivosYLuegoPorTiempo() {
        ranking.registrarPartida("Luis", 8, 200);
        ranking.registrarPartida("Ana", 8, 150);
        ranking.registrarPartida("Eva", 10, 300);
        List<Jugador> orden = ranking.obtenerOrdenado();
        assertEquals("Eva", orden.get(0).getNombre());
        assertEquals("Ana", orden.get(1).getNombre());
        assertEquals("Luis", orden.get(2).getNombre());
    }

    /** Buscar un nombre que no existe devuelve null. */
    @Test
    void buscarInexistenteDevuelveNull() {
        assertNull(ranking.buscar("Nadie"));
    }
}