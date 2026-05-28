# 5. Usar Blue/Green deployment con quality gates y Jira

Fecha: 2026-05-27

## Estado

Aceptada

## Contexto

Oracle Java Bot requiere un proceso de despliegue que reduzca el riesgo de introducir fallas en producción. El sistema contiene funcionalidades críticas para gestión de proyectos, tareas, KPIs, notificaciones, generación de backlog asistida por IA y detección semántica de duplicados.

El despliegue manual o directo sobre una única instancia activa puede generar indisponibilidad, regresiones no detectadas y dificultad para realizar rollback. Además, el equipo ya cuenta con automatización de DevOps, pruebas de regresión y scripts para crear tickets cuando un quality gate falla.

La arquitectura objetivo considera ejecución en OCI Kubernetes Engine, con entrada mediante OCI Load Balancer y Nginx / Ingress Controller. Esto permite aplicar una estrategia Blue/Green donde una versión candidata se despliega y valida antes de recibir tráfico productivo.

## Decisión

Se adopta una estrategia de **Blue/Green deployment** con quality gates automatizados y registro de fallas en Jira.

El flujo de despliegue será:

1. Un developer o DevOps Engineer realiza push o merge hacia una rama protegida.
2. GitHub y GitHub Actions preparan los artefactos necesarios, incluyendo el frontend React/Vite embebido en el backend.
3. OCI DevOps obtiene el código fuente actualizado.
4. OCI DevOps construye y publica imágenes Docker versionadas en OCI Container Registry.
5. OCI DevOps despliega la versión candidata en OKE.
6. El CI/CD Orchestrator ejecuta health checks.
7. El Regression Test Runner ejecuta pruebas automatizadas sobre endpoints críticos.
8. Si los quality gates pasan, Nginx / Ingress Controller promueve tráfico hacia la versión candidata.
9. Si algún quality gate falla, el tráfico no se promueve y se crea un ticket Jira con evidencia accionable.

## Consecuencias

### Positivas

- Reduce el riesgo de introducir fallas directamente en producción.
- Permite validar una versión candidata antes de exponerla a usuarios.
- Facilita rollback al conservar una versión estable disponible.
- Integra pruebas de regresión como parte obligatoria del proceso de despliegue.
- Genera trazabilidad operativa mediante tickets Jira cuando ocurre una falla.
- Mejora la confianza del equipo en releases frecuentes.
- Alinea la operación del sistema con prácticas profesionales de entrega continua.

### Negativas

- Requiere mantener dos versiones o colores durante el proceso de despliegue.
- Aumenta la complejidad de configuración de Nginx / Ingress Controller.
- Requiere pruebas automatizadas confiables; pruebas frágiles pueden bloquear despliegues válidos.
- Puede aumentar consumo temporal de recursos durante despliegues.
- Requiere disciplina para que los cambios de frontend, backend, AI Service y Telegram Bot Service sean versionados y promovidos correctamente.

## Alternativas consideradas

### Despliegue directo sobre producción

Se descartó porque expone a los usuarios a fallas inmediatamente después del despliegue y dificulta rollback controlado.

### Rolling deployment simple

Se consideró porque Kubernetes lo soporta de forma nativa. Sin embargo, se prefirió Blue/Green porque permite validar un ambiente candidato antes de mover tráfico, lo cual se alinea mejor con el quality gate y las pruebas automatizadas existentes.

### Canary deployment

Se consideró como alternativa avanzada. Se descartó para esta etapa porque requiere observabilidad, métricas de error y control gradual de tráfico más sofisticado. Puede evaluarse como evolución futura.

## Resultado

Oracle Java Bot utilizará Blue/Green deployment con quality gates automatizados. La versión candidata será validada mediante health checks y pruebas de regresión antes de recibir tráfico. Si la validación falla, se bloqueará la promoción y se registrará un ticket Jira con evidencia para seguimiento.