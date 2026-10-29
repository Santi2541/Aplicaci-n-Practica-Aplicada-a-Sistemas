package co.edu.poli.all10.vista;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Clase principal de la aplicación AllTen: carga la vista FXML inicial. */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("registro"));
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
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                App.class.getResource("/co/edu/poli/all10prueba/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}