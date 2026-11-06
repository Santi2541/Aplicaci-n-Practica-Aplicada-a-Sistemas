package co.edu.poli.all10.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;

import co.edu.poli.all10.DAO.JugadorDAO;
import co.edu.poli.all10.modelo.GeneradorHash;
import co.edu.poli.all10.modelo.GestorIdioma;
import co.edu.poli.all10.modelo.Jugador;
import co.edu.poli.all10.modelo.Ranking;
import co.edu.poli.all10.modelo.SesionActual;
import co.edu.poli.all10.modelo.ValidarRegistro;
import co.edu.poli.all10.vista.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controlador de la pantalla de acceso. Tiene tres modos: iniciar sesión,
 * crear cuenta y recuperar contraseña. Al entrar o registrarse deja al
 * jugador guardado en {@link SesionActual}.
 */
public class RegistroController {

    private enum Modo { LOGIN, REGISTRO, RECUPERAR }

    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @FXML private TextField txtUsuario;
    @FXML private TextField txtCodigo;
    @FXML private PasswordField txtClave;
    @FXML private PasswordField txtConfirmacion;
    @FXML private Label lblError;
    @FXML private Label lblTitulo;
    @FXML private Label lblAyuda;
    @FXML private Label lblClave;
    @FXML private Label lblConfirmar;
    @FXML private Label lblCodigo;
    @FXML private Button btnAccion;
    @FXML private Hyperlink lnkOlvide;
    @FXML private Hyperlink lnkCrearCuenta;
    @FXML private Hyperlink lnkIniciarSesion;
    @FXML private MenuButton btnIdioma;

    private final ValidarRegistro validar = new ValidarRegistro();
    private final JugadorDAO jugadorDAO = new JugadorDAO();
    private Ranking ranking;
    private Modo modo = Modo.LOGIN;

    @FXML
    private void initialize() {
        SelectorIdioma.configurar(btnIdioma, this::cambiarIdioma);
        aplicarModo(Modo.LOGIN);
    }

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

    // ---------- Cambio de modo ----------

    @FXML private void irALogin(ActionEvent e)     { aplicarModo(Modo.LOGIN); }
    @FXML private void irARegistro(ActionEvent e)  { aplicarModo(Modo.REGISTRO); }
    @FXML private void irARecuperar(ActionEvent e) { aplicarModo(Modo.RECUPERAR); }

    private void aplicarModo(Modo nuevo) {
        modo = nuevo;
        lblError.setText("");
        txtClave.clear();
        txtConfirmacion.clear();
        txtCodigo.clear();

        boolean login = nuevo == Modo.LOGIN;
        boolean registro = nuevo == Modo.REGISTRO;
        boolean recuperar = nuevo == Modo.RECUPERAR;

        mostrar(lblConfirmar, registro || recuperar);
        mostrar(txtConfirmacion, registro || recuperar);
        mostrar(lblCodigo, recuperar);
        mostrar(txtCodigo, recuperar);
        mostrar(lnkOlvide, login);
        mostrar(lnkCrearCuenta, login);
        mostrar(lnkIniciarSesion, registro || recuperar);

        lblConfirmar.setText(GestorIdioma.get("registro.confirmar"));

        if (login) {
            lblTitulo.setText(GestorIdioma.get("login.titulo"));
            lblAyuda.setText(GestorIdioma.get("login.ayuda"));
            lblClave.setText(GestorIdioma.get("registro.clave"));
            btnAccion.setText(GestorIdioma.get("login.boton"));
        } else if (registro) {
            lblTitulo.setText(GestorIdioma.get("registro.titulo"));
            lblAyuda.setText(GestorIdioma.get("registro.ayuda"));
            lblClave.setText(GestorIdioma.get("registro.clave"));
            btnAccion.setText(GestorIdioma.get("registro.boton"));
        } else {
            lblTitulo.setText(GestorIdioma.get("recuperar.titulo"));
            lblAyuda.setText(GestorIdioma.get("recuperar.ayuda"));
            lblClave.setText(GestorIdioma.get("recuperar.claveNueva"));
            btnAccion.setText(GestorIdioma.get("recuperar.boton"));
        }
    }

    private void mostrar(Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }

    // ---------- Botón principal ----------

    @FXML
    private void accionPrincipal(ActionEvent event) {
        switch (modo) {
            case LOGIN:     iniciarSesion(); break;
            case REGISTRO:  registrar();     break;
            default:        recuperar();     break;
        }
    }

    // ---------- Iniciar sesión ----------

    private void iniciarSesion() {
        String usuario = txtUsuario.getText();
        String clave = txtClave.getText();

        if (usuario.isBlank() || clave.isEmpty()) {
            mostrarError(GestorIdioma.get("login.error.vacios"));
            return;
        }
        try {
            String[] cred = jugadorDAO.obtenerCredenciales(usuario);
            if (cred == null || cred[0] == null || cred[1] == null
                    || !coincide(new GeneradorHash().hashear(clave, cred[1]), cred[0])) {
                mostrarError(GestorIdioma.get("login.error.credenciales"));
                return;
            }

            // Se lee el jugador con su mejor resultado y se guarda en la sesión
            Jugador jugador = jugadorDAO.obtenerJugador(usuario);
            if (jugador == null) {
                jugador = new Jugador(usuario, cred[0], cred[1]);
            }
            SesionActual.iniciar(jugador);
            if (ranking != null) {
                ranking.agregar(jugador);
            }
            irAlJuego();
        } catch (SQLException e) {
            mostrarError(GestorIdioma.get("registro.error.conexion"));
            e.printStackTrace();
        }
    }

    // ---------- Crear cuenta ----------

    private void registrar() {
        String usuario = txtUsuario.getText();
        String clave = txtClave.getText();
        String confirmacion = txtConfirmacion.getText();

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
            if (jugadorDAO.existeNombre(usuario)) {
                mostrarError(GestorIdioma.get("registro.error.usuarioEnUso"));
                return;
            }

            GeneradorHash generador = new GeneradorHash();
            String sal = generador.generarSal();
            String hash = generador.hashear(clave, sal);
            Jugador nuevo = new Jugador(usuario, hash, sal);

            if (jugadorDAO.insertar(nuevo)) {
                SesionActual.iniciar(nuevo);
                if (ranking != null) {
                    ranking.agregar(nuevo);
                }
                // Código de recuperación: se muestra una sola vez
                String codigo = generarYGuardarCodigo(usuario);
                mostrarCodigo(codigo);
                irAlJuego();
            } else {
                mostrarError(GestorIdioma.get("registro.error.usuarioEnUso"));
            }
        } catch (SQLException e) {
            mostrarError(GestorIdioma.get("registro.error.conexion"));
            e.printStackTrace();
        }
    }

    // ---------- Recuperar contraseña ----------

    private void recuperar() {
        String usuario = txtUsuario.getText();
        String codigo = txtCodigo.getText().toUpperCase().replace("-", "").replace(" ", "");
        String clave = txtClave.getText();
        String confirmacion = txtConfirmacion.getText();

        if (codigo.isEmpty() || !validar.validarCamposVacios(usuario, clave, confirmacion)) {
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
            String[] cred = jugadorDAO.obtenerCredenciales(usuario);
            GeneradorHash generador = new GeneradorHash();
            if (cred == null || cred[2] == null || cred[3] == null
                    || !coincide(generador.hashear(codigo, cred[3]), cred[2])) {
                mostrarError(GestorIdioma.get("recuperar.error.codigo"));
                return;
            }

            String sal = generador.generarSal();
            jugadorDAO.restablecerContrasena(usuario, generador.hashear(clave, sal), sal);

            // El código se renueva: el anterior ya no sirve
            mostrarCodigo(generarYGuardarCodigo(usuario));

            aplicarModo(Modo.LOGIN);
            txtUsuario.setText(usuario);
            mostrarExito(GestorIdioma.get("recuperar.exito"));
        } catch (SQLException e) {
            mostrarError(GestorIdioma.get("registro.error.conexion"));
            e.printStackTrace();
        }
    }

    // ---------- Apoyo ----------

    private String generarYGuardarCodigo(String usuario) throws SQLException {
        GeneradorHash generador = new GeneradorHash();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(ALFABETO.charAt(RANDOM.nextInt(ALFABETO.length())));
        }
        String codigo = sb.toString();
        String sal = generador.generarSal();
        jugadorDAO.guardarCodigoRecuperacion(usuario, generador.hashear(codigo, sal), sal);
        return codigo;
    }

    private void mostrarCodigo(String codigo) {
        String bonito = codigo.substring(0, 4) + "-" + codigo.substring(4);
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(GestorIdioma.get("registro.codigo.titulo"));
        alerta.setHeaderText(GestorIdioma.get("registro.codigo.encabezado"));
        alerta.setContentText(GestorIdioma.get("registro.codigo.mensaje", bonito));
        alerta.showAndWait();
    }

    private boolean coincide(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8),
                                     b.getBytes(StandardCharsets.UTF_8));
    }

    private void irAlJuego() {
        try {
            App.setRoot("escenario1");
        } catch (IOException e) {
            mostrarError(GestorIdioma.get("registro.error.pantalla"));
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