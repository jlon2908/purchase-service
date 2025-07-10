terraform {
  backend "s3" {
    bucket = "arka-dev-artifacts"
    key    = "terraform/purchase-service/terraform.tfstate"
    region = "us-east-1"
  }
}

