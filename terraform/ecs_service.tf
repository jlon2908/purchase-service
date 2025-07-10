variable "vpc_id" {}
variable "security_group_id" {}
variable "subnet_ids" {
  type = list(string)
}
variable "target_group_arn" {}
variable "ecs_cluster_arn" {}
variable "container_image" {}

resource "aws_ecs_task_definition" "purchase_service" {
  family                   = "purchase-service-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  container_definitions    = jsonencode([
    {
      name      = "purchase-service"
      image     = var.container_image
      essential = true
      portMappings = [
        {
          containerPort = 8086
          hostPort      = 8086
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = "/ecs/purchase-service"
          awslogs-region        = "us-east-1"
          awslogs-stream-prefix = "ecs"
        }
      }
      environment = [
        { name = "DB_HOST", value = var.db_host },
        { name = "DB_JDBC_URL", value = var.db_jdbc_url },
        { name = "DB_PASSWORD", value = var.db_password },
        { name = "DB_USERNAME", value = var.db_username },
        { name = "JWT_SECRET", value = var.jwt_secret }
      ]
    }
  ])
}

resource "aws_iam_role" "ecs_task_execution_role" {
  name = "purchase-service-ecs-task-execution-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "ecs-tasks.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_ecs_service" "purchase_service" {
  name            = "purchase-service"
  cluster         = var.ecs_cluster_arn
  task_definition = aws_ecs_task_definition.purchase_service.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  network_configuration {
    subnets          = var.subnet_ids
    security_groups  = [var.security_group_id]
    assign_public_ip = true
  }
  load_balancer {
    target_group_arn = var.target_group_arn
    container_name   = "purchase-service"
    container_port   = 8080
  }
  depends_on = [aws_iam_role_policy_attachment.ecs_task_execution_role_policy]
}

resource "aws_cloudwatch_log_group" "purchase_service" {
  name              = "/ecs/purchase-service"
  retention_in_days = 14
}

variable "db_host" {}
variable "db_jdbc_url" {}
variable "db_password" {}
variable "db_username" {}
variable "jwt_secret" {}
