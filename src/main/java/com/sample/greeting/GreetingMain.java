package com.sample.greeting;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class GreetingMain {

  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println("Greeting From Main!!!");
    Server server = ServerBuilder.forPort(50052).addService(new GreetingServiceImpl()).build();
    server.start();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("shutdown request recieved");
      server.shutdown();
      System.out.println("shutdown done");
    }));
    server.awaitTermination();

  }
}
