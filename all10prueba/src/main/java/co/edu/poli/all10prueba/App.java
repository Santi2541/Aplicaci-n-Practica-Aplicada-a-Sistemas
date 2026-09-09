/*
(Ejemplo de tarea en la rama REVISION) Nicol tiene que solucionar un error donde el programa
no abre el escenario que deberia, solo abre una pantalla donde dice "primary" y solo da la opción de un boton
(La solución sería cambiar en el metodo "start" el atributo de "scene", en vez de cargar un "primary" debe poner el nombre del escenario realizado <escenario1>)
*/

/* (ejemplo PAS 88) Santiago anturi resolvió el problema porque nicol no estaba disponible. Se cambió el "primary" por "escenario1" */
/*hola a todos buenos dias */

package co.edu.poli.all10prueba;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("escenario1"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
