#include <iostream>
#include "matrix.h"
#include "utils.h"

Matrix::Matrix(size_t r, size_t c, bool rand_init)
	: rows(r), cols(c), data(r*c) {
		if (rand_init) {
			std::mt19937 gen(std::random_device{}());
			std::normal_distribution<> dist(0, 0.1);
			for (auto& v : data) v = dist(gen);
		}
	}

double& Matrix::operator()(size_t i, size_t j) { return data[i*cols + j]; }
double Matrix::operator()(size_t i, size_t j) const { return data[i*cols + j]; }

Matrix matmul(const Matrix& A, const Matrix& B) {
	if (A.cols != B.rows) throw std::runtime_error("Matmul dimension mismatch");
	Matrix R(A.rows, B.cols);
	for (size_t i = 0; i < A.rows; i++) {
		for (size_t j = 0; j < B.cols; j++) {
			double sum = 0;
			for (size_t k = 0; k < A.cols; k++) sum += A(i,k) * B(k,j);
			R(i,j) = sum;
		}
	}
	return R;
}

Matrix take_row(const Matrix& a, const size_t i) {
	Matrix b(1, a.cols);
	for (size_t j = 0; j < a.cols; j++) {
		b(0, j) = a(i, j);
	}
	return b;
}

Matrix expand_matrix(const Matrix& a, const Matrix& b) {
	if (a.cols != b.cols) throw std::runtime_error("In expand matrix a must have same column size as b");
	
	Matrix c(a.rows + b.rows, a.cols);
	for (size_t i = 0; i < a.rows; i++)
		for (size_t j = 0; j < a.cols; j++)
			c(i, j) = a(i, j);
	
	for (size_t i = 0; i < b.rows; i++)
		for (size_t j = 0; j < b.cols; j++)
			c(i + a.rows, j) = b(i, j);
	
	return c;
}

Matrix transpose(const Matrix& A) {
	Matrix R(A.cols, A.rows);
	for (size_t i = 0; i < A.rows; i++)
		for (size_t j = 0; j < A.cols; j++)
			R(j,i) = A(i,j);
	return R;
}

Matrix relu(const Matrix& A) {
	Matrix R(A.rows, A.cols);
	for (size_t i = 0; i < A.data.size(); i++)
		R.data[i] = std::max(0.0, A.data[i]);
	return R;
}

Matrix relu_d(const Matrix& A) {
	Matrix R(A.rows, A.cols);
	for (size_t i = 0; i < A.data.size(); i++)
		R.data[i] = (A.data[i] > 0) ? 1.0 : 0.0;
	return R;
}

Matrix sigmoid(const Matrix& A) {
	Matrix R(A.rows, A.cols);
	for (size_t i = 0; i < A.data.size(); i++)
		R.data[i] = 1.0 / (1.0 + exp(-A.data[i]));
	return R;
}

Matrix sigmoid_d(const Matrix& A) {
	Matrix R(A.rows, A.cols);
	for (size_t i = 0; i < A.data.size(); i++) {
		double s = 1.0 / (1.0 + exp(-A.data[i]));
		R.data[i] = s * (1 - s);
	}
	return R;
}

Matrix mse_loss_grad(const Matrix& y, const Matrix& y_hat) {
	Matrix R(y.rows, y.cols);
	for (size_t i = 0; i < y.data.size(); i++)
		R.data[i] = 2 * (y_hat.data[i] - y.data[i]) / y.data.size();
	return R;
}

double mse_loss(const Matrix& y, const Matrix& y_hat) {
	double sum = 0;
	for (size_t i = 0; i < y.data.size(); i++)
		sum += pow(y.data[i] - y_hat.data[i], 2);
	return sum / y.data.size();
}


NormalizationParams normalize_min_max(Matrix& data) {
	if (data.rows == 0 || data.cols != 1) {
		throw std::runtime_error("Data column must be a vector");
	}

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

OneHotParams fit_one_hot_encoder(const std::vector<std::string>& cat_data) {
	std::set<std::string> unique_cat_set(cat_data.begin(), cat_data.end());

	OneHotParams params;
	int index = 0;
	for (const auto& cat : unique_cat_set)
		params.cat_to_index[cat] = index++;

	params.num_categories = unique_cat_set.size();
	return params;
}

Matrix transform_one_hot_encoder(const std::vector<std::string>& cat_data, const OneHotParams& params) {
	if (params.num_categories == 0) throw std::runtime_error("OneHot not adjusted (fit) or not has categories");
	
	Matrix encoded_matrix(cat_data.size(), params.num_categories);

	for (size_t i = 0; i < cat_data.size(); i++) {
		auto it = params.cat_to_index.find(cat_data[i]);
		if (it != params.cat_to_index.end()) {
			encoded_matrix(i, it->second) = 1.0;
		} else {
			std::cerr << "Unknow categorie '" << cat_data[i] << "'" << std::endl;
		}
	}
	return encoded_matrix;
}

Matrix preprocess(const std::vector<std::vector<std::string>>& a) {
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
			} catch (const std::invalid_argument& ia) {
				std::cerr << "Conversion error (stod) for numeric data " << a[i][col_idx] << std::endl;
				return Matrix();
			} catch (const std::out_of_range& oor) {
				std::cerr << "Numeric data out of range " << a[i][col_idx] << std::endl;
				return Matrix();
			}
		}
		numeric_params[j] = normalize_min_max(cur_numeric_col);

		for (size_t i = 0; i < num_samples; i++) {
			numeric_features_matrix(i, j) = cur_numeric_col(i, 0);
		}
	}

	std::vector<OneHotParams> cat_params(cat_cols_indices.size());
	std::vector<Matrix> encoded_cat_mat;
	size_t total_one_hot_cols = 0;

	for (size_t j = 0; j < cat_cols_indices.size(); j++) {
		size_t original_col_idx = cat_cols_indices[j];
		std::vector<std::string> cur_cat_col(num_samples);
		for (size_t i = 0; i < num_samples; i++)
			cur_cat_col[i] = a[i][original_col_idx];

		cat_params[j] = fit_one_hot_encoder(cur_cat_col);
		Matrix encoded_col = transform_one_hot_encoder(cur_cat_col, cat_params[j]);
		encoded_cat_mat.push_back(encoded_col);
		total_one_hot_cols += cat_params[j].num_categories;
	}


	size_t final_total_cols = numeric_cols_indices.size() + total_one_hot_cols;
	Matrix data(num_samples, final_total_cols);

	for (size_t i = 0; i < num_samples; i++) {
		for (size_t j = 0; j < numeric_cols_indices.size(); j++) {
			data(i, j) = numeric_features_matrix(i, j);
		}
	}

	size_t cur_col_offset = numeric_cols_indices.size();
	for (const auto& encoded_matrix : encoded_cat_mat) {
		for (size_t i = 0; i < num_samples; i++) {
			for (size_t j = 0; j < encoded_matrix.cols; j++) {
				data(i, cur_col_offset + j) = encoded_matrix(i, j);
			}
		}
		cur_col_offset += encoded_matrix.cols;
	}

	return data;
}
