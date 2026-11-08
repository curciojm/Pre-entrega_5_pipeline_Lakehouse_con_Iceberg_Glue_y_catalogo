variable "aws_region" {
  description = "Región de AWS donde se desplegará la infraestructura."
  type        = string
  default     = "us-east-2"
}

variable "environment" {
  description = "Entorno de despliegue."
  type        = string
  default = "dev"
}

variable "kinesis_stream_name" {
  description = "Nombre del Kinesis Data Stream existente que utilizará Flink."
  type        = string
  default     = "dev-kinesis-ingest-stream"
}

variable "flink_artifact_bucket_name" {
  description = "Nombre del bucket S3 que almacenará el artefacto JAR de Flink."
  type        = string
}

variable "lakehouse_bucket_name" {
  description = "Nombre del bucket S3 donde se almacenarán los datos de Iceberg."
  type        = string
}

variable "flink_jar_path" {
  description = "Ruta local del JAR compilado de la aplicación Flink."
  type        = string
}