#!/bin/bash

cd ../

# 1. Build da imagem (aicpp)
docker build -t aicpp -f model/Dockerfile .

# 2. Build da imagem (auth_service)
docker build -t auth_service -f auth_service/Dockerfile .

# 3. Build da imagem (capture_service)
docker build -t capture_service -f capture_service/Dockerfile .

# 4. Build da imagem (plotter)
docker build -t plotter -f plotter/Dockerfile .

