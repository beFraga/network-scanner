# 1. Runtime
FROM python:3.12-slim AS runtime
LABEL authors="clasen"

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    MPLCONFIGDIR=/tmp/matplotlib

# instalar dependências do matplotlib
RUN apt-get update && apt-get install -y \
     libfreetype6 \
     libpng16-16 \
     fonts-dejavu-core \
     && rm -rf /var/lib/apt/lists/*

# gerar usuário
RUN groupadd -r plotuser && useradd -r -g plotuser plotuser

WORKDIR /app

# dependências python
COPY plotter/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# código
COPY plotter/main.py .

# permissão de usuário
RUN chown -R plotuser:plotuser /app

USER plotuser

ENTRYPOINT ["python", "plotter.py", "protocol", "remoteAddress"]