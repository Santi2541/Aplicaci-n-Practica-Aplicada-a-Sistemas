package co.edu.poli.all10.modelo;

public class Evaluador {
	
	public double evaluar(String expresion) {
        String limpia = expresion.trim();
        if (limpia.contains(":")) {
            limpia = limpia.split(":")[1].trim();
        }
        limpia = limpia.replace("×", "*").replace("÷", "/");
        return evaluarCadenaMatematica(limpia);
    }
	/**
     * Analizador sintáctico de descenso recursivo que evalúa una cadena de texto matemática.
     * Resuelve la ecuación respetando la jerarquía de operadores y el uso de paréntesis.
     *
     * @param str La cadena de texto con la expresión matemática limpia.
     * @return El resultado numérico de la evaluación como un valor de coma flotante (double).
     * @throws RuntimeException Si la sintaxis es inválida o contiene caracteres inesperados.
     * @throws ArithmeticException Si ocurre una división por cero.
     */
    public double evaluarCadenaMatematica(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Carácter inesperado: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) {
                        double divisor = parseFactor();
                        if (divisor == 0) throw new ArithmeticException("División por cero");
                        x /= divisor;
                    }
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Sintaxis no válida");
                }
                return x;
            }
        }.parse();
    }
}
