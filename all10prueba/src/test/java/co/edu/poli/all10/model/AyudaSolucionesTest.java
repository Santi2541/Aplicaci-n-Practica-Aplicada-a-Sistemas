package co.edu.poli.all10.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AyudaSolucionesTest {

    /** Evalúa una solución mostrada con el mismo Evaluador que usa el juego. */
    private double evaluar(String solucion) {
        return new Evaluador().evaluar(solucion);
    }

    @Test
    void cadaSolucionDaElObjetivoCorrecto() {
        int[][] partidas = { {2, 3, 4, 5}, {1, 2, 6, 8}, {3, 3, 7, 9} };
        for (int[] numeros : partidas) {
            AyudaSoluciones ayuda = new AyudaSoluciones();
            ayuda.calcular(numeros);
            for (int objetivo = 1; objetivo <= 10; objetivo++) {
                for (String sol : ayuda.solucionesDe(objetivo)) {
                    assertEquals(objetivo, evaluar(sol), 1e-9,
                            "La solución " + sol + " debía dar " + objetivo);
                }
            }
        }
    }

    @Test
    void cadaSolucionAvanzadaDaElObjetivoCorrecto() {
        int[][] partidas = { {2, 3, 4, 9}, {1, 2, 6, 8}, {3, 4, 5, 9} };
        for (int[] numeros : partidas) {
            AyudaSoluciones ayuda = new AyudaSoluciones();
            ayuda.calcular(numeros, true);
            for (int objetivo = 1; objetivo <= 10; objetivo++) {
                for (String sol : ayuda.solucionesDe(objetivo)) {
                    assertEquals(objetivo, evaluar(sol), 1e-9,
                            "La solución " + sol + " debía dar " + objetivo);
                }
            }
        }
    }

    @Test
    void noMuestraMasDeTresNiRepetidas() {
        AyudaSoluciones ayuda = new AyudaSoluciones();
        ayuda.calcular(new int[]{2, 3, 4, 5});
        for (int objetivo = 1; objetivo <= 10; objetivo++) {
            List<String> sol = ayuda.solucionesDe(objetivo);
            assertTrue(sol.size() <= 3);
            assertEquals(sol.size(), new HashSet<>(sol).size());
        }
    }

    @Test
    void usaFormatoDelJuego() {
        AyudaSoluciones ayuda = new AyudaSoluciones();
        ayuda.calcular(new int[]{2, 3, 4, 5});
        for (int objetivo = 1; objetivo <= 10; objetivo++) {
            for (String sol : ayuda.solucionesDe(objetivo)) {
                assertFalse(sol.contains("*"));
                assertFalse(sol.contains("/"));
            }
        }
    }

    @Test
    void objetivoSinSolucion() {
        AyudaSoluciones ayuda = new AyudaSoluciones();
        ayuda.calcular(new int[]{1, 1, 1, 1});   // con 1, 1, 1, 1 lo máximo que se logra es 4
        assertFalse(ayuda.tieneSolucion(10));
        assertTrue(ayuda.solucionesDe(10).isEmpty());
        assertTrue(ayuda.tieneSolucion(4));
    }

    @Test
    void nivelAvanzadoUsaPotenciaYRaizSoloAhi() {
        AyudaSoluciones basica = new AyudaSoluciones();
        basica.calcular(new int[]{2, 3, 4, 9}, false);
        AyudaSoluciones avanzada = new AyudaSoluciones();
        avanzada.calcular(new int[]{2, 3, 4, 9}, true);

        boolean avanzadaUsaOperadorNuevo = false;
        for (int objetivo = 1; objetivo <= 10; objetivo++) {
            for (String sol : basica.solucionesDe(objetivo)) {
                assertFalse(sol.contains("^") || sol.contains("√"));
            }
            for (String sol : avanzada.solucionesDe(objetivo)) {
                if (sol.contains("^") || sol.contains("√")) avanzadaUsaOperadorNuevo = true;
            }
        }
        assertTrue(avanzadaUsaOperadorNuevo);
    }

    @Test
    void pedirAyudaCuentaLaPistaUnaSolaVezPorObjetivo() {
        JuegoAllTen juego = new JuegoAllTen();
        juego.prepararAyuda(new int[]{2, 3, 4, 5});

        ResultadoAyuda primera = juego.pedirAyuda(5);
        assertEquals(ResultadoAyuda.Estado.CON_SOLUCIONES, primera.getEstado());
        assertEquals(1, juego.getPistasUsadas());

        juego.pedirAyuda(5);                       // el mismo objetivo no suma
        assertEquals(1, juego.getPistasUsadas());

        juego.pedirAyuda(6);                       // otro objetivo sí suma
        assertEquals(2, juego.getPistasUsadas());
    }

    @Test
    void sinSolucionNoCuentaComoPista() {
        JuegoAllTen juego = new JuegoAllTen();
        juego.prepararAyuda(new int[]{1, 1, 1, 1});

        ResultadoAyuda r = juego.pedirAyuda(10);
        assertEquals(ResultadoAyuda.Estado.SIN_SOLUCION, r.getEstado());
        assertEquals(0, juego.getPistasUsadas());
    }

    @Test
    void objetivoCompletadoNoCuentaComoPista() {
        JuegoAllTen juego = new JuegoAllTen();
        juego.prepararAyuda(new int[]{2, 3, 4, 5});

        // 2 + 3 + 4 - 5 = 4: se arma la expresión como lo hace la pantalla
        String[] simbolos = {"2", "+", "3", "+", "4", "-", "5"};
        int[] indices = {0, -1, 1, -1, 2, -1, 3};
        for (int i = 0; i < simbolos.length; i++) {
            juego.agregarSimbolo(simbolos[i]);
            if (indices[i] >= 0) juego.usarNumero(indices[i]);
        }
        assertEquals(4, juego.calcular());

        ResultadoAyuda r = juego.pedirAyuda(4);
        assertEquals(ResultadoAyuda.Estado.YA_COMPLETADO, r.getEstado());
        assertEquals(0, juego.getPistasUsadas());
    }
}