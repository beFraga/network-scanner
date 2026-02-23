# 1. Runtime
FROM python:3.12-slim AS runtime
LABEL authors="clasen"

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    MPLCONFIGDIR=/tmp/matplotlib

RUN apt-get update && apt-get install -y \
     libfreetype6 \
     libpng16-16 \
     fonts-dejavu-core \
     && rm -rf /var/lib/apt/lists/*

# generate user
RUN groupadd -r plotuser && useradd -r -g plotuser plotuser

WORKDIR /app

# python dependencies
COPY plotter/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# code
COPY plotter/server.py .
COPY plotter/plotter.py .

# directory to the plots
COPY out_plot/ ./out_plot/

# user permission
RUN chown -R plotuser:plotuser /app/
RUN chown -R plotuser:plotuser /app/out_plot && chmod -R 777 /app/out_plot

USER plotuser

CMD ["gunicorn", "--bind", "0.0.0.0:6767", "server:app"]