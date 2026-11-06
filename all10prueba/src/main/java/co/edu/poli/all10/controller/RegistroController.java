package co.edu.poli.all10.controller;

import java.io.IOException;
import java.sql.SQLException;

import co.edu.poli.all10.DAO.JugadorDAO;
import co.edu.poli.all10.modelo.GeneradorHash;
import co.edu.poli.all10.modelo.GestorIdioma;
import co.edu.poli.all10.modelo.Jugador;
import co.edu.poli.all10.modelo.Ranking;
import co.edu.poli.all10.modelo.ValidarRegistro;
import co.edu.poli.all10.vista.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistroController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private PasswordField txtConfirmacion;
    @FXML private Label lblError;
    @FXML private MenuButton btnIdioma;

    private final ValidarRegistro validar = new ValidarRegistro();
    private final JugadorDAO jugadorDAO = new JugadorDAO();
    private Ranking ranking;

    /** JavaFX lo llama al cargar el FXML: configura el botón de idiomas. */
    @FXML
    private void initialize() {
        SelectorIdioma.configurar(btnIdioma, this::cambiarIdioma);
    }

    /** Recarga la pantalla con el nuevo idioma. */
    private void cambiarIdioma() {
        try {
            App.recargar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Recibe el ranking compartido de la aplicación. */
    public void setRanking(Ranking ranking) {
        this.ranking = ranking;
    }

    @FXML
    private void registrar(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String clave = txtClave.getText();
        String confirmacion = txtConfirmacion.getText();

        // 1. Validaciones del formulario
        if (!validar.validarCamposVacios(usuario, clave, confirmacion)) {
            mostrarError(GestorIdioma.get("registro.error.vacios"));
            return;
        }
        if (!validar.validarLongitudMinima(clave)) {
            mostrarError(GestorIdioma.get("registro.error.longitud",
                    ValidarRegistro.LONGITUD_MINIMA_CLAVE));
            return;
        }
        if (!validar.validarCoincidencia(clave, confirmacion)) {
            mostrarError(GestorIdioma.get("registro.error.coincidencia"));
            return;
        }

        try {
            // 2. Verificar si el usuario ya existe en la base de datos
            if (jugadorDAO.existeNombre(usuario)) {
                mostrarError(GestorIdioma.get("registro.error.usuarioEnUso"));
                return;
            }

            // 3. Generar seguridad (Sal y Hash)
            GeneradorHash generador = new GeneradorHash();
            String sal = generador.generarSal();
            String hash = generador.hashear(clave, sal);

            // 4. Crear el objeto Jugador con las credenciales
            Jugador nuevo = new Jugador(usuario, hash, sal);

            // 5. Guardar en la base de datos
            if (jugadorDAO.insertar(nuevo)) {
                if (ranking != null) {
                    ranking.agregar(nuevo);
                }
                txtUsuario.clear();
                txtClave.clear();
                txtConfirmacion.clear();
                mostrarExito(GestorIdioma.get("registro.exito"));

                // 6. Cambiar a la pantalla del juego
                try {
                    App.setRoot("escenario1");
                } catch (IOException e) {
                    mostrarError(GestorIdioma.get("registro.error.pantalla"));
                    e.printStackTrace();
                }

            } else {
                mostrarError(GestorIdioma.get("registro.error.usuarioEnUso"));
            }
        } catch (SQLException e) {
            mostrarError(GestorIdioma.get("registro.error.conexion"));
            e.printStackTrace();
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