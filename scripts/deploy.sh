#!/bin/bash

# 업데이트 및 필수 패키지 설치
sudo apt update -y
sudo apt upgrade -y
sudo apt install -y unzip curl awscli

# AWS SSM Agent 설치 (Ubuntu 22.04)
if ! command -v amazon-ssm-agent &> /dev/null; then
    sudo snap install amazon-ssm-agent --classic
fi

# SSM Agent 실행
if systemctl list-units --full -all | grep -q "snap.amazon-ssm-agent.amazon-ssm-agent.service"; then
    sudo systemctl restart snap.amazon-ssm-agent.amazon-ssm-agent.service || true
    sudo systemctl enable snap.amazon-ssm-agent.amazon-ssm-agent.service || true
else
    echo "amazon-ssm-agent 서비스가 정상적으로 설치되지 않았습니다."
fi

# Docker 설치
sudo apt install -y docker.io
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ubuntu  # 'ubuntu' 계정을 Docker 그룹에 추가

# Ruby 설치
sudo apt update -y
sudo apt install -y ruby-full

# AWS CodeDeploy Agent 설치
cd /home/ubuntu
if ! wget https://aws-codedeploy-ap-northeast-2.s3.ap-northeast-2.amazonaws.com/latest/install -O install; then
    echo "CodeDeploy Agent 다운로드 실패. 대체 URL 시도..."
    curl -O https://aws-codedeploy-ap-northeast-2.s3.amazonaws.com/latest/install
fi
chmod +x ./install
sudo ./install auto
sudo systemctl enable codedeploy-agent || true
sudo systemctl start codedeploy-agent || true

# AWS 계정 ID 가져오기 (환경변수 설정)
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query "Account" --output text)
export AWS_ACCOUNT_ID

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