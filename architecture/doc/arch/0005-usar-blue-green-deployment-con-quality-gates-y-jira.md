# 5. Adoptar Blue/Green Deployment con quality gates automatizados

Date: 2026-05-28

## Status

Accepted

## Context

Oracle Java Bot requiere un proceso de despliegue que reduzca el riesgo de introducir fallas en producción. El sistema contiene funcionalidades críticas para gestión de proyectos, tareas, KPIs, notificaciones, generación de backlog asistida por IA y detección semántica de duplicados.

Los despliegues directos sobre una única versión activa pueden provocar indisponibilidad, regresiones no detectadas y dificultades para realizar rollback ante incidentes.

Además, el proyecto ya cuenta con automatización de DevOps, pruebas de regresión automatizadas y mecanismos de generación de evidencia cuando una validación falla.

La arquitectura objetivo considera ejecución sobre OCI Kubernetes Engine, utilizando OCI Load Balancer y Nginx / Ingress Controller como punto de entrada. Esto permite desplegar versiones candidatas y validarlas antes de exponerlas a usuarios finales.

## Decision

Se adopta una estrategia de **Blue/Green Deployment** complementada con quality gates automatizados dentro del pipeline de integración y despliegue continuo.

El flujo de despliegue contempla:

1. Un Developer o DevOps Engineer realiza un push o merge hacia una rama protegida.
2. GitHub y GitHub Actions preparan los artefactos necesarios, incluyendo el frontend React/Vite integrado dentro del backend.
3. OCI DevOps obtiene el código fuente actualizado.
4. OCI DevOps construye y publica imágenes Docker versionadas en OCI Container Registry.
5. OCI DevOps despliega una versión candidata dentro del entorno objetivo.
6. Se ejecutan health checks automáticos.
7. Se ejecutan pruebas automatizadas de regresión sobre funcionalidades críticas.
8. Si todos los quality gates son satisfactorios, el tráfico es promovido hacia la nueva versión.
9. Si algún quality gate falla, la promoción es bloqueada y la versión estable continúa atendiendo tráfico.

Como parte del proceso operativo, las fallas detectadas durante la validación podrán generar tickets de seguimiento en Jira con evidencia técnica asociada.

## Consequences

### Positivas

* Reduce significativamente el riesgo de introducir fallas directamente en producción.
* Permite validar una versión candidata antes de exponerla a usuarios finales.
* Facilita rollback al mantener una versión estable disponible durante el despliegue.
* Integra pruebas automatizadas como parte obligatoria del proceso de liberación.
* Mejora la trazabilidad operativa mediante generación automática de evidencia.
* Incrementa la confianza del equipo para realizar despliegues frecuentes.
* Alinea el sistema con prácticas modernas de Continuous Delivery.

### Negativas

* Requiere mantener simultáneamente dos versiones durante el proceso de despliegue.
* Incrementa la complejidad de configuración de Nginx, Ingress Controller y mecanismos de promoción de tráfico.
* Requiere pruebas automatizadas confiables; pruebas inestables pueden bloquear despliegues válidos.
* Puede aumentar temporalmente el consumo de recursos durante cada despliegue.
* Exige disciplina de versionamiento entre frontend, backend, AI Service y Telegram Bot Service.

### Alternativas consideradas

#### Despliegue directo sobre producción

Se descartó porque expone inmediatamente a los usuarios a posibles errores y dificulta la recuperación ante fallas.

#### Rolling Deployment

Se consideró porque Kubernetes lo soporta de manera nativa. Sin embargo, fue descartado como estrategia principal porque no proporciona un entorno completamente aislado para validar una versión candidata antes de recibir tráfico.

#### Canary Deployment

Se consideró como evolución futura. Fue descartado para esta etapa porque requiere observabilidad más avanzada, métricas de negocio y mecanismos sofisticados de control gradual del tráfico.

### Resultado

Oracle Java Bot utilizará Blue/Green Deployment con quality gates automatizados como estrategia principal de despliegue. Las nuevas versiones serán validadas mediante health checks y pruebas de regresión antes de recibir tráfico productivo, reduciendo riesgos operativos y mejorando la confiabilidad de las liberaciones.
