package cl.iplacex.automatizacion;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio que valida credenciales contra una lista de usuarios en memoria.
 *
 * Esta version es deliberadamente simple: las contrasenas se comparan
 * como strings sin hashing. En una iteracion futura se reemplazara
 * por hashing (BCrypt) y persistencia en base de datos.
 */
public class ServicioAutenticacion {

    private final Map<String, String> usuarios;

    public ServicioAutenticacion() {
        this.usuarios = new HashMap<>();
        // Usuarios precargados para esta version de demostracion
        usuarios.put("mathias@iplacex.cl", "secreto123");
        usuarios.put("vallery@iplacex.cl", "admin2026");
    }

    /**
     * Valida las credenciales recibidas.
     *
     * @param email correo del usuario
     * @param password contrasena en texto plano
     * @return resultado con exito (true/false) y mensaje
     */
    public ResultadoLogin autenticar(String email, String password) {
        // Validacion de campos vacios
        if (email == null || email.isBlank() ||
            password == null || password.isBlank()) {
            return new ResultadoLogin(false, "Email y password son obligatorios");
        }

        // Validacion de credenciales
        String passwordEsperada = usuarios.get(email);
        if (passwordEsperada != null && passwordEsperada.equals(password)) {
            return new ResultadoLogin(true, "Bienvenido");
        }

        // Mensaje generico por seguridad: no revela si el usuario existe
        return new ResultadoLogin(false, "Credenciales invalidas");
    }

    /**
     * Resultado inmutable de un intento de login.
     */
    public static class ResultadoLogin {
        private final boolean exito;
        private final String mensaje;

        public ResultadoLogin(boolean exito, String mensaje) {
            this.exito = exito;
            this.mensaje = mensaje;
        }

        public boolean isExito() {
            return exito;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}