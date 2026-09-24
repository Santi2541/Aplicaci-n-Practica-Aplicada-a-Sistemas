package co.edu.poli.all10.steps;

import static org.junit.jupiter.api.Assertions.*;

import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import co.edu.poli.all10.controller.all10controller;


/*
 * Pruebas tipo cucumber en idioma Gherkin establecido en español para tener una mayor
 * claridad de uso en las operaciones.
 */
public class OperacionesSteps {

    private all10controller controller;
    private double resultadoObtenido;
    private Exception excepcionCapturada;

    /**
     * Paso @Dado: Prepara el entorno de prueba inicializando el controlador y sus 
     * componentes simulados (Mocks) para aislar la ejecución de la vista JavaFX.
     */
    @Dado("que el controlador de All10 está inicializado")
    public void inicializarControlador() {
        controller = new all10controller();
        
        controller.inicializarComponentesSeguros();
    }

    /**
     * Paso @Cuando: Procesa una operación válida. Limpia la cadena de caracteres 
     * visuales (como '×' y '÷') y envía la expresión al motor matemático del controlador.
     * 
     * @param expresion La cadena matemática a evaluar (ej. "( 9 - 6 ) + 3 + 2").
     */
    @Cuando("el jugador evalúa la expresión {string}")
    public void evaluarExpresion(String expresion) {
        try {
            String expresionLimpia = expresion.replace("×", "*").replace("÷", "/");
            resultadoObtenido = controller.evaluarCadenaMatematica(expresionLimpia);
        } catch (Exception e) {
            this.excepcionCapturada = e;
        }
    }

    /**
     * Paso @Entonces: Verifica que la evaluación haya finalizado sin excepciones y 
     * que el valor obtenido sea exactamente igual al esperado por el usuario.
     * 
     * @param resultadoEsperado El valor decimal esperado de la operación.
     */
    @Entonces("el resultado calculado debe ser {double}")
    public void verificarResultado(double resultadoEsperado) {
        assertNull(excepcionCapturada, "No debía ocurrir ningún error en la evaluación");
        assertEquals(resultadoEsperado, resultadoObtenido, 0.0001, "El resultado debe coincidir");
    }
    
    /**
    * Paso @Cuando: Ejecuta una expresión matemática diseñada para fallar (como 
    * división por cero) con el propósito de capturar la excepción resultante en memoria.
    * 
    * @param expresion La cadena matemática que forzará el error de sintaxis o cálculo.
    */
    @Cuando("el jugador intenta evaluar la expresión {string}")
    public void intentarEvaluarDivisionPorCero(String expresion) {
        try {
            String expresionLimpia = expresion.replace("×", "*").replace("÷", "/");
            controller.evaluarCadenaMatematica(expresionLimpia);
        } catch (Exception e) {
            this.excepcionCapturada = e;
        }
    }

    /**
     * Paso @Entonces: Valida que la aplicación no colapse ante errores críticos, confirmando 
     * que la excepción capturada sea específicamente una ArithmeticException.
     */
    @Entonces("el sistema debe detectar una división por cero")
    public void verificarExcepcionDivisionPorCero() {
        assertNotNull(excepcionCapturada, "Se esperaba que capturara la excepción de división por cero");
        assertTrue(excepcionCapturada instanceof ArithmeticException, "Debe ser de tipo ArithmeticException");
    }
}