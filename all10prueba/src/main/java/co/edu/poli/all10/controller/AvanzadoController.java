package co.edu.poli.all10.controller;

import java.io.IOException;

import co.edu.poli.all10.modelo.Cronometro;
import co.edu.poli.all10.modelo.JuegoAllTen;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.List;

import co.edu.poli.all10.modelo.Numeros;
import javafx.scene.control.Button;

/**
 * Controlador de la pantalla de dificultad "Raíz y potencia".
 * <p>
 * Reutiliza {@link JuegoAllTen} para las reglas (usar los 4 números, objetivos
 * del 1 al 10 y cronómetro); la vista solo refleja lo que informa el modelo.
 */
public class AvanzadoController {

    @FXML private Label lblTiempo;
    @FXML private Label lblExpresion;
    @FXML private Label lblIconoRespuesta;
    @FXML private Label lblRespuesta;
    
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

    
    private final JuegoAllTen juego = new JuegoAllTen();
    private Button[] arregloPasos;
    private Timeline actualizadorTiempo;
    private final Cronometro cronometro = new Cronometro();

    /** JavaFX lo llama solo al cargar el FXML: arranca el reloj. */
    @FXML
    public void initialize() {
    	arregloPasos = new Button[]{
                btnPaso1, btnPaso2, btnPaso3, btnPaso4, btnPaso5,
                btnPaso6, btnPaso7, btnPaso8, btnPaso9, btnPaso10
            };
    	
    	mostrarNumerosDelDia();
    	

        juego.iniciarPartida();
        
        cronometro.iniciar();
        actualizarLabelTiempo();

        actualizadorTiempo = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> actualizarLabelTiempo()));
        actualizadorTiempo.setCycleCount(Timeline.INDEFINITE);
        actualizadorTiempo.play();
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
        Parent root = FXMLLoader.load(
            getClass().getResource("/co/edu/poli/all10prueba/escenario1.fxml"));
        ((Node) event.getSource()).getScene().setRoot(root);
    }
    
    /**
     * Asigna a los 4 botones numéricos los números generados para el día actual.
     * Son los mismos que ve el jugador en el escenario principal.
     */
    private void mostrarNumerosDelDia() {
        List<Integer> numeros = Numeros.deHoy();
        Button[] botones = { btnNum1, btnNum2, btnNum3, btnNum4 };
        for (int i = 0; i < botones.length; i++) {
            botones[i].setText(String.valueOf(numeros.get(i)));
        }
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
            if (juego.isPartidaTerminada()) {
                actualizadorTiempo.stop();
                actualizarLabelTiempo();
                mostrarRespuesta("🏆", "¡Completado en " + juego.getTiempoFinal() + " s!",
                        "icono-respuesta-ok");
            } else {
                mostrarRespuesta("✔", "¡Correcto! Objetivo " + paso, "icono-respuesta-ok");
            }
        } else if (!juego.todosLosNumerosUsados()) {
            mostrarRespuesta("!", "Usa los 4 números", "icono-respuesta-error");
        } else if ("Error".equals(juego.getExpresion())) {
            mostrarRespuesta("✖", "Operación inválida", "icono-respuesta-error");
        } else {
            mostrarRespuesta("✖", "Incorrecto", "icono-respuesta-error");
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
        mostrarRespuesta("?", "Tu respuesta", null);
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
}