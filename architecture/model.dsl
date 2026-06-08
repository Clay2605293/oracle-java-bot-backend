workspace "Oracle Java Bot" "Modelo C4 de arquitectura objetivo para Oracle Java Bot." {


    properties {
        structurizr.inspection.model.relationship.technology ignore
        structurizr.inspection.model.softwaresystem.documentation ignore
        structurizr.inspection.model.softwaresystem.decisions ignore
    }


    model {
        // =========================================================
        // Personas
        // =========================================================

        manager = person "Manager" "Responsable de gestionar proyectos, equipos, tareas, documentos, métricas y análisis de duplicados."
        developer = person "Developer" "Miembro del equipo de desarrollo que consulta tareas, actualiza avances y recibe notificaciones."
        devopsEngineer = person "DevOps Engineer" "Responsable de operar los pipelines, despliegues, quality gates e infraestructura cloud."

        // =========================================================
        // Sistema principal
        // =========================================================

        oracleJavaBot = softwareSystem "Oracle Java Bot" "Sistema cloud-native para gestión de proyectos, tareas, KPIs, notificaciones, generación de backlog asistida por IA y detección semántica de duplicados." {

            !adrs doc/arch

            nginxIngress = container "Nginx / Ingress Controller" "Punto de entrada interno que enruta tráfico hacia la versión activa del backend y soporta la estrategia Blue/Green." "Nginx / Kubernetes Ingress" {
                tags "Gateway"
            }

            webFrontend = container "Frontend Web embebido" "Interfaz React/Vite construida desde un repositorio separado y empaquetada como contenido estático dentro del backend." "React / Vite / Static Assets" {
                tags "Web Application"
            }

            backend = container "Spring Boot Backend" "API principal del sistema. Gestiona autenticación, autorización, usuarios, equipos, proyectos, tareas, sprints, KPIs, documentos, embeddings y detección de duplicados." "Java / Spring Boot" {
                tags "Application"

                authComponent = component "Authentication Component" "Valida credenciales, emite tokens JWT y habilita el acceso inicial al sistema." "Spring MVC Controller / Service" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/authentication-component.puml"
                }
                securityComponent = component "Authorization and Security Component" "Valida permisos, roles y acceso a recursos protegidos mediante filtros JWT y configuración de seguridad." "Spring Security / JWT Filter" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/authorization-security-component.puml"
                }

                userManagementComponent = component "User Management Component" "Administra usuarios, credenciales, roles, estado de usuario y datos operativos." "Spring Service / Repository" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/user-management-component.puml"
                }
                teamManagementComponent = component "Team Management Component" "Administra equipos, miembros y relaciones entre usuarios." "Spring Service / Repository" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/team-management-component.puml"
                }
                projectManagementComponent = component "Project Management Component" "Administra proyectos, miembros del proyecto y documentos asociados." "Spring Service / Repository" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/project-management-component.puml"
                }

                taskManagementComponent = component "Task Management Component" "Gestiona ciclo de vida de tareas: creación, edición, eliminación, estado, prioridad, comentarios y asignaciones." "Spring Service / Repository" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/task-management-component.puml"
                }
                sprintManagementComponent = component "Sprint Management Component" "Administra sprints y relación de tareas con periodos de trabajo." "Spring Service / Repository" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/sprint-management-component.puml"
                }
                assignmentComponent = component "Assignment Component" "Gestiona responsables y relaciones usuario-tarea." "Spring Service / Repository" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/task-management-component.puml"
                }

                dashboardKpiComponent = component "Dashboard and KPI Component" "Calcula progreso de proyectos, métricas de sprint y desempeño de developers." "Spring Service / Repository" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/dashboard-kpi-component.puml"
                }

                documentStorageComponent = component "Document Storage Component" "Registra metadatos de documentos y coordina almacenamiento en OCI Object Storage." "Spring Service / OCI SDK" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/project-management-component.puml"
                }

                aiBacklogComponent = component "AI Backlog Generation Component" "Orquesta la generación de tareas sugeridas a partir de documentos mediante Kafka y AI Service." "Spring Service / Kafka Producer-Consumer" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/ai-backlog-generation-component.puml"
                }
                vectorEmbeddingComponent = component "Vector Embedding Component" "Genera y actualiza embeddings vectoriales de tareas en Oracle Database 26ai." "Spring Service / Oracle 26ai" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/vector-embedding-component.puml"
                }
                duplicateDetectionComponent = component "Semantic Duplicate Detection Component" "Ejecuta detección de duplicados mediante Oracle Vector Search y persiste resultados." "Spring Service / Oracle Vector Search" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/semantic-duplicate-detection-component.puml"
                }

                messagingComponent = component "Messaging Component" "Publica y consume eventos Kafka relacionados con tareas, generación IA y notificaciones." "Spring Kafka Producer / Consumer" {
                    url "https://github.com/Clay2605293/oracle-java-bot-backend/blob/main/docs/diagrams/messaging-component.puml"
                }
                persistenceComponent = component "Persistence Component" "Encapsula acceso a datos transaccionales, tablas vectoriales, auditoría e integridad referencial." "Spring Data JPA / Oracle JDBC"
            }

            telegramBotService = container "Telegram Bot Service" "Servicio Java/Spring Boot separado del backend principal. Atiende webhooks de Telegram, resuelve comandos conversacionales y coordina notificaciones." "Java / Spring Boot" {
                tags "Application"

                telegramWebhookComponent = component "Telegram Webhook Component" "Recibe actualizaciones de Telegram mediante webhook y las traduce a solicitudes internas del bot." "Spring MVC Controller"

                botCommandParserComponent = component "Bot Command Parser Component" "Interpreta comandos, mensajes y callbacks enviados por usuarios desde Telegram." "Spring Service"

                botConversationStateComponent = component "Conversation State Component" "Administra estado conversacional para flujos guiados de proyectos y tareas." "Spring Service"

                botProjectFlowComponent = component "Project Flow Component" "Orquesta comandos relacionados con consulta y selección de proyectos." "Spring Service"

                botTaskFlowComponent = component "Task Flow Component" "Orquesta comandos relacionados con consulta, actualización y seguimiento de tareas." "Spring Service"

                botUserResolutionComponent = component "Bot User Resolution Component" "Resuelve la identidad Telegram del usuario contra usuarios registrados del sistema." "Spring Service"

                botKeyboardComponent = component "Bot Keyboard Component" "Construye teclados, opciones y respuestas interactivas para Telegram." "Spring Service"

                botNotificationComponent = component "Bot Notification Component" "Prepara y envía notificaciones de eventos de tarea hacia usuarios de Telegram." "Spring Service"

                telegramApiClientComponent = component "Telegram API Client Component" "Invoca la API de Telegram para enviar mensajes, respuestas y notificaciones." "Telegram Bot API Client"

                botMessagingComponent = component "Bot Messaging Component" "Publica y consume eventos Kafka relacionados con notificaciones y eventos de tarea." "Spring Kafka Producer / Consumer"
            }

            aiService = container "AI Service" "Microservicio Python encargado de parsear documentos, invocar OpenAI y generar tareas sugeridas para backlog." "Python / FastAPI" {
                tags "Application"

                aiHealthApiComponent = component "AI Health API Component" "Expone endpoints de salud y verificación operativa del microservicio IA." "FastAPI Controller"

                aiKafkaConsumerComponent = component "AI Kafka Consumer Component" "Consume solicitudes de generación de backlog publicadas por el backend." "Kafka Consumer"

                aiKafkaProducerComponent = component "AI Kafka Producer Component" "Publica resultados de generación de backlog hacia el backend." "Kafka Producer"

                documentRetrievalComponent = component "Document Retrieval Component" "Recupera documentos del proyecto desde OCI Object Storage para análisis." "Python Service / OCI SDK"

                documentParserComponent = component "Document Parser Component" "Extrae texto relevante de documentos cargados por el manager." "Python Service"

                backlogGenerationComponent = component "Backlog Generation Component" "Orquesta la generación de tareas sugeridas a partir del contenido del documento." "Python Service"

                openAiClientComponent = component "OpenAI Client Component" "Invoca OpenAI para transformar contenido de documentos en tareas sugeridas." "HTTP Client"

                suggestionFormatterComponent = component "Suggestion Formatter Component" "Normaliza la respuesta de IA en un formato estructurado de tareas sugeridas." "Python Service"
            }

            kafkaCluster = container "Kafka Cluster" "Broker de eventos autogestionado para flujos asíncronos de generación de backlog, notificaciones y coordinación entre servicios." "Apache Kafka" {
                tags "Messaging"
            }

            zookeeperEnsemble = container "Zookeeper Ensemble" "Coordina el cluster Kafka autogestionado." "Apache Zookeeper" {
                tags "Messaging"
            }

            database = container "Oracle Database 26ai" "Base de datos principal. Mantiene datos transaccionales, usuarios, proyectos, tareas, documentos, sugerencias IA, embeddings vectoriales y resultados de vector search." "Oracle Database 26ai" {
                tags "Database"
            }

            objectStorageContainer = container "OCI Object Storage" "Almacena documentos de proyecto usados para generación de backlog con IA." "OCI Object Storage" {
                tags "Storage"
            }

            regressionTestRunner = container "Regression Test Runner" "Ejecuta pruebas automatizadas de regresión contra despliegues candidatos antes de promover tráfico de producción." "Python / Selenium / Pytest" {
                tags "Testing"
            }

            cicdOrchestrator = container "CI/CD Orchestrator" "Orquesta build, publicación de imágenes, despliegue candidato, health checks, regression tests, promoción Blue/Green y creación de tickets Jira ante fallas." "OCI DevOps Pipeline" {
                tags "DevOps"
            }
        }

        // =========================================================
        // Sistemas externos y plataformas de soporte
        // =========================================================

        github = softwareSystem "GitHub" "Hospeda los repositorios separados de backend, frontend, testing y AI service." {
            tags "External System"
        }

        githubActions = softwareSystem "GitHub Actions" "Automatiza la construcción del frontend React/Vite y su integración hacia el backend." {
            tags "External System"
        }

        ociDevOps = softwareSystem "OCI DevOps" "Ejecuta pipelines de build, despliegue, health checks, pruebas de regresión y promoción Blue/Green." {
            tags "Oracle Cloud"
        }

        ocir = softwareSystem "OCI Container Registry" "Almacena imágenes versionadas del backend, Telegram Bot Service, AI Service y componentes de soporte." {
            tags "Oracle Cloud"
        }

        oke = softwareSystem "OCI Kubernetes Engine" "Plataforma objetivo de ejecución en producción, distribuida en tres Fault Domains dentro de la OCI Región Querétaro." {
            tags "Oracle Cloud"
        }

        oracleDbExternal = softwareSystem "Oracle Database 26ai Service" "Servicio administrado de base de datos Oracle utilizado por el sistema en producción." {
            tags "Oracle Cloud"
        }

        objectStorageExternal = softwareSystem "OCI Object Storage Service" "Servicio administrado de almacenamiento de objetos para documentos del proyecto." {
            tags "Oracle Cloud"
        }

        telegram = softwareSystem "Telegram Platform" "Canal externo para interacción conversacional y notificaciones hacia developers." {
            tags "External System"
        }

        openai = softwareSystem "OpenAI API" "Servicio externo utilizado por el AI Service para generación de tareas sugeridas a partir de documentos." {
            tags "External System"
        }

        jira = softwareSystem "Jira Service" "Sistema externo donde se registran tickets cuando falla el quality gate del despliegue." {
            tags "External System"
        }

        // =========================================================
        // Relaciones de negocio y uso
        // =========================================================

        manager -> oracleJavaBot "Gestiona proyectos, equipos, tareas, documentos, KPIs y análisis de duplicados"
        developer -> oracleJavaBot "Consulta y actualiza tareas desde la Web o Telegram"
        developer -> telegram "Envía comandos y recibe notificaciones"
        devopsEngineer -> github "Mantiene repositorios y revisa cambios"
        devopsEngineer -> ociDevOps "Supervisa pipelines y despliegues"

        // =========================================================
        // Relaciones del sistema principal a nivel contexto
        // =========================================================

        oracleJavaBot -> telegram "Envía y recibe mensajes mediante webhook"
        oracleJavaBot -> openai "Solicita generación de tareas sugeridas"
        oracleJavaBot -> oracleDbExternal "Usa como persistencia transaccional y motor de vector search"
        oracleJavaBot -> objectStorageExternal "Almacena y recupera documentos de proyecto"
        oracleJavaBot -> jira "Genera tickets ante fallas del quality gate"
        oracleJavaBot -> oke "Se ejecuta como servicios contenerizados en producción"

        // =========================================================
        // Relaciones entre containers
        // =========================================================

        manager -> nginxIngress "Accede al sistema Web mediante HTTPS"
        developer -> nginxIngress "Accede al sistema Web mediante HTTPS"

        nginxIngress -> backend "Enruta tráfico hacia la versión activa del backend"
        backend -> webFrontend "Sirve assets estáticos del frontend embebido"
        webFrontend -> backend "Consume APIs REST protegidas" "HTTPS / JSON"

        developer -> telegram "Interactúa con el bot"
        telegram -> telegramBotService "Envía actualizaciones mediante webhook" "HTTPS"
        telegramBotService -> telegram "Envía respuestas y notificaciones" "HTTPS"

        telegramBotService -> backend "Consulta y actualiza información de proyectos y tareas" "HTTPS / JSON"
        telegramBotService -> kafkaCluster "Publica y consume eventos de notificación" "Kafka"

        backend -> database "Lee y escribe datos transaccionales, embeddings y resultados de vector search" "JDBC / Oracle Wallet"
        backend -> objectStorageContainer "Guarda y recupera documentos de proyecto" "OCI SDK / HTTPS"
        backend -> kafkaCluster "Publica solicitudes de generación IA y eventos de tarea" "Kafka"
        backend -> kafkaCluster "Consume respuestas de generación IA y eventos relevantes" "Kafka"

        kafkaCluster -> zookeeperEnsemble "Usa coordinación de brokers" "Zookeeper protocol"
        aiService -> kafkaCluster "Consume solicitudes de generación de backlog y publica resultados" "Kafka"
        aiService -> objectStorageContainer "Lee documentos para análisis" "OCI SDK / HTTPS"
        aiService -> openai "Solicita generación de tareas sugeridas" "HTTPS / JSON"

        backend -> aiService "Puede consultar estado operativo del servicio IA" "HTTP"
        backend -> regressionTestRunner "Expone endpoints evaluados por pruebas de regresión" "HTTPS"

        cicdOrchestrator -> regressionTestRunner "Ejecuta pruebas de regresión contra candidato" "CLI / HTTP"
        cicdOrchestrator -> nginxIngress "Promueve tráfico entre backend-blue y backend-green" "Deployment automation"
        cicdOrchestrator -> jira "Crea ticket si falla el quality gate" "HTTPS / API"

        manager -> webFrontend "Carga documentos y solicita generación de backlog IA"
        webFrontend -> objectStorageContainer "Carga documentos del proyecto mediante flujo autorizado" "HTTPS"
        webFrontend -> backend "Solicita generación de backlog desde documento" "HTTPS / JSON"
        backend -> aiService "Consulta disponibilidad del AI Service" "HTTP"
        kafkaCluster -> aiService "Entrega solicitudes de generación de backlog" "Kafka"
        kafkaCluster -> backend "Entrega resultados de generación de backlog" "Kafka"
        aiService -> kafkaCluster "Publica tareas sugeridas generadas" "Kafka"
        backend -> webFrontend "Entrega tareas sugeridas para revisión del Manager" "HTTPS / JSON"

        
        // =========================================================
        // Relaciones internas del Spring Boot Backend
        // =========================================================

        webFrontend -> authComponent "Solicita autenticación y recibe token JWT" "HTTPS / JSON"
        webFrontend -> securityComponent "Envía solicitudes protegidas con token JWT" "HTTPS / JSON"

        authComponent -> persistenceComponent "Consulta credenciales y datos de usuario"
        securityComponent -> persistenceComponent "Consulta roles, permisos y membresías"

        securityComponent -> userManagementComponent "Autoriza operaciones de usuarios"
        securityComponent -> teamManagementComponent "Autoriza operaciones de equipos"
        securityComponent -> projectManagementComponent "Autoriza operaciones de proyectos"
        securityComponent -> taskManagementComponent "Autoriza operaciones de tareas"
        securityComponent -> dashboardKpiComponent "Autoriza consulta de métricas"
        securityComponent -> aiBacklogComponent "Autoriza generación de backlog IA"
        securityComponent -> duplicateDetectionComponent "Autoriza análisis de duplicados"

        userManagementComponent -> persistenceComponent "Persiste usuarios, credenciales, roles y estado"
        teamManagementComponent -> persistenceComponent "Persiste equipos y miembros"
        projectManagementComponent -> persistenceComponent "Persiste proyectos, miembros y documentos"
        taskManagementComponent -> persistenceComponent "Persiste tareas, comentarios, prioridades, estados y asignaciones"
        sprintManagementComponent -> persistenceComponent "Persiste sprints"
        assignmentComponent -> persistenceComponent "Persiste relaciones usuario-tarea"
        dashboardKpiComponent -> persistenceComponent "Consulta datos para KPIs y progreso"

        projectManagementComponent -> documentStorageComponent "Coordina carga y consulta de documentos"
        documentStorageComponent -> objectStorageContainer "Guarda y recupera archivos" "OCI SDK / HTTPS"
        documentStorageComponent -> persistenceComponent "Persiste metadatos de documentos"

        taskManagementComponent -> vectorEmbeddingComponent "Solicita generación o actualización de embeddings"
        vectorEmbeddingComponent -> persistenceComponent "Persiste embeddings en TASK_VECTOR_EMBEDDING"
        duplicateDetectionComponent -> persistenceComponent "Consulta embeddings y persiste runs/resultados"
        duplicateDetectionComponent -> dashboardKpiComponent "Expone resultados para visualización"

        projectManagementComponent -> aiBacklogComponent "Solicita generación de backlog desde documentos"
        aiBacklogComponent -> messagingComponent "Publica solicitud de generación IA" "Kafka"
        messagingComponent -> aiBacklogComponent "Entrega resultados de generación IA" "Kafka"
        aiBacklogComponent -> taskManagementComponent "Crea tareas aprobadas desde sugerencias IA"
        aiBacklogComponent -> persistenceComponent "Persiste sugerencias en TAREA_AI_SUGERIDA"

        taskManagementComponent -> messagingComponent "Publica eventos de tarea" "Kafka"
        messagingComponent -> kafkaCluster "Publica y consume eventos" "Kafka"

        manager -> webFrontend "Usa la interfaz Web para gestionar tareas y consultar resultados"
        webFrontend -> taskManagementComponent "Solicita creación o actualización de tareas" "HTTPS / JSON"
        webFrontend -> duplicateDetectionComponent "Solicita detección de tareas duplicadas" "HTTPS / JSON"
        webFrontend -> dashboardKpiComponent "Consulta resultados de duplicidad y métricas" "HTTPS / JSON"

        persistenceComponent -> database "Ejecuta consultas y persistencia sobre Oracle Database 26ai" "JDBC / Oracle Wallet"
        vectorEmbeddingComponent -> database "Genera o actualiza embeddings vectoriales de tareas" "Oracle 26ai Vector"
        duplicateDetectionComponent -> database "Ejecuta búsqueda semántica con Oracle Vector Search" "Oracle Vector Search"
        dashboardKpiComponent -> webFrontend "Entrega resultados para visualización" "HTTPS / JSON"
        
        // =========================================================
        // Relaciones internas del Telegram Bot Service
        // =========================================================

        telegram -> telegramWebhookComponent "Envía updates mediante webhook" "HTTPS"

        telegramWebhookComponent -> botCommandParserComponent "Entrega mensajes y callbacks para interpretación"
        botCommandParserComponent -> botConversationStateComponent "Consulta y actualiza estado conversacional"
        botCommandParserComponent -> botUserResolutionComponent "Solicita resolución de identidad Telegram"

        botUserResolutionComponent -> backend "Consulta usuario registrado por telegramId" "HTTPS / JSON"

        botCommandParserComponent -> botProjectFlowComponent "Dirige comandos de proyectos"
        botCommandParserComponent -> botTaskFlowComponent "Dirige comandos de tareas"

        botProjectFlowComponent -> backend "Consulta proyectos disponibles para el usuario" "HTTPS / JSON"
        botTaskFlowComponent -> backend "Consulta y actualiza tareas" "HTTPS / JSON"

        botProjectFlowComponent -> botKeyboardComponent "Solicita opciones interactivas de proyectos"
        botTaskFlowComponent -> botKeyboardComponent "Solicita opciones interactivas de tareas"

        botProjectFlowComponent -> telegramApiClientComponent "Envía respuestas de proyectos"
        botTaskFlowComponent -> telegramApiClientComponent "Envía respuestas de tareas"
        botNotificationComponent -> telegramApiClientComponent "Envía notificaciones de eventos"

        telegramApiClientComponent -> telegram "Envía mensajes mediante Telegram Bot API" "HTTPS"

        botMessagingComponent -> kafkaCluster "Consume eventos de tarea y notificación" "Kafka"
        botMessagingComponent -> botNotificationComponent "Entrega eventos que requieren notificación"
        botNotificationComponent -> botUserResolutionComponent "Resuelve destinatarios Telegram"

        // =========================================================
        // Relaciones internas del AI Service
        // =========================================================

        backend -> aiHealthApiComponent "Consulta estado operativo del servicio IA" "HTTP"

        aiKafkaConsumerComponent -> kafkaCluster "Consume solicitudes de generación de backlog" "Kafka"
        aiKafkaConsumerComponent -> documentRetrievalComponent "Entrega referencia del documento a procesar"

        documentRetrievalComponent -> objectStorageContainer "Recupera archivo de proyecto" "OCI SDK / HTTPS"
        documentRetrievalComponent -> documentParserComponent "Entrega documento descargado"

        documentParserComponent -> backlogGenerationComponent "Entrega texto extraído del documento"
        backlogGenerationComponent -> openAiClientComponent "Solicita generación de tareas sugeridas"
        openAiClientComponent -> openai "Invoca modelo externo" "HTTPS / JSON"

        openAiClientComponent -> suggestionFormatterComponent "Entrega respuesta generada"
        suggestionFormatterComponent -> aiKafkaProducerComponent "Entrega tareas sugeridas estructuradas"
        aiKafkaProducerComponent -> kafkaCluster "Publica resultado de generación de backlog" "Kafka"

        // =========================================================
        // Relaciones DevOps
        // =========================================================

        github -> githubActions "Dispara workflows de integración"
        githubActions -> github "Actualiza artefactos construidos del frontend hacia el backend"
        github -> ociDevOps "Dispara pipelines por cambios en repositorios"
        ociDevOps -> github "Obtiene código fuente"
        ociDevOps -> ocir "Construye y publica imágenes Docker"
        ociDevOps -> oke "Despliega versiones candidatas y promueve tráfico"
        oke -> ocir "Obtiene imágenes de contenedor"
        ociDevOps -> jira "Crea tickets cuando fallan pruebas o health checks"
        ociDevOps -> cicdOrchestrator "Ejecuta orquestación de despliegue, health checks, pruebas y promoción Blue/Green"
        cicdOrchestrator -> ocir "Publica o valida imágenes Docker versionadas"
        cicdOrchestrator -> oke "Despliega versión candidata en OKE"
        regressionTestRunner -> backend "Valida endpoints críticos del backend candidato"

        // =========================================================
        // Deployment Environment
        // =========================================================

        production = deploymentEnvironment "Produccion" {
            deploymentNode "OCI Región Querétaro" "Región cloud donde se despliega la arquitectura objetivo de producción." "Oracle Cloud Infrastructure" {
                tags "Oracle Cloud"

                ociLoadBalancer = infrastructureNode "OCI Load Balancer" "Expone el punto de entrada público del sistema y distribuye tráfico hacia el Ingress Controller." "OCI Load Balancer" {
                    tags "Gateway"
                }

                deploymentNode "OKE Cluster" "Cluster Kubernetes de producción para ejecutar los servicios contenerizados de Oracle Java Bot." "OCI Kubernetes Engine" {
                    tags "Oracle Cloud"

                    deploymentNode "Fault Domain 1" "Fault Domain 1 dentro del Availability Domain de producción." "OCI Fault Domain" {
                        deploymentNode "Worker Node FD1" "Nodo worker de Kubernetes en FD1." "OKE Worker Node" {
                            containerInstance nginxIngress
                            containerInstance backend
                            containerInstance telegramBotService
                            containerInstance aiService
                            containerInstance kafkaCluster
                            containerInstance zookeeperEnsemble
                        }
                    }
                    ociLoadBalancer -> nginxIngress "Distribuye tráfico hacia el Ingress Controller" "HTTPS"

                    deploymentNode "Fault Domain 2" "Fault Domain 2 dentro del Availability Domain de producción." "OCI Fault Domain" {
                        deploymentNode "Worker Node FD2" "Nodo worker de Kubernetes en FD2." "OKE Worker Node" {
                            containerInstance nginxIngress
                            containerInstance backend
                            containerInstance telegramBotService
                            containerInstance aiService
                            containerInstance kafkaCluster
                            containerInstance zookeeperEnsemble
                        }
                    }

                    deploymentNode "Fault Domain 3" "Fault Domain 3 dentro del Availability Domain de producción." "OCI Fault Domain" {
                        deploymentNode "Worker Node FD3" "Nodo worker de Kubernetes en FD3." "OKE Worker Node" {
                            containerInstance nginxIngress
                            containerInstance backend
                            containerInstance telegramBotService
                            containerInstance aiService
                            containerInstance kafkaCluster
                            containerInstance zookeeperEnsemble
                        }
                    }
                }

                deploymentNode "Oracle Database 26ai Service" "Servicio administrado de Oracle Database 26ai usado para persistencia y vector search." "Oracle Database 26ai" {
                    tags "Database"
                    containerInstance database
                }

                deploymentNode "OCI Object Storage Service" "Servicio administrado para documentos de proyecto." "OCI Object Storage" {
                    tags "Storage"
                    containerInstance objectStorageContainer
                }

                deploymentNode "OCI DevOps Service" "Servicio administrado para pipelines de build, testing y deployment." "OCI DevOps" {
                    tags "DevOps"
                    containerInstance cicdOrchestrator
                }

                deploymentNode "OCI Container Registry" "Registry de imágenes Docker usadas por OKE." "OCIR" {
                    tags "DevOps"
                    softwareSystemInstance ocir
                }
            }
        }
    }

    views {
        systemLandscape "SystemLandscape" {
            title "System Landscape - Ecosistema Oracle Java Bot"
            description "Vista general del ecosistema de sistemas, plataformas y actores que interactúan con Oracle Java Bot."
            include *
            autolayout lr
        }

        systemContext oracleJavaBot "SystemContext" {
            title "System Context - Oracle Java Bot"
            description "Vista de contexto funcional del sistema Oracle Java Bot como caja negra, mostrando usuarios principales y dependencias externas directas."

            include manager
            include developer
            include oracleJavaBot
            include telegram
            include openai
            include oracleDbExternal
            include objectStorageExternal
            include jira

            autolayout lr
        }

        container oracleJavaBot "ContainersRuntime" {
            title "Container Diagram - Runtime Oracle Java Bot"
            description "Vista de runtime principal del sistema: entrada Web, backend, Telegram Bot Service, AI Service, mensajería y persistencia."
            include manager
            include developer
            include telegram
            include openai
            include nginxIngress
            include webFrontend
            include backend
            include telegramBotService
            include aiService
            include kafkaCluster
            include zookeeperEnsemble
            include database
            include objectStorageContainer
            autolayout lr
        }

        container oracleJavaBot "ContainersDevOps" {
            title "Container Diagram - DevOps y Blue/Green"
            description "Vista de integración y despliegue continuo: repositorios, pipelines, registry, pruebas, promoción Blue/Green y Jira."
            include devopsEngineer
            include github
            include githubActions
            include ociDevOps
            include ocir
            include oke
            include jira
            include cicdOrchestrator
            include regressionTestRunner
            include nginxIngress
            include backend
            autolayout lr
        }

        container oracleJavaBot "ContainersDataAI" {
            title "Container Diagram - Datos e IA"
            description "Vista de capacidades de datos e inteligencia artificial: documentos, generación de backlog, embeddings, vector search y resultados de duplicidad."
            include manager
            include openai
            include backend
            include aiService
            include kafkaCluster
            include database
            include objectStorageContainer
            autolayout lr
        }

        component backend "BackendComponents" {
            title "Component Diagram - Spring Boot Backend"
            description "Vista interna del backend Spring Boot, basada en los componentes identificados por Event Storming y reflejados en la estructura por features del repositorio."
            include *
            autolayout lr
        }

        component telegramBotService "TelegramBotComponents" {
            title "Component Diagram - Telegram Bot Service"
            description "Vista interna del Telegram Bot Service objetivo, separado del backend principal y basado en webhook para soportar despliegues replicados."
            include *
            autolayout lr
        }

        component aiService "AIServiceComponents" {
            title "Component Diagram - AI Service"
            description "Vista interna del microservicio IA encargado de generación de backlog desde documentos usando Kafka, OCI Object Storage y OpenAI."
            include *
            autolayout lr
        }

        deployment oracleJavaBot "Produccion" "DeploymentProduction" {
            title "Deployment Diagram - Producción en OCI Región Querétaro"
            description "Vista de despliegue objetivo en OCI Kubernetes Engine, distribuida en tres Fault Domains y preparada para Blue/Green deployment."
            include *
            autolayout lr
        }

        dynamic oracleJavaBot "DynamicBlueGreenDeployment" {
            title "Dynamic Diagram - Blue/Green Deployment con Quality Gates"
            description "Secuencia de despliegue continuo: commit, build, publicación de imagen, despliegue candidato, validación, pruebas de regresión, promoción de tráfico y creación de ticket Jira ante fallas."

            devopsEngineer -> github "1. Realiza push o merge a una rama protegida"
            github -> githubActions "2. Dispara workflow de integración del frontend"
            githubActions -> github "3. Publica assets React/Vite construidos hacia el repositorio backend"
            github -> ociDevOps "4. Dispara pipeline de OCI DevOps"
            ociDevOps -> github "5. Obtiene código fuente actualizado"
            ociDevOps -> ocir "6. Construye y publica imagen Docker versionada"
            ociDevOps -> oke "7. Despliega versión candidata en OKE"
            oke -> ocir "8. Descarga imagen candidata"
            ociDevOps -> cicdOrchestrator "9. Ejecuta orquestación de despliegue y validación"
            cicdOrchestrator -> regressionTestRunner "10. Ejecuta health checks y pruebas de regresión"
            regressionTestRunner -> backend "11. Valida endpoints críticos del backend candidato"
            cicdOrchestrator -> nginxIngress "12. Si el quality gate pasa, promueve tráfico hacia el color candidato"
            cicdOrchestrator -> jira "13. Si el quality gate falla, registra ticket con evidencia accionable"

            autolayout lr

        }


        dynamic backend "DynamicDuplicateDetection" {
            title "Dynamic Diagram - Detección de duplicados con Oracle Vector Search"
            description "Secuencia funcional para crear o actualizar una tarea, generar embeddings vectoriales y ejecutar detección semántica de duplicados usando Oracle Database 26ai."

            manager -> webFrontend "1. Crea o actualiza una tarea desde la interfaz Web"
            webFrontend -> securityComponent "2. Envía solicitud protegida con token JWT"
            securityComponent -> taskManagementComponent "3. Autoriza la operación de tarea"
            taskManagementComponent -> persistenceComponent "4. Persiste los datos transaccionales de la tarea"
            persistenceComponent -> database "5. Guarda tarea, estado, prioridad, proyecto y asignaciones"
            taskManagementComponent -> vectorEmbeddingComponent "6. Solicita generar o actualizar embedding de la tarea"
            vectorEmbeddingComponent -> database "7. Guarda embedding vectorial en TASK_VECTOR_EMBEDDING"

            manager -> webFrontend "8. Solicita detección de tareas duplicadas"
            webFrontend -> duplicateDetectionComponent "9. Envía solicitud de análisis semántico"
            duplicateDetectionComponent -> database "10. Ejecuta búsqueda semántica con Oracle Vector Search"
            duplicateDetectionComponent -> persistenceComponent "11. Persiste run y resultados de duplicidad"
            persistenceComponent -> database "12. Guarda AI_VECTOR_DUP_DETECTION_RUN y AI_VECTOR_DUP_RESULT"
            duplicateDetectionComponent -> dashboardKpiComponent "13. Expone resultados para visualización"
            dashboardKpiComponent -> webFrontend "14. Muestra posibles duplicados al Manager"

            autolayout lr
        }

        dynamic oracleJavaBot "DynamicAIBacklogGeneration" {
            title "Dynamic Diagram - Generación de backlog con IA desde documento"
            description "Secuencia funcional para cargar un documento de proyecto, solicitar generación de backlog, procesar el documento en el AI Service, invocar OpenAI y persistir tareas sugeridas."

            manager -> webFrontend "1. Carga documento del proyecto y solicita generación de backlog"
            webFrontend -> backend "2. Envía metadatos del documento y solicitud de generación IA"
            backend -> objectStorageContainer "3. Almacena o recupera documento en OCI Object Storage"
            backend -> database "4. Persiste metadatos del documento en PROYECTO_DOCUMENTO"
            backend -> kafkaCluster "5. Publica solicitud de generación de backlog"
            kafkaCluster -> aiService "6. Entrega solicitud al AI Service"
            aiService -> objectStorageContainer "7. Recupera documento del proyecto"
            aiService -> openai "8. Solicita generación de tareas sugeridas"
            aiService -> kafkaCluster "9. Publica tareas sugeridas generadas"
            kafkaCluster -> backend "10. Entrega resultado de generación IA"
            backend -> database "11. Persiste sugerencias en TAREA_AI_SUGERIDA"
            backend -> webFrontend "12. Muestra tareas sugeridas para aprobación o rechazo"

            autolayout lr
        }

        styles {
            element "Person" {
                shape person
                background #08427B
                color #FFFFFF
            }

            element "Software System" {
                background #1168BD
                color #FFFFFF
            }

            element "External System" {
                background #999999
                color #FFFFFF
            }

            element "Oracle Cloud" {
                background #C74634
                color #FFFFFF
            }

            element "Application" {
                background #438DD5
                color #FFFFFF
            }

            element "Web Application" {
                background #2F95D6
                color #FFFFFF
            }

            element "Gateway" {
                background #F39C12
                color #FFFFFF
            }

            element "Messaging" {
                background #8E44AD
                color #FFFFFF
            }

            element "Database" {
                shape cylinder
                background #27AE60
                color #FFFFFF
            }

            element "Storage" {
                shape cylinder
                background #16A085
                color #FFFFFF
            }

            element "Testing" {
                background #7F8C8D
                color #FFFFFF
            }

            element "DevOps" {
                background #34495E
                color #FFFFFF
            }
        }

        theme default
    }

        configuration {
            scope softwaresystem
        }
}