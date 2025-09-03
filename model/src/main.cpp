#include "Forest.h"
#include <bits/stdc++.h>
#include "utils.h"

using namespace std;

int main() {
    vector<vector<string>> data_test = {
        {"25","2500","corrente","SP"},
        {"30","3000","corrente","RJ"},
        {"40","5000","poupança","MG"},
        {"35","4000","poupança","RS"},
        {"50","7000","corrente","SP"},
        {"29","2800","corrente","RJ"},
        {"45","4500","poupança","MG"},
        {"32","3500","poupança","RS"},
        {"28","2700","corrente","SP"},
        {"55","8000","corrente","RJ"},
        {"38","4200","poupança","MG"},
        {"60","9000","corrente","RS"},
        {"42","4800","poupança","SP"},
        {"36","3900","poupança","RJ"},
        {"27","2600","corrente","MG"},
        {"65","9500","corrente","RS"},
        {"70","10000","investimento","SP"},
        {"33","3600","poupança","RJ"},
        {"37","4100","corrente","MG"},
        {"48","6800","poupança","RS"},
        {"26","2550","corrente","SP"},
        {"31","3100","poupança","RJ"},
        {"39","5200","corrente","MG"},
        {"34","3950","poupança","RS"},
        {"51","7100","corrente","SP"},
        {"30","3000","corrente","RJ"},
        {"46","4600","poupança","MG"},
        {"33","3550","corrente","RS"},
        {"29","2800","poupança","SP"},
        {"56","8100","corrente","RJ"},

        // Outliers
        {"23","10000","investimento","SP"},
        {"80","2000","corrente","MG"},
        {"29","28000","investimento","RJ"},
        {"60","12000","poupança","RS"},
        {"18","500","corrente","SP"}
    };

    Forest forest(5000, 500);
    forest.fit(data_test);

    vector<double> scores;

    for (auto& x : data_test) {
        scores.push_back(forest.anomaly_score(x));
    }

    vector<bool> outliers = ut::hip_test(scores, 1.5);

    for (size_t i = 0; i < scores.size(); i++) {
        cout << "(" << data_test[i][0] << "," << data_test[i][1] << ") -> Score: " << scores[i] << " -> " << (outliers[i] ? "Outlier" : "Normal") << endl;
    }

    return 0;
}
