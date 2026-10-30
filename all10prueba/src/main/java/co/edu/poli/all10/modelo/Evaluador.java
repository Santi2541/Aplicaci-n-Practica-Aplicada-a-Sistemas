package co.edu.poli.all10.modelo;

/**
 * Manipula y evalúa la expresión matemática del juego.
 * <p>
 * Soporta {@code + - * / ( )}, la raíz cuadrada {@code √} y la potencia {@code ^}.
 * También acepta los símbolos visuales {@code × ÷ xʸ ² ³}.
 */
public class Evaluador {
	
	/**
     * Limpia la expresión (símbolos visuales) y la evalúa.
     *
     * @param expresion Expresión tal como la arma el jugador.
     * @return Resultado numérico.
     * @throws ArithmeticException si hay división por cero, raíz negativa o potencia indefinida.
     * @throws RuntimeException    si la sintaxis es inválida.
     */
	public double evaluar(String expresion) {
        String limpia = expresion.trim();
        if (limpia.contains(":")) {
            limpia = limpia.split(":")[1].trim();
        }
        limpia = limpia.replace("×", "*").replace("÷", "/")
                       .replace("xʸ", "^")
                       .replace("²", "^2").replace("³", "^3");
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
                if (pos < str.length()) throw new RuntimeException("Carácter inesperado: " + (char) ch);
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
                double x = parseUnary();
                for (;;) {
                    if      (eat('*')) x *= parseUnary();
                    else if (eat('/')) {
                        double divisor = parseUnary();
                        if (divisor == 0) throw new ArithmeticException("División por cero");
                        x /= divisor;
                    }
                    else return x;
                }
            }

            double parseUnary() {
                if (eat('+')) return parseUnary();
                if (eat('-')) return -parseUnary();
                return parsePower();
            }

            double parsePower() {
                double base = parsePrimary();
                if (eat('^')) {
                    double exponente = parseUnary();   // asociatividad por la derecha
                    double r = Math.pow(base, exponente);
                    if (Double.isNaN(r) || Double.isInfinite(r)) {
                        throw new ArithmeticException("Potencia indefinida");
                    }
                    return r;
                }
                return base;
            }

            double parsePrimary() {
                if (eat('√')) {
                    double radicando = parsePrimary();
                    if (radicando < 0) throw new ArithmeticException("Raíz de un número negativo");
                    return Math.sqrt(radicando);
                }

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
