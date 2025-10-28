#include "Forest.h"
#include "autoencoder.h"
#include <bits/stdc++.h>
#include "utils.h"
#include "Preprocessing.h"
#include <iostream>


#define HASH_SIZE 4

using namespace std;

class Main {
	private:
		static vector<vector<string>> generate_anomaly_dataset(int num_samples, int num_anomalies) {
			std::vector<std::vector<std::string>> dataset;

			// Geradores de números aleatórios
			std::random_device rd;
			std::mt19937 gen(rd());

			// Distribuições para features numéricas "normais"
			std::normal_distribution<> temp_dist(25.0, 2.0); // Temperatura média 25, desvio 2
			std::normal_distribution<> humidity_dist(60.0, 5.0); // Umidade média 60, desvio 5
			std::normal_distribution<> pressure_dist(1010.0, 3.0); // Pressão média 1010, desvio 3

			// Possíveis valores para features categóricas
			vector<string> estado_sensor_options = {"Normal", "Aviso", "Critico"};
			vector<string> localizacao_options = {"Sala_A", "Sala_B", "Corredor", "Exterior"};
			vector<string> status_equipamento_options = {"Online", "Offline", "Manutencao"};

			// Para escolher categorias aleatoriamente
			std::uniform_int_distribution<> dist_estado(0, estado_sensor_options.size() - 1);
			std::uniform_int_distribution<> dist_localizacao(0, localizacao_options.size() - 1);
			std::uniform_int_distribution<> dist_status(0, status_equipamento_options.size() - 1);

			// Gerar amostras normais
			for (int i = 0; i < num_samples - num_anomalies; ++i) {
				vector<string> sample;
				// Numéricas
				sample.push_back(std::to_string(temp_dist(gen)));
				sample.push_back(std::to_string(humidity_dist(gen)));
				sample.push_back(std::to_string(pressure_dist(gen)));
				// Categóricas (favorecer "Normal", "Online")
				sample.push_back(estado_sensor_options[dist_estado(gen) % 2]); // Mais chance de Normal/Aviso
				sample.push_back(localizacao_options[dist_localizacao(gen)]);
				sample.push_back(status_equipamento_options[dist_status(gen) % 2]); // Mais chance de Online/Manutencao
				dataset.push_back(sample);
			}

			// Gerar amostras anômalas
			for (int i = 0; i < num_anomalies; ++i) {
				vector<string> sample;

				// Anomalias Numéricas (valores extremos)
				// Por exemplo, Temperatura muito alta/baixa, Umidade muito baixa, Pressão incomum
				std::uniform_real_distribution<> anomaly_temp_dist(35.0, 45.0); // Temperatura alta
				std::uniform_real_distribution<> anomaly_humidity_dist(10.0, 20.0); // Umidade muito baixa
				std::uniform_real_distribution<> anomaly_pressure_dist(980.0, 990.0); // Pressão muito baixa

				sample.push_back(std::to_string(anomaly_temp_dist(gen)));
				sample.push_back(std::to_string(anomaly_humidity_dist(gen)));
				sample.push_back(std::to_string(anomaly_pressure_dist(gen)));

				// Anomalias Categóricas (combinações raras ou valores "ruins")
				// Ex: Estado_Sensor "Critico" com Status_Equipamento "Offline"
				sample.push_back("Critico"); // Categoria anômala
				sample.push_back("Exterior"); // Categoria incomum para sensores internos
				sample.push_back("Offline");  // Categoria anômala

				dataset.push_back(sample);
			}

			// Embaralhar o dataset para misturar anomalias com dados normais
			//std::shuffle(dataset.begin(), dataset.end(), gen);

			return dataset;
		}
	public:
		static void train() {
			cout << "TRAINING" << endl;
			vector<vector<string>> train_data = Main::generate_anomaly_dataset(1000, 0);
			Matrix train_data_p = Preprocessing::preprocess(train_data, HASH_SIZE);

			Autoencoder ae(train_data_p.cols, 32, 16, 8);
			ae.train(train_data_p, 5000, 0.05);

			ae.save_network();

			vector<vector<string>> validate_data = Main::generate_anomaly_dataset(200, 20);

			for (auto i : validate_data[0]) cout << i << ", ";
			cout << endl;
			Matrix validate_data_p = Preprocessing::preprocess(validate_data, HASH_SIZE);
			for (auto i : Matrix::take_row(validate_data_p, 0).data) cout << i << ", ";
			cout << endl;

			Matrix latent = ae.encode(validate_data_p);

			Forest forest(400, 256);
			forest.fit(latent);

			vector<double> scores;

			for (size_t i = 0; i < latent.rows; i++) {
				Matrix x = Matrix::take_row(latent, i);
				scores.push_back(forest.anomaly_score(x));
			}

			vector<bool> outliers = ut::hip_test(scores, 1);
			int counter = 0;
			for (size_t i = 0; i < scores.size(); i++) {
				if (outliers[i]) counter += 1;
				cout << "(";
				for (auto& r : validate_data[i]) cout << r << ",";
				cout << ") -> Score: " << scores[i] << " -> " << (outliers[i] ? "Outlier" : "Normal") << endl;
			}
			cout << counter << endl;
		}

		static void run() {
			cout << "RUNNING" << endl;
			vector<vector<string>> validate_data = Main::generate_anomaly_dataset(200, 20);
			Matrix validate_data_p = Preprocessing::preprocess(validate_data, HASH_SIZE);

			Autoencoder ae(validate_data_p.cols, 32, 16, 8);
			ae.load_network();
			Matrix latent = ae.encode(validate_data_p);
			Forest forest(400, 256);
			forest.fit(latent);

			vector<double> scores;

			for (size_t i = 0; i < latent.rows; i++) {
				Matrix x = Matrix::take_row(latent, i);
				scores.push_back(forest.anomaly_score(x));
			}

			vector<bool> outliers = ut::hip_test(scores, 1);
			int counter = 0;
			for (size_t i = 0; i < scores.size(); i++) {
				if (outliers[i]) counter += 1;
				cout << "(";
				for (auto& r : validate_data[i]) cout << r << ",";
				cout << ") -> Score: " << scores[i] << " -> " << (outliers[i] ? "Outlier" : "Normal") << endl;
			}
			cout << counter << endl;

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
