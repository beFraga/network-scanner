#include "Tree.h"
#include <random>
#include <cstdlib>
#include <algorithm>
#include <cmath>
#include <iostream>
#include "utils.h"



Tree::Tree(int max_depth) : root(nullptr), max_depth(max_depth) {}

void Tree::fit(vector<vector<string>>& data) {
    root = build_tree(data, 0);
}

Node* Tree::build_tree(vector<vector<string>>& data, int current_depth) {
    Node* node = new Node();
    node->size = data.size();

    if (current_depth >= max_depth || data.size() <= 1) {
        node->is_leaf = true;
        return node;
    }

    int n_features = data[0].size();
    int feature = rand() % n_features;

    if (ut::is_number(data[0][feature])) return numerical_tree(data, current_depth, node, feature);
    else return categorical_tree(data, current_depth, node, feature);
}

Node* Tree::numerical_tree(vector<vector<string>>& data, int current_depth, Node* node, int feature) {
    vector<double> data_feat;

    for (size_t i = 0; i < data.size(); i++) {
        data_feat.push_back(stod(data[i][feature]));
    }

    double min_val = data_feat[0], max_val = data_feat[0];

    for (auto& x : data_feat) {
        min_val = min(min_val, x);
        max_val = max(max_val, x);
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

    vector<vector<string>> left_data, right_data;
    for (size_t i = 0; i < data.size(); i++) {
        if (data_feat[i] < split) left_data.push_back(data[i]);
        else right_data.push_back(data[i]);
    }

    node->left = build_tree(left_data, current_depth + 1);
    node->right = build_tree(right_data, current_depth + 1);

    return node;   
}

Node* Tree::categorical_tree(vector<vector<string>>& data, int current_depth, Node* node, int feature) {
    vector<string> categories;
    for (auto& row : data) {
        categories.push_back(row[feature]);
    }
    sort(categories.begin(), categories.end());
    categories.erase(unique(categories.begin(), categories.end()), categories.end());

    if (categories.size() <= 1) {
        node->is_leaf = true;
        return node;
    }

    std::mt19937 rng(std::random_device{}());
    std::uniform_int_distribution<int> dist(0, categories.size() - 1);
    string split_category = categories[dist(rng)];

    node->feature = feature;
    node->category = split_category;
    node->is_categorical = true;

    vector<vector<string>> left_data, right_data;
    for (auto& row : data) {
        if (row[feature] == split_category) left_data.push_back(row);
        else right_data.push_back(row);
    }

    node->left = build_tree(left_data, current_depth + 1);
    node->right = build_tree(right_data, current_depth + 1);

    return node;
}

int Tree::path_length(vector<string>& x) {
    return path_length_node(root, x, 0);
}

int Tree::path_length_node(Node* node, vector<string>& x, int depth) {
    if (node->is_leaf) return depth;
    if (node->is_categorical) {
        if (x[node->feature] == node->category) return path_length_node(node->left, x, depth + 1);
        else return path_length_node(node->right, x, depth + 1);
    } else {
        if (stod(x[node->feature]) < node->threshold) return path_length_node(node->left, x, depth + 1);
        else return path_length_node(node->right, x, depth + 1);
    }
}
