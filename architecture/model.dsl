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

        oracleJavaBot = softwareSystem "Oracle Java Bot" "Sistema cloud-native para gestión de proyectos, tareas, KPIs, notificaciones, generación de backlog asistida por IA y detección semántica de duplicados."

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

        oracleDb = softwareSystem "Oracle Database 26ai" "Base de datos principal del sistema. Mantiene datos transaccionales, embeddings vectoriales y resultados de búsqueda semántica." {
            tags "Oracle Cloud"
        }

        objectStorage = softwareSystem "OCI Object Storage" "Almacena documentos de proyecto utilizados por los flujos de generación de backlog con IA." {
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
        // Relaciones del sistema principal
        // =========================================================

        oracleJavaBot -> telegram "Envía y recibe mensajes mediante webhook"
        oracleJavaBot -> openai "Solicita generación de tareas sugeridas"
        oracleJavaBot -> oracleDb "Lee y escribe datos transaccionales, embeddings y resultados de vector search"
        oracleJavaBot -> objectStorage "Almacena y recupera documentos de proyecto"
        oracleJavaBot -> jira "Genera tickets ante fallas del quality gate"
        oracleJavaBot -> oke "Se ejecuta como servicios contenerizados en producción"

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
        }

        theme default
    }
}