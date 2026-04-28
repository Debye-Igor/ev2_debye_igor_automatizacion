import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

/**
 * Prueba de carga sobre el endpoint POST /login.
 *
 * Simula usuarios concurrentes intentando autenticarse,combinando casos de exito y fallo para reflejar trafico
 * realista (no todos los login son exitosos en produccion).
 
 * Indicadores monitoreados:
 *   - TPS (transacciones por segundo)
 *   - Latencia: p50, p95, p99
 *   - Tasa de errores HTTP
 *   - Tasa de logins exitosos
 **/


// Metricas custom para enriquecer el reporte
const tasaLoginExitoso = new Rate('login_exitoso');
const tasaLoginFallido = new Rate('login_fallido');
const tiempoRespuestaLogin = new Trend('tiempo_respuesta_login_ms');

// Configuracion del escenario de carga:
// - rampa de 10s subiendo a 10 usuarios virtuales
// - 30s sostenidos con 10 usuarios
// - 10s bajando a 0
export const options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '30s', target: 10 },
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    // El 95% de las peticiones deben responder en menos de 500ms
    'http_req_duration': ['p(95)<500'],
    // Menos del 1% de errores HTTP
    'http_req_failed': ['rate<0.01'],
  },
};

// Le decimos a k6 que los status 200 y 401 son respuestas validas
// del dominio (200 = login exitoso, 401 = credenciales rechazadas).
// Los 5xx u otros si son errores reales del sistema.
const esperarStatusValido = http.expectedStatuses(200, 401);

const BASE_URL = 'http://localhost:4567';

// Mezcla de credenciales: 70% validas, 30% invalidas
const credenciales = [
  { email: 'mathias@iplacex.cl', password: 'secreto123', validas: true },
  { email: 'vallery@iplacex.cl', password: 'admin2026',  validas: true },
  { email: 'mathias@iplacex.cl', password: 'secreto123', validas: true },
  { email: 'mathias@iplacex.cl', password: 'secreto123', validas: true },
  { email: 'vallery@iplacex.cl', password: 'admin2026',  validas: true },
  { email: 'mathias@iplacex.cl', password: 'secreto123', validas: true },
  { email: 'mathias@iplacex.cl', password: 'secreto123', validas: true },
  { email: 'mathias@iplacex.cl', password: 'incorrecta', validas: false },
  { email: 'noexiste@x.cl',      password: 'loquesea',   validas: false },
  { email: '',                   password: 'algo',       validas: false },
];

export default function () {
  // Selecciona credenciales aleatorias del arreglo
  const cred = credenciales[Math.floor(Math.random() * credenciales.length)];

  const payload = JSON.stringify({
    email: cred.email,
    password: cred.password,
  });

  const params = {
    headers: { 'Content-Type': 'application/json' },
    responseCallback: esperarStatusValido,
  };

  const response = http.post(`${BASE_URL}/login`, payload, params);

  // Validaciones: el status esperado depende de si las creds son validas
  const statusEsperado = cred.validas ? 200 : 401;
  check(response, {
    'status correcto segun credenciales': (r) => r.status === statusEsperado,
    'respuesta tiene cuerpo JSON':         (r) => r.body && r.body.length > 0,
    'tiempo respuesta < 500ms':            (r) => r.timings.duration < 500,
  });

  // Registra metricas custom
  tiempoRespuestaLogin.add(response.timings.duration);
  if (cred.validas) {
    tasaLoginExitoso.add(response.status === 200);
  } else {
    tasaLoginFallido.add(response.status === 401);
  }

  // Espera entre 100ms y 500ms entre peticiones (think time del usuario)
  sleep(Math.random() * 0.4 + 0.1);
}