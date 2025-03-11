#!/bin/bash
set -e

# Session manager 설치
sudo yum install -y amazon-ssm-agent
sudo systemctl enable amazon-ssm-agent
sudo systemctl start amazon-ssm-agent

# Docker 설치
sudo yum update -y
sudo amazon-linux-extras enable docker
sudo yum install -y docker
sudo service docker start
sudo usermod -aG docker ec2-user

# CodeDeploy Agent 설치
cd /home/ec2-user
wget https://aws-codedeploy-ap-northeast-2.s3.ap-northeast-2.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto
sudo service codedeploy-agent start

# 환경변수 로드
ECR_REPO_URI="${AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/sparta-store-repo"
CONTAINER_NAME="sparta-app"
PORT=80

# AWS ECR 로그인
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin $ECR_REPO_URI

# 최신 이미지 Pull
docker pull $ECR_REPO_URI:latest

# 기존 컨테이너 종료
docker stop $CONTAINER_NAME || true
docker rm $CONTAINER_NAME || true

# 컨테이너 실행
docker run -d -p $PORT:$PORT --name $CONTAINER_NAME \
  -e GOOGLE_CLIENT_ID="${GOOGLE_CLIENT_ID}" \
  -e GOOGLE_CLIENT_SECRET="${GOOGLE_CLIENT_SECRET}" \
  -e GOOGLE_REDIRECT_URI="${GOOGLE_REDIRECT_URI}" \
  -e MYSQL_DB_PASSWORD="${MYSQL_DB_PASSWORD}" \
  -e MYSQL_URL="${MYSQL_URL}" \
  -e REDIS_HOST="${REDIS_HOST}" \
  -e NAVER_MAIL_USER_NAME="${NAVER_MAIL_USER_NAME}" \
  -e NAVER_MAIL_EMAIL_PASSWORD="${NAVER_MAIL_EMAIL_PASSWORD}" \
  -e TOSS_CLIENT_KEY="${TOSS_CLIENT_KEY}" \
  -e TOSS_SECRET_KEY="${TOSS_SECRET_KEY}" \
  -e JWT_SECRET_KEY="${JWT_SECRET_KEY}" \
  $ECR_REPO_URI:latest