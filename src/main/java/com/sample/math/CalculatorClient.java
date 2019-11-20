package com.sample.math;

import com.sample.math.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

  public static void main(String[] args) {
    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 50054)
        .usePlaintext().build();
    //unary api test - sum of two numbers
    CalculatorServiceBlockingStub serviceGrpc = CalculatorServiceGrpc
        .newBlockingStub(managedChannel);
    System.out.println("Number needed to be added are 10 and 3 : ");
    CalculatorResponse response = serviceGrpc
        .sum(CalculatorRequest.newBuilder().setA(10).setB(3).build());
    System.out.println("Response received");
    System.out.println(response.getResult());

    //server stream api- returns a stream of Responses that represent the prime number decomposition of that number
    CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(managedChannel);
    System.out.println("Number send : " + 1200);
    System.out.println("Response is : ");
    stub.primeNumberDecomposition(
        PrimeNumberDecompositionRequest.newBuilder().setNumber(1200).build())
        .forEachRemaining(obj -> System.out.print(obj.getResult() + " "));
    System.out.println("\ncompleted");
    managedChannel.shutdown();
  }
}
