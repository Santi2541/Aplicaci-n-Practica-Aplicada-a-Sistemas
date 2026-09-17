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
	
	@FXML
    private Label lblExpresion;
	
	@FXML private Label lblTiempo;
	@FXML private Button btnNum1;
    @FXML private Button btnNum2;
    @FXML private Button btnNum3;
    @FXML private Button btnNum4;

    private Timeline cronometro;
    private int segundosTranscurridos = 0;
    
    /**
     * Se ejecuta automáticamente al abrir la ventana.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        iniciarCronometro();
    }
    
    /**
     * Inicia el temporizador desde 00:00
     */
    private void iniciarCronometro() {
        segundosTranscurridos = 0;
        
        cronometro = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            segundosTranscurridos++;
            int minutos = segundosTranscurridos / 60;
            int segundos = segundosTranscurridos % 60;
            
            // Formatea el tiempo con dos dígitos (ej. 01:05)
            if (lblTiempo != null) {
                lblTiempo.setText(String.format("⏰ %02d:%02d", minutos, segundos));
            }
        }));
        
        cronometro.setCycleCount(Timeline.INDEFINITE);
        cronometro.play();
    }
    
    /**
     * Este método se ejecuta cada vez que presiones un botón de número u operador
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
     * Acción del botón "Limpiar": Borra la pantalla y reactiva todos los botones.
     */
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
        String operacion = lblExpresion.getText().trim();

        if (operacion.isEmpty()) {
            return;
        }

        try {
            String operacionLimpia = operacion.replace("×", "*").replace("÷", "/");

            double resultadoNum = evaluarCadenaMatematica(operacionLimpia);

            if (resultadoNum == (long) resultadoNum) {
                lblExpresion.setText(String.format("%d", (long) resultadoNum));
            } else {
                lblExpresion.setText(String.valueOf(resultadoNum));
            }

        } catch (Exception e) {
            lblExpresion.setText("Error");
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
    
    @FXML
    private Label expressionLabel;

    private String expression = "";

    @FXML
    private void onNumber3() {
        addSymbol("3");
    }

    @FXML
    private void onNumber6() {
        addSymbol("6");
    }

    @FXML
    private void onNumber9() {
        addSymbol("9");
    }

    @FXML
    private void onNumber2() {
        addSymbol("2");
    }

    @FXML
    private void onAdd() {
        addSymbol(" + ");
    }

    @FXML
    private void onSubtract() {
        addSymbol(" - ");
    }

    @FXML
    private void onMultiply() {
        addSymbol(" * ");
    }

    @FXML
    private void onDivide() {
        addSymbol(" ÷ ");
    }

    @FXML
    private void onOpenParenthesis() {
        addSymbol("(");
    }

    @FXML
    private void onCloseParenthesis() {
        addSymbol(")");
    }

    private void addSymbol(String symbol) {
        expression += symbol;
        expressionLabel.setText(expression);
    }
}