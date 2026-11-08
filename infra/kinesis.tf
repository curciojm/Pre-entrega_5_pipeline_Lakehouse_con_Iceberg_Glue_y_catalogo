resource "aws_kinesis_stream" "stock_quotes" {
  name             = var.kinesis_stream_name
  retention_period = 48

  stream_mode_details {
    stream_mode = "PROVISIONED"
  }
  shard_count = 2

  tags = {
    Name = "stock-quotes-stream"
  }
}