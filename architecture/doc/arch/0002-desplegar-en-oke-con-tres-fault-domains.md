# 2. Desplegar en OKE con tres Fault Domains

Date: 2026-05-28

## Status

Accepted

## Context

Oracle Java Bot debe operar como una solución cloud-native en Oracle Cloud Infrastructure. El sistema requiere disponibilidad, escalabilidad horizontal, separación de responsabilidades operativas y capacidad de despliegue controlado.

La arquitectura objetivo debe representar un ambiente de producción en la OCI Región Querétaro, utilizando OCI Kubernetes Engine (OKE) como plataforma principal para ejecutar servicios contenerizados. Además, el sistema debe distribuir sus componentes críticos en tres Fault Domains para reducir el impacto de fallas físicas o lógicas dentro del Availability Domain.

Los servicios críticos incluyen:

* Spring Boot Backend
* Java/Spring Boot Telegram Bot Service
* Python/FastAPI AI Service
* Kafka Cluster autogestionado
* Zookeeper Ensemble
* Nginx / Ingress Controller

Oracle Database 26ai y OCI Object Storage se mantienen como servicios administrados externos al cluster, ya que no deben ejecutarse como pods dentro de Kubernetes.

La arquitectura debe representar el estado objetivo de producción del sistema, alineado con prácticas cloud-native empresariales y con la evolución prevista del proyecto.

## Decision

Se adopta OCI Kubernetes Engine (OKE) como plataforma objetivo de producción para Oracle Java Bot, desplegado en la OCI Región Querétaro y distribuido en tres Fault Domains.

La arquitectura de despliegue considera:

1. Un cluster OKE de producción.
2. Worker nodes distribuidos en Fault Domain 1, Fault Domain 2 y Fault Domain 3.
3. Réplicas del backend Spring Boot en los tres Fault Domains.
4. Réplicas del Telegram Bot Service en los tres Fault Domains utilizando webhook en lugar de long polling.
5. Réplicas del AI Service en los tres Fault Domains.
6. Kafka autogestionado con brokers distribuidos entre los tres Fault Domains.
7. Zookeeper distribuido entre los tres Fault Domains para coordinación de Kafka.
8. OCI Load Balancer como punto de entrada público.
9. Nginx / Ingress Controller para enrutar tráfico hacia los servicios internos.
10. Oracle Database 26ai como servicio administrado para persistencia transaccional y vector search.
11. OCI Object Storage como servicio administrado para almacenamiento de documentos de proyecto.
12. OCI Container Registry para almacenar imágenes versionadas de los servicios.

## Consequences

### Positivas

* Mejora la disponibilidad al distribuir servicios críticos entre tres Fault Domains.
* Permite escalabilidad horizontal del backend, AI Service y Telegram Bot Service.
* Reduce el riesgo de interrupción completa ante la falla de un nodo o Fault Domain.
* Facilita estrategias de despliegue como Blue/Green y futuras estrategias Rolling Update o Canary Deployment.
* Alinea el sistema con una arquitectura cloud-native más cercana a un entorno empresarial real.
* Permite separar claramente la capa de cómputo de los servicios administrados de datos y almacenamiento.

### Negativas

* Aumenta la complejidad operativa frente a un despliegue simple basado en OCI Compute y Podman.
* Kafka autogestionado en Kubernetes requiere monitoreo, persistencia, configuración de red y estrategia de recuperación.
* El equipo debe administrar manifests, réplicas, secretos, configuración y health checks.
* Parte de la arquitectura representa el estado objetivo del producto y no necesariamente la implementación actual completa.
* Incrementa la necesidad de observabilidad y monitoreo para detectar problemas de infraestructura distribuida.

### Alternativas consideradas

#### OCI Compute con Podman

Se consideró mantener el despliegue en una instancia OCI Compute utilizando Podman. Esta opción es más simple y coincide mejor con el estado actual del proyecto. Sin embargo, no permite representar adecuadamente alta disponibilidad, distribución en múltiples Fault Domains ni escalabilidad horizontal robusta.

#### Kubernetes local o Docker Compose

Se descartó porque no cumple los requisitos esperados para un ambiente de producción empresarial. No proporciona integración nativa con OCI ni soporta adecuadamente estrategias de disponibilidad basadas en Fault Domains.

#### Servicios administrados para todo el runtime

Se consideró reemplazar Kafka por un servicio administrado como OCI Streaming para reducir la carga operativa. Esta alternativa fue descartada porque el sistema ya utiliza Kafka y se decidió mantener compatibilidad con los flujos existentes, reduciendo el impacto de migración.

### Resultado

Oracle Java Bot tendrá como arquitectura objetivo de producción un despliegue en OCI Kubernetes Engine dentro de la OCI Región Querétaro, distribuido en tres Fault Domains. Los servicios críticos se replicarán para soportar disponibilidad y escalabilidad, mientras que Oracle Database 26ai y OCI Object Storage permanecerán como servicios administrados externos al cluster.
