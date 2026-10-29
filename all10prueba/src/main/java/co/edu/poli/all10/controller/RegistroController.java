package co.edu.poli.all10.controller;

import java.sql.SQLException;

import co.edu.poli.all10.DAO.JugadorDAO;
import co.edu.poli.all10.modelo.GeneradorHash;
import co.edu.poli.all10.modelo.Jugador;
import co.edu.poli.all10.modelo.Ranking;
import co.edu.poli.all10.modelo.ValidarRegistro;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistroController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private PasswordField txtConfirmacion;
    @FXML private Label lblError;

    private final ValidarRegistro validar = new ValidarRegistro();
    private final JugadorDAO jugadorDAO = new JugadorDAO();
    private Ranking ranking;

    /** Recibe el ranking compartido de la aplicación. */
    public void setRanking(Ranking ranking) {
        this.ranking = ranking;
    }

    @FXML
    private void registrar(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String clave = txtClave.getText();
        String confirmacion = txtConfirmacion.getText();

        if (!validar.validarCamposVacios(usuario, clave, confirmacion)) {
            mostrarError("Completa todos los campos.");
            return;
        }
        if (!validar.validarLongitudMinima(clave)) {
            mostrarError("La contraseña debe tener al menos "
                    + ValidarRegistro.LONGITUD_MINIMA_CLAVE + " caracteres.");
            return;
        }
        if (!validar.validarCoincidencia(clave, confirmacion)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }

        try {
            if (jugadorDAO.existeNombre(usuario)) {
                mostrarError("Ese nombre de usuario ya está en uso.");
                return;
            }

            GeneradorHash generador = new GeneradorHash();
            String sal = generador.generarSal();
            String hash = generador.hashear(clave, sal);
            Jugador nuevo = new Jugador(usuario, hash, sal);

            if (jugadorDAO.insertar(nuevo)) {
                if (ranking != null) {
                    ranking.agregar(nuevo);
                }
                txtUsuario.clear();
                txtClave.clear();
                txtConfirmacion.clear();
                mostrarExito("✔ Cuenta creada.");
            } else {
                mostrarError("Ese nombre de usuario ya está en uso.");
            }
        } catch (SQLException e) {
            mostrarError("No se pudo guardar la cuenta. Intenta de nuevo.");
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setStyle("-fx-text-fill: #ef4444;");
        lblError.setText(mensaje);
    }

    private void mostrarExito(String mensaje) {
        lblError.setStyle("-fx-text-fill: #22c55e;");
        lblError.setText(mensaje);
    }
}