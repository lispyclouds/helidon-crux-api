resource "aws_rds_cluster_instance" "cluster_instances" {
  identifier         = "${var.aurora_cluster_name}-instance"
  cluster_identifier = aws_rds_cluster.cluster.id
  instance_class     = var.aurora_instance_class
  engine             = aws_rds_cluster.cluster.engine
  engine_version     = aws_rds_cluster.cluster.engine_version
}

resource "random_password" "aurora_password" {
  length           = 32
  override_special = "!#$%&*()-_=+[]{}<>:?"
}

resource "aws_rds_cluster" "cluster" {
  cluster_identifier     = var.aurora_cluster_name
  database_name          = var.aurora_db_name
  engine                 = "aurora-postgresql"
  master_username        = var.aurora_username
  master_password        = random_password.aurora_password.result
  vpc_security_group_ids = [aws_security_group.aurora-sg.id]
  skip_final_snapshot    = true
  db_subnet_group_name   = aws_db_subnet_group.aurora_subnet_group.name
}

resource "aws_security_group" "aurora-sg" {
  name   = "aurora-security-group"
  vpc_id = aws_vpc.main.id

  ingress {
    protocol    = "tcp"
    from_port   = 5432
    to_port     = 5432
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    protocol    = -1
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_subnet_group" "aurora_subnet_group" {
  name       = "aurora-subnet-group"
  subnet_ids = aws_subnet.private.*.id
}
