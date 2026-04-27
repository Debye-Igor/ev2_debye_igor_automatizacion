# ev2_debye_igor_automatizacion

Proyecto de automatización de pruebas con flujo de integración continua (CI). Implementa una clase Java simple con sus pruebas unitarias atómicas y un pipeline de GitHub Actions que compila y ejecuta los tests automáticamente en cada push y pull request.

## Objetivos

- Aplicar control de versiones con Git usando ramas y pull requests.
- Estructurar un proyecto Java siguiendo las convenciones de Maven.
- Implementar pruebas unitarias atómicas e independientes con JUnit 5.
- Configurar un pipeline de integración continua con GitHub Actions.
- Generar reportes de pruebas

## Stack tecnológico

| Herramienta | Versión | Uso |
|---|---|---|
| Java (Temurin) | 17 LTS | Lenguaje base |
| Maven | 3.9.15 | Gestión de dependencias y ciclo de build |
| JUnit Jupiter | 5.10.2 | Framework de pruebas unitarias |
| Maven Surefire | 3.2.5 | Plugin de ejecución de tests |
| Git | 2.48 | Control de versiones |
| GitHub Actions | — | Servidor de CI |

## Estructura del proyecto

```
ev2_debye_igor_automatizacion/
├── .github/
│   └── workflows/
│       └── ci.yml                  # Definición del pipeline de CI
├── src/
│   ├── main/java/cl/iplacex/automatizacion/
│   │   └── Calculadora.java        # Clase bajo prueba
│   └── test/java/cl/iplacex/automatizacion/
│       └── CalculadoraTest.java    # 6 pruebas unitarias atómicas
├── .gitignore
├── pom.xml                         # Configuración de Maven
└── README.md
```

La estructura sigue la convención estándar de Maven: el código de producción vive en `src/main/java` y los tests en `src/test/java`, ambos espejando el mismo paquete `cl.iplacex.automatizacion`. Esto mantiene la correspondencia 1 a 1 entre cada clase y su clase de prueba.

## Cómo correr el proyecto localmente

### Requisitos

- Java 17 o superior (probado con Temurin 17.0.16)
- Maven 3.6 o superior
- Git

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/Debye-Igor/ev2_debye_igor_automatizacion.git
cd ev2_debye_igor_automatizacion

# 2. Compilar
mvn clean compile

# 3. Ejecutar las pruebas
mvn test
```

Resultado esperado:

```
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Pruebas unitarias

La clase `CalculadoraTest` cubre seis casos para los métodos `sumar` y `restar`:

| Test | Caso cubierto |
|---|---|
| `sumarDosNumerosPositivos` | Operación con dos enteros positivos |
| `sumarConCero` | Sumando con cero (elemento neutro) |
| `sumarNumerosNegativos` | Suma de dos enteros negativos |
| `restarDosNumerosPositivos` | Diferencia positiva |
| `restarMismoNumeroDaCero` | Resta de un número consigo mismo |
| `restarDaResultadoNegativo` | Diferencia que resulta en negativo |

Cada test instancia su propia `Calculadora`, lo que asegura **atomicidad**: ningún test depende del estado dejado por otro, y el orden de ejecución es irrelevante.

## Pipeline de Integración Continua

El pipeline está definido en `.github/workflows/ci.yml` y se dispara automáticamente cuando ocurre cualquiera de estos eventos:

- Push a la rama `main`
- Apertura o actualización de un Pull Request hacia `main`

### Etapas del pipeline

1. **Checkout del repositorio** — descarga el código en el runner.
2. **Configurar JDK 17** — instala Temurin 17 con cache de Maven.
3. **Compilar con Maven** — `mvn -B compile`.
4. **Ejecutar pruebas unitarias** — `mvn -B test`.
5. **Publicar reporte de Surefire** — sube los XML de resultados como artifact descargable, incluso si los tests fallan.

### Reporte navegable

Después de cada ejecución, el reporte de pruebas queda disponible en la pestaña **Actions** del repositorio, dentro de la sección **Artifacts** del workflow correspondiente. El archivo `surefire-reports.zip` contiene los XML detallados con cada test, su tiempo de ejecución y su estado.

## Flujo de trabajo con Git

El proyecto se desarrolló siguiendo un flujo basado en ramas:

- `main` mantiene el código estable.
- Cada bloque de funcionalidad se desarrolla en una rama `feature/...`.
- Los cambios se integran a `main` mediante Pull Request.

Ramas creadas durante el desarrollo:

- `feature/unit-tests-calculadora` — incorporación de las pruebas unitarias.
- `feature/ci-pipeline` — configuración del workflow de GitHub Actions.
- `feature/readme` — documentación del proyecto.

