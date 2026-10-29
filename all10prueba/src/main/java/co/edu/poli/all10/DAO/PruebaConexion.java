package co.edu.poli.all10.DAO;

import co.edu.poli.all10.modelo.Jugador;

/** Prueba temporal de la conexión y de los DAO. Borrar al terminar. */
public class PruebaConexion {

    public static void main(String[] args) throws Exception {
        JugadorDAO jugadorDAO = new JugadorDAO();
        PartidaDAO partidaDAO = new PartidaDAO();

        // Guarda un jugador de prueba con 5 objetivos en 100 segundos
        Jugador prueba = new Jugador("Prueba");
        prueba.registrarPartida(5, 100);
        jugadorDAO.guardar(prueba);
        partidaDAO.guardar("Prueba", prueba.getPuntaje(), 5, 100);

        // Imprime el Top 10
        System.out.println("TOP 10:");
        for (Jugador j : jugadorDAO.listarTop10()) {
            System.out.println(j.getNombre() + " | puntaje " + j.getPuntaje()
                    + " | objetivos " + j.getObjetivosCompletados()
                    + " | tiempo " + j.getMejorTiempo());
        }
    }
}