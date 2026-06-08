# Arquitectura Oracle Java Bot

Este directorio contiene el modelo de arquitectura C4 del sistema Oracle Java Bot, desarrollado con Structurizr DSL como parte del entregable académico de Architecture Model.

El modelo representa la arquitectura objetivo de producción del sistema, considerando despliegue en OCI Kubernetes Engine en la OCI Región Querétaro, distribución en tres Fault Domains, servicios críticos replicados, Kafka autogestionado, Oracle Database 26ai, OCI Object Storage, integración con Telegram, OpenAI, Jira y un flujo de despliegue Blue/Green con quality gates.

## Estructura

```text
architecture/
├── README.md
├── model.dsl
├── workspace.dsl
├── model.json
└── doc/
    └── arch/
        ├── 0001-adoptar-arquitectura-híbrida-layered-component-based-y-event-driven.md
        ├── 0002-desplegar-en-oke-con-tres-fault-domains.md
        ├── 0003-separar-telegram-bot-service-del-backend-principal.md
        ├── 0004-usar-oracle-database-26ai-y-vector-search-para-duplicados.md
        └── 0005-usar-blue-green-deployment-con-quality-gates-y-jira.md

## Sprint 5 — Automatización de diagramas Level 4 con PlantUML

### Objetivo

En este sprint se automatizó la generación de diagramas de código **C4 Level 4** a partir del código fuente Java del backend de Oracle Java Bot. El propósito fue cerrar la brecha entre la arquitectura documentada en Structurizr y la implementación real del sistema, evitando que la documentación arquitectónica quede desactualizada respecto al código.

Esta automatización permite que los diagramas de clases PlantUML se regeneren desde el código Java y se mantengan sincronizados mediante el pipeline de GitHub Actions.

### Alcance implementado

La generación automática se configuró sobre el backend Spring Boot, donde se concentran los principales componentes funcionales del sistema. Los diagramas se generan en la carpeta:

```text
docs/diagrams/
```

Los diagramas Level 4 generados son:

```text
ai-backlog-generation-component.puml
authentication-component.puml
authorization-security-component.puml
dashboard-kpi-component.puml
messaging-component.puml
project-management-component.puml
semantic-duplicate-detection-component.puml
sprint-management-component.puml
task-management-component.puml
team-management-component.puml
user-management-component.puml
vector-embedding-component.puml
```

Cada archivo representa un subconjunto del código Java asociado a un componente arquitectónico del modelo C4 Level 3.

### Integración con Maven

Se agregó el plugin `plantuml-generator-maven-plugin` al `pom.xml` para generar archivos `.puml` desde los paquetes Java del backend.

La generación se ejecuta en la fase:

```text
process-classes
```

Esta fase fue seleccionada porque ocurre después de la compilación de las clases Java. De esta forma, los diagramas se generan con base en clases, métodos, campos y relaciones disponibles en el código compilado, reduciendo el riesgo de artefactos incompletos.

Para generar los diagramas localmente se utiliza:

```bash
./mvnw -DskipTests process-classes
```

### Integración con GitHub Actions

Se creó el workflow:

```text
.github/workflows/generate-code-diagrams.yml
```

Este pipeline se ejecuta cuando hay cambios en:

```text
src/main/java/**
pom.xml
.github/workflows/generate-code-diagrams.yml
```

El flujo automatizado realiza los siguientes pasos:

1. Obtiene el código fuente del repositorio.
2. Configura Java 21.
3. Ejecuta Maven para compilar y generar los diagramas PlantUML.
4. Actualiza automáticamente los archivos `.puml` generados en `docs/diagrams`.

Esto permite que la documentación Level 4 se mantenga sincronizada con el código Java cada vez que cambia la implementación.

### Relación con Structurizr y C4

El modelo arquitectónico principal se mantiene en:

```text
architecture/model.dsl
```

Los componentes C4 Level 3 del backend fueron enlazados con los diagramas Level 4 generados mediante la propiedad `url` de Structurizr. Esto permite navegar desde el componente arquitectónico hacia el archivo PlantUML correspondiente en GitHub.

Ejemplo:

```dsl
authComponent = component "Authentication Component" "Valida credenciales, emite tokens JWT y habilita el acceso inicial al sistema." "Spring MVC Controller / Service" {
    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/authentication-component.puml"
}
```

Con esta integración, la arquitectura documentada en C4 queda conectada directamente con la implementación real del backend.

### Decisiones de alcance

Algunos componentes C4 no tienen un archivo `.puml` exclusivo porque su implementación actual está distribuida dentro de otros paquetes funcionales:

* **Assignment Component** se representa dentro de `task-management-component.puml`, ya que sus clases reales corresponden a `TaskUser*` dentro del paquete de tareas.
* **Document Storage Component** se representa dentro de `project-management-component.puml`, ya que sus clases reales corresponden a `ProjectDocument*` y servicios de almacenamiento dentro del paquete de proyectos.
* **Persistence Component** no se generó como diagrama Level 4 independiente porque es una responsabilidad transversal implementada mediante entidades y repositorios distribuidos en los componentes funcionales.

Esta decisión evita generar diagramas artificiales o demasiado grandes, manteniendo la documentación alineada con la estructura real del código.

### Validación

La solución fue validada mediante los siguientes comandos:

```bash
structurizr-cli validate -workspace architecture/model.dsl
./mvnw -q -DskipTests compile
./mvnw -DskipTests process-classes
wc -l docs/diagrams/*.puml
```

La validación confirmó que:

* El modelo Structurizr DSL es válido.
* El backend compila correctamente.
* Los diagramas PlantUML se generan automáticamente.
* Existen 12 diagramas Level 4 en `docs/diagrams`.
* El pipeline de GitHub Actions ejecuta correctamente la generación automática.

### Valor arquitectónico

Esta automatización fortalece la trazabilidad entre arquitectura e implementación. En lugar de mantener diagramas de clases manuales, el sistema genera los diagramas Level 4 desde el código fuente real, reduciendo inconsistencias y facilitando auditoría, mantenimiento y evolución futura del sistema.

Desde una perspectiva de ingeniería de software, esta práctica mejora la mantenibilidad documental, apoya la gestión de configuración y permite que la arquitectura evolucione junto con el producto.
