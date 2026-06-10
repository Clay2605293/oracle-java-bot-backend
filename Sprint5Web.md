# Sprint 5 Web - GitHub Activity Dashboard Full Stack

## 1. Contexto de la actividad

La actividad original pedia desarrollar y mejorar un sistema Full Stack orientado a la gestion de productividad de desarrolladores, utilizando Spring Boot y React. La version adaptada al proyecto **Oracle Java Bot** consistio en integrar metricas reales de productividad tecnica usando GitHub como fuente de datos.

En lugar de crear un dashboard aislado, la mejora se integro directamente al dashboard de proyecto existente. El sistema ahora permite registrar, consultar y visualizar informacion relacionada con:

- commits por integrante;
- issues creados;
- issues actualmente activos;
- issues cerrados;
- actividad por sprint;
- actividad por repositorio;
- distribucion de contribuciones tecnicas por desarrollador;
- avance del proyecto, tareas, horas estimadas y horas reales.

La implementacion mantiene una separacion clara entre backend y frontend:

- el backend recibe eventos de GitHub, los persiste y expone metricas por REST y GraphQL;
- el frontend consume esas metricas, aplica filtros de dashboard y presenta graficas ejecutivas para managers.

---

## 2. Objetivo de la feature

El objetivo tecnico fue agregar una capa de metricas de productividad basada en GitHub para que un manager pueda visualizar evidencia tecnica del trabajo del equipo dentro del sistema.

La funcionalidad permite responder preguntas como:

- cuantos commits ha realizado cada integrante;
- quien abrio o cerro issues;
- que issues siguen activos;
- que repositorios tienen mayor actividad;
- en que sprint se concentro la actividad tecnica;
- si la actividad tecnica coincide con el avance reportado en tareas;
- si existe concentracion de trabajo en una persona o repositorio.

---

## 3. Arquitectura general Full Stack

La solucion se divide en tres flujos principales.

```text
Flujo 1: Captura de eventos GitHub

GitHub Webhook
-> POST /api/github/webhook
-> Validacion HMAC SHA-256
-> Procesamiento de eventos push/issues
-> Persistencia en Oracle DB
```

```text
Flujo 2: Consulta de metricas para dashboard

Frontend React
-> POST /graphql
-> Queries GraphQL GitHub y KPIs de proyecto
-> Servicios de metricas
-> Repositories SQL/JPA
-> Oracle DB
-> DTOs preparados para graficas
```

```text
Flujo 3: Visualizacion en frontend

React Router
-> Pagina de dashboard de proyecto
-> React Query hooks
-> Servicios REST/GraphQL
-> Transformacion ligera para UI
-> Cards KPI y graficas ECharts
```

Esta separacion es importante porque GitHub Webhooks requieren una URL HTTP fija para recibir eventos externos. Por eso se mantiene un endpoint REST para recepcion de eventos. En cambio, las consultas del dashboard se exponen mediante GraphQL para que el frontend solicite los campos necesarios para cada grafica.

---

## 4. Backend - estructura de carpetas GitHub

La feature se agrego bajo el paquete:

```text
src/main/java/com/oraclejavabot/features/github
```

Estructura principal:

```text
features/github
|-- controller
|   |-- GitHubWebhookController.java
|   |-- GitHubGraphQLController.java
|   `-- GitHubMetricsController.java
|
|-- dto
|   |-- GitHubContributionDTO.java
|   |-- GitHubSprintActivityDTO.java
|   `-- GitHubRepositoryActivityDTO.java
|
|-- model
|   |-- ProjectRepositoryEntity.java
|   |-- GitHubCommitEntity.java
|   `-- GitHubIssueEntity.java
|
|-- repository
|   |-- ProjectRepositoryJpaRepository.java
|   |-- GitHubCommitRepository.java
|   |-- GitHubIssueRepository.java
|   `-- GitHubContributionRepository.java
|
`-- service
    |-- GitHubSignatureService.java
    |-- GitHubWebhookService.java
    `-- GitHubMetricsService.java
```

Responsabilidades por capa:

| Capa | Responsabilidad |
|---|---|
| Controller | Recibir requests REST o GraphQL |
| Service | Coordinar reglas de negocio y procesamiento |
| Repository | Acceso a base de datos |
| DTO | Definir respuestas optimizadas para frontend |
| Model | Mapear tablas de Oracle DB mediante JPA |

---

## 5. Backend - configuracion GitHub

Se agrego configuracion para GitHub en `application.properties`:

```properties
github.api.base-url=https://api.github.com

github.tokens.oracle-java-bot-frontend=${GITHUB_TOKEN_FRONTEND}
github.tokens.oracle-java-bot-backend=${GITHUB_TOKEN_BACKEND}
github.tokens.oracle-java-bot-testing=${GITHUB_TOKEN_TESTING}
github.tokens.oracle-java-bot-ai-service=${GITHUB_TOKEN_AI_SERVICE}

github.webhook.secret=${GITHUB_WEBHOOK_SECRET:}
```

El valor mas importante para la recepcion segura de webhooks es:

```properties
github.webhook.secret=${GITHUB_WEBHOOK_SECRET:}
```

Ese secret se usa para validar que los eventos recibidos realmente vienen de GitHub. En OCI debe existir como variable de entorno dentro del contenedor activo:

```text
GITHUB_WEBHOOK_SECRET=oracle-java-bot-github-webhook-secret
```

Si el contenedor activo no tiene el secret correcto, el webhook responde:

```text
401 Invalid GitHub webhook signature
```

---

## 6. Backend - modelo de base de datos

La feature se apoya en tres tablas principales.

### 6.1 PROJECT_REPOSITORY

Guarda los repositorios asociados a un proyecto.

| Campo | Uso |
|---|---|
| REPOSITORY_ID | Identificador interno del repositorio |
| PROJECT_ID | Proyecto al que pertenece el repo |
| OWNER | Owner de GitHub |
| REPO_NAME | Nombre del repositorio |
| DISPLAY_NAME | Nombre visual opcional |
| DEFAULT_BRANCH | Rama principal, normalmente `main` |
| IS_ACTIVE | Indica si el repo esta activo para metricas |

Repositorios usados por el proyecto:

```text
oracle-java-bot-frontend
oracle-java-bot-backend
oracle-java-bot-testing
oracle-java-bot-ai-service
```

### 6.2 GITHUB_COMMIT

Guarda los commits recibidos desde GitHub.

| Campo | Uso |
|---|---|
| COMMIT_SHA | Identificador unico del commit |
| REPOSITORY_ID | Repo asociado |
| PROJECT_ID | Proyecto asociado |
| AUTHOR_USERNAME | Usuario GitHub que realizo el commit |
| AUTHOR_EMAIL | Email del autor |
| AUTHOR_NAME | Nombre del autor |
| COMMIT_MESSAGE | Mensaje del commit |
| COMMIT_DATE | Fecha real del commit en GitHub |
| BRANCH_NAME | Rama donde ocurrio |
| IS_MERGE_COMMIT | Marca si es merge commit |

La llave primaria es `COMMIT_SHA`, lo que evita duplicar commits si GitHub reenvia eventos o si el webhook se procesa mas de una vez.

### 6.3 GITHUB_ISSUE

Guarda los issues recibidos desde GitHub.

| Campo | Uso |
|---|---|
| ISSUE_ID | ID global del issue en GitHub |
| REPOSITORY_ID | Repo asociado |
| PROJECT_ID | Proyecto asociado |
| ISSUE_NUMBER | Numero visible del issue en el repo |
| TITLE | Titulo del issue |
| STATE | Estado actual: `open` o `closed` |
| AUTHOR_USERNAME | Usuario que creo el issue |
| CLOSED_BY_USERNAME | Usuario que cerro el issue |
| CREATED_AT_GITHUB | Fecha de creacion |
| CLOSED_AT_GITHUB | Fecha de cierre |
| IS_PULL_REQUEST | Marca para ignorar PRs tratados como issues |

El backend ignora eventos que correspondan a pull requests para no mezclar PRs con issues reales.

---

## 7. Backend - recepcion de eventos con REST Webhook

Archivo:

```text
GitHubWebhookController.java
```

Endpoint:

```http
POST /api/github/webhook
```

El endpoint recibe eventos enviados por GitHub. Soporta principalmente:

- `ping`;
- `push`;
- `issues`.

Flujo del controller:

1. Lee el header `X-GitHub-Event`.
2. Lee el header `X-Hub-Signature-256`.
3. Recibe el payload JSON del evento.
4. Valida la firma con `GitHubSignatureService`.
5. Si la firma es invalida, responde `401 Unauthorized`.
6. Si el evento es `ping`, responde confirmacion.
7. Si el evento es `push` o `issues`, delega el procesamiento a `GitHubWebhookService`.

Fragmento logico:

```java
if (!signatureService.isValidSignature(safePayload, signature)) {
  return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
      .body("Invalid GitHub webhook signature");
}

if ("push".equals(event) || "issues".equals(event)) {
  webhookService.processEvent(event, safePayload);
  return ResponseEntity.ok("GitHub " + event + " event processed");
}
```

---

## 8. Backend - seguridad con HMAC SHA-256

Archivo:

```text
GitHubSignatureService.java
```

Este servicio valida que el payload recibido coincida con la firma enviada por GitHub en:

```text
X-Hub-Signature-256
```

El backend calcula internamente:

```text
HMAC_SHA256(payload, GITHUB_WEBHOOK_SECRET)
```

Despues compara esa firma con la firma enviada por GitHub usando `MessageDigest.isEqual`, evitando comparaciones inseguras de strings. Esto evita que clientes externos puedan insertar commits o issues falsos en la base de datos.

---

## 9. Backend - procesamiento de eventos GitHub

Archivo:

```text
GitHubWebhookService.java
```

### 9.1 Evento push

Cuando GitHub envia un evento `push`, el backend:

1. Lee el payload JSON.
2. Extrae el repositorio (`owner` + `repoName`).
3. Busca el repositorio en `PROJECT_REPOSITORY`.
4. Extrae la rama desde `ref`.
5. Recorre el arreglo `commits`.
6. Omite commits sin SHA.
7. Omite commits ya existentes.
8. Guarda el commit en `GITHUB_COMMIT`.

Datos guardados:

- SHA del commit;
- repo asociado;
- proyecto asociado;
- autor GitHub;
- email;
- nombre;
- mensaje;
- rama;
- fecha del commit;
- URL del commit;
- si es merge commit.

### 9.2 Evento issues

Cuando GitHub envia un evento `issues`, el backend:

1. Lee el payload JSON.
2. Extrae el repositorio.
3. Busca el repo en `PROJECT_REPOSITORY`.
4. Extrae el objeto `issue`.
5. Ignora el evento si corresponde a un pull request.
6. Busca el issue por `ISSUE_ID`.
7. Si existe, lo actualiza.
8. Si no existe, lo crea.
9. Guarda estado, autor, assignee, fechas, URL y usuario que cerro el issue.

Esto permite que el backend mantenga el estado actual del issue, incluyendo si esta abierto o cerrado.

---

## 10. Backend - exposicion de metricas con GraphQL

Archivo:

```text
GitHubGraphQLController.java
```

El dashboard consume metricas mediante:

```http
POST /graphql
```

El schema actualizado contiene tres queries principales:

```graphql
githubContributions(projectId: ID!, sprintId: ID): [GitHubContribution!]!
githubSprintActivity(projectId: ID!): [GitHubSprintActivity!]!
githubRepositoryActivity(projectId: ID!, sprintId: ID, developerId: ID): [GitHubRepositoryActivity!]!
```

Estas queries fueron disenadas para alimentar directamente las graficas del dashboard.

---

## 11. Backend - queries GitHub

### 11.1 githubContributions

Devuelve actividad tecnica agrupada por integrante del equipo.

```graphql
query {
  githubContributions(projectId: "70BD1C78282D46BF913A573354FA3D55") {
    userId
    name
    email
    githubUsername
    totalCommits
    openedIssues
    activeIssues
    closedIssues
  }
}
```

Campos:

| Campo | Descripcion |
|---|---|
| userId | ID interno del usuario |
| name | Nombre completo |
| email | Correo del usuario |
| githubUsername | Usuario GitHub configurado |
| totalCommits | Commits realizados |
| openedIssues | Issues creados historicamente |
| activeIssues | Issues actualmente abiertos |
| closedIssues | Issues cerrados |

La query acepta `sprintId` opcional. Si se envia, limita commits e issues al rango de fechas del sprint seleccionado.

### 11.2 githubSprintActivity

Alimenta la grafica de actividad GitHub por sprint.

```graphql
query {
  githubSprintActivity(projectId: "70BD1C78282D46BF913A573354FA3D55") {
    sprintId
    sprintName
    totalCommits
    openedIssues
    closedIssues
  }
}
```

Agrupa los datos comparando fechas de GitHub contra los rangos definidos en la tabla `SPRINT`:

```text
COMMIT_DATE dentro de FECHA_INICIO y FECHA_FIN
CREATED_AT_GITHUB dentro de FECHA_INICIO y FECHA_FIN
CLOSED_AT_GITHUB dentro de FECHA_INICIO y FECHA_FIN
```

### 11.3 githubRepositoryActivity

Devuelve actividad tecnica por repositorio.

```graphql
query {
  githubRepositoryActivity(projectId: "70BD1C78282D46BF913A573354FA3D55") {
    repositoryId
    owner
    repoName
    totalCommits
    openedIssues
    activeIssues
    closedIssues
  }
}
```

Campos:

| Campo | Descripcion |
|---|---|
| repositoryId | ID interno del repo |
| owner | Owner del repo en GitHub |
| repoName | Nombre del repositorio |
| totalCommits | Commits registrados en ese repo |
| openedIssues | Issues creados en ese repo |
| activeIssues | Issues actualmente abiertos |
| closedIssues | Issues cerrados |

La query acepta filtros opcionales:

```graphql
githubRepositoryActivity(projectId: ID!, sprintId: ID, developerId: ID)
```

El filtro por `developerId` filtra actividad real del developer:

- commits por `AUTHOR_USERNAME`;
- issues creados o activos por `AUTHOR_USERNAME`;
- issues cerrados por `CLOSED_BY_USERNAME`.

El frontend no debe filtrar por `owner`, porque `owner` representa el dueno del repositorio, no el developer que contribuyo.

---

## 12. Backend - repository principal de metricas

Archivo:

```text
GitHubContributionRepository.java
```

Este repository usa SQL nativo con `EntityManager` porque las metricas requieren agregaciones avanzadas, subconsultas y filtros condicionales.

Metodos principales:

```java
findContributionsByProjectId(String projectIdHex, String sprintIdHex)
findSprintActivityByProjectId(String projectIdHex)
findRepositoryActivityByProjectId(String projectIdHex, String sprintIdHex, String developerIdHex)
```

Se uso SQL nativo porque las consultas necesitan:

- convertir IDs HEX a `RAW(16)` con `HEXTORAW`;
- devolver IDs en formato legible con `RAWTOHEX`;
- contar commits, issues creados, issues activos e issues cerrados;
- relacionar usuarios con equipos y proyectos;
- aplicar filtros opcionales por sprint;
- aplicar filtros opcionales por developer;
- cruzar fechas de commits/issues con rangos de sprint.

---

## 13. Backend - filtros implementados

### 13.1 Filtro por sprint

El filtro por sprint se aplica comparando fechas:

```text
commitDate >= sprint.fechaInicio
commitDate < sprint.fechaFin + 1
```

Se usa `< FECHA_FIN + 1` para incluir todo el ultimo dia del sprint y evitar perder eventos que ocurren despues de las 00:00:00.

El filtro aplica en:

- `githubContributions`;
- `githubRepositoryActivity`.

### 13.2 Filtro por developer

El filtro por developer se aplica en `githubRepositoryActivity` usando el `developerId` interno y buscando su `GITHUB_USERNAME` en la tabla `USUARIO`.

Esto permite filtrar la actividad por repositorio cuando el manager selecciona un desarrollador especifico.

### 13.3 Helper normalizeOptionalHex

El repository incluye un helper:

```java
private String normalizeOptionalHex(String value) {
  if (value == null || value.isBlank()) {
    return null;
  }

  String trimmedValue = value.trim();
  if ("all".equalsIgnoreCase(trimmedValue)) {
    return null;
  }

  return trimmedValue;
}
```

Esto permite que el frontend envie `null`, vacio o `all` cuando no quiere aplicar filtro.

---

## 14. Backend - diferencia entre issues creados, activos y cerrados

Durante el desarrollo se detecto una ambiguedad importante: el termino "Issues abiertos" podia interpretarse de dos formas.

La solucion final diferencia tres conceptos:

| Metrica | Significado real |
|---|---|
| openedIssues | Issues creados historicamente |
| activeIssues | Issues que actualmente tienen `STATE = 'open'` |
| closedIssues | Issues cerrados historicamente |

Esta separacion evita calcular issues activos como:

```text
openedIssues - closedIssues
```

Ese calculo no es confiable porque un issue puede cerrarse y reabrirse, o porque pueden existir eventos historicos no capturados por webhook. La fuente correcta para issues activos es el estado actual almacenado en `GITHUB_ISSUE.STATE`.

---

## 15. Frontend - objetivo de la integracion GitHub

El frontend no implementa una feature aislada de GitHub. La integracion se agrego dentro del dashboard de proyecto existente para consumir las metricas calculadas por el backend y visualizarlas como KPIs y graficas.

La responsabilidad del frontend en esta integracion es:

- consumir el contrato GraphQL expuesto por el backend;
- enviar `projectId`, `sprintId` y `developerId` segun los filtros seleccionados;
- mostrar commits, issues creados, issues activos e issues cerrados;
- actualizar las graficas cuando cambia el filtro por sprint o developer;
- evitar calculos de atribucion que pertenecen al backend, especialmente en actividad por repositorio.

El frontend no procesa webhooks ni calcula metricas desde eventos crudos. Esa logica queda en backend. El frontend solo consume DTOs ya consolidados para visualizacion.

---

## 16. Frontend - archivos relacionados con GitHub Activity

La integracion vive principalmente en la feature de proyectos:

```text
src/features/proyectos
|-- components
|   `-- ProjectDashboard.tsx
|
|-- graphql
|   `-- dashboardKpiQueries.ts
|
|-- hooks
|   `-- useProyectos.ts
|
|-- services
|   `-- projectDashboardGraphqlService.ts
|
`-- styles
    `-- ProjectDashboard.module.css
```

Responsabilidad de cada archivo:

| Archivo | Responsabilidad |
|---|---|
| `dashboardKpiQueries.ts` | Define la query GraphQL `GitHubKpis` |
| `projectDashboardGraphqlService.ts` | Declara tipos TypeScript y ejecuta la query |
| `useProyectos.ts` | Expone `useGitHubKpis` con React Query |
| `ProjectDashboard.tsx` | Consume datos, calcula cards y arma opciones de ECharts |
| `ProjectDashboard.module.css` | Mantiene layout y estilos de la seccion del dashboard |

---

## 17. Frontend - cliente GraphQL usado

La query de GitHub usa el cliente GraphQL compartido:

```text
src/shared/api/graphqlClient.ts
```

Este cliente construye el endpoint a partir de la variable de entorno:

```ts
const API_URL = import.meta.env.VITE_API_URL;
const GRAPHQL_ENDPOINT = `${API_URL.replace(/\/+$/, "")}/graphql`;
```

El request se envia con `fetch` usando el formato esperado por GraphQL:

```json
{
  "query": "...",
  "variables": {}
}
```

Si hay token JWT en `localStorage`, se agrega al header:

```text
Authorization: Bearer <token>
```

Por eso el dashboard puede consumir `/graphql` usando la misma sesion autenticada del frontend.

---

## 18. Frontend - query GitHubKpis

La query implementada en frontend consolida los tres datasets necesarios para el dashboard en un solo request:

```graphql
query GitHubKpis($projectId: ID!, $sprintId: ID, $developerId: ID) {
  githubContributions(projectId: $projectId, sprintId: $sprintId) {
    userId
    name
    email
    githubUsername
    totalCommits
    openedIssues
    activeIssues
    closedIssues
  }

  githubSprintActivity(projectId: $projectId) {
    sprintId
    sprintName
    totalCommits
    openedIssues
    closedIssues
  }

  githubRepositoryActivity(
    projectId: $projectId
    sprintId: $sprintId
    developerId: $developerId
  ) {
    repositoryId
    owner
    repoName
    totalCommits
    openedIssues
    activeIssues
    closedIssues
  }
}
```

Datasets consumidos:

| Dataset | Uso en frontend |
|---|---|
| `githubContributions` | Actividad por integrante y distribucion porcentual de commits |
| `githubSprintActivity` | Actividad GitHub por sprint |
| `githubRepositoryActivity` | Actividad GitHub por repositorio |

---

## 19. Frontend - variables enviadas al backend

El dashboard usa dos filtros visibles:

```text
Filtro por Sprints
Filtro por Developer
```

Internamente se usa el valor centinela:

```ts
export const ALL_DASHBOARD_FILTER = "ALL";
```

Antes de llamar al backend, el frontend convierte ese valor a `null`:

```ts
const toDashboardNullableId = (value?: string) =>
  !value || value === ALL_DASHBOARD_FILTER ? null : value;
```

La llamada a GitHub queda asi:

```ts
getGitHubKpis({
  projectId: projectId!,
  sprintId: toDashboardNullableId(sprintId),
  developerId: toDashboardNullableId(developerId),
});
```

Comportamiento esperado:

| Sprint seleccionado | Developer seleccionado | `sprintId` enviado | `developerId` enviado |
|---|---|---|---|
| Todos | Todo el equipo | `null` | `null` |
| Sprint especifico | Todo el equipo | ID del sprint | `null` |
| Todos | Developer especifico | `null` | ID del developer |
| Sprint especifico | Developer especifico | ID del sprint | ID del developer |

Esto coincide con el contrato backend:

```graphql
githubRepositoryActivity(projectId: ID!, sprintId: ID, developerId: ID)
```

---

## 20. Frontend - tipos TypeScript del contrato GitHub

Los tipos estan definidos en:

```text
src/features/proyectos/services/projectDashboardGraphqlService.ts
```

### 20.1 GitHubContribution

```ts
export interface GitHubContribution {
  userId: string;
  name: string;
  email: string;
  githubUsername: string | null;
  totalCommits: number;
  openedIssues: number;
  activeIssues: number;
  closedIssues: number;
}
```

Este tipo alimenta:

- Actividad tecnica por integrante;
- Distribucion porcentual de commits;
- cards generales de commits e issues.

### 20.2 GitHubSprintActivity

```ts
export interface GitHubSprintActivity {
  sprintId: string;
  sprintName: string;
  totalCommits: number;
  openedIssues: number;
  closedIssues: number;
}
```

Este tipo alimenta la grafica de actividad GitHub por sprint.

### 20.3 GitHubRepositoryActivity

```ts
export interface GitHubRepositoryActivity {
  repositoryId: string;
  owner: string;
  repoName: string;
  totalCommits: number;
  openedIssues: number;
  activeIssues: number;
  closedIssues: number;
}
```

Este tipo alimenta la grafica de actividad GitHub por repositorio.

### 20.4 GitHubKpis

El servicio transforma la respuesta GraphQL al objeto que usa el dashboard:

```ts
export interface GitHubKpis {
  contributions: GitHubContribution[];
  sprintActivity: GitHubSprintActivity[];
  repositoryActivity: GitHubRepositoryActivity[];
}
```

---

## 21. Frontend - hook useGitHubKpis

El hook vive en:

```text
src/features/proyectos/hooks/useProyectos.ts
```

Implementacion logica:

```ts
export const useGitHubKpis = (
  projectId?: string,
  sprintId?: string,
  developerId?: string
) => {
  return useQuery({
    queryKey: ["githubKpis", projectId, sprintId, developerId],
    queryFn: () =>
      getGitHubKpis({
        projectId: projectId!,
        sprintId: toDashboardNullableId(sprintId),
        developerId: toDashboardNullableId(developerId),
      }),
    enabled: !!projectId,
  });
};
```

La `queryKey` incluye `sprintId` y `developerId`. Por eso, cuando el usuario cambia cualquiera de los filtros, React Query vuelve a consultar el backend y actualiza las graficas.

---

## 22. Frontend - consumo en ProjectDashboard

El componente que renderiza la seccion es:

```text
src/features/proyectos/components/ProjectDashboard.tsx
```

La llamada se hace con los filtros seleccionados:

```ts
const githubKpisQuery = useGitHubKpis(
  projectId,
  selectedSprintId,
  selectedDeveloperId
);
```

Despues se separan los tres datasets:

```ts
const githubContributions = githubKpis?.contributions ?? [];
const githubSprintActivity = githubKpis?.sprintActivity ?? [];
const githubRepositoryActivity = githubKpis?.repositoryActivity ?? [];
```

Punto importante: `githubRepositoryActivity` se usa directamente como viene del backend. El frontend no filtra por `repo.owner`, porque `owner` representa al dueno del repositorio y no al developer que hizo la contribucion.

La atribucion por developer en repositorios se resuelve en backend usando:

```text
AUTHOR_USERNAME
CLOSED_BY_USERNAME
USUARIO.GITHUB_USERNAME
```

---

## 23. Frontend - cards GitHub

El frontend calcula cards agregadas a partir de `githubContributions`:

```ts
const githubSummary = filteredGitHubContributions.reduce(
  (totals, contribution) => ({
    totalCommits: totals.totalCommits + contribution.totalCommits,
    openedIssues: totals.openedIssues + contribution.openedIssues,
    activeIssues: totals.activeIssues + contribution.activeIssues,
    closedIssues: totals.closedIssues + contribution.closedIssues,
  }),
  {
    totalCommits: 0,
    openedIssues: 0,
    activeIssues: 0,
    closedIssues: 0,
  }
);
```

Cards visibles:

```text
Commits GitHub
Issues creados
Issues cerrados
Issues activos
Tasa de cierre
Repos monitoreados
```

La tasa de cierre se calcula como:

```ts
const githubIssueClosureRate =
  githubSummary.openedIssues === 0
    ? 0
    : (githubSummary.closedIssues / githubSummary.openedIssues) * 100;
```

Repos monitoreados se obtiene desde el dataset del backend:

```ts
const monitoredRepos = githubRepositoryActivity.length;
```

---

## 24. Frontend - graficas GitHub implementadas

La seccion del dashboard se llama:

```text
GitHub Activity
```

### 24.1 Actividad tecnica por integrante

Fuente:

```text
githubContributions
```

Tipo:

```text
Bar chart agrupado
```

Series:

- Commits;
- Issues creados;
- Issues activos;
- Issues cerrados.

Esta grafica permite comparar contribucion tecnica por integrante.

### 24.2 Distribucion porcentual de commits

Fuente:

```text
githubContributions
```

Tipo:

```text
Pie chart / donut chart
```

Metrica:

```text
porcentaje de commits por integrante
```

Esta grafica ayuda a detectar concentracion de trabajo en una sola persona.

### 24.3 Actividad GitHub por sprint

Fuente:

```text
githubSprintActivity
```

Tipo:

```text
Bar chart agrupado
```

Series:

- Commits;
- Issues creados;
- Issues cerrados.

Esta grafica permite comparar actividad tecnica contra el avance del sprint.

### 24.4 Actividad GitHub por repositorio

Fuente:

```text
githubRepositoryActivity
```

Tipo:

```text
Bar chart agrupado
```

Series:

- Commits;
- Issues creados;
- Issues activos;
- Issues cerrados.

Esta grafica usa el filtro backend por `developerId`. Si se selecciona un developer especifico, el backend devuelve los conteos reales de ese developer por repositorio.

El frontend solamente formatea el label del repositorio para hacerlo mas legible:

```ts
const formatRepositoryLabel = (repoName: string) =>
  repoName.replace(/^oracle-java-bot-/, "");
```

Ejemplos:

```text
oracle-java-bot-frontend -> frontend
oracle-java-bot-backend -> backend
oracle-java-bot-testing -> testing
oracle-java-bot-ai-service -> ai-service
```

---

## 25. Frontend - comportamiento esperado de filtros GitHub

### 25.1 Filtro por sprint

Cuando el usuario selecciona un sprint especifico, el frontend manda ese `sprintId` al backend. Esto afecta:

- `githubContributions`;
- `githubRepositoryActivity`.

La grafica `githubSprintActivity` no necesita `sprintId` porque ya viene agrupada por todos los sprints del proyecto.

### 25.2 Filtro por developer

Cuando el usuario selecciona un developer especifico, el frontend manda ese `developerId` al backend.

Esto afecta directamente:

- `githubRepositoryActivity`.

Para `githubContributions`, el frontend puede mostrar la actividad del integrante seleccionado usando el arreglo ya recibido y el `userId` del developer.

### 25.3 Todo el equipo

Cuando se selecciona `Todo el equipo`, el frontend manda:

```json
{
  "developerId": null
}
```

El backend interpreta `null` como ausencia de filtro y devuelve actividad del equipo completo.

### 25.4 Todos los sprints

Cuando se selecciona `Todos`, el frontend manda:

```json
{
  "sprintId": null
}
```

El backend interpreta `null` como ausencia de filtro y devuelve actividad de todo el proyecto.

---

## 26. Integracion con repositorios GitHub

Para alimentar la base de datos en tiempo real, cada repositorio debe tener configurado el webhook:

```text
Payload URL:
http://159.54.133.243:8080/api/github/webhook

Content type:
application/json

Secret:
oracle-java-bot-github-webhook-secret

Events:
Pushes
Issues
```

Repositorios registrados en el proyecto:

```text
oracle-java-bot-frontend
oracle-java-bot-backend
oracle-java-bot-testing
oracle-java-bot-ai-service
```

---

## 27. Pruebas manuales backend

### 27.1 Health check

```bash
curl -i http://159.54.133.243:8080/api/health
```

Respuesta esperada:

```json
{
  "status": "UP",
  "service": "oracle-java-bot-backend"
}
```

### 27.2 Webhook con firma valida

```powershell
$secret = "oracle-java-bot-github-webhook-secret"
$payload = "{}"

$hmac = New-Object System.Security.Cryptography.HMACSHA256
$hmac.Key = [Text.Encoding]::UTF8.GetBytes($secret)

$hashBytes = $hmac.ComputeHash([Text.Encoding]::UTF8.GetBytes($payload))
$signature = "sha256=" + (($hashBytes | ForEach-Object { $_.ToString("x2") }) -join "")

curl.exe -i -X POST http://159.54.133.243:8080/api/github/webhook `
  -H "Content-Type: application/json" `
  -H "X-GitHub-Event: ping" `
  -H "X-Hub-Signature-256: $signature" `
  -d "{}"
```

Respuesta esperada:

```text
HTTP/1.1 200
GitHub webhook ping received
```

### 27.3 GraphQL: contribuciones por integrante

```powershell
$body = @{
  query = 'query { githubContributions(projectId: "70BD1C78282D46BF913A573354FA3D55") { name githubUsername totalCommits openedIssues activeIssues closedIssues } }'
} | ConvertTo-Json -Compress

$response = Invoke-RestMethod `
  -Uri "http://159.54.133.243:8080/graphql" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

$response.data.githubContributions
```

### 27.4 GraphQL: actividad por sprint

```powershell
$body = @{
  query = 'query { githubSprintActivity(projectId: "70BD1C78282D46BF913A573354FA3D55") { sprintName totalCommits openedIssues closedIssues } }'
} | ConvertTo-Json -Compress

$response = Invoke-RestMethod `
  -Uri "http://159.54.133.243:8080/graphql" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

$response.data.githubSprintActivity
```

### 27.5 GraphQL: actividad por repositorio

```powershell
$body = @{
  query = 'query { githubRepositoryActivity(projectId: "70BD1C78282D46BF913A573354FA3D55") { owner repoName totalCommits openedIssues activeIssues closedIssues } }'
} | ConvertTo-Json -Compress

$response = Invoke-RestMethod `
  -Uri "http://159.54.133.243:8080/graphql" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

$response.data.githubRepositoryActivity
```

---

## 28. Pruebas manuales frontend GitHub

### 28.1 Instalar dependencias

```bash
npm install
```

### 28.2 Configurar `.env`

```env
VITE_API_URL=http://159.54.133.243:8080
```

### 28.3 Ejecutar localmente

```bash
npm run dev
```

### 28.4 Build de produccion

```bash
npm run build
```

### 28.5 Verificacion de dashboard GitHub

1. Iniciar sesion.
2. Entrar a `/proyectos`.
3. Abrir un proyecto.
4. Verificar cards generales de KPIs.
5. Verificar filtros `Filtro por Sprints` y `Filtro por Developer`.
6. Confirmar que la seccion `GitHub Activity` aparece despues de las graficas globales de developer.
7. Cambiar sprint y verificar que cambien:
   - Distribucion porcentual de commits;
   - Actividad GitHub por repositorio.
8. Cambiar developer y verificar que cambie:
   - Actividad tecnica por integrante;
   - Actividad GitHub por repositorio.
9. Confirmar que `Issues activos` usa `activeIssues`.
10. Confirmar que `Repos monitoreados` usa `githubRepositoryActivity.length`.

---

## 29. Decisiones tecnicas importantes

### 29.1 GraphQL para dashboards

Se eligio GraphQL para metricas porque el dashboard necesita consultar muchos campos agregados en una sola pantalla. Esto evita multiples requests REST para cada grafica.

### 29.2 React Query para cache y sincronizacion

React Query evita manejar manualmente estados de carga, error y cache. Tambien permite invalidar datos despues de mutations.

### 29.3 ECharts para visualizacion

ECharts permite graficas agrupadas, stacked bars, line charts y pie charts con configuracion flexible.

### 29.4 Filtro por developer en backend para repositorios

La grafica `Actividad GitHub por repositorio` usa `githubRepositoryActivity` ya filtrado por backend. No se filtra en frontend por `repo.owner`, porque el owner del repositorio no equivale al usuario que hizo commits o cerro issues.

### 29.5 Issues activos como metrica separada

`activeIssues` se trata como metrica propia porque representa estado actual. No se calcula como diferencia entre creados y cerrados.

---

## 30. Resultado final

El sistema queda como un dashboard Full Stack integrado:

- GitHub envia eventos reales al backend mediante webhooks seguros.
- Spring Boot valida, procesa y persiste commits e issues.
- Oracle DB almacena repositorios, commits e issues.
- GraphQL consolida metricas por integrante, sprint y repositorio.
- React consume esas metricas con React Query.
- El dashboard presenta cards y graficas para managers.
- Los filtros por sprint y developer afectan las metricas relevantes.
- La seccion GitHub Activity conecta avance de tareas con evidencia tecnica real.

La mejora final convierte el dashboard de proyecto en una vista mas completa de productividad, combinando metricas de gestion interna con actividad tecnica obtenida directamente desde GitHub.