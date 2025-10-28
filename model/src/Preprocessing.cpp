#include <iostream>
#include <functional>
#include "Preprocessing.h"
#include "utils.h"

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

HashParams Preprocessing::fit_hash_encoder(size_t num_bins) {
    return { num_bins };
}

Matrix Preprocessing::transform_hash_encoder(const std::vector<std::string>& cat_data, const HashParams& params) {
    if (params.num_bins == 0)
        throw std::runtime_error("Hash encoder must have num_bins > 0");

    Matrix encoded_matrix(cat_data.size(), params.num_bins, 0.0);

    std::hash<std::string> hasher;

    for (size_t i = 0; i < cat_data.size(); i++) {
        size_t hash_value = hasher(cat_data[i]) % params.num_bins;
        encoded_matrix(i, hash_value) = 1.0;
    }

    return encoded_matrix;
}

Matrix Preprocessing::preprocess(const std::vector<std::vector<std::string>>& a, size_t hash_bins) {
    size_t num_samples = a.size();
    size_t num_features = a[0].size();

    std::vector<size_t> numeric_cols_indices, cat_cols_indices;

    for (size_t i = 0; i < num_features; i++) {
        if (ut::is_number(a[0][i])) numeric_cols_indices.push_back(i);
        else cat_cols_indices.push_back(i);
    }

    Matrix numeric_features_matrix(num_samples, numeric_cols_indices.size());
    std::vector<NormalizationParams> numeric_params(numeric_cols_indices.size());

    for (size_t j = 0; j < numeric_cols_indices.size(); j++) {
        size_t col_idx = numeric_cols_indices[j];
        Matrix cur_numeric_col(num_samples, 1);

        for (size_t i = 0; i < num_samples; i++) {
            try {
                cur_numeric_col(i, 0) = std::stod(a[i][col_idx]);
            } catch (...) {
                std::cerr << "Error converting numeric data: " << a[i][col_idx] << std::endl;
                return Matrix();
            }
        }

        numeric_params[j] = normalize_min_max(cur_numeric_col);

        for (size_t i = 0; i < num_samples; i++)
            numeric_features_matrix(i, j) = cur_numeric_col(i, 0);
    }

    HashParams hash_params = fit_hash_encoder(hash_bins);
    std::vector<Matrix> encoded_cat_mat;
    size_t total_hash_cols = cat_cols_indices.size() * hash_bins;

    for (size_t j = 0; j < cat_cols_indices.size(); j++) {
        size_t original_col_idx = cat_cols_indices[j];
        std::vector<std::string> cur_cat_col(num_samples);

        for (size_t i = 0; i < num_samples; i++)
            cur_cat_col[i] = a[i][original_col_idx];

        Matrix encoded_col = transform_hash_encoder(cur_cat_col, hash_params);
        encoded_cat_mat.push_back(encoded_col);
    }

    size_t final_total_cols = numeric_cols_indices.size() + total_hash_cols;
    Matrix data(num_samples, final_total_cols);

    for (size_t i = 0; i < num_samples; i++) {
        for (size_t j = 0; j < numeric_cols_indices.size(); j++)
            data(i, j) = numeric_features_matrix(i, j);
    }

    size_t cur_col_offset = numeric_cols_indices.size();
    for (const auto& encoded_matrix : encoded_cat_mat) {
        for (size_t i = 0; i < num_samples; i++)
            for (size_t j = 0; j < encoded_matrix.cols; j++)
                data(i, cur_col_offset + j) = encoded_matrix(i, j);
        cur_col_offset += encoded_matrix.cols;
    }

    return data;
}
