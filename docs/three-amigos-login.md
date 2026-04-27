# Sesión Three Amigos: Funcionalidad de Login

## Información de la sesión

- **Fecha simulada:** 25 de abril de 2026
- **Duración:** 45 minutos
- **Funcionalidad a definir:** Login de usuario en aplicación web
- **Participantes:**
  - Vallery Zamora — Product Owner
  - Debye Igor — Desarrollador
  - Mathias Igor — QA / Automatización

## Contexto

El equipo necesita acordar el comportamiento del módulo de autenticación antes de comenzar el desarrollo. La sesión busca alinear las expectativas de negocio, las restricciones técnicas y los casos de prueba críticos.

## Aportes por rol

### Product Owner — Vallery Zamora

> "Necesitamos que un usuario registrado pueda ingresar con su correo y contraseña, y que el sistema valide rápidamente. Si las credenciales son correctas, debería entrar al dashboard. Si son incorrectas, queremos un mensaje claro pero que no dé pistas a un atacante sobre si el usuario existe o no."

Prioridades de negocio:
- Tiempo de respuesta menor a 500ms en condiciones normales.
- Mensaje de error genérico ("credenciales inválidas") por seguridad.
- No bloquear cuenta tras intentos fallidos en esta primera versión (queda para iteración futura).

### Desarrollador — Debye Igor

> "Técnicamente puedo implementar la validación contra una base de usuarios en memoria para esta versión. Las contraseñas se compararán como string por simplicidad en este sprint, pero quedará registrado como deuda técnica para usar hashing en la siguiente iteración."

Restricciones técnicas:
- API REST simple con un endpoint `POST /login`.
- Respuesta 200 si las credenciales son válidas, 401 en caso contrario.
- El servicio retorna un objeto `{ "exito": true/false, "mensaje": "..." }`.

### QA — Mathias Igor

> "Quiero asegurarme de cubrir el caso de login exitoso, las credenciales incorrectas, y por lo menos un caso de borde como password vacío. También me preocupa que el endpoint pueda responder distinto si el usuario existe pero la password es incorrecta vs si el usuario no existe — eso podría filtrar información."

Casos de prueba a cubrir:
- Login exitoso con credenciales válidas.
- Login fallido con password incorrecta.
- Login fallido con usuario inexistente.
- Login con campos vacíos.

## Criterios de aceptación

Después de discutir, el equipo acuerda los siguientes criterios:

1. **CA-1:** Dado un usuario registrado, cuando ingresa su email y password correctos, el sistema responde con éxito y mensaje "Bienvenido".
2. **CA-2:** Cuando las credenciales son inválidas (sea por usuario inexistente, password incorrecta o ambos), el sistema responde con error y mensaje genérico "Credenciales inválidas".
3. **CA-3:** Cuando alguno de los campos está vacío, el sistema responde con mensaje "Email y password son obligatorios".

## Ejemplos discutidos

| Email | Password | Resultado esperado | Mensaje |
|---|---|---|---|
| `mathias@iplacex.cl` | `secreto123` | éxito | Bienvenido |
| `mathias@iplacex.cl` | `cualquiera` | error | Credenciales inválidas |
| `noexiste@x.cl` | `loquesea` | error | Credenciales inválidas |
| (vacío) | `secreto123` | error | Email y password son obligatorios |
| `mathias@iplacex.cl` | (vacío) | error | Email y password son obligatorios |

Estos ejemplos serán la base de los escenarios Gherkin a implementar.

## Decisiones de la sesión

- El mensaje de error es **genérico** por motivos de seguridad (no revelar si el usuario existe).
- Se usará una lista de usuarios en memoria para esta primera versión, no base de datos.
- El bloqueo por intentos fallidos queda fuera del alcance de esta iteración.
- La validación de formato del email queda como mejora futura, no se valida en este sprint.

## Próximos pasos

1. Debye implementa el servicio `ServicioAutenticacion` y el endpoint REST.
2. Mathias escribe los escenarios Gherkin y los step definitions con Cucumber.
3. La integración corre automáticamente en el pipeline de CI.