provider "aws" {
  shared_credentials_file = "$HOME/.aws/credentials"
  profile                 = "aws-vanya"
  region                  = var.aws_region
}
