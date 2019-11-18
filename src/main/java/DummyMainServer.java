import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class DummyMainServer {

  public static void main(String[] args) {
    System.out.println("Hello gRPC!!");
    Server server1 = ServerBuilder.forPort(50051).build();
    try {
      server1.start();
      server1.shutdownNow();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("shutdown server");
  }

}
