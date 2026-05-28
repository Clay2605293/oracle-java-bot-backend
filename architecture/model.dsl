workspace "Oracle Java Bot" "Modelo C4 de arquitectura objetivo para Oracle Java Bot." {

    model {
        manager = person "Manager" "Responsable de gestionar proyectos, tareas, equipos y métricas."
        developer = person "Developer" "Miembro del equipo que consulta y actualiza tareas."

        oracleJavaBot = softwareSystem "Oracle Java Bot" "Sistema cloud-native para gestión de proyectos, tareas, KPIs, notificaciones e IA."

        manager -> oracleJavaBot "Gestiona proyectos, tareas, documentos, métricas y detección de duplicados"
        developer -> oracleJavaBot "Consulta y actualiza tareas desde la Web o Telegram"
    }

    views {
        systemContext oracleJavaBot "SystemContext" {
            include *
            autolayout lr
        }

        theme default
    }
}