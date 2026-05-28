# 4. Usar Oracle Database 26ai y Vector Search para duplicados

Fecha: 2026-05-27

## Estado

Aceptada

## Contexto

Oracle Java Bot debe ayudar al manager a mejorar la calidad del backlog detectando tareas potencialmente duplicadas dentro de un proyecto.

Inicialmente, la detección de duplicados podía resolverse mediante un microservicio externo de IA o mediante comparación semántica fuera de la base de datos. Sin embargo, el sistema ya utiliza Oracle Database como fuente principal de verdad y la arquitectura objetivo busca aprovechar capacidades nativas de Oracle para reducir acoplamiento, mejorar trazabilidad y mantener los resultados dentro del ecosistema de datos principal.

La base de datos actual incluye estructuras especializadas para embeddings y resultados de detección vectorial, entre ellas:

- `TASK_VECTOR_EMBEDDING`
- `AI_VECTOR_DUP_DETECTION_RUN`
- `AI_VECTOR_DUP_RESULT`

La tabla `TASK_VECTOR_EMBEDDING` almacena embeddings asociados a tareas y proyectos. Las tablas `AI_VECTOR_DUP_DETECTION_RUN` y `AI_VECTOR_DUP_RESULT` permiten registrar ejecuciones de análisis, parámetros, estado, resultados, similitud y distancia.

## Decisión

Se decide utilizar **Oracle Database 26ai** como motor principal de persistencia y análisis semántico para la detección de tareas duplicadas.

La detección de duplicados se basará en:

1. Generación o actualización de embeddings vectoriales cuando una tarea se crea o modifica.
2. Persistencia de embeddings en `TASK_VECTOR_EMBEDDING`.
3. Ejecución de búsqueda semántica usando Oracle Vector Search.
4. Registro de cada ejecución en `AI_VECTOR_DUP_DETECTION_RUN`.
5. Registro de pares de tareas similares en `AI_VECTOR_DUP_RESULT`.
6. Exposición de resultados al manager mediante el backend y el dashboard.

El AI Service en Python no será el motor principal de comparación para duplicados en la arquitectura objetivo. Su responsabilidad principal se mantiene en la generación de backlog asistida por IA a partir de documentos.

## Consecuencias

### Positivas

- Reduce dependencia de servicios externos para una capacidad crítica del sistema.
- Mantiene embeddings, resultados y trazabilidad dentro de Oracle Database 26ai.
- Permite consultar resultados históricos de detección por proyecto y ejecución.
- Facilita auditoría, revisión y análisis posterior.
- Disminuye movimiento de datos sensibles fuera de la base principal.
- Alinea el proyecto con tecnologías Oracle, coherente con el contexto del producto.
- Permite que el backend gestione el flujo completo de detección sin delegar comparación semántica a un servicio externo.

### Negativas

- Aumenta dependencia tecnológica hacia Oracle Database 26ai.
- Requiere diseño cuidadoso de índices, tamaño de embeddings y umbrales de similitud.
- El equipo debe entender operaciones vectoriales y sus implicaciones de rendimiento.
- Cambios futuros en modelo de embeddings pueden requerir backfill o migraciones.
- La generación de embeddings debe mantenerse consistente cuando se crean o modifican tareas.

## Alternativas consideradas

### Detección con LLM directo

Se descartó como estrategia principal porque puede ser más costosa, menos trazable y más difícil de auditar. Además, cada análisis dependería de llamadas externas y prompts, lo que complica reproducibilidad.

### Detección mediante microservicio Python con embeddings externos

Se consideró porque permite aislar lógica de IA. Sin embargo, se descartó como motor principal para duplicados porque mueve datos fuera de la base y duplica responsabilidades que Oracle 26ai puede manejar nativamente.

### Comparación textual tradicional

Se descartó porque la similitud léxica no captura adecuadamente duplicados semánticos cuando las tareas usan diferentes palabras para describir el mismo trabajo.

## Resultado

Oracle Java Bot utilizará Oracle Database 26ai y Oracle Vector Search como base de la detección semántica de tareas duplicadas. El backend Spring Boot orquestará la generación de embeddings, ejecución de búsqueda, persistencia de resultados y exposición al dashboard.