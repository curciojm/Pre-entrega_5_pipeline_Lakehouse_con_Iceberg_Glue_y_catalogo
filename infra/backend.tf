terraform {
  backend "s3" {
    bucket       = "dataeng-terraform-state-pre-entrega-5-2026"
    key          = "Pre-entrega_5_lakehouse/terraform.tfstate"
    region       = "us-east-2"
    encrypt      = true
    use_lockfile = true
  }
}