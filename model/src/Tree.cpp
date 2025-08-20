#include "Tree.h"
#include <random>
#include <cstdlib>
#include <algorithm>
#include <cmath>

Tree::Tree(int max_depth) : root(nullptr), max_depth(max_depth) {}

void Tree::fit(vector<vector<double>>& data) {
    root = build_tree(data, 0);
}

Node* Tree::build_tree(vector<vector<double>>& data, int current_depth) {
    Node* node = new Node();
    node->size = data.size();

    if (current_depth >= max_depth || data.size() <= 1) {
        node->is_leaf = true;
        return node;
    }

    int n_features = data[0].size();
    int feature = rand() % n_features;

    double min_val = data[0][feature], max_val = data[0][feature];

    for (auto& row : data) {
        min_val = min(min_val, row[feature]);
        max_val = max(max_val, row[feature]);
    }

    if (min_val == max_val) {
        node->is_leaf = true;
        return node;
    }

    std::mt19937 rng(std::random_device{}());
    std::uniform_real_distribution<double> dist(min_val, max_val);

    double split = dist(rng);
    node->feature = feature;
    node->threshold = split;

    vector<vector<double>> left_data, right_data;
    for (auto& row : data) {
        if (row[feature] < split) left_data.push_back(row);
        else right_data.push_back(row);
    }

    node->left = build_tree(left_data, current_depth + 1);
    node->right = build_tree(right_data, current_depth + 1);

    return node;
}

int Tree::path_length(vector<double>& x) {
    return path_length_node(root, x, 0);
}

int Tree::path_length_node(Node* node, vector<double>& x, int depth) {
    if (node->is_leaf) return depth;
    if (x[node->feature] < node->threshold) return path_length_node(node->left, x, depth + 1);
    else return path_length_node(node->right, x, depth + 1);
}
