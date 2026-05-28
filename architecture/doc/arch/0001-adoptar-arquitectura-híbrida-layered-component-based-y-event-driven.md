# 1. Adoptar arquitectura híbrida layered, component-based y event-driven

Date: 2026-05-28

## Status

Accepted

## Context

Oracle Java Bot es un sistema cloud-native orientado a mejorar la gestión de proyectos, tareas, visibilidad operativa, notificaciones y soporte de IA para equipos de desarrollo Java.

El sistema no se limita a operaciones CRUD. Además de gestionar usuarios, equipos, proyectos, sprints y tareas, debe soportar flujos asíncronos como generación de backlog con IA, notificaciones por Telegram, procesamiento de documentos y detección semántica de duplicados mediante Oracle Database 26ai.

La arquitectura debe ser mantenible, trazable, escalable y viable dentro del alcance académico del proyecto. También debe reflejar la estructura real de los repositorios existentes:

- `oracle-java-bot-backend`
- `oracle-java-bot-frontend`
- `oracle-java-bot-ai-service`
- `oracle-java-bot-testing`

El backend actual está organizado por features y capas internas, con paquetes como `auth`, `security`, `users`, `teams`, `projects`, `tasks`, `sprints`, `kpis`, `ai`, `bot` y `messaging`. Además, existen producers, consumers y eventos Kafka para desacoplar flujos asíncronos.

## Decision

Se adopta una arquitectura híbrida compuesta por los siguientes estilos:

1. **Layered Architecture**, aplicada dentro del backend Spring Boot mediante separación entre controllers, services, repositories, DTOs y models.

2. **Component-Based Architecture**, aplicada en la organización funcional del backend por capacidades del dominio: autenticación, seguridad, usuarios, equipos, proyectos, tareas, sprints, KPIs, IA, mensajería y persistencia.

3. **Event-Driven Architecture**, aplicada en los flujos asíncronos que utilizan Kafka, especialmente generación de backlog con IA, eventos de tarea y notificaciones.

4. **Microservices Architecture parcial**, aplicada únicamente a servicios independientes como AI Service y Telegram Bot Service. El sistema completo no se declara como microservicios puros, ya que el backend Spring Boot sigue actuando como núcleo modular del producto.

## Consequences

### Positivas

- La arquitectura refleja mejor la realidad del sistema y evita afirmar que todo el producto es microservicios puros.
- La separación por capas facilita mantenibilidad, pruebas unitarias y evolución del backend.
- La separación por componentes permite trazabilidad entre requisitos, componentes, código y pruebas.
- Kafka desacopla operaciones asíncronas y reduce dependencia temporal entre backend, AI Service y Telegram Bot Service.
- La arquitectura permite evolucionar progresivamente hacia servicios más independientes sin reescribir todo el sistema.

### Negativas

- El sistema requiere disciplina para evitar que el backend modular se convierta en un monolito desordenado.
- Kafka introduce complejidad operativa adicional.
- La separación parcial de servicios exige definir contratos claros entre backend, AI Service y Telegram Bot Service.
- La arquitectura híbrida requiere documentación explícita para evitar confusión entre estilos.

### Alternativas consideradas

#### Monolito tradicional

Se descartó porque concentraría demasiadas responsabilidades en un único despliegue y dificultaría la evolución independiente de IA, Telegram y procesamiento asíncrono.

#### Microservicios puros

Se descartó como estrategia inicial porque no se cuentan con los recursos económicos para desplegar una base de datos independiente por servicio. Además, aumentaría la complejidad de despliegue, observabilidad, contratos, versionamiento y operación. Para el alcance académico y estado actual del producto, sería sobredimensionado.

#### Arquitectura únicamente layered

Se descartó porque no representa adecuadamente los flujos asíncronos y distribuidos del sistema, especialmente Kafka, AI Service y Telegram Bot Service.

### Resultado

Oracle Java Bot utilizará una arquitectura híbrida layered, component-based y event-driven, con microservicios parciales para capacidades que justifican independencia operativa. Esta decisión permite balancear mantenibilidad, escalabilidad, trazabilidad y viabilidad de implementación.