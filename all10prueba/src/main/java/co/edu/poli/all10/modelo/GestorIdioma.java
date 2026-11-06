package co.edu.poli.all10.modelo;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Administra el idioma activo y entrega los textos traducidos.
 * No depende de JavaFX, por lo que se puede probar sin interfaz gráfica.
 */
public final class GestorIdioma {

    private static final String BASE = "co.edu.poli.all10prueba.mensajes";

    private static Idioma idioma = Idioma.ES;
    private static ResourceBundle bundle = cargar(idioma);

    private GestorIdioma() { }

    private static ResourceBundle cargar(Idioma i) {
        return ResourceBundle.getBundle(BASE, i.getLocale());
    }

    /** @return Idioma activo. */
    public static Idioma getIdioma() { return idioma; }

    /** Cambia el idioma activo. */
    public static void setIdioma(Idioma nuevo) {
        idioma = nuevo;
        bundle = cargar(nuevo);
    }

    /** @return Bundle del idioma activo (para pasarlo al FXMLLoader). */
    public static ResourceBundle getBundle() { return bundle; }

    /** @return Texto de la clave, o {@code !clave!} si no existe. */
    public static String get(String clave) {
        try {
            return bundle.getString(clave);
        } catch (MissingResourceException e) {
            return "!" + clave + "!";
        }
    }

    /** @return Texto de la clave con {0}, {1}... reemplazados por los argumentos. */
    public static String get(String clave, Object... args) {
        Object[] textos = new Object[args.length];
        for (int i = 0; i < args.length; i++) textos[i] = String.valueOf(args[i]);
        return new MessageFormat(get(clave), bundle.getLocale()).format(textos);
    }
}