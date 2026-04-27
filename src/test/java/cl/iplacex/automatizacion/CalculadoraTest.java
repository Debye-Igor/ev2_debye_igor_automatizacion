package cl.iplacex.automatizacion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
  Pruebas unitarias para la clase Calculadora.
  Cada test es atomico: verifica un solo escenario, no depende de otros tests y no comparte estado.
  
*/

class CalculadoraTest {

    @Test
    @DisplayName("Sumar dos numeros positivos devuelve la suma correcta")
    void sumarDosNumerosPositivos() {
        Calculadora calc = new Calculadora();
        int resultado = calc.sumar(2, 3);
        assertEquals(5, resultado);
    }

    @Test
    @DisplayName("Sumar un numero con cero devuelve el mismo numero")
    void sumarConCero() {
        Calculadora calc = new Calculadora();
        assertEquals(7, calc.sumar(7, 0));
    }

    @Test
    @DisplayName("Sumar numeros negativos devuelve resultado negativo")
    void sumarNumerosNegativos() {
        Calculadora calc = new Calculadora();
        assertEquals(-8, calc.sumar(-3, -5));
    }


    @Test
    @DisplayName("Restar dos numeros positivos devuelve la diferencia")
    void restarDosNumerosPositivos() {
        Calculadora calc = new Calculadora();
        int resultado = calc.restar(10, 4);
        assertEquals(6, resultado);
    }

    @Test
    @DisplayName("Restar un numero a si mismo devuelve cero")
    void restarMismoNumeroDaCero() {
        Calculadora calc = new Calculadora();
        assertEquals(0, calc.restar(5, 5));
    }

    @Test
    @DisplayName("Restar un numero mayor a uno menor devuelve negativo")
    void restarDaResultadoNegativo() {
        Calculadora calc = new Calculadora();
        assertEquals(-3, calc.restar(2, 5));
    }
}

