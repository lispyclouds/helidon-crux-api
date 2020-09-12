terraform {
  backend "s3" {
    bucket = "helidon-crux-tfstates"
    key    = "helidon-crux-api/state.tfstate"
    region = "eu-central-1"
  }
}
