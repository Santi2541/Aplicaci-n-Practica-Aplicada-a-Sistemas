package co.edu.poli.all10.modelo;

import java.time.LocalDate;
import java.util.*;

/*
 * Clase encargada de generar los números diarios con la garantía de que los objetivos se pueden solucionar
 * (Se actualiza cada 24h)
 */
public class Numeros {

	private static final int CANTIDAD = 4;

	/*
	 * Genera la lista que comparte con todo el proyecto
	 */
    public static List<Integer> deHoy() {
        return paraFecha(LocalDate.now());
    }
    
    /*
     * Genera los numeros diarios, si recibe un "false" de @resuelveTodos , entonces genera una nueva lista  
     */
    public static List<Integer> paraFecha(LocalDate fecha) {
        Random rnd = new Random(fecha.toEpochDay());
        while (true) {
            List<Integer> nums = new ArrayList<>();
            for (int i = 0; i < CANTIDAD; i++) nums.add(rnd.nextInt(9) + 1);
            if (resuelveTodos(nums)) return nums;
        }
    }
    
    /*
     * Garantiza que los 4 numeros diarios puedan resolver todos los objetivos
     */
    private static boolean resuelveTodos(List<Integer> nums) {
        List<Double> valores = new ArrayList<>();
        for (int n : nums) valores.add((double) n);
        Set<Integer> alcanzables = new HashSet<>();
        explorar(valores, alcanzables);
        for (int objetivo = 1; objetivo <= 10; objetivo++) {
            if (!alcanzables.contains(objetivo)) return false;
        }
        return true;
    }
    
    /**
     * Explora de forma recursiva todos los resultados que se pueden obtener
     * combinando los valores dados con las cuatro operaciones básicas
     *
     * <p>En cada paso toma dos valores de la lista, los combina con cada
     * operación y reemplaza ese par por el resultado. Repite el proceso con
     * la lista más corta hasta que queda un solo valor (caso base).</p>
     *
     * <p>Solo se guardan los resultados enteros, porque los objetivos del
     * juego (1 al 10) son enteros. Las divisiones por cero se descartan.</p>
     *
     * @param v   valores actuales disponibles para combinar; en la primera
     *            llamada son los 4 números del día
     * @param res conjunto donde se acumulan los resultados enteros alcanzables;
     *            se modifica durante la ejecución
     */
    private static void explorar(List<Double> v, Set<Integer> res) {
        if (v.size() == 1) {
            double x = v.get(0);
            if (Math.abs(x - Math.round(x)) < 1e-9) res.add((int) Math.round(x));
            return;
        }
        for (int i = 0; i < v.size(); i++) {
            for (int j = 0; j < v.size(); j++) {
                if (i == j) continue;
                List<Double> resto = new ArrayList<>();
                for (int k = 0; k < v.size(); k++) if (k != i && k != j) resto.add(v.get(k));
                double a = v.get(i), b = v.get(j);
                double[] resultados = { a + b, a - b, a * b, b != 0 ? a / b : Double.NaN };
                for (double r : resultados) {
                    if (Double.isNaN(r)) continue;
                    List<Double> sig = new ArrayList<>(resto);
                    sig.add(r);
                    explorar(sig, res);
                }
            }
        }
    }
    
}
