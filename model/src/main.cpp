#include "Forest.h"
#include <bits/stdc++.h>

using namespace std;

double mean(const std::vector<double>& scores) {
    double sum = 0.0;
    for (double s : scores) sum += s;
    return sum / scores.size();
}

double stddev(const std::vector<double>& scores, double m) {
    double sum = 0.0;
    for (double s : scores) sum += (s - m) * (s - m);
    return std::sqrt(sum / scores.size());
}

std::vector<bool> detect_outliers(const std::vector<double>& scores) {
    double m = mean(scores);
    double s = stddev(scores, m);
    std::vector<bool> outliers;
    for (double score : scores) {
        outliers.push_back(score > m + 2 * s);
    }
    return outliers;
}


int main() {
    vector<vector<double>> data_test = {
        {1.0, 2.0}, {1.2, 2.1}, {0.8, 1.9},
        {8.0, 8.0}, {8.2, 7.9}, {7.8, 8.1},
        {20.0, 20.0}
    };

    Forest forest(50, 5);
    forest.fit(data_test);

    vector<double> scores;
    
    for (auto& x : data_test) {
        scores.push_back(forest.anomaly_score(x));
    }

    vector<bool> outliers = detect_outliers(scores);

    for (size_t i = 0; i < scores.size(); i++) {
        cout << "(" << data_test[i][0] << "," << data_test[i][1] << ") -> Score: " << scores[i] << " -> " << (outliers[i] ? "Outlier" : "Normal") << endl;
    }

    return 0;
}
