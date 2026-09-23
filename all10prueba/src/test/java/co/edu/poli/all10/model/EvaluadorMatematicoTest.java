package co.edu.poli.all10.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.poli.all10.controller.all10controller;

class EvaluadorMatematicoTest {

    private all10controller controller;

    @BeforeEach
    void setUp() {
        controller = new all10controller();
    }

    @Test
    void testValidacionPrecedenciaOperadores() {
        
        double resultado = controller.evaluarCadenaMatematica("9 + 3 * 2");
        assertEquals(15.0, resultado, "Debe resolver primero 3*2=6 y luego 9+6=15");
    }
    
    @Test
    void testValidacionUsoDeParentesis() {
        double resultado = controller.evaluarCadenaMatematica("( 9 + 3 ) * 2");
        assertEquals(24.0, resultado, "Debe resolver primero el paréntesis (9+3)=12 y luego 12*2=24");
    }
    
    @Test
    void testValidacionSumaYRestaCombinada() {
        
        double resultado = controller.evaluarCadenaMatematica("9 + 6 - 3 - 2");
        assertEquals(10.0, resultado, "La operación 9+6-3-2 debe evaluar exactamente a 10.0");
    }
    
    @Test
    void testEvaluarCadenaMatematicaDivisionPorCero() {
        assertThrows(ArithmeticException.class, () -> {
            controller.evaluarCadenaMatematica("6 / ( 3 - 3 )");
        }, "Debe lanzar ArithmeticException al intentar dividir por cero");
    }
    
    @Test
    void testEvaluarCadenaMatematicaOperacionCombinada() {
        String expresion = "( 9 - 6 ) + 3 + 2";
        double resultado = controller.evaluarCadenaMatematica(expresion.replace("×", "*").replace("÷", "/"));
        assertEquals(8.0, resultado, "La operación (9-6)+3+2 debe evaluar exactamente a 8.0");
    }

}