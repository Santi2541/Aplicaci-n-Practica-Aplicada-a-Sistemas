package co.edu.poli.all10.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import co.edu.poli.all10.modelo.Numeros;

/**
 * Pruebas unitarias de {@link Numeros}, la clase que genera los 4 números
 * del juego AllTen.
 * <p>
 * Verifican que la lista de números:
 * <ul>
 *   <li>es estable: la misma fecha siempre produce los mismos números,</li>
 *   <li>cambia de un día a otro,</li>
 *   <li>cumple el formato esperado (4 números entre 1 y 9),</li>
 *   <li>coincide con la fecha actual cuando se usa {@link Numeros#deHoy()}.</li>
 * </ul>
 * Las pruebas no dependen de JavaFX ni de la interfaz gráfica.
 */
class NumerosTest {

	   /** Primer día del rango de fechas que se revisa en las pruebas. */
    private static final LocalDate INICIO = LocalDate.of(2026, 10, 20);
    
    /** Cantidad de días consecutivos que se revisan. */
    private static final int DIAS = 365;

    /**
     * Comprueba que generar los números dos veces para una misma fecha
     * da siempre el mismo resultado.
     * <p>
     * Es la propiedad que garantiza que todos los jugadores vean los
     * mismos números durante el mismo día.
     */
    @Test
    void lamismaFechaDaLosMismosNumeros() {
        for (int i = 0; i < DIAS; i++) {
            LocalDate dia = INICIO.plusDays(i);
            assertEquals(Numeros.paraFecha(dia), Numeros.paraFecha(dia), "Cambió en " + dia);
        }
    }

    /**
     * Comprueba que los números cambian con el paso de los días.
     * <p>
     * Revisa dos cosas en un año: que existan muchas combinaciones distintas
     * y que casi nunca se repita la lista de un día al siguiente. Se permiten
     * hasta 3 repeticiones consecutivas porque, al ser pocas combinaciones
     * posibles, una coincidencia ocasional es normal.
     */
    @Test
    void losNumerosCambianDeUnDiaAOtro() {
        Set<List<Integer>> distintas = new HashSet<>();
        List<Integer> anterior = null;
        int consecutivasIguales = 0;

        for (int i = 0; i < DIAS; i++) {
            List<Integer> hoy = Numeros.paraFecha(INICIO.plusDays(i));
            distintas.add(hoy);
            if (hoy.equals(anterior)) consecutivasIguales++;
            anterior = hoy;
        }

        assertTrue(distintas.size() > 100, "Muy pocas combinaciones: " + distintas.size());
        assertTrue(consecutivasIguales <= 3, "Demasiados días seguidos iguales: " + consecutivasIguales);
    }

    /**
     * Comprueba el formato de cada lista generada: exactamente 4 números
     * y todos dentro del rango de 1 a 9.
     */
    @Test
    void cadaListaTieneCuatroNumerosEntre1Y9() {
        for (int i = 0; i < DIAS; i++) {
            List<Integer> lista = Numeros.paraFecha(INICIO.plusDays(i));
            assertEquals(4, lista.size());
            for (int n : lista) {
                assertTrue(n >= 1 && n <= 9, "Número fuera de rango: " + n);
            }
        }
    }

    /**
     * Comprueba que {@link Numeros#deHoy()} usa la fecha actual del sistema,
     * es decir, que equivale a llamar a {@link Numeros#paraFecha(LocalDate)}
     * con {@link LocalDate#now()}.
     */
    @Test
    void deHoyCoincideConLaFechaDelSistema() {
        assertEquals(Numeros.paraFecha(LocalDate.now()), Numeros.deHoy());
    }
}
