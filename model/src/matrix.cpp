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

Matrix Matrix::matmul(const Matrix& A, const Matrix& B) {
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

Matrix Matrix::take_row(const Matrix& a, const size_t i) {
	Matrix b(1, a.cols);
	for (size_t j = 0; j < a.cols; j++) {
		b(0, j) = a(i, j);
	}
	return b;
}

Matrix Matrix::expand_matrix(const Matrix& a, const Matrix& b) {
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

Matrix Matrix::transpose(const Matrix& A) {
	Matrix R(A.cols, A.rows);
	for (size_t i = 0; i < A.rows; i++)
		for (size_t j = 0; j < A.cols; j++)
			R(j,i) = A(i,j);
	return R;
}

Matrix Matrix::relu(const Matrix& A) {
	Matrix R(A.rows, A.cols);
	for (size_t i = 0; i < A.data.size(); i++)
		R.data[i] = std::max(0.0, A.data[i]);
	return R;
}

Matrix Matrix::relu_d(const Matrix& A) {
	Matrix R(A.rows, A.cols);
	for (size_t i = 0; i < A.data.size(); i++)
		R.data[i] = (A.data[i] > 0) ? 1.0 : 0.0;
	return R;
}

Matrix Matrix::sigmoid(const Matrix& A) {
	Matrix R(A.rows, A.cols);
	for (size_t i = 0; i < A.data.size(); i++)
		R.data[i] = 1.0 / (1.0 + exp(-A.data[i]));
	return R;
}

Matrix Matrix::sigmoid_d(const Matrix& A) {
	Matrix R(A.rows, A.cols);
	for (size_t i = 0; i < A.data.size(); i++) {
		double s = 1.0 / (1.0 + exp(-A.data[i]));
		R.data[i] = s * (1 - s);
	}
	return R;
}

Matrix Matrix::mse_loss_grad(const Matrix& y, const Matrix& y_hat) {
	Matrix R(y.rows, y.cols);
	for (size_t i = 0; i < y.data.size(); i++)
		R.data[i] = 2 * (y_hat.data[i] - y.data[i]) / y.data.size();
	return R;
}

double Matrix::mse_loss(const Matrix& y, const Matrix& y_hat) {
	double sum = 0;
	for (size_t i = 0; i < y.data.size(); i++)
		sum += pow(y.data[i] - y_hat.data[i], 2);
	return sum / y.data.size();
}


Matrix Matrix::error(const Matrix& a, const Matrix& b) {
	if (a.rows != b.rows || a.cols != b.cols) throw std::runtime_error("[Matrix] dimensions mismatch");
	Matrix r(a.rows, a.cols);
	for (size_t i = 0; i < a.rows; i++)
		for (size_t j = 0; j < a.cols; j++)
			r(i,j) = pow(a(i,j) - b(i,j), 2);
	return r;
}
