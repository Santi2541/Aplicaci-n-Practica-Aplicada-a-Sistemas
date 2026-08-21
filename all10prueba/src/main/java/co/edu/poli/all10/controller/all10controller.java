package co.edu.poli.all10.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class all10controller {

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