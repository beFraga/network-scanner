CXX = g++
CXXFLAGS = -std=c++17 -Wall -O3 -Iinclude -Isrc `pkg-config --cflags protobuf grpc++`
LDFLAGS = `pkg-config --libs protobuf grpc++ grpc` -lgrpc++_reflection -ldl

SRC_DIR = src
INC_DIR = include
BUILD_DIR = build
PROTO_DIR = ../capture_service/src/main/proto

PROTO_NAME = packet_analysis

PROTO_CC = $(SRC_DIR)/$(PROTO_NAME).pb.cc \
           $(SRC_DIR)/$(PROTO_NAME).grpc.pb.cc

PROTO_O = $(BUILD_DIR)/$(PROTO_NAME).pb.o \
          $(BUILD_DIR)/$(PROTO_NAME).grpc.pb.o

SRCS = $(filter-out $(SRC_DIR)/grpc_server.cpp, $(wildcard $(SRC_DIR)/*.cpp))
OBJS = $(patsubst $(SRC_DIR)/%.cpp, $(BUILD_DIR)/%.o, $(SRCS)) \
       $(BUILD_DIR)/grpc_server.o

TARGET = $(BUILD_DIR)/isolation_forest

all: $(TARGET)

# 🔹 Geração dos protos
$(PROTO_CC): $(PROTO_DIR)/*.proto
	mkdir -p $(SRC_DIR)
	protoc -I $(PROTO_DIR) \
		--cpp_out=$(SRC_DIR) \
		--grpc_out=$(SRC_DIR) \
		--plugin=protoc-gen-grpc=`which grpc_cpp_plugin` \
		$^

# 🔹 Compilação dos protos
$(BUILD_DIR)/%.pb.o: $(SRC_DIR)/%.pb.cc | $(BUILD_DIR)
	$(CXX) $(CXXFLAGS) -c $< -o $@

$(BUILD_DIR)/%.grpc.pb.o: $(SRC_DIR)/%.grpc.pb.cc | $(BUILD_DIR)
	$(CXX) $(CXXFLAGS) -c $< -o $@

# 🔹 Compilação geral
$(BUILD_DIR)/%.o: $(SRC_DIR)/%.cpp | $(BUILD_DIR)
	$(CXX) $(CXXFLAGS) -c $< -o $@

$(TARGET): $(PROTO_CC) $(PROTO_O) $(OBJS)
	$(CXX) $(CXXFLAGS) -o $@ $(PROTO_O) $(OBJS) $(LDFLAGS)

$(BUILD_DIR):
	mkdir -p $(BUILD_DIR) state-dict config

train: $(TARGET)
	./$(TARGET) 0

run: $(TARGET)
	./$(TARGET) 1

clean:
	rm -rf $(BUILD_DIR) state-dict config
