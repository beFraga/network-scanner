#include <iostream>
#include <functional>
#include "Preprocessing.h"
#include "utils.h"

#include <unordered_map>
#include <unordered_set>
#include <vector>
#include <string>
#include <iostream>
#include <stdexcept>
#include "json.hpp"
#include <fstream>

using json = nlohmann::json;

Matrix Preprocessing::transform_one_hot_with_flag(
		const std::vector<std::string>& cat_data,
		const std::unordered_map<std::string, size_t>& cat_index_map
		) {
	size_t n_samples = cat_data.size();
	size_t n_categories = cat_index_map.size();
	Matrix encoded(n_samples, n_categories + 1, 0.0);

	for (size_t i = 0; i < n_samples; i++) {
		auto it = cat_index_map.find(cat_data[i]);
		if (it != cat_index_map.end()) {
			encoded(i, it->second) = 1.0;
		} else {
			encoded(i, n_categories) = 1.0;
		}
	}
	return encoded;
}


Matrix Preprocessing::preprocess_train(
		const std::vector<std::vector<std::string>>& data,
		OneHotParams& params,
		json& config,
		std::vector<bool> type_features
		) {
	size_t n_samples = data.size();
	size_t n_features = data[0].size();

	std::vector<size_t> numeric_cols, cat_cols;
	for (size_t j = 0; j < n_features; j++) {
		if (type_features[j]) numeric_cols.push_back(j);
		else cat_cols.push_back(j);
	}

	// --- Numeric ---
	Matrix numeric_matrix(n_samples, numeric_cols.size());
	config["numeric_params"] = json::array(); 
	for (size_t j = 0; j < numeric_cols.size(); j++) {
		Matrix col(n_samples, 1);
		for (size_t i = 0; i < n_samples; i++)
			col(i,0) = std::stod(data[i][numeric_cols[j]]);
		NormalizationParams norm = normalize_min_max(col);

		json norm_json;
		norm_json["minv"] = norm.min;
		norm_json["maxv"] = norm.max;
		config["numeric_params"].push_back(norm_json);

		for (size_t i = 0; i < n_samples; i++)
			numeric_matrix(i,j) = col(i,0);
	}

	// --- Categórico ---
	params.cat_to_index.resize(cat_cols.size());
	std::vector<Matrix> encoded_cat;
	config["categories"] = json::array();

	for (size_t j = 0; j < cat_cols.size(); j++) {
		std::unordered_map<std::string, size_t> cat_map;
		std::vector<std::string> cur_col(n_samples);
		json cat_list = json::array();

		for (size_t i = 0; i < n_samples; i++) cur_col[i] = data[i][cat_cols[j]];

		size_t idx = 0;
		for (const auto& val : cur_col) {
			if (cat_map.find(val) == cat_map.end()) {
				cat_map[val] = idx++;
				cat_list.push_back(val);
			}
		}

		params.cat_to_index[j] = cat_map;
		config["categories"].push_back(cat_list);

		Matrix enc_col = transform_one_hot_with_flag(cur_col, cat_map);
		encoded_cat.push_back(enc_col);
	}

	size_t total_cat_cols = 0;
	for (const auto& m : encoded_cat) total_cat_cols += m.cols;

	Matrix out(n_samples, numeric_cols.size() + total_cat_cols);

	// numeric
	for (size_t i = 0; i < n_samples; i++)
		for (size_t j = 0; j < numeric_cols.size(); j++)
			out(i,j) = numeric_matrix(i,j);

	// categorical
	size_t offset = numeric_cols.size();
	for (const auto& m : encoded_cat) {
		for (size_t i = 0; i < n_samples; i++)
			for (size_t j = 0; j < m.cols; j++)
				out(i, offset + j) = m(i,j);
		offset += m.cols;
	}

	return out;
}

Matrix Preprocessing::preprocess(
		const std::vector<std::vector<std::string>>& data,
		const std::string& config_path,
		std::vector<bool> type_features
		) {
	json config;
	std::ifstream f(config_path);
	if (!f.is_open()) throw std::runtime_error("Cannot open preprocessing config");
	f >> config;
	f.close();

	size_t n_samples = data.size();
	size_t n_features = data[0].size();

	std::vector<size_t> numeric_cols, cat_cols;
	for (size_t j = 0; j < n_features; j++) {
		if (type_features[j]) numeric_cols.push_back(j);
		else cat_cols.push_back(j);
	}

	// --- Numeric: normalizar usando os parâmetros salvos ---
	Matrix numeric_matrix(n_samples, numeric_cols.size());
	for (size_t j = 0; j < numeric_cols.size(); j++) {
		double minv = config["numeric_params"][j]["minv"];
		double maxv = config["numeric_params"][j]["maxv"];
		double range = maxv - minv;
		for (size_t i = 0; i < n_samples; i++) {
			double val = std::stod(data[i][numeric_cols[j]]);
			if (range == 0) numeric_matrix(i,j) = 0.0;
			else numeric_matrix(i,j) = (val - minv) / range;
		}
	}

	// --- Categórico ---
	OneHotParams params;
	params.cat_to_index.resize(cat_cols.size());
	std::vector<Matrix> encoded_cat;
	size_t total_cat_cols = 0;
	for (size_t j = 0; j < cat_cols.size(); j++) {
		size_t idx = 0;
		for (auto& cat : config["categories"][j])
			params.cat_to_index[j][cat.get<std::string>()] = idx++;

		std::vector<std::string> cur_col(n_samples);
		for (size_t i = 0; i < n_samples; i++)
			cur_col[i] = data[i][cat_cols[j]];

		Matrix enc_col = transform_one_hot_with_flag(cur_col, params.cat_to_index[j]);
		total_cat_cols += enc_col.cols;
		encoded_cat.push_back(enc_col);
	}

	Matrix out(n_samples, numeric_cols.size() + total_cat_cols);

	// numeric
	for (size_t i = 0; i < n_samples; i++)
		for (size_t j = 0; j < numeric_cols.size(); j++)
			out(i,j) = numeric_matrix(i,j);

	// categorical
	size_t offset = numeric_cols.size();
	for (const auto& m : encoded_cat) {
		for (size_t i = 0; i < n_samples; i++)
			for (size_t j = 0; j < m.cols; j++)
				out(i, offset + j) = m(i,j);
		offset += m.cols;
	}

	return out;
}

void Preprocessing::save_config(const json& config, const std::string& path) {
	std::ofstream f(path);
	f << config.dump(4);
	f.close();
}

NormalizationParams Preprocessing::normalize_min_max(Matrix& data) {
	if (data.rows == 0 || data.cols != 1)
		throw std::runtime_error("Data column must be a vector");

	double minv = data(0, 0);
	double maxv = data(0, 0);

	for (size_t i = 0; i < data.rows; i++) {
		double val = data(i, 0);
		if (val < minv) minv = val;
		if (val > maxv) maxv = val;
	}

	double range = maxv - minv;

	if (range == 0)
		for (size_t i = 0; i < data.rows; i++)
			data(i, 0) = 0.0;
	else
		for (size_t i = 0; i < data.rows; i++)
			data(i, 0) = (data(i, 0) - minv) / range;

	return {minv, maxv};
}
