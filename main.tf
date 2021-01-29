provider "aws" {
  region = "eu-central-1"
}

data "aws_caller_identity" "current" {}

resource "aws_iam_role_policy" "iam_for_lambda_policy" {
  name = "iam_for_lambda_policy"
  role = aws_iam_role.iam_for_lambda.id

  policy = <<-EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": "logs:CreateLogGroup",
            "Resource": "arn:aws:logs:eu-central-1:${data.aws_caller_identity.current.account_id}:*"
        },
        {
            "Effect": "Allow",
            "Action": [
                "logs:CreateLogStream",
                "logs:PutLogEvents"
            ],
            "Resource": [
                "arn:aws:logs:eu-central-1:${data.aws_caller_identity.current.account_id}:log-group:/aws/lambda/reverse-audio:*"
            ]
        }
    ]
}
  EOF
}

resource "aws_iam_role" "iam_for_lambda" {
  name = "iam_for_lambda"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

resource "aws_lambda_function" "reverse-audio" {
  filename      = "build/distributions/aws-lambda-sound-reverser.zip"
  function_name = "reverse-audio"
  handler       = "pl.kelog.ReverseAudioHandler::handleRequest"
  role          = aws_iam_role.iam_for_lambda.arn
  runtime       = "java11"
  timeout       = 30
  memory_size   = 1024

  source_code_hash = filebase64sha256("build/distributions/aws-lambda-sound-reverser.zip")
}
