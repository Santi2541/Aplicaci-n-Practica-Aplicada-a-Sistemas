package co.edu.poli.all10.modelo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Calcula posibles soluciones para los objetivos del 1 al 10 con los cuatro
 * números de la partida, usando cada número una sola vez.
 * <p>
 * Nivel básico: + - × ÷. Nivel avanzado: además potencia ({@code ^}) y raíz
 * ({@code índice √ radicando}). Los resultados intermedios de + - × ÷ pueden ser
 * fraccionarios, pero el resultado final debe ser entero. La potencia y la raíz solo
 * se permiten entre enteros y con resultado entero.
 * <p>
 * Se muestran primero las soluciones con enteros en todos los pasos y, solo si hacen
 * falta, las que usan fracciones. En el nivel avanzado van primero las que usan
 * potencia o raíz.
 */
public class AyudaSoluciones {

    public static final int OBJETIVO_MIN = 1;
    public static final int OBJETIVO_MAX = 10;
    public static final int MAX_SOLUCIONES = 3;

    /** Límite provisional para resultados de potencia y raíz (pendiente de definir). */
    public static final int MAX_RESULTADO = 1000;

    private static final double EPS = 1e-9;
    private static final char[] BASICOS = {'+', '-', '×', '÷'};
    private static final char[] AVANZADOS = {'+', '-', '×', '÷', '^', '√'};

    /** Con potencia o raíz primero, luego menos paréntesis, luego más corta, luego alfabético. */
    private static final Comparator<String> ORDEN = Comparator
            .comparingInt(AyudaSoluciones::prioridadAvanzada)
            .thenComparingInt(AyudaSoluciones::contarParentesis)
            .thenComparingInt(String::length)
            .thenComparing(Comparator.naturalOrder());

    private final Map<Integer, Set<String>> exactas = new HashMap<>();
    private final Map<Integer, Set<String>> conFracciones = new HashMap<>();
    private boolean permitirFracciones;
    private char[] operadores = BASICOS;

    private static class Termino {
        final double valor;
        final String texto;
        Termino(double valor, String texto) {
            this.valor = valor;
            this.texto = texto;
        }
    }

    /**
     * Calcula las soluciones del nivel básico.
     *
     * @param numeros Los cuatro números de la partida.
     */
    public void calcular(int[] numeros) {
        calcular(numeros, false);
    }

    /**
     * Calcula las soluciones de los objetivos 1 al 10.
     *
     * @param numeros  Los cuatro números de la partida.
     * @param avanzado {@code true} para incluir potencia y raíz.
     */
    public void calcular(int[] numeros, boolean avanzado) {
        operadores = avanzado ? AVANZADOS : BASICOS;
        exactas.clear();
        conFracciones.clear();
        for (int o = OBJETIVO_MIN; o <= OBJETIVO_MAX; o++) {
            exactas.put(o, new TreeSet<>(ORDEN));
            conFracciones.put(o, new TreeSet<>(ORDEN));
        }
        List<Termino> inicial = new ArrayList<>();
        for (int n : numeros) {
            inicial.add(new Termino(n, String.valueOf(n)));
        }
        permitirFracciones = false;
        buscar(inicial);
        permitirFracciones = true;
        buscar(inicial);
    }

    /**
     * Devuelve hasta {@value #MAX_SOLUCIONES} soluciones del objetivo, sin repetidas
     * y escritas con × y ÷.
     *
     * @param objetivo Objetivo del 1 al 10.
     * @return Lista de expresiones (vacía si no hay solución).
     */
    public List<String> solucionesDe(int objetivo) {
        List<String> resultado = new ArrayList<>();
        agregarHasta(resultado, exactas.get(objetivo));
        agregarHasta(resultado, conFracciones.get(objetivo));
        return resultado;
    }

    /** @return {@code true} si el objetivo tiene al menos una solución. */
    public boolean tieneSolucion(int objetivo) {
        return !solucionesDe(objetivo).isEmpty();
    }

    private void agregarHasta(List<String> destino, Set<String> origen) {
        if (origen == null) return;
        for (String s : origen) {
            if (destino.size() == MAX_SOLUCIONES) return;
            destino.add(s);
        }
    }

    // ---------- búsqueda ----------

    private void buscar(List<Termino> lista) {
        if (lista.size() == 1) {
            Termino t = lista.get(0);
            if (esEntero(t.valor)) {
                int v = (int) Math.round(t.valor);
                if (v >= OBJETIVO_MIN && v <= OBJETIVO_MAX) {
                    String texto = quitarExternos(t.texto);
                    if (!permitirFracciones) {
                        exactas.get(v).add(texto);
                    } else if (!exactas.get(v).contains(texto)) {
                        conFracciones.get(v).add(texto);
                    }
                }
            }
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            for (int j = 0; j < lista.size(); j++) {
                if (i == j) continue;
                Termino a = lista.get(i);
                Termino b = lista.get(j);
                for (char op : operadores) {
                    // + y × son conmutativos: con un solo orden basta
                    if (esConmutativo(op) && i > j) continue;

                    Double r = operar(op, a.valor, b.valor);
                    if (r == null) continue;

                    String texto;
                    if (esConmutativo(op) && a.texto.compareTo(b.texto) > 0) {
                        texto = "(" + b.texto + " " + op + " " + a.texto + ")";
                    } else {
                        texto = "(" + a.texto + " " + op + " " + b.texto + ")";
                    }

                    List<Termino> nueva = new ArrayList<>();
                    for (int k = 0; k < lista.size(); k++) {
                        if (k != i && k != j) nueva.add(lista.get(k));
                    }
                    nueva.add(new Termino(r, texto));
                    buscar(nueva);
                }
            }
        }
    }

    private static boolean esConmutativo(char op) {
        return op == '+' || op == '×';
    }

    private static boolean esEntero(double x) {
        return Math.abs(x - Math.round(x)) < EPS;
    }

    /** Devuelve null si la operación no se permite en este modo. */
    private Double operar(char op, double a, double b) {
        double r;
        switch (op) {
            case '+': r = a + b; break;
            case '-': r = a - b; break;
            case '×': r = a * b; break;
            case '÷':
                if (Math.abs(b) < EPS) return null;
                r = a / b;
                break;
            case '^': {
                // a = base, b = exponente: solo enteros, exponente no negativo
                if (!esEntero(a) || !esEntero(b)) return null;
                long base = Math.round(a), exp = Math.round(b);
                if (exp < 0 || (base == 0 && exp == 0)) return null;
                long p = potencia(base, exp);
                if (p == Long.MIN_VALUE) return null;
                return (double) p;
            }
            case '√': {
                // a = índice, b = radicando: solo enteros, índice >= 2, radicando >= 0
                if (!esEntero(a) || !esEntero(b)) return null;
                long indice = Math.round(a), radicando = Math.round(b);
                if (indice < 2 || radicando < 0) return null;
                long raiz = raizExacta(radicando, indice);
                if (raiz < 0) return null;
                return (double) raiz;
            }
            default: return null;
        }
        if (!permitirFracciones && !esEntero(r)) return null;
        return r;
    }

    /** base^exp, o Long.MIN_VALUE si se pasa del límite. */
    private static long potencia(long base, long exp) {
        long r = 1;
        for (long i = 0; i < exp; i++) {
            r *= base;
            if (Math.abs(r) > MAX_RESULTADO) return Long.MIN_VALUE;
        }
        return r;
    }

    /** Raíz entera exacta, o -1 si no es exacta. */
    private static long raizExacta(long radicando, long indice) {
        long aprox = Math.round(Math.pow(radicando, 1.0 / indice));
        for (long c = Math.max(0, aprox - 1); c <= aprox + 1; c++) {
            long p = potencia(c, indice);
            if (p != Long.MIN_VALUE && p == radicando) return c;
        }
        return -1;
    }

    // ---------- formato ----------

    private static String quitarExternos(String s) {
        if (s.length() < 2 || s.charAt(0) != '(' || s.charAt(s.length() - 1) != ')') return s;
        int nivel = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') nivel++;
            else if (s.charAt(i) == ')') nivel--;
            if (nivel == 0 && i < s.length() - 1) return s;
        }
        return s.substring(1, s.length() - 1);
    }

    private static int contarParentesis(String s) {
        int n = 0;
        for (char c : s.toCharArray()) if (c == '(') n++;
        return n;
    }

    /** 0 si usa potencia o raíz (van primero), 1 si no. */
    private static int prioridadAvanzada(String s) {
        return (s.indexOf('^') >= 0 || s.indexOf('√') >= 0) ? 0 : 1;
    }
}