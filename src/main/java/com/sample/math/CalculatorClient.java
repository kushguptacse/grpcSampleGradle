package com.sample.math;

import com.sample.math.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

  public static void main(String[] args) {
    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 50054)
        .usePlaintext().build();
    CalculatorServiceBlockingStub serviceGrpc = CalculatorServiceGrpc
        .newBlockingStub(managedChannel);
    CalculatorResponse response = serviceGrpc
        .sum(CalculatorRequest.newBuilder().setA(10).setB(3).build());
    System.out.println("Response received");
    System.out.println(response.getResult());
  }
}
