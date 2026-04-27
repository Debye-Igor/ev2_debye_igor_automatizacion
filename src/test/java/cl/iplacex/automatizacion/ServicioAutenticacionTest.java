package cl.iplacex.automatizacion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del ServicioAutenticacion.
 *
 * Validan la logica de negocio de forma aislada, sin levantar
 * el servidor HTTP. Cubren el caso feliz, los casos de fallo
 * y los casos de borde acordados en la sesion Three Amigos.
 */
class ServicioAutenticacionTest {

    @Test
    @DisplayName("Credenciales validas retornan exito y mensaje de bienvenida")
    void credencialesValidasRetornanExito() {
        ServicioAutenticacion servicio = new ServicioAutenticacion();

        ServicioAutenticacion.ResultadoLogin resultado =
            servicio.autenticar("mathias@iplacex.cl", "secreto123");

        assertTrue(resultado.isExito());
        assertEquals("Bienvenido", resultado.getMensaje());
    }

    @Test
    @DisplayName("Password incorrecta retorna mensaje generico de credenciales invalidas")
    void passwordIncorrectaRetornaError() {
        ServicioAutenticacion servicio = new ServicioAutenticacion();

        ServicioAutenticacion.ResultadoLogin resultado =
            servicio.autenticar("mathias@iplacex.cl", "passwordmala");

        assertFalse(resultado.isExito());
        assertEquals("Credenciales invalidas", resultado.getMensaje());
    }

    @Test
    @DisplayName("Usuario inexistente retorna el mismo mensaje generico que password mala")
    void usuarioInexistenteRetornaMismoErrorGenerico() {
        ServicioAutenticacion servicio = new ServicioAutenticacion();

        ServicioAutenticacion.ResultadoLogin resultado =
            servicio.autenticar("nadie@iplacex.cl", "loquesea");

        assertFalse(resultado.isExito());
        assertEquals("Credenciales invalidas", resultado.getMensaje());
    }

    @Test
    @DisplayName("Email vacio retorna mensaje de campos obligatorios")
    void emailVacioRetornaCamposObligatorios() {
        ServicioAutenticacion servicio = new ServicioAutenticacion();

        ServicioAutenticacion.ResultadoLogin resultado =
            servicio.autenticar("", "secreto123");

        assertFalse(resultado.isExito());
        assertEquals("Email y password son obligatorios", resultado.getMensaje());
    }

    @Test
    @DisplayName("Password vacio retorna mensaje de campos obligatorios")
    void passwordVacioRetornaCamposObligatorios() {
        ServicioAutenticacion servicio = new ServicioAutenticacion();

        ServicioAutenticacion.ResultadoLogin resultado =
            servicio.autenticar("mathias@iplacex.cl", "");

        assertFalse(resultado.isExito());
        assertEquals("Email y password son obligatorios", resultado.getMensaje());
    }
}