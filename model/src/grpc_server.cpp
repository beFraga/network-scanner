#include "packet_analysis.grpc.pb.h"
#include "Forest.h"
#include "autoencoder.h"
#include "Preprocessing.h"
#include "utils.h"

// --- NOVOS INCLUDES NECESSÁRIOS ---
#include <grpcpp/server.h>
#include <grpcpp/server_builder.h>
#include <grpcpp/security/server_credentials.h>
// ----------------------------------

#include <mutex>
#include <vector>
#include <string>
#include <iostream>

using grpc::Server;
using grpc::ServerBuilder;
using grpc::ServerContext;
using grpc::Status;
using grpc::ServerReader;

// Namespaces gerados pelo protoc
using packet::PacketAnalysisService;
using packet::PacketAnalyzer;
using packet::PacketWindow;
using packet::AnalysisAck;
using packet::EventBatch;
using packet::PacketEvent;
using packet::TcpMeta;
using google::protobuf::Empty;

class PacketServiceImpl final : public PacketAnalysisService::Service, public PacketAnalyzer::Service {
private:
    // Buffer para armazenar os resultados até o Java buscá-los
    std::vector<PacketEvent> processed_events;
    std::mutex mu_; // Mutex para proteger o acesso concorrente ao buffer

    // Instâncias da IA e configurações
    Autoencoder* ae = nullptr;
    // Variável auxiliar para rastrear o tamanho da entrada atual da rede
    int current_ae_cols = 0;

    OneHotParams params;
    std::vector<bool> type_features = {false, false, false, false, false, true};
    std::string config_path = "config/preprocessing.json";

public:
    PacketServiceImpl() {
        // Tenta carregar uma rede padrão, se houver configuração salva.
        // Se não houver, o ae permanece null até o primeiro request chegar.
        try {
            // Nota: Se não soubermos o tamanho exato agora, deixamos para instanciar
            // no primeiro StreamWindow.
            std::cout << "[Server] Serviço iniciado. Aguardando pacotes para carregar/configurar IA." << std::endl;
        } catch (...) {
            std::cerr << "[Server] Erro na inicialização." << std::endl;
        }
    }

    // RPC 1: Recebe janela de pacotes do Java
    Status StreamWindow(ServerContext* context, ServerReader<PacketWindow>* reader, AnalysisAck* response) override {
        PacketWindow window;
        std::vector<std::vector<std::string>> batch_data;
        std::vector<int64_t> batch_ids;
        std::vector<TcpMeta> raw_packets;

        std::cout << "[Server] Recebendo stream de pacotes..." << std::endl;

        // 1. Ler o stream do gRPC
        while (reader->Read(&window)) {
            std::cout << "iniciando leitura: " << std::endl;
            for (const auto& tcp : window.tcp_packets()) {
                std::vector<std::string> row;

                // Conversão: Proto -> Vector<string>
                row.push_back(tcp.method());
                row.push_back(tcp.protocol());
                row.push_back(std::to_string(tcp.id()));
                row.push_back(std::to_string(tcp.sequence_number()));
                row.push_back(tcp.local_address());
                row.push_back(tcp.remote_address());
                row.push_back(std::to_string(tcp.remote_port()));
                row.push_back(std::to_string(tcp.payload_size())); // Placeholder PayloadSize

                batch_data.push_back(row);
                batch_ids.push_back(tcp.id());
                raw_packets.push_back(tcp);
            }
            std::cout << "fim de leitura" << endl;
        }

        if (batch_data.empty()) {
            response->set_accepted(true);
            response->set_message("Janela vazia.");
            return Status::OK;
        }

        // 2. Processamento IA
        try {
            // Pré-processamento
            Matrix validate_data_p = Preprocessing::preprocess(batch_data, config_path, type_features);

            // Verifica se precisamos instanciar ou recriar o Autoencoder
            // (Se ae for nulo OU se o número de colunas mudou)
            if (ae == nullptr || current_ae_cols != validate_data_p.cols) {
                if (ae != nullptr) delete ae;

                std::cout << "[Server] Carregando Autoencoder com input: " << validate_data_p.cols << std::endl;
                ae = new Autoencoder(validate_data_p.cols, 32, 16, 8);
                ae->load_network();
                current_ae_cols = validate_data_p.cols; // Atualiza nosso rastreador interno
            }

            Matrix decoded = ae->forward(validate_data_p);
            Matrix result = Matrix::error(validate_data_p, decoded);

            // Isolation Forest
            Forest forest(400, 256);
            forest.fit(result);

            std::vector<double> scores;
            for (size_t i = 0; i < result.rows; i++) {
                Matrix x = Matrix::take_row(result, i);
                scores.push_back(forest.anomaly_score(x));
            }

            // Teste de Hipótese (Outliers)
            std::vector<bool> outliers = ut::hip_test(scores, 1);

            // 3. Guardar resultados
            {
                std::lock_guard<std::mutex> lock(mu_);
                int outliers_count = 0;
                for (size_t i = 0; i < scores.size(); i++) {
                    // Opcional: só guardar se for outlier para economizar banda,
                    // mas seu proto EventBatch sugere enviar tudo ou flaggeados.
                    // Aqui enviamos todos com a flag setada corretamente.

                    PacketEvent event;
                    event.set_method(raw_packets[i].method());
                    event.set_protocol(raw_packets[i].protocol());
                    event.set_id(raw_packets[i].id());
                    event.set_local_address(raw_packets[i].local_address());
                    event.set_remote_address(raw_packets[i].remote_address());
                    event.set_remote_port(raw_packets[i].remote_port());
                    event.set_sequence_number(raw_packets[i].sequence_number());
                    event.set_payload_size(raw_packets[i].payload_size());
                    event.set_flag(outliers[i]);

                    processed_events.push_back(event);
                    if(outliers[i]) outliers_count++;
                }
                std::cout << "[Server] Batch processado. Outliers: " << outliers_count << "/" << scores.size() << std::endl;
            }

            response->set_accepted(true);
            response->set_message("OK");

        } catch (const std::exception& e) {
            std::cerr << "Erro na IA: " << e.what() << std::endl;
            response->set_accepted(false);
            response->set_message(e.what());
            // Não retornamos CANCELLED para não quebrar o cliente Java, apenas avisamos no ack
            return Status::OK;
        }

        return Status::OK;
    }

    // RPC 2: Java busca os eventos processados
    Status GetEvents(ServerContext* context, const Empty* request, EventBatch* response) override {
        std::lock_guard<std::mutex> lock(mu_);

        for (const auto& evt : processed_events) {
            *response->add_events() = evt;
        }

        processed_events.clear();
        return Status::OK;
    }
};

void RunServer() {
    std::string server_address("0.0.0.0:50051");
    PacketServiceImpl service;

    ServerBuilder builder;
    builder.AddListeningPort(server_address, grpc::InsecureServerCredentials());
    builder.RegisterService((PacketAnalysisService::Service*)&service);
    builder.RegisterService((PacketAnalyzer::Service*)&service);

    std::unique_ptr<Server> server(builder.BuildAndStart());
    std::cout << "Servidor gRPC escutando em " << server_address << std::endl;
    server->Wait();
}