#pragma once
#include "Node.h"
#include "matrix.h"

using namespace std;

class Tree {
    public:
        Node* root;
        int max_depth;

        Tree(int max_depth);

        void fit(Matrix& data);
        int path_length(Matrix& x);

    private:
        Node* build_tree(Matrix& data, int current_depth);
        int path_length_node(Node* node, Matrix& x, int depth);
};
