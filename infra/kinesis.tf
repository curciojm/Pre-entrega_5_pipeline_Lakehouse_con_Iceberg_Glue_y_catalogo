resource "aws_kinesis_stream" "stock_quotes" {
  name             = var.kinesis_stream_name
  retention_period = 48

  stream_mode_details {
    # PROVISIONED modificamos nosotros los recursos necesarios, ON-DEMAND, los adm aws ej esacala recursos cuando es necesario
    stream_mode = "PROVISIONED"
  }
  # recorda lectura 2mb y escritura 1mb
  shard_count = 2

  tags = {
    Name = "stock-quotes-stream"
  }
}