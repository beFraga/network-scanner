#include "dense.h"

Dense::Dense(size_t in_dim, size_t out_dim, bool sigmoid)
	: W(in_dim, out_dim, true), b(1, out_dim), use_sigmoid(sigmoid) {}

Matrix Dense::forward(const Matrix& x) {
	input = x;
	z = Matrix::matmul(x, W);
	for (size_t i = 0; i < z.rows; i++)
		for (size_t j = 0; j < z.cols; j++)
			z(i,j) += b(0,j);
	a = use_sigmoid ? Matrix::sigmoid(z) : Matrix::relu(z);
	return a;
}

Matrix Dense::backward(const Matrix& grad, double lr) {
	Matrix dz = grad;
	if (use_sigmoid) dz = Matrix::sigmoid_d(z);
	else dz = Matrix::relu_d(z);
	for (size_t i = 0; i < dz.data.size(); i++)
		dz.data[i] *= grad.data[i];

	Matrix dW = Matrix::matmul(Matrix::transpose(input), dz);
	Matrix db(1, b.cols);
	for (size_t j = 0; j < b.cols; j++) {
		double sum = 0;
		for (size_t i = 0; i < dz.rows; i++)
			sum += dz(i,j);
		db(0,j) = sum;
	}

	Matrix dX = Matrix::matmul(dz, Matrix::transpose(W));

	for (size_t i = 0; i < W.data.size(); i++) W.data[i] -= lr*dW.data[i];
	for (size_t i = 0; i < b.data.size(); i++) b.data[i] -= lr*db.data[i];

	return dX;
}
