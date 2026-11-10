package co.edu.poli.all10.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import co.edu.poli.all10.modelo.GestorIdioma;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Genera una imagen PNG con los resultados de una partida terminada
 * (jugador, puntos, objetivos y tiempo) para que el jugador la comparta.
 */
public class ExportadorResultados {

	private ExportadorResultados() { }
	
	/**
     * Pide al jugador dónde guardar la imagen y la escribe como PNG.
     *
     * @return {@code true} si se guardó; {@code false} si canceló el diálogo.
     * @throws IOException si no se puede escribir el archivo.
     */
	public static boolean exportar(Window dueno, String nombre, int puntaje,int objetivos, int segundos) throws IOException {
		FileChooser selector = new FileChooser();
		selector.setTitle(GestorIdioma.get("export.guardar"));
		selector.setInitialFileName("AllTen-resultado.png");
		selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
			
		File destino = selector.showSaveDialog(dueno);
		if (destino == null) return false;
		if (!destino.getName().toLowerCase().endsWith(".png")) {
			destino = new File(destino.getPath() + ".png");
			}
	
		WritableImage imagen = capturar(crearTarjeta(nombre, puntaje, objetivos, segundos));
		ImageIO.write(SwingFXUtils.fromFXImage(imagen, null), "png", destino);
		return true;
	}
	
	/** Construye la tarjeta visual con los resultados (estilos en línea, sin depender del CSS). */
    private static VBox crearTarjeta(String nombre, int puntaje, int objetivos, int segundos) {
        VBox tarjeta = new VBox(8,
                etiqueta("AllTen", 32, true),
                etiqueta(nombre, 22, true),
                etiqueta(String.format("%s  %02d:%02d",
                        GestorIdioma.get("export.tiempo"), segundos / 60, segundos % 60), 40, true),
                etiqueta(GestorIdioma.get("export.puntos", puntaje), 24, true),
                etiqueta(GestorIdioma.get("export.objetivos", objetivos), 16, false),
                etiqueta(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), 12, false));
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(32));
        tarjeta.setPrefWidth(420);
        tarjeta.setStyle("-fx-background-color: linear-gradient(to bottom, #7C3AED, #B39DDB);");
        return tarjeta;
    }
    
    private static Label etiqueta(String texto, int tamano, boolean negrita) {
        Label l = new Label(texto);
        l.setStyle("-fx-text-fill: white; -fx-font-size: " + tamano + "px; -fx-font-family: 'Segoe UI', sans-serif;"
                + (negrita ? " -fx-font-weight: bold;" : ""));
        return l;
    }
    
    /** Captura la tarjeta como imagen al doble de resolución para que se vea nítida. */
    private static WritableImage capturar(VBox tarjeta) {new Scene(tarjeta);tarjeta.applyCss();
        tarjeta.autosize();
        tarjeta.layout();
        SnapshotParameters parametros = new SnapshotParameters();
        parametros.setTransform(Transform.scale(2, 2));
        return tarjeta.snapshot(parametros, null);
    }
}
