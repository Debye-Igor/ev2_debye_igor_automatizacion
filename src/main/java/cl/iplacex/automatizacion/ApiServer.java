package cl.iplacex.automatizacion;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.stop;
import static spark.Spark.awaitInitialization;

/**
 * Mini servidor HTTP que expone el endpoint POST /login.
 *
 * Se usa Spark Java por su simplicidad: levanta un servidor
 * embebido en pocas lineas, sin necesidad de configurar
 * un contenedor de aplicaciones.
 */
public class ApiServer {

    private static final int PUERTO_DEFECTO = 4567;
    private final ServicioAutenticacion servicio;
    private final Gson gson;

    public ApiServer() {
        this.servicio = new ServicioAutenticacion();
        this.gson = new Gson();
    }

    /**
     * Inicia el servidor en el puerto indicado y registra las rutas.
     */
    public void iniciar(int puerto) {
        port(puerto);

        post("/login", (request, response) -> {
            response.type("application/json");

            // Parsear el body JSON
            JsonObject body;
            try {
                body = gson.fromJson(request.body(), JsonObject.class);
            } catch (Exception e) {
                response.status(400);
                return "{\"exito\":false,\"mensaje\":\"JSON invalido\"}";
            }

            String email = body != null && body.has("email")
                ? body.get("email").getAsString() : "";
            String password = body != null && body.has("password")
                ? body.get("password").getAsString() : "";

            ServicioAutenticacion.ResultadoLogin resultado =
                servicio.autenticar(email, password);

            // 200 si exito, 401 si fallo
            response.status(resultado.isExito() ? 200 : 401);

            JsonObject respuesta = new JsonObject();
            respuesta.addProperty("exito", resultado.isExito());
            respuesta.addProperty("mensaje", resultado.getMensaje());
            return gson.toJson(respuesta);
        });

        awaitInitialization();
        System.out.println("ApiServer escuchando en puerto " + puerto);
    }

    public void detener() {
        stop();
    }

    public static void main(String[] args) {
        new ApiServer().iniciar(PUERTO_DEFECTO);
    }
}