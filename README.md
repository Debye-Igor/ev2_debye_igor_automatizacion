# ev2_debye_igor_automatizacion

Proyecto de automatización de pruebas con flujo de integración continua. Implementa una clase Java simple con sus pruebas unitarias atómicas, una funcionalidad de login con cobertura BDD, pruebas de carga y un pipeline de GitHub Actions que compila y ejecuta todo automáticamente en cada push y pull request.

## Objetivos

- Aplicar control de versiones con Git usando ramas y pull requests.
- Estructurar un proyecto Java siguiendo las convenciones de Maven.
- Implementar pruebas unitarias atómicas e independientes con JUnit 5.
- Cubrir una funcionalidad de negocio con escenarios BDD usando Cucumber.
- Medir el rendimiento del endpoint de login con pruebas de carga en k6.
- Configurar un pipeline de integración continua con GitHub Actions.
- Generar reportes de pruebas accesibles para el equipo.

## Stack tecnológico

| Herramienta | Versión | Uso |
|---|---|---|
| Java (Temurin) | 17 LTS | Lenguaje base |
| Maven | 3.9.15 | Gestión de dependencias y ciclo de build |
| JUnit Jupiter | 5.10.2 | Framework de pruebas unitarias |
| Maven Surefire | 3.2.5 | Plugin de ejecución de tests |
| Cucumber Java | 7.18.1 | Framework BDD |
| Cucumber JUnit Platform | 7.18.1 | Integración de Cucumber con JUnit 5 |
| Spark Java | 2.9.4 | Servidor HTTP embebido para el endpoint de login |
| Gson | 2.11.0 | Serialización JSON |
| k6 | 1.7.x | Pruebas de carga |
| Git | 2.48 | Control de versiones |
| GitHub Actions | — | Servidor de CI |

## Estructura del proyecto

```
ev2_debye_igor_automatizacion/
├── .github/
│   └── workflows/
│       └── ci.yml                          # Definición del pipeline de CI
├── docs/
│   ├── three-amigos-login.md               # Acta de la sesión Three Amigos
│   └── performance-monitoring.md           # Diseño de dashboards y alertas
├── performance/
│   └── login-load-test.js                  # Script de carga con k6
├── src/
│   ├── main/java/cl/iplacex/automatizacion/
│   │   ├── Calculadora.java                # Clase bajo prueba (Actividad 1)
│   │   ├── ServicioAutenticacion.java      # Lógica de validación de credenciales
│   │   └── ApiServer.java                  # Endpoint POST /login con Spark
│   └── test/
│       ├── java/cl/iplacex/automatizacion/
│       │   ├── CalculadoraTest.java        # 6 pruebas unitarias de Calculadora
│       │   ├── ServicioAutenticacionTest.java  # 5 pruebas del servicio
│       │   ├── RunCucumberTest.java        # Runner de Cucumber con JUnit 5
│       │   └── steps/
│       │       └── LoginSteps.java         # Step definitions BDD
│       └── resources/features/
│           └── login.feature               # Escenarios Gherkin en español
├── .gitignore
├── pom.xml
└── README.md
```

La estructura sigue la convención estándar de Maven: el código de producción vive en `src/main/java` y los tests en `src/test/java`, ambos espejando el mismo paquete `cl.iplacex.automatizacion`. Los archivos `.feature` están en `src/test/resources/features/` que es la ruta donde Cucumber los busca por defecto. Las pruebas de carga viven fuera de Maven, en `performance/`, porque k6 no se integra al ciclo `mvn test`.

## Cómo correr el proyecto localmente

### Requisitos

- Java 17 o superior
- Maven 3.6 o superior
- Git
- k6 (solo para las pruebas de carga, opcional)

### Compilar y correr todas las pruebas (unitarias y BDD)

```bash
git clone https://github.com/Debye-Igor/ev2_debye_igor_automatizacion.git
cd ev2_debye_igor_automatizacion
mvn clean compile
mvn test
```

Resultado esperado:

```
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Los 16 tests son: 6 de Calculadora, 5 de ServicioAutenticacion y 5 de Cucumber (1 escenario simple + 4 ejemplos del scenario outline).

### Levantar el servidor manualmente

```bash
mvn exec:java "-Dexec.mainClass=cl.iplacex.automatizacion.ApiServer"
```

El endpoint queda disponible en `http://localhost:4567/login` y acepta peticiones POST con JSON `{"email":"...", "password":"..."}`.

## Pruebas unitarias

### CalculadoraTest

Cubre seis casos para los métodos `sumar` y `restar`:

| Test | Caso cubierto |
|---|---|
| `sumarDosNumerosPositivos` | Operación con dos enteros positivos |
| `sumarConCero` | Sumando con cero |
| `sumarNumerosNegativos` | Suma de dos enteros negativos |
| `restarDosNumerosPositivos` | Diferencia positiva |
| `restarMismoNumeroDaCero` | Resta de un número consigo mismo |
| `restarDaResultadoNegativo` | Diferencia que resulta en negativo |

### ServicioAutenticacionTest

Valida la lógica del servicio de login de forma aislada (sin levantar HTTP):

| Test | Caso cubierto |
|---|---|
| `credencialesValidasRetornanExito` | Login correcto |
| `passwordIncorrectaRetornaError` | Password mala |
| `usuarioInexistenteRetornaMismoErrorGenerico` | Mensaje genérico por seguridad |
| `emailVacioRetornaCamposObligatorios` | Validación de campo vacío |
| `passwordVacioRetornaCamposObligatorios` | Validación de campo vacío |

Cada test instancia su propia clase bajo prueba. Ningún test depende del estado dejado por otro, y el orden de ejecución es irrelevante.

## Pruebas BDD con Cucumber

La funcionalidad de login se valida también con escenarios BDD escritos en Gherkin, en español. Esto permite que el acta de la sesión Three Amigos (`docs/three-amigos-login.md`) y los escenarios ejecutables compartan el mismo vocabulario.

### Escenarios cubiertos

El archivo `src/test/resources/features/login.feature` contiene:

- 1 escenario simple: login exitoso con credenciales válidas.
- 1 esquema del escenario con 4 ejemplos:
  - Password incorrecta
  - Usuario inexistente
  - Email vacío
  - Password vacío

### Cómo se ejecutan

Los escenarios BDD se corren con el mismo `mvn test` que las pruebas unitarias. La clase `RunCucumberTest` actúa como puente entre Cucumber y JUnit 5.

Antes de cada suite, los step definitions levantan el `ApiServer` en el puerto 4567 (`@BeforeAll`). Las peticiones HTTP se hacen con el cliente nativo de Java 17 (`java.net.http.HttpClient`), sin necesidad de agregar otra dependencia.

### Reporte navegable

Cada ejecución genera el archivo `target/cucumber-report.html`, que se publica como artifact descargable en el pipeline de CI.

## Pruebas de performance con k6

Las pruebas de carga sobre el endpoint `POST /login` están en `performance/login-load-test.js`.

### Configuración del escenario

- Rampa de 10 segundos subiendo a 10 usuarios virtuales.
- 30 segundos sostenidos con 10 VUs.
- Descenso de 10 segundos hasta 0.
- Mezcla de credenciales: 70% válidas, 30% inválidas, para reflejar tráfico realista.

### Métricas monitoreadas

- TPS (peticiones por segundo).
- Latencia en percentiles p50, p90, p95 y p99.
- Tasa de errores HTTP (excluyendo 401, que son respuestas válidas del dominio).
- Métricas custom: `login_exitoso`, `login_fallido`, `tiempo_respuesta_login_ms`.

### Resultados de referencia

En la corrida final con el endpoint corriendo localmente:

- p95 de latencia: ~1 ms
- TPS sostenido: ~27 req/s
- Tasa de errores HTTP: 0%
- Total de peticiones: ~1370 en 50 segundos

En máquinas con más recursos los números podrían ser distintos. Los thresholds están conservadores a propósito.

### Sobre la primera corrida

La primera ejecución del script reportó el threshold `http_req_failed` en 28.45%, lo que aparentaba ser un fallo. Tras revisar los datos, se identificó que k6 contaba los HTTP 401 como errores cuando en realidad son la respuesta correcta para credenciales inválidas (el 30% del tráfico simulado). El script se ajustó con `http.expectedStatuses(200, 401)` para alinear las métricas con el dominio, y la corrida posterior mostró ambos thresholds en verde.

### Cómo ejecutarlas

```bash
# Terminal 1: levantar el servidor
mvn exec:java "-Dexec.mainClass=cl.iplacex.automatizacion.ApiServer"

# Terminal 2: ejecutar la prueba de carga
k6 run performance/login-load-test.js
```

## Monitoreo y alertas

El documento `docs/performance-monitoring.md` describe cómo se conectarían las métricas capturadas por k6 a un sistema de monitoreo en producción: diseño conceptual del dashboard, reglas de alertas con sus umbrales y canales, e integración con el pipeline de CI.

No se implementa el dashboard real (Grafana, Prometheus, etc.) porque escapaba al alcance del taller, pero el documento sirve como diseño que puede llevarse a la práctica con cualquier stack de monitoreo conocido.

## Pipeline de Integración Continua

El pipeline está definido en `.github/workflows/ci.yml` y se dispara automáticamente cuando:

- Se hace push a la rama `main`.
- Se abre o actualiza un Pull Request hacia `main`.

### Etapas del pipeline

1. Checkout del repositorio.
2. Configurar JDK 17 (Temurin) con cache de Maven.
3. Compilar con Maven (`mvn -B compile`).
4. Ejecutar pruebas unitarias y BDD (`mvn -B test`).
5. Publicar el reporte de Surefire como artifact.
6. Publicar el reporte HTML de Cucumber como artifact.

### Reportes navegables

Cada ejecución sube dos artifacts a la pestaña **Actions** del repositorio:

- `surefire-reports`: XML detallados de cada test (Junit, Cucumber).
- `cucumber-html-report`: reporte HTML navegable con cada escenario, sus pasos y tiempos de ejecución.

Ambos quedan disponibles para descarga durante 30 días después de cada corrida.

## Flujo de trabajo con Git

El proyecto se desarrolló siguiendo un flujo basado en ramas:

- `main` mantiene el código estable.
- Cada bloque de funcionalidad se desarrolla en una rama `feature/...`.
- Los cambios se integran a `main` mediante Pull Request.
- Los PR disparan el pipeline antes de mergear.

Ramas creadas durante el desarrollo:

- `feature/unit-tests-calculadora` — pruebas unitarias de Calculadora.
- `feature/ci-pipeline` — configuración inicial del workflow.
- `feature/docs-iniciales` — README y acta Three Amigos.
- `feature/login-bdd` — servicio de autenticación, endpoint REST, BDD y reporte Cucumber en pipeline.
- `feature/performance` — pruebas de carga con k6 y diseño de monitoreo.

## Decisiones de diseño

**Por qué JUnit 5 y no JUnit 4.** JUnit 5 es la versión actual del framework y es la que se trabaja en el material de estudio. Ofrece anotaciones más expresivas como `@DisplayName`, mejor manejo de pruebas parametrizadas y arquitectura modular.

**Por qué Spark Java en vez de Spring Boot.** El alcance del taller solo requería un endpoint POST. Spark Java permite levantar un servidor HTTP en menos de diez líneas, sin la sobrecarga de configurar un proyecto Spring. Para un sistema más amplio Spring sería la opción correcta, pero aquí habría agregado complejidad sin valor.

**Por qué k6 en vez de JMeter.** k6 ofrece scripts en JavaScript con sintaxis directa y resultados profesionales con mínima configuración. JMeter habría requerido aprender su GUI y su estructura XML, gastando tiempo que era más útil dedicar al análisis de resultados.

**Por qué `@BeforeAll` y no `@Before` en los step definitions.** Spark Java mantiene un singleton estático del servidor que no permite reiniciarlo varias veces dentro del mismo proceso JVM. Levantar el servidor una sola vez para toda la suite resuelve el problema sin sacrificar la idempotencia, porque las peticiones individuales no comparten estado relevante entre escenarios.

**Por qué GitHub Actions y no Jenkins.** Para un proyecto pequeño y de aprendizaje, Actions es nativo de GitHub, no requiere infraestructura adicional, y el historial queda visible públicamente en el mismo lugar que el código.


