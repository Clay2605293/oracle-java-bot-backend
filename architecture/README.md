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