package co.edu.poli.all10.modelo;

/**
 * Valida los datos del formulario de registro antes de crear la cuenta.
 * Cada método devuelve true cuando la validación se cumple.
 */
public class ValidarRegistro {

    public static final int LONGITUD_MINIMA_CLAVE = 6;

    /** true si ningún campo está vacío (los espacios solos cuentan como vacío). */
    public boolean validarCamposVacios(String usuario, String clave, String confirmacion) {
        return !estaVacio(usuario) && !estaVacio(clave) && !estaVacio(confirmacion);
    }

    /** true si la contraseña tiene al menos LONGITUD_MINIMA_CLAVE caracteres. */
    public boolean validarLongitudMinima(String clave) {
        return clave != null && clave.length() >= LONGITUD_MINIMA_CLAVE;
    }

    /** true si la contraseña y su confirmación son exactamente iguales. */
    public boolean validarCoincidencia(String clave, String confirmacion) {
        return clave != null && clave.equals(confirmacion);
    }

    /** true solo si pasan las tres validaciones. */
    public boolean validarRegistro(String usuario, String clave, String confirmacion) {
        return validarCamposVacios(usuario, clave, confirmacion)
                && validarLongitudMinima(clave)
                && validarCoincidencia(clave, confirmacion);
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
