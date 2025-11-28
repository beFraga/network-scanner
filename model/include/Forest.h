#pragma once
#include "Tree.h"
#include <vector>

using namespace std;

class Forest {
    public:
        int n_trees;
        int max_depth;
        int sample_size;
        vector<Tree> trees;

        Forest(int n_trees=100, int sample_size=256);

        void fit(Matrix& data);
        double anomaly_score(Matrix& x);

    private:
        double c_factor(int n);
};
