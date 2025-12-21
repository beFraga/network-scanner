FROM ubuntu:latest
LABEL authors="clasen"

RUN apt update && apt install -y \
    build-essential \
    cmake \
    libgrpc++-dev \
    protobuf-compiler

WORKDIR /app
COPY model/ /app

RUN make run

EXPOSE 50051

ENTRYPOINT ["top", "-b"]