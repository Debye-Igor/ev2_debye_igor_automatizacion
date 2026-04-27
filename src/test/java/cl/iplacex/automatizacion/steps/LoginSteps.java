package cl.iplacex.automatizacion.steps;

import cl.iplacex.automatizacion.ApiServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Step definitions para la funcionalidad de login.
 
 * Estrategia: el ApiServer se levanta una sola vez antes de toda la
 * suite (BeforeAll) y se apaga al final (AfterAll). Esto es necesario
 * porque Spark Java mantiene un singleton estatico y no permite
 * reiniciar el servidor multiples veces dentro del mismo proceso JVM.
 
 * La idempotencia se preserva porque el ServicioAutenticacion no
 * mantiene estado mutable entre peticiones: cada llamada es
 * independiente.
 **/

public class LoginSteps {

    private static final int PUERTO_TEST = 4567;
    private static final String BASE_URL = "http://localhost:" + PUERTO_TEST;

    private static ApiServer servidor;
    private static HttpClient cliente;

    private HttpResponse<String> respuesta;

    @BeforeAll
    public static void levantarServidor() {
        servidor = new ApiServer();
        servidor.iniciar(PUERTO_TEST);
        cliente = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    }

    @AfterAll
    public static void detenerServidor() {
        if (servidor != null) {
            servidor.detener();
        }
    }

    @Dado("que el servidor de autenticación está disponible")
    public void elServidorEstaDisponible() {
        // Pre-condicion documentativa: el servidor ya fue levantado
        // en @BeforeAll antes de iniciar la suite de escenarios.
    }

    @Cuando("envío una solicitud de login con email {string} y password {string}")
    public void envioSolicitudLogin(String email, String password) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
            .build();

        respuesta = cliente.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Entonces("la respuesta tiene código {int}")
    public void laRespuestaTieneCodigo(int codigoEsperado) {
        assertEquals(codigoEsperado, respuesta.statusCode());
    }

    @Y("el cuerpo de la respuesta indica éxito con mensaje {string}")
    public void elCuerpoIndicaExitoConMensaje(String mensajeEsperado) {
        JsonObject json = JsonParser.parseString(respuesta.body()).getAsJsonObject();
        assertEquals(true, json.get("exito").getAsBoolean());
        assertEquals(mensajeEsperado, json.get("mensaje").getAsString());
    }

    @Y("el cuerpo de la respuesta indica fallo con mensaje {string}")
    public void elCuerpoIndicaFalloConMensaje(String mensajeEsperado) {
        JsonObject json = JsonParser.parseString(respuesta.body()).getAsJsonObject();
        assertEquals(false, json.get("exito").getAsBoolean());
        assertEquals(mensajeEsperado, json.get("mensaje").getAsString());
    }
}