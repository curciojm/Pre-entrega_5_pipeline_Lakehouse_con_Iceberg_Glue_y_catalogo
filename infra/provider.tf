provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "Pre-entrega-5-Lakehouse"
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}

# Provider sin tags automáticos para Managed Flink
provider "aws" {
  alias  = "untagged"
  region = var.aws_region
}