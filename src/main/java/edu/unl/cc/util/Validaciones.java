package edu.unl.cc.util;

public class Validaciones {

    /**
     * Valida que una cadena no esté vacía o sea nula.
     * @param texto La cadena a validar.
     * @return true si la cadena es nula o está vacía (después de quitar espacios), false de lo contrario.
     */
    public static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    /**
     * Valida si una cadena de cédula es válida según las reglas:
     * 1. No debe estar vacía.
     * 2. Debe tener exactamente 10 dígitos.
     * 3. Debe contener solo números.
     *
     * @param cedula La cédula a validar.
     * @return true si la cédula cumple las reglas, false de lo contrario.
     */
    public static boolean validarCedula(String cedula) {
        if (estaVacio(cedula)) {
            return false;
        }
        // \d{10} -> Coincide exactamente 10 veces con un digito.
        return cedula.matches("\\d{10}");
    }

    /**
     * Valida si una cadena es un nombre o apellido válido:
     * 1. No debe estar vacío.
     * 2. Debe contener solo letras y espacios.
     *
     * @param texto El nombre or apellido a validar.
     * @return true si es un nombre/apellido válido, false de lo contrario.
     */
    public static boolean validarNombreApellido(String texto) {
        if (estaVacio(texto)) {
            return false;
        }

        // Convertimos el String en un array de caracteres
        char[] caracteres = texto.toCharArray();

        // Iteramos por cada caracter
        for (char c : caracteres) {

            // Verificamos si el caracter NO es una letra Y NO es un espacio en blanco
            if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                // Si encontramos un caracter inválido (número, símbolo, etc.)
                // retornamos false inmediatamente.
                return false;
            }
        }
        // Si el bucle termina, significa que todos los caracteres
        // fueron válidos (letras o espacios).
        return true;
    }
}
