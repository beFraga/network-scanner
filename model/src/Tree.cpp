#include "Tree.h"
#include <random>
#include <cstdlib>
#include <algorithm>
#include <cmath>
#include <iostream>
#include "utils.h"
#include "Preprocessing.h"



Tree::Tree(int max_depth) : root(nullptr), max_depth(max_depth) {}

void Tree::fit(Matrix& data) {
    root = build_tree(data, 0);
}

Node* Tree::build_tree(Matrix& data, int current_depth) {
    Node* node = new Node();
    node->size = data.rows;

    if (current_depth >= max_depth || data.rows <= 1) {
        node->is_leaf = true;
        return node;
    }

    int n_features = data.cols;
    int feature = rand() % n_features;

	Matrix data_feat = Matrix::take_row(data, feature);
	Matrix data_col = Matrix::transpose(data_feat);
	NormalizationParams minmax = Preprocessing::normalize_min_max(data_col);

    if (minmax.min == minmax.max) {
        node->is_leaf = true;
        return node;
    }

    std::mt19937 rng(std::random_device{}());
    std::uniform_real_distribution<double> dist(minmax.min, minmax.max);

    double split = dist(rng);
    node->feature = feature;
    node->threshold = split;

    Matrix left_data(0, n_features), right_data(0, n_features);
    for (size_t i = 0; i < data.rows; i++) {
		Matrix row_data = Matrix::take_row(data, i);
        if (data_feat(0, i) < split) left_data = Matrix::expand_matrix(left_data, row_data);
        else right_data = Matrix::expand_matrix(right_data, row_data);
    }

    node->left = build_tree(left_data, current_depth + 1);
    node->right = build_tree(right_data, current_depth + 1);

    return node;
}

int Tree::path_length(Matrix& x) {
    return path_length_node(root, x, 0);
}

int Tree::path_length_node(Node* node, Matrix& x, int depth) {
    if (node->is_leaf) return depth;
    if (x(0, node->feature) < node->threshold) return path_length_node(node->left, x, depth + 1);
    else return path_length_node(node->right, x, depth + 1);
}
