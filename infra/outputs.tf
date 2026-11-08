# outputs.tf: exponer información de los recursos creados por Terraform, para que podamos consultarla o utilizarla desde otros módulos y configuraciones
# terraform output para chequear las salidas declaradas
output "kinesis_stream_name" {
  description = "Nombre del Kinesis Data Stream."
  value       = aws_kinesis_stream.stock_quotes.name
}

output "kinesis_stream_arn" {
  description = "ARN del Kinesis Data Stream."
  value       = aws_kinesis_stream.stock_quotes.arn
}

output "flink_application_name" {
  description = "Nombre de la aplicación AWS Managed Flink."
  value       = aws_kinesisanalyticsv2_application.stock_lakehouse.name
}

output "flink_application_arn" {
  description = "ARN de la aplicación AWS Managed Flink."
  value       = aws_kinesisanalyticsv2_application.stock_lakehouse.arn
}

output "lakehouse_bucket_name" {
  description = "Nombre del bucket S3 del Lakehouse."
  value       = aws_s3_bucket.lakehouse.bucket
}

output "flink_artifact_bucket_name" {
  description = "Nombre del bucket S3 que almacena el JAR."
  value       = aws_s3_bucket.flink_artifacts.bucket
}

output "glue_database_name" {
  description = "Nombre de la base de datos del Glue Data Catalog."
  value       = aws_glue_catalog_database.lakehouse.name
}