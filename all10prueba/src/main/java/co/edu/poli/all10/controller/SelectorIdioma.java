package co.edu.poli.all10.controller;

import co.edu.poli.all10.modelo.GestorIdioma;
import co.edu.poli.all10.modelo.Idioma;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;

/** Llena el botón de idiomas (MenuButton) y avisa cuando el jugador cambia de idioma. */
public final class SelectorIdioma {

    private SelectorIdioma() { }

    /**
     * Configura el botón con la lista de idiomas.
     *
     * @param boton     MenuButton del FXML.
     * @param alCambiar Acción a ejecutar después de cambiar el idioma (normalmente recargar la vista).
     */
    public static void configurar(MenuButton boton, Runnable alCambiar) {
        if (boton == null) return;
        boton.setText("🌐 " + GestorIdioma.getIdioma().name());
        boton.setTooltip(new Tooltip(GestorIdioma.get("idioma.tooltip")));
        boton.getItems().clear();

        ToggleGroup grupo = new ToggleGroup();
        for (Idioma i : Idioma.values()) {
            RadioMenuItem item = new RadioMenuItem(i.getNombre());
            item.setToggleGroup(grupo);
            item.setSelected(i == GestorIdioma.getIdioma());
            item.setOnAction(e -> {
                if (i != GestorIdioma.getIdioma()) {
                    GestorIdioma.setIdioma(i);
                    alCambiar.run();
                }
            });
            boton.getItems().add(item);
        }
    }
}