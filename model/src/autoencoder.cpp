#include "autoencoder.h"
#include <iostream>

Autoencoder::Autoencoder(size_t in_dim, size_t h1, size_t h2, size_t latent)
	: e1(in_dim, h1), e2(h1, h2), bottleneck(h2, latent, true), d1(latent, h2), 
	d2(h2, h1), out(h1, in_dim, true) {}

Matrix Autoencoder::encode(const Matrix& x) {
	Matrix h = e1.forward(x);
	h = e2.forward(h);
	return bottleneck.forward(h);
}

Matrix Autoencoder::forward(const Matrix& x) {
	Matrix h = encode(x);
	h = d1.forward(h);
	h = d2.forward(h);
	return out.forward(h);
}

void Autoencoder::train(const Matrix& x, int epochs, double lr) {
	for (int ep = 0; ep < epochs; ep++) {
		Matrix y_hat = forward(x);
		double loss = mse_loss(x, y_hat);
		Matrix grad = mse_loss_grad(x, y_hat);

		grad = out.backward(grad, lr);
		grad = d2.backward(grad, lr);
		grad = bottleneck.backward(grad, lr);
		grad = e2.backward(grad, lr);
		grad = e1.backward(grad, lr);

		if (ep % 100 == 0) std::cout << "Epoch " << ep << " Loss=" << loss << "\n";
	}
}
