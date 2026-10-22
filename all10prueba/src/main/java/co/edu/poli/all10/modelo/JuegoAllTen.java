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
    
    /**
     * Obtiene la expresión matemática actual en construcción.
     * 
     * @return Cadena con la expresión actual.
     */
    public String getExpresion() { return expresion; }

    /**
     * Agrega un símbolo u operador a la expresión actual.
     * 
     * @param simbolo El carácter u operador a concatenar.
     */
    public void agregarSimbolo(String simbolo) {
        expresion = expresion + " " + simbolo;
    }
    
    /**
     * Marca como utilizado uno de los 4 números base dados.
     * 
     * @param indice Índice del número utilizado (rango de 0 a 3).
     */
    public void usarNumero(int indice) {          // 0 a 3
        numerosUsados[indice] = true;
    }
    
    /**
     * Verifica si el jugador ha utilizado los 4 números base asignados.
     * 
     * @return {@code true} si los 4 números fueron utilizados; {@code false} en caso contrario.
     */
    public boolean todosLosNumerosUsados() {
        for (boolean usado : numerosUsados) {
            if (!usado) return false;
        }
        return true;
    }

    /**
     * Consulta si un paso u objetivo específico (1 al 10) ha sido completado.
     * 
     * @param paso Número del objetivo a consultar (rango de 1 a 10).
     * @return {@code true} si el objetivo fue completado; {@code false} en caso contrario.
     */
    public boolean isPasoCompletado(int paso) {   // 1 a 10
        return pasosCompletados[paso - 1];
    }

    /**
     * Restablece la expresión actual y habilita el uso de los números base.
     */
    public void limpiar() {
        expresion = "";
        java.util.Arrays.fill(numerosUsados, false);
    }

    /**
     * Elimina el último carácter o elemento ingresado en la expresión matemática.
     */
    public void borrarUltimo() {
        String t = expresion.trim();
        if (!t.isEmpty()) {
            expresion = t.substring(0, t.length() - 1).trim();
        }
    }

    /**
     * Evalúa la expresión construida aplicando las reglas de negocio.
     * <p>
     * Devuelve el objetivo (1-10) alcanzado o 0 si no se completó ninguno.
     * Deja el resultado en {@link #getExpresion()}.
     * 
     * @return El número entero del objetivo alcanzado (1 al 10), o {@code 0} si no se completó ninguno
     *         o si la jugada no es válida.
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

            if (Math.abs(r - Math.round(r)) < 1e-9) {
                int entero = (int) Math.round(r);
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
