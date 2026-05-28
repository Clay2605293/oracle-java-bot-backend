workspace "Oracle Java Bot" "Modelo C4 de arquitectura objetivo para Oracle Java Bot." {

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

            nginxIngress = container "Nginx / Ingress Controller" "Punto de entrada interno que enruta tráfico hacia la versión activa del backend y soporta la estrategia Blue/Green." "Nginx / Kubernetes Ingress" {
                tags "Gateway"
            }

            webFrontend = container "Frontend Web embebido" "Interfaz React/Vite construida desde un repositorio separado y empaquetada como contenido estático dentro del backend." "React / Vite / Static Assets" {
                tags "Web Application"
            }

            backend = container "Spring Boot Backend" "API principal del sistema. Gestiona autenticación, autorización, usuarios, equipos, proyectos, tareas, sprints, KPIs, documentos, embeddings y detección de duplicados." "Java / Spring Boot" {
                tags "Application"
            }

            telegramBotService = container "Telegram Bot Service" "Servicio Java/Spring Boot separado del backend principal. Atiende webhooks de Telegram, resuelve comandos conversacionales y coordina notificaciones." "Java / Spring Boot" {
                tags "Application"
            }

            aiService = container "AI Service" "Microservicio Python encargado de parsear documentos, invocar OpenAI y generar tareas sugeridas para backlog." "Python / FastAPI" {
                tags "Application"
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
            description "Vista de contexto del sistema Oracle Java Bot, sus usuarios principales y dependencias externas."
            include *
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
}