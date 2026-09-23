package co.edu.poli.all10.steps;

import static org.junit.jupiter.api.Assertions.*;

import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import co.edu.poli.all10.controller.all10controller;

public class OperacionesSteps {

    private all10controller controller;
    private double resultadoObtenido;
    private Exception excepcionCapturada;

    @Dado("que el controlador de All10 está inicializado")
    public void inicializarControlador() {
        controller = new all10controller();
        // Garantizamos que el controlador tenga sus botones e hilos preparados
        controller.inicializarComponentesSeguros();
    }

    @Cuando("el jugador evalúa la expresión {string}")
    public void evaluarExpresion(String expresion) {
        try {
            String expresionLimpia = expresion.replace("×", "*").replace("÷", "/");
            resultadoObtenido = controller.evaluarCadenaMatematica(expresionLimpia);
        } catch (Exception e) {
            this.excepcionCapturada = e;
        }
    }

    @Entonces("el resultado calculado debe ser {double}")
    public void verificarResultado(double resultadoEsperado) {
        assertNull(excepcionCapturada, "No debía ocurrir ningún error en la evaluación");
        assertEquals(resultadoEsperado, resultadoObtenido, 0.0001, "El resultado debe coincidir");
    }

    @Cuando("el jugador intenta evaluar la expresión {string}")
    public void intentarEvaluarDivisionPorCero(String expresion) {
        try {
            String expresionLimpia = expresion.replace("×", "*").replace("÷", "/");
            controller.evaluarCadenaMatematica(expresionLimpia);
        } catch (Exception e) {
            this.excepcionCapturada = e;
        }
    }

    @Entonces("el sistema debe detectar una división por cero")
    public void verificarExcepcionDivisionPorCero() {
        assertNotNull(excepcionCapturada, "Se esperaba que capturara la excepción de división por cero");
        assertTrue(excepcionCapturada instanceof ArithmeticException, "Debe ser de tipo ArithmeticException");
    }
}