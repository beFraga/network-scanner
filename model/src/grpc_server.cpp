#include "packet_analysis.grpc.pb.h"

using namespace packet;

class PacketAnalysisServiceImpl final
  : public PacketAnalysisService::Service {

public:
  grpc::Status StreamWindow(
    grpc::ServerContext* context,
    grpc::ServerReader<PacketWindow>* reader,
    AnalysisAck* reply) override {

    PacketWindow window;
    while (reader->Read(&window)) {}

    reply->set_accepted(true);
    reply->set_message("Window processed");
    return grpc::Status::OK;
  }
};
