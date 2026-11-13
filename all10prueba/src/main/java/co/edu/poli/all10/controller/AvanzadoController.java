package co.edu.poli.all10.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import co.edu.poli.all10.DAO.JugadorDAO;
import co.edu.poli.all10.DAO.PartidaDAO;
import co.edu.poli.all10.modelo.Cronometro;
import co.edu.poli.all10.modelo.GestorIdioma;
import co.edu.poli.all10.modelo.JuegoAllTen;
import co.edu.poli.all10.modelo.Jugador;
import co.edu.poli.all10.modelo.Numeros;
import co.edu.poli.all10.modelo.Partida;
import co.edu.poli.all10.modelo.ResultadoAyuda;
import co.edu.poli.all10.modelo.SesionActual;
import co.edu.poli.all10.vista.App;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controlador de la pantalla de dificultad "Raíz y potencia".
 * <p>
 * Reutiliza {@link JuegoAllTen} para las reglas (usar los 4 números, objetivos
 * del 1 al 10 y cronómetro); la vista solo refleja lo que informa el modelo.
 * También muestra la ayuda con posibles soluciones por objetivo (incluyendo
 * potencia y raíz) y lleva el contador de pistas.
 */
public class AvanzadoController {

    @FXML private Label lblTiempo;
    @FXML private Label lblExpresion;
    @FXML private Label lblIconoRespuesta;
    @FXML private Label lblRespuesta;

    @FXML private MenuButton btnIdioma;

    @FXML private Button btnNum1;
    @FXML private Button btnNum2;
    @FXML private Button btnNum3;
    @FXML private Button btnNum4;
    @FXML private Button btnPotencia;

    @FXML private Button btnPaso1;
    @FXML private Button btnPaso2;
    @FXML private Button btnPaso3;
    @FXML private Button btnPaso4;
    @FXML private Button btnPaso5;
    @FXML private Button btnPaso6;
    @FXML private Button btnPaso7;
    @FXML private Button btnPaso8;
    @FXML private Button btnPaso9;
    @FXML private Button btnPaso10;

    @FXML private Button btnExportar;

    // Ayuda: posibles soluciones por objetivo
    @FXML private Label lblPistas;
    @FXML private VBox panelAyuda;
    @FXML private FlowPane filaObjetivosAyuda;
    @FXML private Label lblSolucionesAyuda;

    private final JuegoAllTen juego = new JuegoAllTen();
    private Button[] arregloPasos;
    private Timeline actualizadorTiempo;
    private final Cronometro cronometro = new Cronometro();

    private static final int OBJETIVOS = 10;

    private final JugadorDAO jugadorDAO = new JugadorDAO();
    private final PartidaDAO partidaDAO = new PartidaDAO();
    private String ultimoNombre;
    private boolean partidaGuardada = false;

    /** JavaFX lo llama solo al cargar el FXML: arranca el reloj. */
    @FXML
    public void initialize() {
        arregloPasos = new Button[]{
                btnPaso1, btnPaso2, btnPaso3, btnPaso4, btnPaso5,
                btnPaso6, btnPaso7, btnPaso8, btnPaso9, btnPaso10
            };

        SelectorIdioma.configurar(btnIdioma, this::cambiarIdioma);

        mostrarNumerosDelDia();

        juego.iniciarPartida();

        cronometro.iniciar();
        actualizarLabelTiempo();

        actualizadorTiempo = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> actualizarLabelTiempo()));
        actualizadorTiempo.setCycleCount(Timeline.INDEFINITE);
        actualizadorTiempo.play();
    }

    /**
     * Cambia el idioma: detiene el reloj, abandona la partida en curso
     * y recarga la vista con los textos del nuevo idioma.
     */
    private void cambiarIdioma() {
        actualizadorTiempo.stop();
        cronometro.descartar();
        juego.abandonarPartida();
        try {
            App.recargar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Muestra en {@code lblTiempo} los segundos del cronómetro con formato mm:ss. */
    private void actualizarLabelTiempo() {
        int total = cronometro.obtenerSegundos();
        lblTiempo.setText(String.format("🕒 %02d:%02d", total / 60, total % 60));
    }

    /**
     * Vuelve al escenario principal. La partida en curso se abandona: no se registra tiempo.
     *
     * @param event El evento de acción disparado por el botón Volver.
     * @throws IOException si no se puede cargar escenario1.fxml.
     */
    @FXML
    private void volver(ActionEvent event) throws IOException {
        actualizadorTiempo.stop();
        cronometro.descartar();
        App.setRoot("escenario1");
    }

    /**
     * Asigna a los 4 botones numéricos los números generados para la partida
     * y le entrega esos números a la ayuda (nivel avanzado: con potencia y raíz).
     */
    private void mostrarNumerosDelDia() {
        List<Integer> numeros = Numeros.aleatorios();
        Button[] botones = { btnNum1, btnNum2, btnNum3, btnNum4 };
        int[] arreglo = new int[botones.length];
        for (int i = 0; i < botones.length; i++) {
            botones[i].setText(String.valueOf(numeros.get(i)));
            arreglo[i] = numeros.get(i);
        }
        juego.prepararAyuda(arreglo, true);
        actualizarContadorPistas();
    }

    /**
     * Captura el clic en números y operadores. El botón {@code xʸ} agrega {@code ^}.
     * Si es uno de los 4 números, lo marca como usado y lo deshabilita.
     *
     * @param event El evento de acción disparado por el botón presionado.
     */
    @FXML
    private void presionarBoton(ActionEvent event) {
        Button b = (Button) event.getSource();
        String simbolo = (b == btnPotencia) ? "^" : b.getText();
        juego.agregarSimbolo(simbolo);

        Button[] nums = botonesNumericos();
        for (int i = 0; i < nums.length; i++) {
            if (b == nums[i]) {
                juego.usarNumero(i);
                b.setDisable(true);
            }
        }
        lblExpresion.setText(juego.getExpresion());
        reiniciarRespuesta();
    }

    /**
     * Elimina el último símbolo de la expresión y libera el número si era uno de los 4.
     *
     * @param event El evento de acción disparado por el botón Borrar.
     */
    @FXML
    private void borrarUltimo(ActionEvent event) {
        juego.borrarUltimo();
        lblExpresion.setText(juego.getExpresion());
        actualizarBotonesNumericos();
        reiniciarRespuesta();
    }

    /**
     * Evalúa la operación. Si da un entero entre 1 y 10, marca el objetivo como
     * completado; al completar los 10, detiene el reloj.
     *
     * @param event El evento de acción disparado por el botón Verificar.
     */
    @FXML
    private void calcularResultado(ActionEvent event) {
        int paso = juego.calcular();
        lblExpresion.setText(juego.getExpresion());

        if (paso > 0) {
            marcarPasoComoCompletado(paso);
            if (panelAyuda != null && panelAyuda.isVisible()) construirSelectorObjetivos();
            if (juego.isPartidaTerminada()) {
                actualizadorTiempo.stop();
                actualizarLabelTiempo();
                mostrarRespuesta("🏆", GestorIdioma.get("juego.completadoEn", juego.getTiempoFinal()),
                        "icono-respuesta-ok");
                btnExportar.setVisible(true);
                btnExportar.setManaged(true);
                if (!partidaGuardada) {
                    partidaGuardada = true;
                    guardarAlTerminar(OBJETIVOS, juego.getTiempoFinal());
                }
            } else {
                mostrarRespuesta("✔", GestorIdioma.get("juego.correcto", paso), "icono-respuesta-ok");
            }
        } else if ("Error".equals(juego.getExpresion())) {
            mostrarRespuesta("✖", GestorIdioma.get("juego.invalida"), "icono-respuesta-error");
        } else if (!juego.todosLosNumerosUsados()) {
            if (juego.cantidadNumerosUsados() < 2) {
                mostrarRespuesta("!", GestorIdioma.get("juego.minimo"), "icono-respuesta-error");
            } else {
                mostrarRespuesta("=", GestorIdioma.get("juego.resultado", juego.getExpresion()), null);
            }
        } else {
            mostrarRespuesta("✖", GestorIdioma.get("juego.incorrecto"), "icono-respuesta-error");
        }
    }

    /** Marca un objetivo (1 a 10) como completado cambiando su estilo y su texto. */
    private void marcarPasoComoCompletado(int numeroPaso) {
        Button btn = arregloPasos[numeroPaso - 1];
        btn.getStyleClass().remove("paso-actual");
        if (!btn.getStyleClass().contains("paso-completado")) {
            btn.getStyleClass().add("paso-completado");
        }
        btn.setText("✔");
    }

    /** Muestra en la tarjeta de respuesta un icono, un texto y el estilo (ok o error). */
    private void mostrarRespuesta(String icono, String texto, String estilo) {
        lblIconoRespuesta.getStyleClass().removeAll("icono-respuesta-ok", "icono-respuesta-error");
        if (estilo != null) lblIconoRespuesta.getStyleClass().add(estilo);
        lblIconoRespuesta.setText(icono);
        lblRespuesta.setText(texto);
    }

    /** Devuelve la tarjeta de respuesta a su estado inicial. */
    private void reiniciarRespuesta() {
        mostrarRespuesta("?", GestorIdioma.get("juego.respuesta"), null);
    }

    /** Sincroniza los 4 botones numéricos con el modelo: deshabilita los usados. */
    private void actualizarBotonesNumericos() {
        Button[] nums = botonesNumericos();
        for (int i = 0; i < nums.length; i++) {
            nums[i].setDisable(juego.estaUsado(i));
        }
    }

    /** Devuelve los 4 botones numéricos en orden (índices 0 a 3 del modelo). */
    private Button[] botonesNumericos() {
        return new Button[]{ btnNum1, btnNum2, btnNum3, btnNum4 };
    }

    // ---------------------------------------------------------------
    // AYUDA: posibles soluciones por objetivo
    // ---------------------------------------------------------------

    /** Abre o cierra el panel de ayuda con el selector de objetivos. */
    @FXML
    private void onAbrirAyuda() {
        if (panelAyuda == null) return;
        boolean mostrar = !panelAyuda.isVisible();
        panelAyuda.setVisible(mostrar);
        panelAyuda.setManaged(mostrar);
        if (mostrar) {
            construirSelectorObjetivos();
            if (lblSolucionesAyuda != null) lblSolucionesAyuda.setText("");
        }
    }

    /** Dibuja los botones 1 al 10; los objetivos ya completados llevan ✓. */
    private void construirSelectorObjetivos() {
        if (filaObjetivosAyuda == null) return;
        filaObjetivosAyuda.getChildren().clear();
        for (int i = 1; i <= 10; i++) {
            final int objetivo = i;
            boolean hecho = juego.isPasoCompletado(i);
            Button b = new Button(hecho ? i + " ✓" : String.valueOf(i));
            b.getStyleClass().add("btn-ayuda-objetivo");
            if (hecho) b.getStyleClass().add("ayuda-completado");
            b.setOnAction(e -> mostrarAyuda(objetivo));
            filaObjetivosAyuda.getChildren().add(b);
        }
    }

    /** Pide la ayuda al modelo, muestra el resultado y actualiza el contador de pistas. */
    private void mostrarAyuda(int objetivo) {
        ResultadoAyuda r = juego.pedirAyuda(objetivo);
        String texto;
        switch (r.getEstado()) {
            case CON_SOLUCIONES:
                texto = GestorIdioma.get("ayuda.objetivo", objetivo) + "\n"
                        + String.join("\n", r.getSoluciones());
                break;
            case SIN_SOLUCION:
                texto = GestorIdioma.get("ayuda.sinSolucion");
                break;
            default:
                texto = GestorIdioma.get("ayuda.yaResuelto");
                break;
        }
        if (lblSolucionesAyuda != null) lblSolucionesAyuda.setText(texto);
        actualizarContadorPistas();
    }

    /** Muestra en pantalla cuántas pistas se han usado. */
    private void actualizarContadorPistas() {
        if (lblPistas != null) {
            lblPistas.setText(String.valueOf(juego.getPistasUsadas()));
        }
    }

    /** Exporta una imagen con los resultados de la partida terminada. */
    @FXML
    private void exportarImagen(ActionEvent event) {
        Jugador actual = SesionActual.getJugador();
        String nombre = (actual != null) ? actual.getNombre() : "AllTen";
        try {
            boolean guardada = ExportadorResultados.exportar(
                    lblExpresion.getScene().getWindow(), nombre,
                    10 * Jugador.PUNTOS_POR_OBJETIVO, 10, juego.getTiempoFinal());
            if (guardada) {
                Alert ok = new Alert(Alert.AlertType.INFORMATION, GestorIdioma.get("export.exito"));
                App.estilizar(ok);
                ok.showAndWait();
            }
        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, GestorIdioma.get("export.error"));
            App.estilizar(error);
            error.showAndWait();
        }
    }

    /** Guarda el resultado a nombre del jugador con sesión; si no hay sesión, pide el nombre. */
    private void guardarAlTerminar(int objetivos, int segundos) {
        Jugador actual = SesionActual.getJugador();
        if (actual == null) {
            pedirNombreYGuardar(objetivos, segundos);
            return;
        }
        guardarResultado(actual.getNombre(), objetivos, segundos);

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(GestorIdioma.get("dialogo.titulo"));
        alerta.setHeaderText(GestorIdioma.get("dialogo.cabecera",
                objetivos * Jugador.PUNTOS_POR_OBJETIVO, objetivos, segundos));
        App.estilizar(alerta);
        alerta.showAndWait();
    }

    /** Pide el nombre con una ventana y guarda el resultado. Solo si no hay sesión. */
    private void pedirNombreYGuardar(int objetivos, int segundos) {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle(GestorIdioma.get("dialogo.titulo"));
        dialogo.setHeaderText(GestorIdioma.get("dialogo.cabecera",
                objetivos * Jugador.PUNTOS_POR_OBJETIVO, objetivos, segundos));
        dialogo.setContentText(GestorIdioma.get("dialogo.nombre"));
        Optional<String> nombre = dialogo.showAndWait();
        nombre.filter(n -> !n.trim().isEmpty())
              .ifPresent(n -> guardarResultado(n.trim(), objetivos, segundos));
    }

    /** Registra la partida conservando el mejor resultado del jugador y la guarda en la BD. */
    private void guardarResultado(String nombre, int objetivos, int segundos) {
        ultimoNombre = nombre;
        try {
            Jugador jugador = jugadorDAO.obtenerJugador(nombre);
            if (jugador == null) jugador = new Jugador(nombre);
            jugador.registrarPartida(objetivos, segundos);
            jugadorDAO.guardar(jugador);
            partidaDAO.guardar(jugador.getNombre(),
                    objetivos * Jugador.PUNTOS_POR_OBJETIVO, objetivos, segundos);
        } catch (SQLException e) {
            System.err.println("No se pudo guardar el resultado: " + e.getMessage());
        }
    }

    /** Borra toda la expresión y libera los 4 números para un nuevo intento. */
    @FXML
    private void limpiarTodo(ActionEvent event) {
        juego.limpiar();
        lblExpresion.setText("");
        actualizarBotonesNumericos();
        reiniciarRespuesta();
    }

    /** Abandona la partida: no se registra tiempo ni puntaje y se cierra la aplicación. */
    @FXML
    private void abandonarTablero(ActionEvent event) {
        actualizadorTiempo.stop();
        cronometro.descartar();
        juego.abandonarPartida();
        Platform.exit();
    }

    /** Cierra la sesión: detiene el reloj, descarta la partida y vuelve al inicio de sesión. */
    @FXML
    private void cerrarSesion(ActionEvent event) {
        actualizadorTiempo.stop();
        cronometro.descartar();
        juego.abandonarPartida();
        SesionActual.cerrar();
        try {
            App.setRoot("registro");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Abre la ventana con el ranking por velocidad y el historial de partidas. */
    @FXML
    private void verRanking(ActionEvent event) {
        try {
            App.abrirVentana("ranking", GestorIdioma.get("ranking.titulo"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Muestra mejor puntaje, mejor tiempo, posición e historial del jugador con sesión. */
    @FXML
    private void verMisResultados(ActionEvent event) {
        Jugador actual = SesionActual.getJugador();
        if (actual == null) return;
        try {
            Jugador datos = jugadorDAO.obtenerJugador(actual.getNombre());
            if (datos == null) return;
            int posicion = jugadorDAO.obtenerPosicion(actual.getNombre());
            List<Partida> partidas = jugadorDAO.listarPartidas(actual.getNombre());

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            StringBuilder sb = new StringBuilder();
            if (partidas.isEmpty()) {
                sb.append(GestorIdioma.get("sesion.historial.vacio"));
            }
            for (Partida p : partidas) {
                sb.append(GestorIdioma.get("sesion.historial.linea",
                        p.getPuntaje(), p.getObjetivosCompletados(), p.getTiempo(),
                        p.getFecha().format(formato))).append("\n");
            }

            TextArea area = new TextArea(sb.toString());
            area.setEditable(false);
            area.setPrefRowCount(10);

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle(GestorIdioma.get("sesion.resultados.titulo"));
            alerta.setHeaderText(GestorIdioma.get("sesion.resultados.cabecera",
                    datos.getPuntaje(), datos.getMejorTiempo(), posicion));
            alerta.getDialogPane().setContent(area);
            App.estilizar(alerta);
            alerta.showAndWait();
        } catch (SQLException e) {
            System.err.println("No se pudieron consultar los resultados: " + e.getMessage());
        }
    }
}