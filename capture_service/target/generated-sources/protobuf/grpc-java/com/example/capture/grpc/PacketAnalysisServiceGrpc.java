package com.example.capture.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Service
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: packet_analysis.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class PacketAnalysisServiceGrpc {

  private PacketAnalysisServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "packet.PacketAnalysisService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.capture.grpc.PacketAnalysisProto.PacketWindow,
      com.example.capture.grpc.PacketAnalysisProto.AnalysisAck> getStreamWindowMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StreamWindow",
      requestType = com.example.capture.grpc.PacketAnalysisProto.PacketWindow.class,
      responseType = com.example.capture.grpc.PacketAnalysisProto.AnalysisAck.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<com.example.capture.grpc.PacketAnalysisProto.PacketWindow,
      com.example.capture.grpc.PacketAnalysisProto.AnalysisAck> getStreamWindowMethod() {
    io.grpc.MethodDescriptor<com.example.capture.grpc.PacketAnalysisProto.PacketWindow, com.example.capture.grpc.PacketAnalysisProto.AnalysisAck> getStreamWindowMethod;
    if ((getStreamWindowMethod = PacketAnalysisServiceGrpc.getStreamWindowMethod) == null) {
      synchronized (PacketAnalysisServiceGrpc.class) {
        if ((getStreamWindowMethod = PacketAnalysisServiceGrpc.getStreamWindowMethod) == null) {
          PacketAnalysisServiceGrpc.getStreamWindowMethod = getStreamWindowMethod =
              io.grpc.MethodDescriptor.<com.example.capture.grpc.PacketAnalysisProto.PacketWindow, com.example.capture.grpc.PacketAnalysisProto.AnalysisAck>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StreamWindow"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.capture.grpc.PacketAnalysisProto.PacketWindow.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.capture.grpc.PacketAnalysisProto.AnalysisAck.getDefaultInstance()))
              .setSchemaDescriptor(new PacketAnalysisServiceMethodDescriptorSupplier("StreamWindow"))
              .build();
        }
      }
    }
    return getStreamWindowMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PacketAnalysisServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PacketAnalysisServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PacketAnalysisServiceStub>() {
        @java.lang.Override
        public PacketAnalysisServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PacketAnalysisServiceStub(channel, callOptions);
        }
      };
    return PacketAnalysisServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PacketAnalysisServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PacketAnalysisServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PacketAnalysisServiceBlockingStub>() {
        @java.lang.Override
        public PacketAnalysisServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PacketAnalysisServiceBlockingStub(channel, callOptions);
        }
      };
    return PacketAnalysisServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PacketAnalysisServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PacketAnalysisServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PacketAnalysisServiceFutureStub>() {
        @java.lang.Override
        public PacketAnalysisServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PacketAnalysisServiceFutureStub(channel, callOptions);
        }
      };
    return PacketAnalysisServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Service
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default io.grpc.stub.StreamObserver<com.example.capture.grpc.PacketAnalysisProto.PacketWindow> streamWindow(
        io.grpc.stub.StreamObserver<com.example.capture.grpc.PacketAnalysisProto.AnalysisAck> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getStreamWindowMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service PacketAnalysisService.
   * <pre>
   * Service
   * </pre>
   */
  public static abstract class PacketAnalysisServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return PacketAnalysisServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service PacketAnalysisService.
   * <pre>
   * Service
   * </pre>
   */
  public static final class PacketAnalysisServiceStub
      extends io.grpc.stub.AbstractAsyncStub<PacketAnalysisServiceStub> {
    private PacketAnalysisServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PacketAnalysisServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PacketAnalysisServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.example.capture.grpc.PacketAnalysisProto.PacketWindow> streamWindow(
        io.grpc.stub.StreamObserver<com.example.capture.grpc.PacketAnalysisProto.AnalysisAck> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getStreamWindowMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service PacketAnalysisService.
   * <pre>
   * Service
   * </pre>
   */
  public static final class PacketAnalysisServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<PacketAnalysisServiceBlockingStub> {
    private PacketAnalysisServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PacketAnalysisServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PacketAnalysisServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service PacketAnalysisService.
   * <pre>
   * Service
   * </pre>
   */
  public static final class PacketAnalysisServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<PacketAnalysisServiceFutureStub> {
    private PacketAnalysisServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PacketAnalysisServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PacketAnalysisServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_STREAM_WINDOW = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_STREAM_WINDOW:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.streamWindow(
              (io.grpc.stub.StreamObserver<com.example.capture.grpc.PacketAnalysisProto.AnalysisAck>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getStreamWindowMethod(),
          io.grpc.stub.ServerCalls.asyncClientStreamingCall(
            new MethodHandlers<
              com.example.capture.grpc.PacketAnalysisProto.PacketWindow,
              com.example.capture.grpc.PacketAnalysisProto.AnalysisAck>(
                service, METHODID_STREAM_WINDOW)))
        .build();
  }

  private static abstract class PacketAnalysisServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PacketAnalysisServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.capture.grpc.PacketAnalysisProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PacketAnalysisService");
    }
  }

  private static final class PacketAnalysisServiceFileDescriptorSupplier
      extends PacketAnalysisServiceBaseDescriptorSupplier {
    PacketAnalysisServiceFileDescriptorSupplier() {}
  }

  private static final class PacketAnalysisServiceMethodDescriptorSupplier
      extends PacketAnalysisServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    PacketAnalysisServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PacketAnalysisServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PacketAnalysisServiceFileDescriptorSupplier())
              .addMethod(getStreamWindowMethod())
              .build();
        }
      }
    }
    return result;
  }
}
