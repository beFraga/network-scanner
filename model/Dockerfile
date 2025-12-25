# 1. Build
FROM ubuntu:24.04 AS builder

RUN apt update && apt install -y \
    build-essential \
    pkg-config \
    libgrpc++-dev \
    libprotobuf-dev \
    protobuf-compiler-grpc \
    protobuf-compiler \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY model/Makefile ./
COPY model/include/ ./include/
COPY model/src/ ./src/
COPY proto/ ./proto/
COPY model/*.json ./

RUN which protoc
RUN which grpc_cpp_plugin
RUN make train

# 2. Runtime
FROM ubuntu:24.04

# ambiente de produção com porta 50051
ENV APP_PORT=50051 \
    APP_ENV=production \
    MODEL_THREADS=1

# dependências mínimas
RUN apt update && apt install -y \
    libgrpc++1.51 \
    libprotobuf32 \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# seta usuário não root
RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /app

COPY --from=builder /app/build/isolation_forest .
COPY --from=builder /app/state-dict/ ./state-dict/
COPY --from=builder /app/config/ ./config/

# construção do ambiente do root (sem acesso ao usuario)
RUN chown -R root:root /app && \
    chmod 0555 /app && \
    chmod 0555 /app/isolation_forest && \
    chmod -R 0444 /app/config && \
    chmod -R 0555 /app/state-dict

# modo usuário
USER appuser

EXPOSE ${APP_PORT}
ENTRYPOINT ["./isolation_forest", "1"]
