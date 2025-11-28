#include "Forest.h"
#include "autoencoder.h"
#include <bits/stdc++.h>
#include "utils.h"
#include "Preprocessing.h"
#include <iostream>
#include <tuple>
#include "json.hpp"
#include <filesystem>

using json = nlohmann::json;


#define HASH_SIZE 4

using namespace std;

class Main {
	private:
		static tuple<vector<vector<string>>, vector<size_t>> read_data() {
			json j;
			std::ifstream f("data.json");
			if (!f.is_open()) throw std::runtime_error("Cannot open data file");
			f >> j;
			f.close();

			vector<vector<string>> data;
			vector<size_t> ids;

			if (!j.is_array())
				throw runtime_error("Expected a JSON array at the root");

			for (const auto& item : j) {
				if (!item.is_object())
					throw runtime_error("Each element in array must be a JSON object");

				vector<string> row;
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
			cout << "TRAINING" << endl;

			vector<bool> type_features = {false, false, false, false, false, true};
			json j;
			OneHotParams params;

			auto [train_data, ids] = Main::read_data();
			Matrix train_data_p = Preprocessing::preprocess_train(train_data, params, j, type_features);

			Preprocessing::save_config(j, "config/preprocessing.json");
			Autoencoder ae(train_data_p.cols, 32, 16, 8);
			ae.train(train_data_p, 5000, 0.05);

			ae.save_network();
		}

		static void run() {
			cout << "RUNNING" << endl;
			vector<bool> type_features = {false, false, false, false, false, true};
			auto [validate_data, ids] = Main::read_data();
			Matrix validate_data_p = Preprocessing::preprocess(validate_data, "config/preprocessing.json", type_features);

			Autoencoder ae(validate_data_p.cols, 32, 16, 8);
			ae.load_network();
			Matrix decoded = ae.forward(validate_data_p);
			Matrix result = Matrix::error(validate_data_p, decoded);

			Forest forest(400, 256);
			forest.fit(result);

			vector<double> scores;

			for (size_t i = 0; i < result.rows; i++) {
				Matrix x = Matrix::take_row(result, i);
				scores.push_back(forest.anomaly_score(x));
			}

			vector<bool> outliers = ut::hip_test(scores, 1);
			json j = json::array();

			int counter = 0;
			for (size_t i = 0; i < scores.size(); i++) {
				j.push_back({
						{"id", ids[i]},
						{"flag", outliers[i]}
				});
				if (outliers[i]) counter += 1;
				cout << "(";
				for (auto& r : validate_data[i]) cout << r << ",";
				cout << ") -> Score: " << scores[i] << " -> " << (outliers[i] ? "Outlier" : "Normal") << endl;
			}
			cout << counter << endl;
			
			ofstream f("response.json");
			f << j.dump(4);
			f.close();
		}

};


int main(int argc, char* argv[]) {
	if (argc != 2) {
		std::cerr << "[main] falha ao ler argv[1]" << endl;
		return 1;
	}
	if (strcmp(argv[1], "0") == 0) Main::train();
	else Main::run();

	return 0;
}
