# 3. Separar Telegram Bot Service del backend principal

Date: 2026-05-28

## Status

Accepted

## Context

Oracle Java Bot integra Telegram como canal conversacional para que los developers consulten tareas, actualicen avances y reciban notificaciones relacionadas con eventos del sistema.

En la implementación actual, el bot de Telegram se ejecuta dentro del backend Spring Boot principal. Este enfoque funciona correctamente en un despliegue simple, pero genera problemas cuando existen múltiples instancias del backend, especialmente bajo estrategias de Blue/Green Deployment o escalamiento horizontal en Kubernetes.

El principal problema identificado es el uso de long polling. Cuando dos o más instancias ejecutan simultáneamente el mismo bot, pueden competir por las actualizaciones recibidas desde Telegram, generar errores de concurrencia o producir comportamientos inconsistentes.

La arquitectura objetivo requiere que el backend pueda escalar de forma independiente, sin que el canal Telegram limite la disponibilidad, escalabilidad o estrategia de despliegue del sistema.

## Decision

Se decide separar la funcionalidad de Telegram en un servicio independiente denominado **Telegram Bot Service**.

El servicio será implementado como un componente independiente basado en Java y Spring Boot, separado del backend principal y utilizando un esquema basado en **webhooks** en lugar de long polling.

Las responsabilidades del Telegram Bot Service serán:

1. Recibir actualizaciones desde Telegram mediante webhook.
2. Interpretar comandos, mensajes y callbacks.
3. Administrar el estado conversacional de los usuarios.
4. Resolver identidad de usuarios mediante `telegramId`.
5. Consultar y actualizar información mediante APIs del backend principal.
6. Publicar y consumir eventos Kafka relacionados con notificaciones y automatizaciones.
7. Enviar respuestas y notificaciones utilizando la Telegram Bot API.

El backend Spring Boot conservará la lógica de negocio principal, persistencia, autorización y APIs del dominio. El Telegram Bot Service actuará únicamente como canal de interacción con Telegram.

## Consequences

### Positivas

* Permite escalar el backend principal sin conflictos derivados del uso de long polling.
* Hace viable la ejecución de múltiples instancias bajo Blue/Green Deployment y Kubernetes.
* Separa el canal conversacional de la lógica principal del sistema.
* Permite escalar Telegram Bot Service de manera independiente.
* Reduce el acoplamiento operativo entre Telegram y la aplicación web.
* Facilita la migración a webhooks, modelo más adecuado para arquitecturas distribuidas.
* Permite definir contratos claros entre Telegram Bot Service, Backend y Kafka.

### Negativas

* Introduce un servicio adicional que debe desplegarse, monitorearse y mantenerse.
* Requiere garantizar idempotencia en el procesamiento de actualizaciones provenientes de Telegram.
* Requiere configuración segura del webhook y exposición controlada mediante Ingress Controller.
* Incrementa la comunicación entre servicios y la necesidad de contratos estables.
* Implica refactorizar parte de la funcionalidad actualmente integrada en el backend principal.

### Alternativas consideradas

#### Mantener Telegram dentro del backend utilizando long polling

Se descartó porque impide escalar el backend de forma segura. Con múltiples instancias, las actualizaciones pueden procesarse de forma inconsistente y generar conflictos operativos.

#### Mantener Telegram dentro del backend deshabilitando el bot en instancias secundarias

Se descartó porque introduce configuraciones frágiles dependientes del entorno y mantiene el acoplamiento entre el backend y el canal conversacional.

#### Utilizar un servicio externo administrado para Telegram

Se consideró utilizar una plataforma externa para gestionar la interacción con Telegram. Esta alternativa fue descartada para conservar control sobre la lógica conversacional, integración con Kafka y comunicación con el backend.

### Resultado

La arquitectura objetivo separa Telegram en un **Telegram Bot Service** independiente, basado en Java/Spring Boot y comunicación mediante webhooks. Esta decisión mejora disponibilidad, escalabilidad, mantenibilidad y compatibilidad con estrategias modernas de despliegue como Blue/Green Deployment sobre OCI Kubernetes Engine.
