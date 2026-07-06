# Research: US-01 · Reserva de Cita en Línea 24/7

**Feature**: `specs/001-reserva-cita-online`
**Date**: 2026-07-05
**Status**: Complete — all NEEDS CLARIFICATION resolved

## Decisión 1: Control de concurrencia para reservas (FR-004)

**Decisión**: Bloqueo pesimista (`SELECT FOR UPDATE`) a nivel de base de datos sobre
`FranjaHoraria` durante la transacción de reserva, más una restricción UNIQUE en base de
datos como defensa en profundidad.

**Rationale**: En un sistema de salud, una reserva doble tiene impacto directo en la
atención del paciente. El bloqueo pesimista garantiza que dos transacciones concurrentes
sobre la misma franja nunca produzcan dos reservas exitosas. El bloqueo optimista (con
`@Version`) requeriría lógica de reintentos y sigue siendo vulnerable a condiciones de
carrera en entornos con alta concurrencia.

**Alternativas consideradas**:
- Bloqueo optimista con `@Version`: rechazado — la lógica de reintentos aumenta la
  complejidad y aún puede producir reservas dobles si el manejo es incorrecto.
- Solo restricción UNIQUE en BD: rechazado como solución única — no retroalimenta al
  usuario con franjas alternativas de forma controlada; sólo lanza una excepción de BD.

**Implementación**: Spring Data JPA con `@Lock(LockModeType.PESSIMISTIC_WRITE)` en el
método de repositorio que obtiene la franja antes de confirmar la reserva.

---

## Decisión 2: Integración de notificaciones WhatsApp (FR-005)

**Decisión**: Puerto de salida `NotificacionGatewayPort` (interfaz en la capa de
aplicación). La implementación concreta se inyecta en el adaptador de mensajería
(`adapter/out/messaging/WhatsAppNotificacionAdapter`). La notificación se envía de forma
asíncrona tras confirmar la cita para no bloquear la respuesta al paciente.

**Rationale**: Clean Architecture exige que el caso de uso no conozca detalles de
WhatsApp. El puerto desacopla la lógica de negocio de la infraestructura de mensajería.
La ejecución asíncrona garantiza que el registro de la cita no falle si el servicio de
WhatsApp tiene latencia temporal.

**Alternativas consideradas**:
- Llamada directa a SDK de WhatsApp desde el caso de uso: rechazado — viola el
  Principio I de la constitución (dependencia hacia afuera).
- Patrón Outbox + cola de mensajes: considerado para producción; para el alcance actual
  de esta historia, `@Async` de Spring es suficiente. El outbox puede incorporarse si el
  SLA de entrega requiere garantías de exactamente-una-vez.

**Implementación**: Interfaz `NotificacionGatewayPort` en `application/port/out/`.
Adaptador `WhatsAppNotificacionAdapter` anotado con `@Async` para envío no bloqueante.
Un stub/mock del adaptador se usará en pruebas unitarias e integración.

---

## Decisión 3: openapi-generator con Gradle y Spring Boot 4.x

**Decisión**: Plugin `org.openapi.generator` versión 7.x para Gradle. Generador `spring`.
Opciones: `interfaceOnly=true`, `useSpringBoot3=true` (Spring Boot 4.x = Spring 6.x,
mismas anotaciones), `useTags=true`. El código generado va a `build/generated/`.

**Rationale**: `interfaceOnly=true` genera solo las interfaces Java de la API. Los
controladores implementan esas interfaces, lo que mantiene el control en manos del
desarrollador y evita herencia forzada de clases generadas.

**Alternativas consideradas**:
- Generación de clases abstractas (`interfaceOnly=false`): rechazado — obliga a extender
  clases generadas, lo que dificulta la implementación de Clean Architecture.
- Swagger Codegen (v2): rechazado — mantenimiento limitado; openapi-generator es el fork
  activo y más completo.

**Build config relevante**:
```groovy
openApiGenerate {
    generatorName = "spring"
    inputSpec = "$rootDir/src/main/resources/openapi/citasalud-api.yml"
    outputDir = "$buildDir/generated"
    apiPackage = "org.ups.citasaludservice.adapter.in.web.api"
    modelPackage = "org.ups.citasaludservice.adapter.in.web.dto"
    configOptions = [
        interfaceOnly: "true",
        useSpringBoot3: "true",
        useTags: "true"
    ]
}
```

---

## Decisión 4: JaCoCo — configuración para Clean Architecture

**Decisión**: Excluir el paquete generado por openapi-generator de los cálculos de
cobertura. Dos umbrales independientes: por clase (>80%) y global (≥80%).

**Rationale**: El código generado no es responsabilidad del equipo y no puede ser
modificado. Incluirlo en la cobertura distorsionaría las métricas reales del código de
negocio.

**Configuración relevante**:
```groovy
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit { minimum = 0.80 }  // global line coverage
        }
        rule {
            element = 'CLASS'
            excludes = [
                'org.ups.citasaludservice.adapter.in.web.dto.*',
                'org.ups.citasaludservice.adapter.in.web.api.*',
                'org.ups.citasaludservice.infrastructure.config.*'
            ]
            limit { minimum = 0.80 }  // per-class line coverage
        }
    }
}
```

---

## Decisión 5: Cucumber-JVM con Spring Boot 4.x y JUnit 5

**Decisión**: `io.cucumber:cucumber-spring` + `io.cucumber:cucumber-junit-platform-engine`
para integración con JUnit 5. Feature files en `src/test/resources/features/`. Step
definitions en `src/test/java/.../functional/steps/`.

**Rationale**: Cucumber-Spring permite levantar el contexto de Spring para pruebas
funcionales. La integración con JUnit Platform es compatible con Gradle `test` task y
con el reporte de JaCoCo.

**Configuración de test runner**:
```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,
    value = "org.ups.citasaludservice.functional")
public class CucumberTestRunner {}
```

**Alternativas consideradas**:
- JBehave: rechazado — ecosistema menos activo, integración con Spring Boot más verbosa.
- RestAssured para pruebas funcionales HTTP sin Cucumber: puede complementar pero no
  reemplaza la expresividad de los escenarios Gherkin requeridos por el Principio II.
