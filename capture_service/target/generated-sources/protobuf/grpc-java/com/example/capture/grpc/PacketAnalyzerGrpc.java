package com.example.capture.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: packet_analysis.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class PacketAnalyzerGrpc {

  private PacketAnalyzerGrpc() {}

  public static final java.lang.String SERVICE_NAME = "packet.PacketAnalyzer";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.example.capture.grpc.PacketAnalysisProto.EventBatch> getGetEventsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetEvents",
      requestType = com.google.protobuf.Empty.class,
      responseType = com.example.capture.grpc.PacketAnalysisProto.EventBatch.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.example.capture.grpc.PacketAnalysisProto.EventBatch> getGetEventsMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.example.capture.grpc.PacketAnalysisProto.EventBatch> getGetEventsMethod;
    if ((getGetEventsMethod = PacketAnalyzerGrpc.getGetEventsMethod) == null) {
      synchronized (PacketAnalyzerGrpc.class) {
        if ((getGetEventsMethod = PacketAnalyzerGrpc.getGetEventsMethod) == null) {
          PacketAnalyzerGrpc.getGetEventsMethod = getGetEventsMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, com.example.capture.grpc.PacketAnalysisProto.EventBatch>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetEvents"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.capture.grpc.PacketAnalysisProto.EventBatch.getDefaultInstance()))
              .setSchemaDescriptor(new PacketAnalyzerMethodDescriptorSupplier("GetEvents"))
              .build();
        }
      }
    }
    return getGetEventsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PacketAnalyzerStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PacketAnalyzerStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PacketAnalyzerStub>() {
        @java.lang.Override
        public PacketAnalyzerStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PacketAnalyzerStub(channel, callOptions);
        }
      };
    return PacketAnalyzerStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PacketAnalyzerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PacketAnalyzerBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PacketAnalyzerBlockingStub>() {
        @java.lang.Override
        public PacketAnalyzerBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PacketAnalyzerBlockingStub(channel, callOptions);
        }
      };
    return PacketAnalyzerBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PacketAnalyzerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PacketAnalyzerFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PacketAnalyzerFutureStub>() {
        @java.lang.Override
        public PacketAnalyzerFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PacketAnalyzerFutureStub(channel, callOptions);
        }
      };
    return PacketAnalyzerFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getEvents(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.example.capture.grpc.PacketAnalysisProto.EventBatch> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetEventsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service PacketAnalyzer.
   */
  public static abstract class PacketAnalyzerImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return PacketAnalyzerGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service PacketAnalyzer.
   */
  public static final class PacketAnalyzerStub
      extends io.grpc.stub.AbstractAsyncStub<PacketAnalyzerStub> {
    private PacketAnalyzerStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PacketAnalyzerStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PacketAnalyzerStub(channel, callOptions);
    }

    /**
     */
    public void getEvents(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.example.capture.grpc.PacketAnalysisProto.EventBatch> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetEventsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service PacketAnalyzer.
   */
  public static final class PacketAnalyzerBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<PacketAnalyzerBlockingStub> {
    private PacketAnalyzerBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PacketAnalyzerBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PacketAnalyzerBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.example.capture.grpc.PacketAnalysisProto.EventBatch getEvents(com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetEventsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service PacketAnalyzer.
   */
  public static final class PacketAnalyzerFutureStub
      extends io.grpc.stub.AbstractFutureStub<PacketAnalyzerFutureStub> {
    private PacketAnalyzerFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PacketAnalyzerFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PacketAnalyzerFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.capture.grpc.PacketAnalysisProto.EventBatch> getEvents(
        com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetEventsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_EVENTS = 0;

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
        case METHODID_GET_EVENTS:
          serviceImpl.getEvents((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<com.example.capture.grpc.PacketAnalysisProto.EventBatch>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetEventsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.google.protobuf.Empty,
              com.example.capture.grpc.PacketAnalysisProto.EventBatch>(
                service, METHODID_GET_EVENTS)))
        .build();
  }

  private static abstract class PacketAnalyzerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PacketAnalyzerBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.capture.grpc.PacketAnalysisProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PacketAnalyzer");
    }
  }

  private static final class PacketAnalyzerFileDescriptorSupplier
      extends PacketAnalyzerBaseDescriptorSupplier {
    PacketAnalyzerFileDescriptorSupplier() {}
  }

  private static final class PacketAnalyzerMethodDescriptorSupplier
      extends PacketAnalyzerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    PacketAnalyzerMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (PacketAnalyzerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PacketAnalyzerFileDescriptorSupplier())
              .addMethod(getGetEventsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
