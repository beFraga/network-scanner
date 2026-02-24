#!/bin/bash

USER_GH="arthurclasen"              # user no github
REPO_NAME="network_scanner_images"  # repo no github
REGISTRY="ghcr.io"

# imagens que estarão no repositório
declare -A IMAGES=(
  ["aicpp:latest"]="aicpp"
  ["capture_service:latest"]="capture_service"
  ["auth_service:latest"]="auth_service"
  ["plotter:latest"]="plotter"
)

echo "Iniciando push"

# percorrer todas as imagens
for LOCAL in "${!IMAGES[@]}"; do
  REMOTE_TAG="${IMAGES[$LOCAL]}"
  # ex full_name = ghcr.io/arthurclasen/network_scanner_images:aicpp
  FULL_NAME="$REGISTRY/$USER_GH/$REPO_NAME:$REMOTE_TAG"

  docker tag "$LOCAL" "$FULL_NAME"

  docker push "$FULL_NAME"

  # limpeza da imagem duplicada
  docker rmi "$FULL_NAME"
done
