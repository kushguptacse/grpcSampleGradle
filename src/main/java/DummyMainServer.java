import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class DummyMainServer {

  public static void main(String[] args) throws InterruptedException, IOException {
    System.out.println("Hello gRPC!!");
    Server server1 = ServerBuilder.forPort(50051).build();
    server1.start();
    //Runtime.getRuntime().addShutdownHook(new Thread(() -> {
     // System.out.println("Received Shutdown Request");
    //}));
    //server1.awaitTermination();
    server1.shutdown();
    System.out.println("stopped server!!");
  }

}
