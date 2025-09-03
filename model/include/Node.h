#pragma once
#include <vector>
#include <string>

using namespace std;

struct Node {
    bool is_leaf;
    bool is_categorical;
    int feature;
    double threshold;
    string category;
    int size;
    Node* left;
    Node* right;

    Node() : is_leaf(false), is_categorical(false), feature(-1), threshold(0), category(""), size(0), left(nullptr), right(nullptr) {}
};
