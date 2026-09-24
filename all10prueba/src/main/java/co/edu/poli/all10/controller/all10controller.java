package co.edu.poli.all10.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.animation.KeyFrame;
import javafx.fxml.Initializable;
import javafx.util.Duration;

/**
 * Controlador principal de la interfaz gráfica del juego AllTen.
 * Sigue el patrón MVC para gestionar la interacción del usuario, el temporizador 
 * y la validación de las reglas del juego.
 * 
 * Se encarga de procesar la entrada de botones, restringir el uso de números repetidos,
 * evaluar la expresión matemática ingresada y actualizar el progreso de los objetivos.
 */
public class all10controller implements Initializable {
    
    @FXML private Label lblExpresion;
    @FXML private Label lblTiempo;
    
    // Botones de números principales
    @FXML private Button btnNum1;
    @FXML private Button btnNum2;
    @FXML private Button btnNum3;
    @FXML private Button btnNum4;
    
    // Lista de Objetivos (1 al 10)
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
    private Timeline cronometro;
    private int segundosTranscurridos = 0;
    
    /**
     * Inicializa el controlador al cargar la vista FXML.
     *
     * @param location  Ubicación utilizada para resolver rutas relativas para el objeto raíz.
     * @param resources Recursos utilizados para localizar el objeto raíz.
     */
    public void initialize(URL location, ResourceBundle resources) {
    	
        inicializarComponentesSeguros();

        establecerListaIncompleta();

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
     * Protector para JUnit: Inicializa instancias simuladas si las anotaciones 
     * @FXML son null por ejecutarse fuera de JavaFX.
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
    
    /*
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

                    // 3. AHORA TODAS LAS CASILLAS USAN 'paso-actual' (Borde Morado)
                    btnPaso.getStyleClass().add("paso-actual");
                }
            }
        }
    }
    
    /*
     * Inicia el cronómetro solo si estamos en entorno gráfico
     */
    private void iniciarCronometro() {
        segundosTranscurridos = 0;
        
        cronometro = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            segundosTranscurridos++;
            int minutos = segundosTranscurridos / 60;
            int segundos = segundosTranscurridos % 60;
            
            if (lblTiempo != null) {
                lblTiempo.setText(String.format("⏰ %02d:%02d", minutos, segundos));
            }
        }));
        
        cronometro.setCycleCount(Timeline.INDEFINITE);
        cronometro.play();
    }
     
    /**
     * Captura el evento de clic en los botones de la interfaz (números y operadores).
     * Concatena el valor del botón a la expresión en pantalla y deshabilita el botón 
     * si corresponde a uno de los 4 números principales para evitar que se repita.
     *
     * @param event El evento de acción disparado por el botón presionado.
     */
    @FXML
    private void presionarBoton(ActionEvent event) {
        Button botonPresionado = (Button) event.getSource();
        String textoBoton = botonPresionado.getText();
        
        lblExpresion.setText(lblExpresion.getText() + " " + textoBoton);
        
        if (botonPresionado == btnNum1 || botonPresionado == btnNum2 || 
            botonPresionado == btnNum3 || botonPresionado == btnNum4) {
            botonPresionado.setDisable(true);
        }
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
        lblExpresion.setText("");

        btnNum1.setDisable(false);
        btnNum2.setDisable(false);
        btnNum3.setDisable(false);
        btnNum4.setDisable(false);
    }
    
    /**
     * Elimina el último carácter ingresado en la pantalla de expresión.
     * Útil para corregir errores tipográficos sin necesidad de limpiar toda la ecuación.
     *
     * @param event El evento de acción disparado por el botón Borrar.
     */
    @FXML
	public void borrarUltimo(ActionEvent event) {
        String textoActual = lblExpresion.getText().trim();
        if (!textoActual.isEmpty()) {
            lblExpresion.setText(textoActual.substring(0, textoActual.length() - 1).trim());
        }
    }

    
    /**
     * Evalúa la operación matemática ingresada en pantalla.
     * Valida de forma estricta que los 4 números base hayan sido utilizados. Si la operación
     * es válida y da como resultado un número entero entre 1 y 10, marca el objetivo 
     * correspondiente como completado en la interfaz.
     *
     * @param event El evento de acción disparado por el botón Verificar.
     */
    @FXML
	public void calcularResultado(ActionEvent event) {
        boolean usoLosCuatroNumeros = btnNum1.isDisabled() && 
                                     btnNum2.isDisabled() && 
                                     btnNum3.isDisabled() && 
                                     btnNum4.isDisabled();

        if (!usoLosCuatroNumeros) {
            System.out.println("❌ Faltan números por presionar. N1: " + btnNum1.isDisabled() + 
                               " N2: " + btnNum2.isDisabled() + 
                               " N3: " + btnNum3.isDisabled() + 
                               " N4: " + btnNum4.isDisabled());
            return;
        }

        String expresion = lblExpresion.getText().trim();
        System.out.println("🔍 Texto leído en pantalla: " + expresion);

        if (expresion.isEmpty()) {
            return;
        }

        try {
            if (expresion.contains(":")) {
                expresion = expresion.split(":")[1].trim();
            }

            String expresionLimpia = expresion.replace("×", "*").replace("÷", "/");
            double resultadoNum = evaluarCadenaMatematica(expresionLimpia);
            System.out.println("🧮 Resultado evaluado: " + resultadoNum);

            if (resultadoNum == (long) resultadoNum) {
                int resultadoEntero = (int) resultadoNum;
                lblExpresion.setText(String.valueOf(resultadoEntero));

                if (resultadoEntero >= 1 && resultadoEntero <= 10) {
                    System.out.println("🎯 Intentando marcar el objetivo: " + resultadoEntero);
                    marcarPasoComoCompletado(resultadoEntero);
                } else {
                    System.out.println("⚠️ El resultado (" + resultadoEntero + ") está fuera del rango 1-10.");
                }
            } else {
                lblExpresion.setText(String.valueOf(resultadoNum));
            }

        } catch (Exception e) {
            System.out.println("⚠️ Error en evaluación: " + e.getMessage());
            lblExpresion.setText("Error");
        }
    }

    
    /**
     * Actualiza el estilo visual de un botón en la lista de objetivos para indicar que 
     * ha sido alcanzado con éxito.
     *
     * @param numeroPaso El número entero (1-10) del objetivo que se acaba de completar.
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
    
    
    /**
     * Analizador sintáctico de descenso recursivo que evalúa una cadena de texto matemática.
     * Resuelve la ecuación respetando la jerarquía de operadores y el uso de paréntesis.
     *
     * @param str La cadena de texto con la expresión matemática limpia.
     * @return El resultado numérico de la evaluación como un valor de coma flotante (double).
     * @throws RuntimeException Si la sintaxis es inválida o contiene caracteres inesperados.
     * @throws ArithmeticException Si ocurre una división por cero.
     */
    public double evaluarCadenaMatematica(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Carácter inesperado: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) {
                        double divisor = parseFactor();
                        if (divisor == 0) throw new ArithmeticException("División por cero");
                        x /= divisor;
                    }
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Sintaxis no válida");
                }
                return x;
            }
        }.parse();
    }
}