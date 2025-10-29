CXX = g++

CXXFLAGS = -std=c++17 -Wall -O2 -Iinclude

SRC_DIR = src
INC_DIR = include
BUILD_DIR = build

SRCS = $(wildcard $(SRC_DIR)/*.cpp)
OBJS = $(patsubst $(SRC_DIR)/%.cpp, $(BUILD_DIR)/%.o, $(SRCS))
TARGET = $(BUILD_DIR)/isolation_forest

all: $(TARGET) run

train: $(TARGET) train_helper

$(TARGET): $(OBJS)
	$(CXX) $(CXXFLAGS) -o $@ $^

$(BUILD_DIR)/%.o: $(SRC_DIR)/%.cpp | $(BUILD_DIR)
	$(CXX) $(CXXFLAGS) -c $< -o $@

$(BUILD_DIR):
	mkdir -p $(BUILD_DIR)
	mkdir -p state-dict
	mkdir -p config
	touch config/preprocessing.json
	touch response.json

run:
	./$(TARGET) 1

train_helper:
	./$(TARGET) 0

clean:
	rm -rf $(BUILD_DIR)
	rm -rf state-dict
	rm -rf config
	rm response.json
