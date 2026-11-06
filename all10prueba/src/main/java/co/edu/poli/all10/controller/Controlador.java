package co.edu.poli.all10.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import co.edu.poli.all10.DAO.JugadorDAO;
import co.edu.poli.all10.DAO.PartidaDAO;
import co.edu.poli.all10.modelo.GestorIdioma;
import co.edu.poli.all10.modelo.JuegoAllTen;
import co.edu.poli.all10.modelo.Jugador;
import co.edu.poli.all10.modelo.Numeros;
import co.edu.poli.all10.modelo.Partida;
import co.edu.poli.all10.modelo.Ranking;
import co.edu.poli.all10.modelo.SesionActual;
import co.edu.poli.all10.vista.App;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.util.Duration;

/**
 * Controlador principal de la interfaz gráfica del juego AllTen.
 * Sigue el patrón MVC para gestionar la interacción del usuario, el temporizador
 * y la validación de las reglas del juego.
 * <p>
 * Se encarga de procesar la entrada de botones, restringir el uso de números repetidos,
 * evaluar la expresión matemática ingresada y actualizar el progreso de los objetivos.
 * Al terminar la partida guarda el resultado a nombre del jugador con sesión iniciada
 * (si no hay sesión, pide el nombre) y permite cerrar la sesión y ver sus resultados.
 */
public class Controlador implements Initializable {

    @FXML private Label lblExpresion;
    @FXML private Label lblTiempo;
    @FXML private Label lblIconoRespuesta;
    @FXML private Label lblRespuesta;

    @FXML private MenuButton btnIdioma;

    @FXML private Button btnNum1;
    @FXML private Button btnNum2;
    @FXML private Button btnNum3;
    @FXML private Button btnNum4;

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

    private Button[] arregloPasos;
    private Timeline actualizadorTiempo;

    /** Objetivos (pasos 1 a 10) logrados en la partida actual. */
    private final boolean[] pasosLogrados = new boolean[10];

    private final JuegoAllTen juego;
    private final Ranking ranking;
    private final JugadorDAO jugadorDAO;
    private final PartidaDAO partidaDAO;

    /** Crea el controlador; JavaFX lo invoca al cargar el FXML. */
    public Controlador() {
        this.juego = new JuegoAllTen();
        this.ranking = new Ranking();
        this.jugadorDAO = new JugadorDAO();
        this.partidaDAO = new PartidaDAO();
    }

    /** @return El juego que controla esta vista. */
    public JuegoAllTen getJuego() { return juego; }

    /** @return El ranking de jugadores. */
    public Ranking getRanking() { return ranking; }

    /**
     * Inicializa el controlador al cargar la vista FXML.
     *
     * @param location  Ubicación utilizada para resolver rutas relativas para el objeto raíz.
     * @param resources Recursos utilizados para localizar el objeto raíz.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        inicializarComponentesSeguros();

        SelectorIdioma.configurar(btnIdioma, this::cambiarIdioma);

        mostrarNumerosDelDia();

        establecerListaIncompleta();

        cargarRanking();

        juego.iniciarPartida();

        if (lblTiempo != null) {
            iniciarCronometro();
        }

        javafx.application.Platform.runLater(() -> {
            if (lblExpresion != null) {
                lblExpresion.requestFocus();
            }
        });
    }

    /**
     * Cambia el idioma: detiene el reloj, abandona la partida en curso
     * y recarga la vista con los textos del nuevo idioma.
     */
    private void cambiarIdioma() {
        detenerActualizadorTiempo();
        juego.abandonarPartida();
        try {
            App.recargar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Protector para JUnit: inicializa instancias simuladas si las anotaciones
     * "FXML" son null por ejecutarse fuera de JavaFX.
     */
    public void inicializarComponentesSeguros() {
        if (btnPaso1 == null) btnPaso1 = new Button("1");
        if (btnPaso2 == null) btnPaso2 = new Button("2");
        if (btnPaso3 == null) btnPaso3 = new Button("3");
        if (btnPaso4 == null) btnPaso4 = new Button("4");
        if (btnPaso5 == null) btnPaso5 = new Button("5");
        if (btnPaso6 == null) btnPaso6 = new Button("6");
        if (btnPaso7 == null) btnPaso7 = new Button("7");
        if (btnPaso8 == null) btnPaso8 = new Button("8");
        if (btnPaso9 == null) btnPaso9 = new Button("9");
        if (btnPaso10 == null) btnPaso10 = new Button("10");

        if (btnNum1 == null) btnNum1 = new Button("9");
        if (btnNum2 == null) btnNum2 = new Button("6");
        if (btnNum3 == null) btnNum3 = new Button("3");
        if (btnNum4 == null) btnNum4 = new Button("2");

        if (lblExpresion == null) lblExpresion = new Label("");

        arregloPasos = new Button[]{
            btnPaso1, btnPaso2, btnPaso3, btnPaso4, btnPaso5,
            btnPaso6, btnPaso7, btnPaso8, btnPaso9, btnPaso10
        };
    }

    /**
     * Inicia la lista vacia para que el usuario logre iniciar una nueva sesión de juego
     */
    private void establecerListaIncompleta() {
        if (arregloPasos != null) {
            for (int i = 0; i < arregloPasos.length; i++) {
                Button btnPaso = arregloPasos[i];
                if (btnPaso != null) {
                    btnPaso.getStyleClass().removeAll("paso-completado", "paso-actual", "paso-pendiente");
                    btnPaso.setStyle(null);

                    int numeroPaso = i + 1;
                    btnPaso.setText(String.valueOf(numeroPaso));

                    // Todas las casillas usan 'paso-actual' (borde morado)
                    btnPaso.getStyleClass().add("paso-actual");
                }
            }
        }
    }

    /**
     * Inicia el cronómetro solo si estamos en entorno gráfico.
     */
    private void iniciarCronometro() {
        actualizarLabelTiempo();

        actualizadorTiempo = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> actualizarLabelTiempo()));
        actualizadorTiempo.setCycleCount(Timeline.INDEFINITE);
        actualizadorTiempo.play();
    }

    /**
     * Muestra en {@code lblTiempo} los segundos que informa el modelo, con formato mm:ss.
     */
    private void actualizarLabelTiempo() {
        int total = juego.getSegundos();
        if (lblTiempo != null) {
            lblTiempo.setText(String.format("⏰ %02d:%02d", total / 60, total % 60));
        }
    }

    /**
     * Detiene el refresco del reloj en pantalla, si estaba corriendo.
     */
    private void detenerActualizadorTiempo() {
        if (actualizadorTiempo != null) actualizadorTiempo.stop();
    }

    /**
     * Captura el clic en los botones de números y operadores.
     * Le informa al modelo y deshabilita el botón si es uno de los 4 números principales.
     *
     * @param event El evento de acción disparado por el botón presionado.
     */
    @FXML
    private void presionarBoton(ActionEvent event) {
        Button b = (Button) event.getSource();
        juego.agregarSimbolo(b.getText());

        Button[] nums = { btnNum1, btnNum2, btnNum3, btnNum4 };
        for (int i = 0; i < nums.length; i++) {
            if (b == nums[i]) {
                juego.usarNumero(i);
                b.setDisable(true);
            }
        }
        lblExpresion.setText(juego.getExpresion());
    }

    /**
     * Restablece el estado de la entrada del usuario.
     * Borra la expresión actual en pantalla y vuelve a habilitar los 4 botones numéricos
     * para permitir un nuevo intento.
     *
     * @param event El evento de acción disparado por el botón Limpiar.
     */
    @FXML
    public void limpiarTodo(ActionEvent event) {
        juego.limpiar();
        lblExpresion.setText("");
        for (Button b : new Button[]{ btnNum1, btnNum2, btnNum3, btnNum4 }) {
            b.setDisable(false);
        }
    }

    /**
     * Elimina el último carácter ingresado en la pantalla de expresión.
     * Útil para corregir errores tipográficos sin necesidad de limpiar toda la ecuación.
     *
     * @param event El evento de acción disparado por el botón Borrar.
     */
    @FXML
    public void borrarUltimo(ActionEvent event) {
        juego.borrarUltimo();
        lblExpresion.setText(juego.getExpresion());
        actualizarBotonesNumericos();
    }

    /**
     * Sincroniza los 4 botones numéricos con el modelo: deshabilita los usados
     * y habilita los disponibles.
     */
    private void actualizarBotonesNumericos() {
        Button[] nums = botonesNumericos();
        for (int i = 0; i < nums.length; i++) {
            nums[i].setDisable(juego.estaUsado(i));
        }
    }

    /** Muestra en la tarjeta de respuesta un icono, un texto y el estilo (ok o error). */
    private void mostrarRespuesta(String icono, String texto, String estilo) {
        if (lblIconoRespuesta == null || lblRespuesta == null) return;
        lblIconoRespuesta.getStyleClass().removeAll("icono-respuesta-ok", "icono-respuesta-error");
        if (estilo != null) lblIconoRespuesta.getStyleClass().add(estilo);
        lblIconoRespuesta.setText(icono);
        lblRespuesta.setText(texto);
    }

    /**
     * Evalúa la operación matemática ingresada en pantalla.
     * Valida de forma estricta que los 4 números base hayan sido utilizados. Si la operación
     * es válida y da como resultado un número entero entre 1 y 10, marca el objetivo
     * correspondiente como completado en la interfaz. Cuando se completa la partida,
     * detiene el reloj y guarda el resultado a nombre del jugador con sesión.
     *
     * @param event El evento de acción disparado por el botón Verificar.
     */
    @FXML
    public void calcularResultado(ActionEvent event) {
        int paso = juego.calcular();
        lblExpresion.setText(juego.getExpresion());

        if (paso > 0) {
            mostrarRespuesta("✔", GestorIdioma.get("juego.correcto", paso), "icono-respuesta-ok");
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

        if (paso > 0) {
            pasosLogrados[paso - 1] = true;
            marcarPasoComoCompletado(paso);
            if (juego.isPartidaTerminada()) {
                detenerActualizadorTiempo();
                actualizarLabelTiempo();
                guardarAlTerminar(contarObjetivosCompletados(), juego.getSegundos());
            }
        }
    }

    /**
     * Actualiza el estilo visual de un botón de la lista de objetivos
     * para indicar que fue alcanzado.
     *
     * @param numeroPaso Número (1-10) del objetivo completado.
     */
    public void marcarPasoComoCompletado(int numeroPaso) {
        int indice = numeroPaso - 1;

        if (arregloPasos == null) {
            System.out.println("❌ ERROR CRÍTICO: 'arregloPasos' es NULL. Revisa el método initialize().");
            return;
        }

        if (indice >= 0 && indice < arregloPasos.length) {
            Button btnObjetivo = arregloPasos[indice];

            if (btnObjetivo == null) {
                System.out.println("❌ ERROR: El botón en la posición " + indice + " (Paso " + numeroPaso + ") es NULL. Revisa los fx:id en Scene Builder.");
                return;
            }

            btnObjetivo.getStyleClass().removeAll("paso-actual", "paso-pendiente", "paso-completado");
            btnObjetivo.setText("✔");
            btnObjetivo.getStyleClass().add("paso-completado");
            btnObjetivo.setStyle("-fx-background-color: #E8F5E9 !important; -fx-text-fill: #2E7D32 !important; -fx-border-color: #A5D6A7 !important; -fx-border-radius: 8px; -fx-font-weight: bold;");

            System.out.println("✅ ¡Paso " + numeroPaso + " marcado con éxito!");
        }
    }

    /** Devuelve los 4 botones numéricos en orden (índices 0 a 3 del modelo). */
    private Button[] botonesNumericos() {
        return new Button[]{ btnNum1, btnNum2, btnNum3, btnNum4 };
    }

    /**
     * Asigna a los 4 botones numéricos los números generados para el día actual.
     * Los valores definidos en el FXML solo son un valor por defecto.
     */
    private void mostrarNumerosDelDia() {
        List<Integer> numeros = Numeros.aleatorios();
        System.out.println("Números de hoy: " + numeros);

        Button[] botones = botonesNumericos();

        for (int i = 0; i < botones.length; i++) {
            botones[i].setText(String.valueOf(numeros.get(i)));
        }
    }

    /**
     * Abandona la partida actual: detiene el reloj y reinicia la entrada.
     * No se registra tiempo ni se guarda puntaje.
     */
    @FXML
    public void abandonarTablero() {
        detenerActualizadorTiempo();
        juego.abandonarPartida();
        juego.limpiar();
        java.util.Arrays.fill(pasosLogrados, false);
        lblExpresion.setText("");
        actualizarBotonesNumericos();
        actualizarLabelTiempo();     // vuelve a 00:00
        System.out.println("Partida abandonada: no se registra tiempo.");
    }

    /** Carga todos los jugadores guardados en la BD al ranking en memoria. */
    private void cargarRanking() {
        try {
            for (Jugador j : jugadorDAO.listarTodos()) {
                ranking.agregar(j);
            }
        } catch (SQLException e) {
            System.err.println("No se pudo cargar el ranking: " + e.getMessage());
        }
    }

    /**
     * Registra el resultado de la partida y guarda jugador y partida en la BD.
     *
     * @param nombre    Nombre del jugador.
     * @param objetivos Objetivos completados (0 a 10).
     * @param segundos  Tiempo de la partida en segundos.
     */
    private void guardarResultado(String nombre, int objetivos, int segundos) {
        Jugador jugador = ranking.registrarPartida(nombre, objetivos, segundos);
        try {
            jugadorDAO.guardar(jugador);
            partidaDAO.guardar(jugador.getNombre(),
                    objetivos * Jugador.PUNTOS_POR_OBJETIVO, objetivos, segundos);
        } catch (SQLException e) {
            System.err.println("No se pudo guardar el resultado: " + e.getMessage());
        }
    }

    /**
     * Pide el nombre al jugador con una ventana y guarda su resultado.
     * Solo se usa cuando no hay una sesión iniciada.
     *
     * @param objetivos Objetivos completados (0 a 10).
     * @param segundos  Tiempo de la partida en segundos.
     */
    private void pedirNombreYGuardar(int objetivos, int segundos) {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle(GestorIdioma.get("dialogo.titulo"));
        dialogo.setHeaderText(GestorIdioma.get("dialogo.cabecera",
                objetivos * Jugador.PUNTOS_POR_OBJETIVO, objetivos, segundos));
        dialogo.setContentText(GestorIdioma.get("dialogo.nombre"));
        Optional<String> nombre = dialogo.showAndWait();
        nombre.filter(n -> !n.trim().isEmpty())
              .ifPresent(n -> guardarResultado(n, objetivos, segundos));
    }

    /**
     * Guarda el resultado a nombre del jugador con sesión iniciada.
     * Si por alguna razón no hay sesión, pide el nombre como antes.
     *
     * @param objetivos Objetivos completados (0 a 10).
     * @param segundos  Tiempo de la partida en segundos.
     */
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
        alerta.showAndWait();
    }

    /** @return Cantidad de objetivos (1 a 10) logrados en la partida actual. */
    private int contarObjetivosCompletados() {
        int total = 0;
        for (boolean logrado : pasosLogrados) {
            if (logrado) total++;
        }
        return total;
    }

    /** Cierra la sesión: detiene el reloj, descarta la partida y vuelve al inicio de sesión. */
    @FXML
    private void cerrarSesion(ActionEvent event) {
        detenerActualizadorTiempo();
        juego.abandonarPartida();
        SesionActual.cerrar();
        try {
            App.setRoot("registro");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Muestra mejor puntaje, mejor tiempo, posición e historial del jugador con sesión. */
    @FXML
    private void verMisResultados(ActionEvent event) {
        Jugador actual = SesionActual.getJugador();
        if (actual == null) {
            return;
        }
        try {
            Jugador datos = jugadorDAO.obtenerJugador(actual.getNombre());
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
            alerta.showAndWait();
        } catch (SQLException e) {
            System.err.println("No se pudieron consultar los resultados: " + e.getMessage());
        }
    }

    @FXML
    private void abrirDificultad(ActionEvent event) throws IOException {
        detenerActualizadorTiempo();
        App.setRoot("escenario2");
    }
}