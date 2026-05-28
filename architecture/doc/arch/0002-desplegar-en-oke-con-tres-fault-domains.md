# 2. Desplegar en OKE con tres Fault Domains

Fecha: 2026-05-27

## Estado

Aceptada

## Contexto

Oracle Java Bot debe operar como una solución cloud-native en Oracle Cloud Infrastructure. El sistema requiere disponibilidad, escalabilidad horizontal, separación de responsabilidades operativas y capacidad de despliegue controlado.

La arquitectura objetivo debe representar un ambiente de producción en la OCI Región Querétaro, utilizando OCI Kubernetes Engine como plataforma principal para ejecutar servicios contenerizados. Además, el sistema debe distribuir sus componentes críticos en tres Fault Domains para reducir el impacto de fallas físicas o lógicas dentro del Availability Domain.

Los servicios críticos incluyen:

- Spring Boot Backend.
- Java/Spring Boot Telegram Bot Service.
- Python/FastAPI AI Service.
- Kafka Cluster autogestionado.
- Zookeeper Ensemble.
- Nginx / Ingress Controller.

Oracle Database 26ai y OCI Object Storage se mantienen como servicios administrados externos al cluster, ya que no deben ejecutarse como pods dentro de Kubernetes.

## Decisión

Se adopta OCI Kubernetes Engine como plataforma objetivo de producción para Oracle Java Bot, desplegado en la OCI Región Querétaro y distribuido en tres Fault Domains.

La arquitectura de despliegue considera:

1. Un cluster OKE de producción.
2. Worker nodes distribuidos en Fault Domain 1, Fault Domain 2 y Fault Domain 3.
3. Réplicas del backend Spring Boot en los tres Fault Domains.
4. Réplicas del Telegram Bot Service en los tres Fault Domains, usando webhook en lugar de long polling.
5. Réplicas del AI Service en los tres Fault Domains.
6. Kafka autogestionado con brokers distribuidos entre los tres Fault Domains.
7. Zookeeper distribuido entre los tres Fault Domains para coordinación de Kafka.
8. OCI Load Balancer como punto de entrada público.
9. Nginx / Ingress Controller para enrutar tráfico hacia los servicios internos.
10. Oracle Database 26ai como servicio administrado para persistencia transaccional y vector search.
11. OCI Object Storage como servicio administrado para documentos de proyecto.
12. OCI Container Registry para almacenar imágenes versionadas.

## Consecuencias

### Positivas

- Mejora la disponibilidad al distribuir servicios críticos entre tres Fault Domains.
- Permite escalabilidad horizontal de backend, AI Service y Telegram Bot Service.
- Reduce el riesgo de interrupción completa ante falla de un nodo o Fault Domain.
- Facilita estrategias de despliegue como Blue/Green y futuras estrategias rolling/canary.
- Alinea el sistema con una arquitectura cloud-native más cercana a un entorno empresarial real.
- Permite separar servicios administrados, como Oracle Database 26ai y Object Storage, de la capa de cómputo.

### Negativas

- Aumenta la complejidad operativa frente a un despliegue simple en una VM con Podman.
- Kafka autogestionado en Kubernetes requiere monitoreo, persistencia, configuración de red y estrategia de recuperación.
- El equipo debe administrar manifests, réplicas, configuración, secretos y health checks.
- Para el alcance académico, parte de la arquitectura representa el estado objetivo, no necesariamente el estado actual completamente implementado.

## Alternativas consideradas

### OCI Compute con Podman

Se consideró mantener el despliegue en una instancia OCI Compute usando Podman. Esta opción es más simple y coincide mejor con el estado actual, pero no permite representar adecuadamente alta disponibilidad, distribución en tres Fault Domains ni escalabilidad horizontal robusta.

### Kubernetes local o Docker Compose

Se descartó porque no cumple con los requisitos del cliente ya que para producción porque no representa una arquitectura cloud-native empresarial ni soporta adecuadamente la distribución en Fault Domains.

### Servicios administrados para todo el runtime

Se consideró reemplazar Kafka por un servicio administrado como OCI Streaming y reducir operación propia. Se descartó para esta arquitectura objetivo porque el sistema ya usa Kafka y se decidió mantenerlo autogestionado para conservar compatibilidad con los flujos actuales.

## Resultado

Oracle Java Bot tendrá como arquitectura objetivo de producción un despliegue en OCI Kubernetes Engine dentro de la OCI Región Querétaro, distribuido en tres Fault Domains. Los servicios críticos se replicarán para soportar disponibilidad y escalabilidad, mientras que Oracle Database 26ai y OCI Object Storage se mantendrán como servicios administrados externos al cluster.