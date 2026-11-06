package co.edu.poli.all10.modelo;

import java.util.Locale;

/** Idiomas disponibles en el juego. El español usa el bundle raíz (mensajes.properties). */
public enum Idioma {
    ES("Español", Locale.ROOT),
    EN("English", Locale.ENGLISH);

    private final String nombre;
    private final Locale locale;

    Idioma(String nombre, Locale locale) {
        this.nombre = nombre;
        this.locale = locale;
    }

    /** @return Nombre del idioma escrito en ese mismo idioma. */
    public String getNombre() { return nombre; }

    /** @return Locale asociado al idioma. */
    public Locale getLocale() { return locale; }
}