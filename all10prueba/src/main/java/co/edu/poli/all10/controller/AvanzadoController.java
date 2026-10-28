package co.edu.poli.all10.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

public class AvanzadoController {

    @FXML
    private void volver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
            getClass().getResource("/co/edu/poli/all10prueba/escenario1.fxml"));
        ((Node) event.getSource()).getScene().setRoot(root);
    }
}