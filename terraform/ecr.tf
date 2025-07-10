resource "aws_ecr_repository" "purchase_service" {
  name = "purchase-service"
  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true
  }
}

output "ecr_repository_url" {
  value = aws_ecr_repository.purchase_service.repository_url
}

