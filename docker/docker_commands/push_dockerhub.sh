#!/bin/bash

docker login

USER_GH="clasen32257"         # user dockerhub
REPO_NAME="network_scanner"   # repo dockerhub

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

    FULL_NAME="$USER_GH"/"$REPO_NAME":"$REMOTE_TAG"

    docker tag "$LOCAL" "$FULL_NAME"

    docker push "$FULL_NAME"

    DIGEST=$(docker inspect --format='{{index .RepoDigests 0}}' "$FULL_NAME")
    SHA="${DIGEST#*@}"
    IMMUTABLE_REF="$FULL_NAME@$SHA"

    cosign sign -y "$IMMUTABLE_REF"

    cosign verify \
         --certificate-identity "arthurclasendemelo@gmail.com" \
         --certificate-oidc-issuer "https://accounts.google.com" \
         "$IMMUTABLE_REF"

    # limpeza da imagem duplicada
    docker rmi "$FULL_NAME"
done
