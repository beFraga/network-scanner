class PacketAnalysisServiceImpl final
  : public PacketAnalysisService::Service {

  grpc::Status StreamWindow(
    grpc::ServerContext* context,
    grpc::ServerReader<PacketWindow>* reader,
    AnalysisAck* reply) override {

    PacketWindow window;
    while (reader->Read(&window)) {
        // Atualiza modelo
    }

    reply->set_accepted(true);
    reply->set_message("Window processed");
    return grpc::Status::OK;
  }
};
