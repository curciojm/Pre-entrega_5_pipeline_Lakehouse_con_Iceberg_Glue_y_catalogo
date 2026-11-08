resource "aws_glue_catalog_database" "lakehouse" {
  name        = "stock_lakehouse_${var.environment}"
  description = "Glue database for the Stock Lakehouse project"
  # crea una dependencia implícita entre los recursos, sabe que tiene que crear primero ese bucket
  location_uri = "s3://${aws_s3_bucket.lakehouse.bucket}/warehouse/"

  tags = {
    Project = "Pre-entrega-5-Lakehouse"
  }
}