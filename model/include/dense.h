#ifndef DENSE_HPP
#define DENSE_HPP

#include "matrix.h"

struct Dense {
	Matrix W, b, input, z, a;
	bool use_sigmoid;

	Dense(size_t in_dim, size_t out_dim, bool sigmoid=false);

	Matrix forward(const Matrix& x);
	Matrix backward(const Matrix& grad, double lr);
};

#endif
