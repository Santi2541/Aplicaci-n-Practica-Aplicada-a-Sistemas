package co.edu.poli.all10.modelo;

import java.util.Collections;
import java.util.List;

public class ResultadoAyuda {

    public enum Estado { CON_SOLUCIONES, SIN_SOLUCION, YA_COMPLETADO }

    private final Estado estado;
    private final int objetivo;
    private final List<String> soluciones;
    private final boolean pistaNueva;

    private ResultadoAyuda(Estado estado, int objetivo, List<String> soluciones, boolean pistaNueva) {
        this.estado = estado;
        this.objetivo = objetivo;
        this.soluciones = soluciones;
        this.pistaNueva = pistaNueva;
    }

    public static ResultadoAyuda conSoluciones(int objetivo, List<String> soluciones, boolean pistaNueva) {
        return new ResultadoAyuda(Estado.CON_SOLUCIONES, objetivo, soluciones, pistaNueva);
    }

    public static ResultadoAyuda sinSolucion(int objetivo) {
        return new ResultadoAyuda(Estado.SIN_SOLUCION, objetivo, Collections.emptyList(), false);
    }

    public static ResultadoAyuda yaCompletado(int objetivo) {
        return new ResultadoAyuda(Estado.YA_COMPLETADO, objetivo, Collections.emptyList(), false);
    }

    public Estado getEstado() { return estado; }
    public int getObjetivo() { return objetivo; }
    public List<String> getSoluciones() { return soluciones; }
    public boolean isPistaNueva() { return pistaNueva; }
}
