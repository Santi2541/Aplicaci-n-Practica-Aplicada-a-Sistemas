package co.edu.poli.all10.modelo;

/**
 * Modelo del juego AllTen.
 * <p>
 * Mantiene el estado de la partida (expresión en construcción, números base
 * utilizados y objetivos del 1 al 10 completados) y aplica las reglas:
 * la expresión solo se evalúa si se usaron los cuatro números base, y un
 * objetivo se completa únicamente cuando el resultado es un entero entre 1 y 10.
 * <p>
 * Es independiente de JavaFX, por lo que puede probarse sin interfaz gráfica.
 * La evaluación matemática se delega en {@link Evaluador}.
 */
public class JuegoAllTen {
	
	private final Evaluador evaluador = new Evaluador();
    private final boolean[] numerosUsados = new boolean[4];
    private final boolean[] pasosCompletados = new boolean[10];
    private String expresion = "";
    
    public String getExpresion() { return expresion; }

    public void agregarSimbolo(String simbolo) {
        expresion = expresion + " " + simbolo;
    }
    
    public void usarNumero(int indice) {          // 0 a 3
        numerosUsados[indice] = true;
    }
    
    public boolean todosLosNumerosUsados() {
        for (boolean usado : numerosUsados) {
            if (!usado) return false;
        }
        return true;
    }

    public boolean isPasoCompletado(int paso) {   // 1 a 10
        return pasosCompletados[paso - 1];
    }

    public void limpiar() {
        expresion = "";
        java.util.Arrays.fill(numerosUsados, false);
    }

    public void borrarUltimo() {
        String t = expresion.trim();
        if (!t.isEmpty()) {
            expresion = t.substring(0, t.length() - 1).trim();
        }
    }

    /**
     * Evalúa la expresión. Devuelve el objetivo (1-10) alcanzado,
     * o 0 si no se completó ninguno. Deja el resultado en getExpresion().
     */
    public int calcular() {
        if (!todosLosNumerosUsados() || expresion.trim().isEmpty()) {
            return 0;
        }
        try {
            String e = expresion.trim();
            if (e.contains(":")) e = e.split(":")[1].trim();
            e = e.replace("×", "*").replace("÷", "/");

            double r = evaluador.evaluar(e);

            if (r == (long) r) {
                int entero = (int) r;
                expresion = String.valueOf(entero);
                if (entero >= 1 && entero <= 10) {
                    pasosCompletados[entero - 1] = true;
                    return entero;
                }
            } else {
                expresion = String.valueOf(r);
            }
        } catch (Exception ex) {
            expresion = "Error";
        }
        return 0;
    }
}
