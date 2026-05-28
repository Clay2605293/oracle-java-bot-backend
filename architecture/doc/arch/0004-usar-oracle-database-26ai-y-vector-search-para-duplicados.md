# 4. Usar Oracle Database 26ai y Vector Search para duplicados

Date: 2026-05-28

## Status

Accepted

## Context

Oracle Java Bot debe ayudar al manager a mejorar la calidad del backlog detectando tareas potencialmente duplicadas dentro de un proyecto.

Inicialmente, la detección de duplicados se implementó mediante servicios externos de inteligencia artificial y análisis semántico fuera de la base de datos. Sin embargo, el sistema utiliza Oracle Database como fuente principal de verdad y la arquitectura objetivo busca aprovechar capacidades nativas de Oracle para reducir acoplamiento, mejorar trazabilidad y mantener los resultados dentro del ecosistema principal de datos.

La base de datos actual incluye estructuras especializadas para embeddings y resultados de detección vectorial, entre ellas:

* `TASK_VECTOR_EMBEDDING`
* `AI_VECTOR_DUP_DETECTION_RUN`
* `AI_VECTOR_DUP_RESULT`

La tabla `TASK_VECTOR_EMBEDDING` almacena embeddings asociados a tareas y proyectos. Las tablas `AI_VECTOR_DUP_DETECTION_RUN` y `AI_VECTOR_DUP_RESULT` permiten registrar ejecuciones de análisis, parámetros, estado, resultados, similitud y distancia entre tareas.

El sistema requiere una solución capaz de identificar similitud semántica entre tareas aun cuando se utilicen diferentes palabras o estructuras para describir el mismo trabajo.

## Decision

Se decide utilizar **Oracle Database 26ai** como plataforma principal para almacenamiento de embeddings, búsqueda vectorial y persistencia de resultados relacionados con la detección de tareas duplicadas.

La detección de duplicados se basará en:

1. Generación o actualización de embeddings cuando una tarea se crea o modifica.
2. Persistencia de embeddings en `TASK_VECTOR_EMBEDDING`.
3. Ejecución de búsquedas semánticas utilizando Oracle Vector Search.
4. Registro de cada ejecución en `AI_VECTOR_DUP_DETECTION_RUN`.
5. Registro de pares de tareas similares en `AI_VECTOR_DUP_RESULT`.
6. Exposición de resultados mediante el backend y los dashboards del sistema.

El backend Spring Boot será responsable de orquestar el flujo completo de detección. El AI Service en Python dejará de ser el mecanismo principal para comparación semántica de duplicados y se enfocará principalmente en capacidades de generación asistida de backlog y procesamiento documental.

La arquitectura también deja abierta la posibilidad de migrar progresivamente la generación de embeddings hacia capacidades nativas de Oracle AI cuando dichas funcionalidades formen parte de la solución objetivo.

## Consequences

### Positivas

* Reduce la dependencia de servicios externos para una capacidad crítica del sistema.
* Mantiene embeddings, resultados y trazabilidad dentro de Oracle Database 26ai.
* Permite consultar resultados históricos por proyecto y ejecución.
* Facilita auditoría, revisión y análisis posterior de decisiones tomadas por el sistema.
* Reduce movimiento innecesario de datos fuera de la base principal.
* Alinea el proyecto con tecnologías Oracle, coherente con el contexto del producto.
* Permite que el backend gestione el flujo completo de detección sin depender de un motor externo para cada análisis.
* Simplifica la arquitectura al concentrar almacenamiento, búsqueda y resultados dentro de una misma plataforma.

### Negativas

* Incrementa la dependencia tecnológica hacia Oracle Database 26ai.
* Requiere diseño cuidadoso de índices vectoriales, dimensiones de embeddings y umbrales de similitud.
* El equipo debe adquirir conocimientos sobre búsqueda vectorial y optimización de consultas semánticas.
* Cambios futuros en modelos de embeddings pueden requerir reprocesamiento masivo de información.
* La calidad de los resultados dependerá de la calidad y consistencia de los embeddings generados.

### Alternativas consideradas

#### Detección utilizando LLMs directamente

Se descartó como estrategia principal porque incrementa costos operativos, dificulta la trazabilidad de resultados y depende de llamadas externas para cada análisis realizado.

#### Detección mediante microservicio Python especializado

Se consideró porque permite aislar la lógica de inteligencia artificial. Sin embargo, se descartó como mecanismo principal porque duplica responsabilidades que Oracle Database 26ai puede ejecutar de manera nativa y obliga a mover datos fuera de la plataforma principal.

#### Comparación textual tradicional

Se descartó porque la similitud léxica no captura adecuadamente relaciones semánticas entre tareas que describen el mismo trabajo utilizando vocabularios diferentes.

### Resultado

Oracle Java Bot utilizará Oracle Database 26ai y Oracle Vector Search como base de la detección semántica de tareas duplicadas. El backend Spring Boot orquestará la generación de embeddings, ejecución de búsquedas vectoriales, persistencia de resultados y exposición de información al dashboard del sistema.
