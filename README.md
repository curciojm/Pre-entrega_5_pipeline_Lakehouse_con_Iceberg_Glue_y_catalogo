# Pre-entrega 5 – Lakehouse con Apache Iceberg, AWS Glue y Managed Flink

## Descripción

Implementación de un pipeline de procesamiento de datos financieros en tiempo real utilizando AWS Kinesis Data Streams, Apache Flink, Apache Iceberg y AWS Glue Data Catalog.

El sistema recibe cotizaciones bursátiles generadas mediante un producer en Python, calcula agregaciones por ticker en ventanas de un minuto y almacena los resultados en formato Parquet sobre Amazon S3, utilizando Iceberg como formato de tabla.

## Arquitectura

```text
Python Producer
      |
      v
AWS Kinesis Data Streams
      |
      v
AWS Managed Service for Apache Flink
      |
      |-- JSON parsing
      |-- Event-time processing
      |-- Watermarks
      |-- 1-minute tumbling windows
      |-- Aggregations by ticker
      |
      v
Apache Iceberg
      |
      |-- Parquet files
      |-- Metadata and snapshots
      |
      v
Amazon S3 + AWS Glue Data Catalog
      |
      v
Amazon Athena
```

## Tecnologías

- Python 3
- boto3
- Java
- Apache Flink 1.20
- Apache Iceberg 1.7.1
- AWS Kinesis Data Streams
- AWS Managed Service for Apache Flink
- AWS Glue Data Catalog
- Amazon S3
- Amazon Athena
- Terraform

## Estructura del proyecto

```text
Pre-entrega_5_pipeline_Lakehouse_con_Iceberg_Glue_y_catalogo/
│
├── app/
│   ├── src/
│   └── pom.xml
│
├── infra/
│   ├── *.tf
│   └── terraform.tfvars.example
│
├── producer/
│   └── producer.py
│
├── evidencia/
│
├── .gitignore
└── README.md
```

## Producer

El producer desarrollado en Python genera cotizaciones aleatorias para los tickers AAPL, MSFT, GOOGL y AMZN.

Cada evento contiene:

- `TICKER`: símbolo bursátil.
- `PRICE`: precio generado.
- `EVENT_TIME`: timestamp UTC en formato ISO 8601.

Los eventos se envían a Kinesis mediante `put_records`, utilizando el ticker como partition key.

Configuración de generación:

- 20 eventos por lote.
- Intervalo de 2 segundos entre lotes.

### Ejecución

Instalar dependencias:

```bash
pip install boto3
```

Configurar las credenciales AWS mediante un perfil local o variables de entorno.

Ejecutar desde la carpeta raíz del proyecto:

```bash
python producer/producer.py
```

Presionar `Ctrl+C` para detener el producer.

## Procesamiento con Flink

La aplicación Java consume eventos desde Kinesis y realiza:

1. Deserialización de eventos JSON.
2. Conversión de timestamps a tiempo de evento.
3. Asignación de watermarks.
4. Agrupación por ticker.
5. Ventanas temporales de un minuto.
6. Cálculo de agregaciones:
   - Cantidad de operaciones (`trade_count`).
   - Precio promedio (`avg_price`).
   - Precio máximo (`max_price`).
   - Precio mínimo (`min_price`).

La aplicación se ejecuta mediante AWS Managed Service for Apache Flink.

## Almacenamiento con Iceberg

Los resultados se escriben en una tabla Apache Iceberg administrada mediante AWS Glue Data Catalog.

- Database: `stock_lakehouse_dev`
- Table: `stock_aggregations`
- Storage: Amazon S3
- File format: Parquet
- Partitioning: `days(window_start)`

Iceberg administra los metadatos y snapshots de la tabla, permitiendo consultar los datos mediante Athena.

## Consultas en Athena

Seleccionar el catálogo `AwsDataCatalog` y la base de datos `stock_lakehouse_dev`.

Consultar los últimos resultados:

```sql
SELECT *
FROM stock_aggregations
ORDER BY window_start DESC
LIMIT 20;
```

Consultar agregaciones por ticker:

```sql
SELECT
    ticker,
    SUM(trade_count) AS total_trades,
    ROUND(AVG(avg_price), 2) AS average_price,
    MAX(max_price) AS highest_price,
    MIN(min_price) AS lowest_price
FROM stock_aggregations
GROUP BY ticker
ORDER BY ticker;
```

## Infraestructura

La infraestructura AWS se administra mediante Terraform y utiliza la región `us-east-2`.

### Configuración inicial

Desde la carpeta `infra`, crear el archivo local de variables a partir del ejemplo:

```bash
cp terraform.tfvars.example terraform.tfvars
```

Revisar los nombres de los buckets y configurar valores únicos disponibles en AWS.

### Compilación de la aplicación

Desde la carpeta `app`:

```bash
mvn clean package
```

El JAR generado se utiliza como artefacto de la aplicación Flink.

### Despliegue

Desde la carpeta `infra`:

```bash
terraform init
terraform plan
terraform apply
```

### Destrucción de recursos

```bash
terraform destroy
```

**Importante:** la destrucción elimina los recursos administrados por Terraform. Los buckets S3 tienen configurado `force_destroy = false` para evitar su eliminación automática cuando contienen objetos. Es necesario vaciarlos antes de destruirlos.

El archivo `terraform.tfvars` contiene la configuración local y no se incluye en el repositorio. Se proporciona `terraform.tfvars.example` como plantilla.

## Validación

El funcionamiento del pipeline se verifica mediante:

- Recepción de eventos en Kinesis.
- Ejecución de la aplicación Managed Flink.
- Procesamiento de cotizaciones y agregaciones por ventanas.
- Creación de la tabla en Glue Data Catalog.
- Escritura de datos y snapshots de Iceberg en S3.
- Consultas SQL sobre las agregaciones mediante Athena.