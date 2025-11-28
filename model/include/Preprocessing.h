#ifndef PREPROCESSING_HPP
#define PREPROCESSING_HPP

#include "matrix.h"
#include <string>
#include <vector>

struct Preprocessing {
    static Matrix transform_one_hot_with_flag(const std::vector<std::string>& cat_data, const std::unordered_map<std::string, size_t>& cat_index_map);
	static Matrix preprocess_train(const std::vector<std::vector<std::string>>& data, OneHotParams& params, json& config, std::vector<bool> type_features);
	static void save_config(const json& config, const std::string& path);
	static Matrix preprocess(const std::vector<std::vector<std::string>>& data, const std::string& config_path, std::vector<bool> type_features);
	static NormalizationParams normalize_min_max(Matrix& data);
};

#endif

