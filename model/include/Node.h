#pragma once
#include <vector>
#include <string>

using namespace std;

struct Node {
    bool is_leaf;
    int feature;
    double threshold;
    int size;
    Node* left;
    Node* right;

    Node() : is_leaf(false), feature(-1), threshold(0), size(0), left(nullptr), right(nullptr) {}
};
