# Pre-entrega 5 – Lakehouse con Apache Iceberg, AWS Glue y Managed Flink

## Descripción

Implementación de un pipeline de procesamiento de datos financieros en tiempo real utilizando AWS Kinesis Data Streams, Apache Flink, Apache Iceberg y AWS Glue Data Catalog.

El sistema recibe cotizaciones bursátiles, calcula agregaciones por ticker en ventanas de un minuto y almacena los resultados en formato Parquet sobre Amazon S3, utilizando Iceberg como formato de tabla.

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
│   ├── producer.py
│   └── pom.xml
│
├── infra/
│   ├── *.tf
│   └── terraform.tfvars
│
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

Configurar las credenciales AWS mediante el perfil local o variables de entorno.

Ejecutar:

```bash
python producer.py
```

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

La infraestructura AWS se administra mediante Terraform.

Para inicializar y revisar los cambios:

```bash
terraform init
terraform plan
terraform apply
```

La infraestructura utiliza la región `us-east-2`.

Para eliminar los recursos:

```bash
terraform destroy
```

**Importante:** la destrucción de la infraestructura elimina los recursos administrados por Terraform. El bucket de datos está configurado para permitir su eliminación aunque contenga objetos.

## Validación

El funcionamiento del pipeline se verifica mediante:

- Recepción de eventos en Kinesis.
- Ejecución de la aplicación Managed Flink.
- Creación de la tabla en Glue Data Catalog.
- Escritura de datos y snapshots de Iceberg en S3.
- Consultas SQL sobre las agregaciones mediante Athena.