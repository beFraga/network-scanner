#ifndef AUTOENCODER_HPP
#define AUTOENCODER_HPP

#include "dense.h"

struct Autoencoder {
	Dense e1, e2, bottleneck, d1, d2, out;
	
	Autoencoder(size_t in_dim, size_t h1, size_t h2, size_t latent);

	Matrix encode(const Matrix& x);
	Matrix forward(const Matrix& x);
	void train(const Matrix& x, int epochs, double lr);

	void save_network();
	void load_network();
};

#endif
