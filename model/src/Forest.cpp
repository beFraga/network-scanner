#include "Forest.h"
#include <cstdlib>
#include <random>
#include <cmath>

#define EULERMAS 0.5772156649

Forest::Forest(int n_trees, int sample_size) : n_trees(n_trees), sample_size(sample_size) {
    max_depth = (int)ceil(log2(sample_size));
}


void Forest::fit(vector<vector<double>>& data) {
    trees.clear();
    int n_samples = data.size();

    std::mt19937 rng(std::random_device{}());
    std::uniform_int_distribution<int> dist(0, n_samples - 1);

    for (int i = 0; i < n_trees; i++) {
        vector<vector<double>> sample;
        for (int j = 0; j < sample_size; j++) {

            int idx = dist(rng);
            sample.push_back(data[idx]);
        }
        Tree tree(max_depth);
        tree.fit(sample);
        trees.push_back(tree);
    }
}

double Forest::c_factor(int n) {
    if (n <= 1) return 0;
    return 2.0 * (log(n - 1) + EULERMAS) - 2.0 * (n - 1) / n;
}

double Forest::anomaly_score(vector<double>& x) {
    double avg_path = 0.0;
    for (auto& tree : trees) {
        avg_path += tree.path_length(x);
    }
    avg_path /= n_trees;
    return pow(2.0, -avg_path / c_factor(sample_size));
}
