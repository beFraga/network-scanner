#ifndef MATRIX_HPP
#define MATRIX_HPP

#include <vector>
#include <random>
#include <cmath>
#include <stdexcept>
#include <string>
#include <map>
#include <set>

struct Matrix {
	size_t rows, cols;
	std::vector<double> data;
	
	Matrix() : rows(0), cols(0), data() {}
	Matrix(size_t r, size_t c, bool rand_init=false);

	double& operator()(size_t i, size_t j);
	double operator()(size_t i, size_t j) const;
};

Matrix matmul(const Matrix& A, const Matrix& B);
Matrix take_row(const Matrix& a, const size_t i);
Matrix expand_matrix(const Matrix& a, const Matrix& b);
Matrix transpose(const Matrix& A);
Matrix relu(const Matrix& A);
Matrix relu_d(const Matrix& A);
Matrix sigmoid(const Matrix& A);
Matrix sigmoid_d(const Matrix& A);
Matrix mse_loss_grad(const Matrix& y, const Matrix& y_hat);
double mse_loss(const Matrix& y, const Matrix& y_hat);


struct NormalizationParams {
	double min, max;
};

struct OneHotParams {
	std::map<std::string, int> cat_to_index;
	size_t num_categories;
};

NormalizationParams	normalize_min_max(Matrix& data);

OneHotParams fit_one_hot_encoder(const std::vector<std::string>& cat_data);
Matrix transform_one_hot_encoder(const std::vector<std::string>& cat_data, const OneHotParams& params);

Matrix preprocess(const std::vector<std::vector<std::string>>& a);

#endif
