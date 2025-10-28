#include "autoencoder.h"
#include <iostream>
#include <fstream>
#include <cstdint>

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
		double loss = Matrix::mse_loss(x, y_hat);
		Matrix grad = Matrix::mse_loss_grad(x, y_hat);

		grad = out.backward(grad, lr);
		grad = d2.backward(grad, lr);
		grad = bottleneck.backward(grad, lr);
		grad = e2.backward(grad, lr);
		grad = e1.backward(grad, lr);

		if (ep % 100 == 0) std::cout << "Epoch " << ep << " Loss=" << loss << "\n";
	}
}


void Autoencoder::save_network() {
	std::vector<Matrix> matricesw = {e1.W, e2.W, bottleneck.W, d1.W, d2.W, out.W};
	std::string fw = "state-dict/weights.bin";
	std::ofstream outw(fw, std::ios::binary);
	if (!outw) throw std::runtime_error("[autoencoder] error openning the file: " + fw);
	uint64_t countw = matricesw.size();
	outw.write(reinterpret_cast<const char*>(&countw), sizeof(uint64_t));
	for (const auto& mat : matricesw) {
		outw.write(reinterpret_cast<const char*>(&mat.rows), sizeof(size_t));
		outw.write(reinterpret_cast<const char*>(&mat.cols), sizeof(size_t));

		if (!mat.data.empty()) {
			outw.write(reinterpret_cast<const char*>(mat.data.data()), mat.data.size() * sizeof(double));
		}
	}

	outw.close();

	std::vector<Matrix> matricesb = {e1.b, e2.b, bottleneck.b, d1.b, d2.b, out.b};
	std::string fb = "state-dict/bias.bin";
	std::ofstream outb(fb, std::ios::binary);
	if (!outb) throw std::runtime_error("[autoencoder] error openning the file: " + fb);
	uint64_t countb = matricesb.size();
	outb.write(reinterpret_cast<const char*>(&countb), sizeof(uint64_t));
	for (const auto& mat : matricesb) {
		outb.write(reinterpret_cast<const char*>(&mat.rows), sizeof(size_t));
		outb.write(reinterpret_cast<const char*>(&mat.cols), sizeof(size_t));

		if (!mat.data.empty()) {
			outb.write(reinterpret_cast<const char*>(mat.data.data()), mat.data.size() * sizeof(double));
		}
	}

	outb.close();
}

void Autoencoder::load_network() {
	std::vector<Dense*> layers = {&e1, &e2, &bottleneck, &d1, &d2, &out};

	std::string fw = "state-dict/weights.bin";
	std::ifstream inw(fw, std::ios::binary);
    if (!inw) throw std::runtime_error("[autoencoder] error openning the file: " + fw);

    uint64_t numw;
    inw.read(reinterpret_cast<char*>(&numw), sizeof(uint64_t));

    std::vector<Matrix> matricesw;
    matricesw.reserve(numw);

    for (uint64_t k = 0; k < numw; ++k) {
        Matrix mat;
        inw.read(reinterpret_cast<char*>(&mat.rows), sizeof(size_t));
        inw.read(reinterpret_cast<char*>(&mat.cols), sizeof(size_t));
		
        if (mat.rows == 0 || mat.cols == 0)
            throw std::runtime_error("[autoencoder::load_networks] Matrix dimensions mismatch: " + fw);

        mat.data.resize(mat.rows * mat.cols);
        inw.read(reinterpret_cast<char*>(mat.data.data()), mat.data.size() * sizeof(double));

		layers[k]->W = mat;
    }

    inw.close();


	std::string fb = "state-dict/bias.bin";
	std::ifstream inb(fb, std::ios::binary);
    if (!inb) throw std::runtime_error("[autoencoder] error openning the file: " + fb);

    uint64_t numb;
    inb.read(reinterpret_cast<char*>(&numb), sizeof(uint64_t));

    std::vector<Matrix> matricesb;
    matricesb.reserve(numb);

    for (uint64_t k = 0; k < numb; ++k) {
        Matrix mat;
        inb.read(reinterpret_cast<char*>(&mat.rows), sizeof(size_t));
        inb.read(reinterpret_cast<char*>(&mat.cols), sizeof(size_t));
		
        if (mat.rows == 0 || mat.cols == 0)
            throw std::runtime_error("[autoencoder::load_networks] Matrix dimensions mismatch: " + fw);

        mat.data.resize(mat.rows * mat.cols);
        inb.read(reinterpret_cast<char*>(mat.data.data()), mat.data.size() * sizeof(double));

		layers[k]->b = mat;
    }

    inb.close();
}
