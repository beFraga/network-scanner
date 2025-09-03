#pragma once
#include "Node.h"

using namespace std;

class Tree {
    public:
        Node* root;
        int max_depth;

        Tree(int max_depth);

        void fit(vector<vector<string>>& data);
        int path_length(vector<string>& x);

    private:
        Node* build_tree(vector<vector<string>>& data, int current_depth);
        Node* numerical_tree(vector<vector<string>>& data, int current_depth, Node* node, int feature);
        Node* categorical_tree(vector<vector<string>>& data, int current_depth, Node* node, int feature);
        int path_length_node(Node* node, vector<string>& x, int depth);
};
