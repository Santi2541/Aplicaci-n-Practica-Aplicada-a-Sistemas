# language: es
Característica: Evaluación de operaciones matemáticas en All10

  Escenario: El jugador evalúa una expresión matemática válida usando los 4 números
    Dado que el controlador de All10 está inicializado
    Cuando el jugador evalúa la expresión "( 9 - 6 ) + 3 + 2"
    Entonces el resultado calculado debe ser 8.0

  Escenario: Manejo de división por cero
    Dado que el controlador de All10 está inicializado
    Cuando el jugador intenta evaluar la expresión "6 / ( 3 - 3 )"
    Entonces el sistema debe detectar una división por cero