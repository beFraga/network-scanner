#include "dense.h"

Dense::Dense(size_t in_dim, size_t out_dim, bool sigmoid)
	: W(in_dim, out_dim, true), b(1, out_dim), use_sigmoid(sigmoid) {}

Matrix Dense::forward(const Matrix& x) {
	input = x;
	z = matmul(x, W);
	for (size_t i = 0; i < z.rows; i++)
		for (size_t j = 0; j < z.cols; j++)
			z(i,j) += b(0,j);
	a = use_sigmoid ? sigmoid(z) : relu(z);
	return a;
}

Matrix Dense::backward(const Matrix& grad, double lr) {
	Matrix dz = grad;
	if (use_sigmoid) dz = sigmoid_d(z);
	else dz = relu_d(z);
	for (size_t i = 0; i < dz.data.size(); i++)
		dz.data[i] *= grad.data[i];

	Matrix dW = matmul(transpose(input), dz);
	Matrix db(1, b.cols);
	for (size_t j = 0; j < b.cols; j++) {
		double sum = 0;
		for (size_t i = 0; i < dz.rows; i++)
			sum += dz(i,j);
		db(0,j) = sum;
	}

	Matrix dX = matmul(dz, transpose(W));

	for (size_t i = 0; i < W.data.size(); i++) W.data[i] -= lr*dW.data[i];
	for (size_t i = 0; i < b.data.size(); i++) b.data[i] -= lr*db.data[i];

	return dX;
}
