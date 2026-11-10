package co.edu.poli.all10.controller;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import co.edu.poli.all10.DAO.JugadorDAO;
import co.edu.poli.all10.modelo.GestorIdioma;
import co.edu.poli.all10.modelo.Jugador;
import co.edu.poli.all10.modelo.Partida;
import co.edu.poli.all10.modelo.RegistroVelocidad;
import co.edu.poli.all10.modelo.SesionActual;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Ventana de ranking por velocidad (todos los jugadores) y de partidas
 * anteriores del jugador con sesión, ambas con la fecha en que se lograron.
 */
public class RankingController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private TableView<RegistroVelocidad> tblVelocidad;
    @FXML private TableColumn<RegistroVelocidad, String> colPos;
    @FXML private TableColumn<RegistroVelocidad, String> colJugador;
    @FXML private TableColumn<RegistroVelocidad, String> colTiempo;
    @FXML private TableColumn<RegistroVelocidad, String> colFecha;

    @FXML private TableView<Partida> tblMias;
    @FXML private TableColumn<Partida, String> colMiFecha;
    @FXML private TableColumn<Partida, String> colMiObjetivos;
    @FXML private TableColumn<Partida, String> colMiTiempo;
    @FXML private TableColumn<Partida, String> colMiPuntaje;

    @FXML private Label lblError;

    private final JugadorDAO jugadorDAO = new JugadorDAO();

    @FXML
    private void initialize() {
        tblVelocidad.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblMias.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colPos.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(tblVelocidad.getItems().indexOf(c.getValue()) + 1)));
        colJugador.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colTiempo.setCellValueFactory(c -> new SimpleStringProperty(mmss(c.getValue().getTiempo())));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().format(FORMATO)));

        colMiFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().format(FORMATO)));
        colMiObjetivos.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getObjetivosCompletados())));
        colMiTiempo.setCellValueFactory(c -> new SimpleStringProperty(mmss(c.getValue().getTiempo())));
        colMiPuntaje.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getPuntaje())));

        tblVelocidad.setPlaceholder(new Label(GestorIdioma.get("ranking.vacio")));
        tblMias.setPlaceholder(new Label(GestorIdioma.get("ranking.vacio")));

        try {
            tblVelocidad.setItems(FXCollections.observableArrayList(jugadorDAO.listarRankingVelocidad()));
            Jugador actual = SesionActual.getJugador();
            if (actual != null) {
                tblMias.setItems(FXCollections.observableArrayList(
                        jugadorDAO.listarPartidas(actual.getNombre())));
            }
        } catch (SQLException e) {
            lblError.setText(GestorIdioma.get("ranking.error"));
            System.err.println("No se pudo cargar el ranking: " + e.getMessage());
        }
    }

    private static String mmss(int s) {
        return String.format("%02d:%02d", s / 60, s % 60);
    }

    @FXML
    private void cerrar(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}