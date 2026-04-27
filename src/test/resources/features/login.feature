# language: es
Característica: Login de usuario
  Como usuario registrado del sistema
  Quiero autenticarme con mi email y contraseña
  Para acceder a la aplicación

  Antecedentes:
    Dado que el servidor de autenticación está disponible

  Escenario: Login exitoso con credenciales válidas
    Cuando envío una solicitud de login con email "mathias@iplacex.cl" y password "secreto123"
    Entonces la respuesta tiene código 200
    Y el cuerpo de la respuesta indica éxito con mensaje "Bienvenido"

  Esquema del escenario: Login fallido por credenciales o campos vacíos
    Cuando envío una solicitud de login con email "<email>" y password "<password>"
    Entonces la respuesta tiene código 401
    Y el cuerpo de la respuesta indica fallo con mensaje "<mensaje>"

    Ejemplos:
      | email                | password    | mensaje                            |
      | mathias@iplacex.cl   | incorrecta  | Credenciales invalidas             |
      | nadie@iplacex.cl     | loquesea    | Credenciales invalidas             |
      |                      | secreto123  | Email y password son obligatorios  |
      | mathias@iplacex.cl   |             | Email y password son obligatorios  |