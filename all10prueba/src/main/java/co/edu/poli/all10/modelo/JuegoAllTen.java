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
    private final java.util.ArrayList<Integer> indicesPorSimbolo = new java.util.ArrayList<>();
    private final Cronometro cronometro = new Cronometro();
    private String expresion = "";
    private int tiempoFinal = -1;   // -1 = la partida aún no termina
    
    private final AyudaSoluciones ayuda = new AyudaSoluciones();
    private final java.util.Set<Integer> objetivosConPista = new java.util.HashSet<>();
    private int pistasUsadas = 0;
    
    /**
     * Inicia la partida: arranca el cronómetro.
     * Se llama al mostrar el tablero, sin esperar a que el jugador toque un número.
     */
    public void iniciarPartida() { 
    	cronometro.iniciar(); 
    }                    
    
    /**
     * Obtiene los segundos transcurridos de la partida.
     * Mientras se juega avanza; al terminar, queda fijo en el tiempo final.
     *
     * @return Segundos enteros transcurridos.
     */
    public int getSegundos() { 
    	return cronometro.obtenerSegundos(); 
    }
    
    /**
     * Obtiene la expresión matemática actual en construcción.
     * 
     * @return Cadena con la expresión actual.
     */
    public String getExpresion() { 
    	return expresion; 
    }

    /**
     * Agrega un símbolo u operador a la expresión actual y lo registra como
     * "no número" (-1) en {@code indicesPorSimbolo}. Si el símbolo es uno de los
     * números base, {@link #usarNumero(int)} actualiza ese registro.
     *
     * @param simbolo El carácter u operador a concatenar.
     */
    
    public void agregarSimbolo(String simbolo) {
        expresion = expresion + " " + simbolo;
        indicesPorSimbolo.add(-1);
    }
    
    /**
     * Marca como utilizado uno de los 4 números base y lo asocia al último
     * símbolo agregado, para poder liberarlo si luego se borra.
     *
     * @param indice Índice del número utilizado (rango de 0 a 3).
     */
    
    public void usarNumero(int indice) {          // 0 a 3
        numerosUsados[indice] = true;
        indicesPorSimbolo.set(indicesPorSimbolo.size() - 1, indice);
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
     * Verifica si el jugador ya completó los 10 objetivos.
     *
     * @return {@code true} si los 10 objetivos están completados; {@code false} en caso contrario.
     */
    public boolean todosLosPasosCompletados() {
        for (boolean completado : pasosCompletados) {
            if (!completado) return false;
        }
        return true;
    }
    
    /**
     * Indica si la partida terminó, es decir, si se resolvieron los 10 objetivos.
     *
     * @return {@code true} si la partida terminó; {@code false} si sigue en curso o fue abandonada.
     */
    public boolean isPartidaTerminada() { 
    	return tiempoFinal >= 0; 
    }
    
    /**
     * Obtiene el tiempo final de la partida.
     *
     * @return Segundos que tardó en resolver los 10 objetivos, o {@code -1} si la partida no ha terminado.
     */
    public int getTiempoFinal() { 
    	return tiempoFinal; 
    }
    
    /** Indica si el número base en esa posición (0 a 3) está usado. */
    public boolean estaUsado(int indice) {
        return numerosUsados[indice];
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
        indicesPorSimbolo.clear();
        java.util.Arrays.fill(numerosUsados, false);
    }
    
    /**
     * Elimina el último símbolo completo de la expresión (no un carácter).
     * Si el símbolo borrado era un número base, lo marca como disponible.
     * Si la pantalla muestra un resultado (tras verificar) o "Error", limpia todo.
     */
    public void borrarUltimo() {
        String t = expresion.trim();
        if (t.isEmpty()) return;

        // Si la expresión es un resultado (tras Verificar) o "Error", se limpia todo
        if (indicesPorSimbolo.isEmpty()) {
            limpiar();
            return;
        }

        int ultimo = indicesPorSimbolo.remove(indicesPorSimbolo.size() - 1);
        if (ultimo >= 0) numerosUsados[ultimo] = false;   // libera el número

        int corte = t.lastIndexOf(' ');
        expresion = (corte == -1) ? "" : t.substring(0, corte).trim();
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
        if (expresion.trim().isEmpty()) return 0;

        boolean completa = todosLosNumerosUsados();
        if (!completa && cantidadNumerosUsados() < 2) return 0;

        indicesPorSimbolo.clear();
        try {
            String e = expresion.trim();
            if (e.contains(":")) e = e.split(":")[1].trim();
            e = e.replace("×", "*").replace("÷", "/");

            double r = evaluador.evaluar(e);

            if (Math.abs(r - Math.round(r)) < 1e-9) {
                int entero = (int) Math.round(r);
                expresion = String.valueOf(entero);
                // Solo cuenta como objetivo si se usaron los 4 números
                if (completa && entero >= 1 && entero <= 10) {
                    pasosCompletados[entero - 1] = true;
                    if (todosLosPasosCompletados()) {
                        cronometro.detener();
                        tiempoFinal = cronometro.obtenerSegundos();
                    }
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
    
    /**
     * Abandona la partida: descarta el cronómetro y no registra tiempo.
     * Una partida abandonada nunca queda con tiempo final.
     */
    public void abandonarPartida() {
        cronometro.descartar();
        tiempoFinal = -1;
    }
    
    /** @return Cantidad de números base (0 a 4) usados en la expresión. */
    public int cantidadNumerosUsados() {
        int total = 0;
        for (boolean usado : numerosUsados) if (usado) total++;
        return total;
    }
  
    /**
     * Prepara la ayuda del nivel básico con los cuatro números de la partida
     * y reinicia el contador de pistas.
     *
     * @param numeros Los cuatro números de la partida.
     */
    public void prepararAyuda(int[] numeros) {
        prepararAyuda(numeros, false);
    }

    /**
     * Prepara la ayuda con los cuatro números de la partida y reinicia el contador
     * de pistas. Se llama cada vez que se generan números nuevos.
     *
     * @param numeros  Los cuatro números de la partida.
     * @param avanzado {@code true} si el nivel incluye potencia y raíz.
     */
    public void prepararAyuda(int[] numeros, boolean avanzado) {
        ayuda.calcular(numeros, avanzado);
        objetivosConPista.clear();
        pistasUsadas = 0;
    }

    /**
     * Pide las soluciones posibles de un objetivo.
     * <p>
     * Cuenta como una pista usada solo cuando muestra soluciones y es la primera vez
     * que se consulta ese objetivo. Si el objetivo ya está completado o no tiene
     * solución, no cuenta.
     *
     * @param objetivo Objetivo del 1 al 10.
     * @return El resultado de la ayuda (con soluciones, sin solución o ya completado).
     */
    public ResultadoAyuda pedirAyuda(int objetivo) {
        if (isPasoCompletado(objetivo)) {
            return ResultadoAyuda.yaCompletado(objetivo);
        }
        java.util.List<String> soluciones = ayuda.solucionesDe(objetivo);
        if (soluciones.isEmpty()) {
            return ResultadoAyuda.sinSolucion(objetivo);
        }
        boolean nueva = objetivosConPista.add(objetivo);   // el mismo objetivo no vuelve a sumar
        if (nueva) pistasUsadas++;
        return ResultadoAyuda.conSoluciones(objetivo, soluciones, nueva);
    }

    /** @return Cantidad de pistas usadas en la partida actual. */
    public int getPistasUsadas() {
        return pistasUsadas;
    }
}