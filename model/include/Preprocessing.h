#ifndef PREPROCESSING_HPP
#define PREPROCESSING_HPP

#include "matrix.h"
#include <string>
#include <vector>

struct Preprocessing {
    static NormalizationParams normalize_min_max(Matrix& data);

    static HashParams fit_hash_encoder(size_t num_bins);
    static Matrix transform_hash_encoder(const std::vector<std::string>& cat_data, const HashParams& params);

    static Matrix preprocess(const std::vector<std::vector<std::string>>& a, size_t hash_bins = 128);
};

#endif

