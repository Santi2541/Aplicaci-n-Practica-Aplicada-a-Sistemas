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
    
    public void initialize(URL location, ResourceBundle resources) {
        arregloPasos = new Button[]{
            btnPaso1, btnPaso2, btnPaso3, btnPaso4, btnPaso5, 
            btnPaso6, btnPaso7, btnPaso8, btnPaso9, btnPaso10
        };

        establecerListaIncompleta();
        iniciarCronometro();

        javafx.application.Platform.runLater(() -> {
            if (lblExpresion != null) {
                lblExpresion.requestFocus();
            }
        });
    }
    
    private void establecerListaIncompleta() {
        for (int i = 0; i < arregloPasos.length; i++) {
            Button btnPaso = arregloPasos[i];
            if (btnPaso != null) {
                btnPaso.getStyleClass().removeAll("paso-completado", "paso-actual", "paso-pendiente");
                btnPaso.setStyle(null);

                int numeroPaso = i + 1;
                btnPaso.setText(String.valueOf(numeroPaso));

                btnPaso.getStyleClass().add("paso-actual");
            }
        }
    }
    
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
    
    @FXML
    private void limpiarTodo(ActionEvent event) {
        lblExpresion.setText("");

        btnNum1.setDisable(false);
        btnNum2.setDisable(false);
        btnNum3.setDisable(false);
        btnNum4.setDisable(false);
    }
    
    @FXML
    private void borrarUltimo(ActionEvent event) {
        String textoActual = lblExpresion.getText().trim();
        if (!textoActual.isEmpty()) {
            lblExpresion.setText(textoActual.substring(0, textoActual.length() - 1).trim());
        }
    }

    @FXML
    private void calcularResultado(ActionEvent event) {
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

    private void marcarPasoComoCompletado(int numeroPaso) {
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
    
    private double evaluarCadenaMatematica(final String str) {
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