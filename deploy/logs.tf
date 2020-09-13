# Set up CloudWatch group and log stream and retain logs for 30 days
resource "aws_cloudwatch_log_group" "hc_log_group" {
  name              = "/ecs/hc-app"
  retention_in_days = 30

  tags = {
    Name = "hc-log-group"
  }
}

resource "aws_cloudwatch_log_stream" "hc_log_stream" {
  name           = "hc-log-stream"
  log_group_name = aws_cloudwatch_log_group.hc_log_group.name
}
