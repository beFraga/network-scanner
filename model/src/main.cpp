#include "Forest.h"
#include "autoencoder.h"
#include <bits/stdc++.h>
#include "utils.h"
#include "Preprocessing.h"
#include <iostream>
#include <tuple>
#include "json.hpp"
#include <filesystem>

// Importante: declaração da função do servidor
void RunServer();

using json = nlohmann::json;
using namespace std;

// Classe Main mantida APENAS para o modo de treino (Legacy)
class LegacyTrainer {
    private:
        static tuple<vector<vector<string>>, vector<size_t>> read_data() {
            // ... (Mesmo código de leitura de JSON do seu main original para treino)
            // Mantive a leitura do data.json pois o treino geralmente é feito
            // com um dataset estático antes de rodar a aplicação em tempo real.
            json j;
            std::ifstream f("data.json");
            if (!f.is_open()) throw std::runtime_error("Cannot open data file for training");
            f >> j;
            f.close();

            vector<vector<string>> data;
            vector<size_t> ids;

            if (!j.is_array()) throw runtime_error("Expected a JSON array");

            for (const auto& item : j) {
                 vector<string> row;
                 // (Lógica original de parsing mantida para compatibilidade de treino)
                 for (auto it = item.begin(); it != item.end(); ++it) {
                    if (it.key() == "id") ids.push_back(it.value());
                    if (it.value().is_string()) row.push_back(it.value());
                    else if (it.value().is_number_integer()) row.push_back(to_string(it.value().get<long long>()));
                    else if (it.value().is_number_float()) row.push_back(to_string(it.value().get<double>()));
                    else if (it.value().is_boolean()) row.push_back(it.value().get<bool>() ? "true" : "false");
                    else if (it.value().is_null()) row.push_back("null");
                    else row.push_back(it.value().dump());
                }
                data.push_back(row);
            }
            return make_tuple(data, ids);
        }

    public:
        static void train() {
            cout << "TRAINING MODE (Legacy File Based)" << endl;
            vector<bool> type_features = {false, false, false, false, false, true};
            json j;
            OneHotParams params;

            try {
                auto [train_data, ids] = LegacyTrainer::read_data();
                Matrix train_data_p = Preprocessing::preprocess_train(train_data, params, j, type_features);

                Preprocessing::save_config(j, "config/preprocessing.json");
                Autoencoder ae(train_data_p.cols, 32, 16, 8);
                ae.train(train_data_p, 5000, 0.05);

                ae.save_network();
                cout << "Treino concluido e modelos salvos." << endl;
            } catch (exception& e) {
                cerr << "Erro no treino: " << e.what() << endl;
            }
        }
};

int main(int argc, char* argv[]) {
    if (argc != 2) {
        std::cerr << "Uso: ./isolation_forest <MODO>" << endl;
        std::cerr << "0 = Treinar (Lê data.json)" << endl;
        std::cerr << "1 = Rodar Servidor gRPC" << endl;
        return 1;
    }

    if (strcmp(argv[1], "0") == 0) {
        LegacyTrainer::train();
    } else {
        // Inicia o Servidor gRPC que substitui a lógica de loop de arquivos
        RunServer();
    }

    return 0;
}