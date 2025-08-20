#pragma once
#include "Node.h"
#include <vector>

using namespace std;

class Tree {
    public:
        Node* root;
        int max_depth;

        Tree(int max_depth);

        void fit(vector<vector<double>>& data);
        int path_length(vector<double>& x);

    private:
        Node* build_tree(vector<vector<double>>& data, int current_depth);
        int path_length_node(Node* node, vector<double>& x, int depth);
};
