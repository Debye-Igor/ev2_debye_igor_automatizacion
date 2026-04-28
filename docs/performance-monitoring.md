# Monitoreo de Performance: dashboards y alertas

Este documento describe cómo se monitorearían en producción los indicadores capturados por la prueba de carga (`performance/login-load-test.js`), y cómo se configurarían las alertas para detectar regresiones de forma temprana.

## Indicadores monitoreados

La prueba con k6 captura cuatro grupos de métricas relevantes para el endpoint `POST /login`:

### 1. Throughput (TPS)
- **Métrica k6:** `http_reqs` (total y por segundo)
- **Resultado obtenido:** ~27 req/s sostenidos con 10 usuarios virtuales concurrentes
- **Por qué importa:** mide la capacidad del sistema para atender carga. Una caída sostenida indica problemas de capacidad o un cuello de botella en el servicio.

### 2. Latencia
- **Métricas k6:** `http_req_duration` con percentiles p50, p90, p95, p99
- **Resultado obtenido:** p95 = 0.97 ms, p99 = ~5 ms, máximo = 5.19 ms
- **Por qué importa:** los promedios mienten. Los percentiles altos (p95, p99) reflejan la experiencia real de los usuarios "peor servidos". Es la métrica que más impacto tiene en la percepción de calidad.

### 3. Tasa de errores
- **Métrica k6:** `http_req_failed`
- **Resultado obtenido:** 0.00% de errores genuinos (tras alinear los códigos esperados con el dominio)
- **Por qué importa:** distinguir entre errores técnicos (5xx, timeouts) y respuestas válidas del dominio (401 con credenciales inválidas) es crítico para no falsificar las alarmas.

### 4. Iteraciones y duración
- **Métricas k6:** `iterations`, `iteration_duration`
- **Por qué importa:** muestra el flujo completo del usuario (request + think time), no solo la latencia HTTP. Útil para detectar problemas que solo aparecen bajo cierto patrón de uso.

## Diseño conceptual del dashboard

El dashboard de monitoreo se diseñaría con **cuatro paneles** principales, alineados a los indicadores anteriores:

| Panel | Tipo de visualización | Métrica de origen | Qué responde |
|---|---|---|---|
| **TPS en tiempo real** | Gráfico de líneas | `http_reqs` por segundo | ¿El sistema está atendiendo el volumen esperado? |
| **Latencia por percentil** | Gráfico de líneas multi-serie (p50, p95, p99) | `http_req_duration` | ¿La experiencia de usuario es estable? |
| **Tasa de errores** | Gráfico de área + número grande | `http_req_failed` | ¿Hay errores genuinos en producción? |
| **Distribución de status** | Gráfico de barras apiladas (200, 401, 500, etc.) | Conteo por código HTTP | ¿Cómo se distribuye el tráfico entre éxitos, fallos esperados y fallos reales? |

### Stack tecnológico propuesto (no implementado en este taller)

Para una implementación productiva, el flujo sería:

1. **k6 → InfluxDB**: k6 puede exportar métricas en tiempo real con `--out influxdb=http://...`
2. **InfluxDB → Grafana**: Grafana consume los datos y renderiza los paneles
3. **Grafana → Slack/Email/PagerDuty**: cuando una métrica cruza un umbral, dispara una notificación

Alternativas equivalentes: Prometheus + Grafana, Datadog, New Relic, ELK Stack.

## Reglas de alertas

Las alertas se configuran sobre métricas que reflejan **degradación percibible** por el usuario, no sobre cualquier variación menor. La idea es minimizar la fatiga de alertas (alerta solo cuando hay un problema real).

| Métrica | Umbral | Severidad | Canal | Acción esperada |
|---|---|---|---|---|
| Latencia p95 | > 500 ms durante 5 min | Warning | Slack `#monitoreo` | Revisar logs, verificar carga |
| Latencia p95 | > 1 s durante 2 min | Critical | Slack `#oncall` + email | Investigación inmediata |
| Tasa de errores 5xx | > 1% durante 1 min | Critical | Slack `#oncall` + email | Posible incidente, despertar al on-call |
| TPS | Caída > 50% vs línea base | Warning | Slack `#monitoreo` | Revisar disponibilidad de servicios dependientes |
| Threshold de k6 cruzado en CI | Cualquier `✗` | Warning | Comentario en PR | Bloquear merge hasta análisis |

### Notas de diseño

- Los umbrales se calibran a partir de la **línea base** medida en pruebas como la de este taller. Por ejemplo, si la latencia p95 normal es ~1 ms, alertar a 500 ms (500x sobre la base) es razonablemente conservador.
- **Las alertas Critical se escalan automáticamente** si no se reconocen en 10 min: van a un segundo on-call, luego al líder técnico.
- **Las alertas Warning no despiertan a nadie de noche**, solo se revisan en horario laboral salvo que haya un patrón sostenido.

## Integración con el pipeline de CI

Las pruebas de performance pueden integrarse al pipeline de varias formas según el costo aceptado:

| Estrategia | Frecuencia | Ventajas | Desventajas |
|---|---|---|---|
| **En cada PR** | Por cada cambio | Detecta regresiones inmediatamente | Costoso (50s+ por corrida) |
| **Programada (nightly)** | 1 vez al día | Bajo costo, suficiente para detección de tendencias | Detección tardía de regresiones |
| **Manual (botón)** | A demanda | Flexibilidad | Depende de la disciplina del equipo |
| **En staging pre-deploy** | Antes de desplegar a producción | Detecta antes de que el problema llegue al usuario | Atrasa el deploy |

Para este proyecto, la estrategia más razonable sería **nightly + manual a demanda**: una corrida automática diaria que documente la tendencia, y la posibilidad de dispararla desde un PR cuando el cambio sea sospechoso de afectar performance.

Si un threshold falla en una corrida nightly, el resultado se publica como **comentario automático en un issue de GitHub** y se notifica por Slack al canal `#monitoreo`.

## Conclusión

Las métricas que captura k6 (TPS, latencia, errores) son la base sobre la que se construye un sistema de monitoreo confiable. El dashboard hace que esas métricas sean **visibles y accionables**, y las alertas hacen que sean **proactivas**, no esperamos a que un usuario reporte un problema para enterarnos estamos en constante monitoreo del sitema.

La iteración con los thresholds en este taller (alinear los HTTP 401 con el dominio)me hizo darme cuenta que las métricas por si mismas no dicen mucho si no se entiende el dominio del sistema.