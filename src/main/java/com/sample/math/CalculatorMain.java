package com.sample.math;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class CalculatorMain {

  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println("server calculator started");
    Server server = ServerBuilder.forPort(50054).addService(new CalculatorServiceImpl()).build();
    server.start();
    Runtime.getRuntime().addShutdownHook(new Thread(()->{
      System.out.println("Shutdown started");
      server.shutdown();
      System.out
          .println("shutdown done");
    }));

    server.awaitTermination();
  }
}
