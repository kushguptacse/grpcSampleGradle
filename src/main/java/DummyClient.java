import com.dummy.sample.DummyServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class DummyClient {

  public static void main(String[] args) {
    System.out.println("Inside gRPC Client");
    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext() // only needed in development mode as if we dont useplaintext here gRPC will give SSL exception
        .build();
    System.out.println("Creating Stub");

    DummyServiceGrpc.DummyServiceBlockingStub blockingStub = DummyServiceGrpc.newBlockingStub(managedChannel);

    DummyServiceGrpc.DummyServiceFutureStub asyncStub = DummyServiceGrpc.newFutureStub(managedChannel);

    System.out.println("Shutting down channel");
    managedChannel.shutdown();
  }
}
