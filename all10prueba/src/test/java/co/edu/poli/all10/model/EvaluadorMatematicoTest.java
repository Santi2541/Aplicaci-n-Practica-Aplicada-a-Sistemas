package co.edu.poli.all10.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.poli.all10.controller.all10controller;


/**
 * Suite de pruebas unitarias para el controlador del juego AllTen.
 * Valida de forma aislada el motor de evaluación matemática, garantizando el 
 * correcto procesamiento de la jerarquía de operadores, los signos de agrupación 
 * y el manejo seguro de excepciones sin requerir la interfaz gráfica de JavaFX.
 */
class EvaluadorMatematicoTest {

    private all10controller controller;

    /**
     * Prepara el entorno antes de cada prueba inicializando una nueva instancia 
     * limpia del controlador para evitar interferencias de estado entre tests.
     */
    @BeforeEach
    void setUp() {
        controller = new all10controller();
    }

    /**
     * Verifica que el motor matemático respete la jerarquía estándar de operadores.
     * Asegura que las multiplicaciones y divisiones se ejecuten estrictamente 
     * antes que las sumas y restas.
     */
    @Test
    void testValidacionPrecedenciaOperadores() {
        
        double resultado = controller.evaluarCadenaMatematica("9 + 3 * 2");
        assertEquals(15.0, resultado, "Debe resolver primero 3*2=6 y luego 9+6=15");
    }
    
    /**
     * Comprueba que el uso de signos de agrupación (paréntesis) altere 
     * correctamente el orden de evaluación predeterminado de la expresión.
     */
    @Test
    void testValidacionUsoDeParentesis() {
        double resultado = controller.evaluarCadenaMatematica("( 9 + 3 ) * 2");
        assertEquals(24.0, resultado, "Debe resolver primero el paréntesis (9+3)=12 y luego 12*2=24");
    }
    
    /**
     * Valida el cálculo secuencial de operaciones combinadas de la misma jerarquía 
     * para garantizar precisión en resultados lineales.
     */
    @Test
    void testValidacionSumaYRestaCombinada() {
        
        double resultado = controller.evaluarCadenaMatematica("9 + 6 - 3 - 2");
        assertEquals(10.0, resultado, "La operación 9+6-3-2 debe evaluar exactamente a 10.0");
    }
    
    /**
     * Asegura la robustez del sistema frente a errores matemáticos críticos.
     * Verifica que el analizador capture y lance una ArithmeticException al 
     * detectar un intento de división por cero.
     */
    @Test
    void testEvaluarCadenaMatematicaDivisionPorCero() {
        assertThrows(ArithmeticException.class, () -> {
            controller.evaluarCadenaMatematica("6 / ( 3 - 3 )");
        }, "Debe lanzar ArithmeticException al intentar dividir por cero");
    }
    
    /**
     * Simula un escenario completo y válido del juego All10.
     * Evalúa una cadena que incluye reemplazo de símbolos visuales, uso de los 
     * cuatro números permitidos, paréntesis y múltiples operadores.
     */
    @Test
    void testEvaluarCadenaMatematicaOperacionCombinada() {
        String expresion = "( 9 - 6 ) + 3 + 2";
        double resultado = controller.evaluarCadenaMatematica(expresion.replace("×", "*").replace("÷", "/"));
        assertEquals(8.0, resultado, "La operación (9-6)+3+2 debe evaluar exactamente a 8.0");
    }

}