package co.edu.poli.all10.vista;

import java.io.IOException;

import co.edu.poli.all10.modelo.GestorIdioma;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

/** Clase principal de la aplicación AllTen: carga la vista FXML inicial. */
public class App extends Application {

    private static Scene scene;
    private static String fxmlActual = "registro";

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML(fxmlActual));
        stage.setTitle("AllTen");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Cambia la vista raíz de la escena.
     *
     * @param fxml Nombre del archivo FXML, sin extensión.
     * @throws IOException si no se puede cargar el archivo.
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        fxmlActual = fxml;
    }

    /**
     * Vuelve a cargar la vista actual con el idioma activo.
     *
     * @throws IOException si no se puede cargar el archivo.
     */
    public static void recargar() throws IOException {
        setRoot(fxmlActual);
    }

    /**
     * Abre una vista en una ventana modal sin cerrar la actual.
     *
     * @param fxml   Nombre del FXML, sin extensión.
     * @param titulo Título de la ventana.
     * @throws IOException si no se puede cargar el archivo.
     */
    public static void abrirVentana(String fxml, String titulo) throws IOException {
        Stage ventana = new Stage();
        ventana.initOwner(scene.getWindow());
        ventana.initModality(Modality.WINDOW_MODAL);
        ventana.setTitle(titulo);
        ventana.setScene(new Scene(loadFXML(fxml)));
        ventana.showAndWait();
    }

    /**
     * Aplica la colorimetría de AllTen a un Alert.
     *
     * @param alerta Alert a estilizar (llamar antes de showAndWait).
     */
    public static void estilizar(Alert alerta) {
        alerta.getDialogPane().getStylesheets().add(
                App.class.getResource("/co/edu/poli/all10prueba/style.css").toExternalForm());
        alerta.getDialogPane().getStyleClass().add("dialogo-allten");
        alerta.setGraphic(null);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                App.class.getResource("/co/edu/poli/all10prueba/" + fxml + ".fxml"),
                GestorIdioma.getBundle());
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}