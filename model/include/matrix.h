#ifndef MATRIX_HPP
#define MATRIX_HPP

#include <vector>
#include <random>
#include <cmath>
#include <stdexcept>
#include <string>
#include <map>
#include <set>
#include <unordered_map>
#include "json.hpp"

using json = nlohmann::json;

struct Matrix {
	size_t rows, cols;
	std::vector<double> data;

	Matrix() : rows(0), cols(0), data() {}
	Matrix(size_t r, size_t c, bool rand_init=false);

	double& operator()(size_t i, size_t j);
	double operator()(size_t i, size_t j) const;

	static Matrix matmul(const Matrix& A, const Matrix& B);
	static Matrix take_row(const Matrix& a, const size_t i);
	static Matrix expand_matrix(const Matrix& a, const Matrix& b);
	static Matrix transpose(const Matrix& A);
	static Matrix relu(const Matrix& A);
	static Matrix relu_d(const Matrix& A);
	static Matrix sigmoid(const Matrix& A);
	static Matrix sigmoid_d(const Matrix& A);
	static Matrix mse_loss_grad(const Matrix& y, const Matrix& y_hat);
	static double mse_loss(const Matrix& y, const Matrix& y_hat);
	static Matrix error(const Matrix& a, const Matrix& b);
};

struct NormalizationParams {
	double min, max;
};

struct OneHotParams {
	std::vector<std::unordered_map<std::string, size_t>> cat_to_index; // por feature
};

#endif
