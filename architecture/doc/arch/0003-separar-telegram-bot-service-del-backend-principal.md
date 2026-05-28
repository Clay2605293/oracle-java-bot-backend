# 3. Separar Telegram Bot Service del backend principal

Fecha: 2026-05-27

## Estado

Aceptada

## Contexto

Oracle Java Bot integra Telegram como canal conversacional para que los developers consulten tareas, actualicen avances y reciban notificaciones relacionadas con eventos del sistema.

En la implementación actual, el bot de Telegram se ejecuta dentro del backend Spring Boot principal. Este enfoque funciona en un despliegue simple, pero genera problemas cuando existen múltiples instancias del backend, especialmente bajo una estrategia Blue/Green o bajo escalamiento horizontal en Kubernetes.

El problema principal es el uso de long polling. Cuando dos o más instancias del backend ejecutan simultáneamente el mismo bot con long polling, pueden competir por las actualizaciones de Telegram, generar errores de concurrencia o provocar comportamiento inconsistente. Esto limita la capacidad de replicar el backend y afecta directamente la estrategia de alta disponibilidad.

La arquitectura objetivo requiere que el backend pueda escalar de forma independiente y que el canal Telegram pueda operar sin bloquear despliegues Blue/Green ni réplicas en tres Fault Domains.

## Decisión

Se decide separar la funcionalidad de Telegram en un container independiente llamado **Telegram Bot Service**.

Este servicio será modelado como un **Java/Spring Boot Telegram Bot Service**, separado del backend principal, y usará un esquema basado en **webhook** en lugar de long polling.

El servicio tendrá las siguientes responsabilidades:

1. Recibir actualizaciones de Telegram mediante webhook.
2. Interpretar comandos, mensajes y callbacks.
3. Administrar estado conversacional.
4. Resolver identidad de usuarios mediante `telegramId`.
5. Consultar o actualizar tareas y proyectos a través del backend principal.
6. Publicar y consumir eventos Kafka relacionados con notificaciones.
7. Enviar respuestas y notificaciones mediante Telegram Bot API.

El backend Spring Boot conservará la lógica de negocio principal, persistencia, autorización y APIs de proyectos/tareas. El Telegram Bot Service actuará como canal de interacción, no como dueño de la lógica central del dominio.

## Consecuencias

### Positivas

- Permite escalar el backend principal sin conflictos por long polling.
- Hace viable Blue/Green deployment con múltiples instancias del backend.
- Separa el canal conversacional Telegram de la lógica principal del sistema.
- Permite escalar Telegram Bot Service de forma independiente.
- Reduce el acoplamiento operativo entre interacción por Telegram y API Web.
- Facilita migrar a webhook, que es más adecuado para despliegues replicados.
- Permite definir contratos claros entre Telegram Bot Service, backend y Kafka.

### Negativas

- Introduce un servicio adicional que debe desplegarse, monitorearse y versionarse.
- Requiere asegurar idempotencia en el procesamiento de updates de Telegram.
- Requiere configuración segura de webhook y exposición controlada mediante Ingress.
- Agrega comunicación entre servicios, lo que aumenta la necesidad de contratos estables.
- Puede requerir ajustes en el código actual, ya que parte de la funcionalidad vive dentro del backend.

## Alternativas consideradas

### Mantener Telegram dentro del backend con long polling

Se descartó porque impide escalar el backend de forma segura. Con múltiples instancias, el bot puede competir por actualizaciones y generar comportamiento inconsistente.

### Mantener Telegram dentro del backend pero desactivar el bot en réplicas secundarias

Se descartó porque crea una configuración frágil y dependiente del ambiente. Además, no elimina el acoplamiento entre backend y canal conversacional.

### Usar un servicio externo administrado para el bot

Se consideró, pero se descartó para mantener control sobre la lógica conversacional, integración con Kafka y comunicación con el backend.

## Resultado

La arquitectura objetivo separa Telegram en un **Java/Spring Boot Telegram Bot Service** independiente, basado en webhook y desplegado como servicio replicable dentro de OKE. Esta decisión mejora disponibilidad, escalabilidad y compatibilidad con Blue/Green deployment.