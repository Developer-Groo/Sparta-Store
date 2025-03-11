#!/bin/bash
set -e

# 업데이트 및 필수 패키지 설치
sudo apt update -y
sudo apt upgrade -y
sudo apt install -y unzip curl awscli

# AWS SSM Agent 설치 (Ubuntu 22.04)
if ! command -v amazon-ssm-agent &> /dev/null; then
    sudo snap install amazon-ssm-agent --classic
fi
sudo systemctl enable amazon-ssm-agent
sudo systemctl start amazon-ssm-agent

# Docker 설치
sudo apt install -y docker.io
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ubuntu  # 'ubuntu' 계정을 Docker 그룹에 추가

# AWS CodeDeploy Agent 설치
cd /home/ubuntu
wget https://aws-codedeploy-ap-northeast-2.s3.ap-northeast-2.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto
sudo systemctl enable codedeploy-agent
sudo systemctl start codedeploy-agent

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