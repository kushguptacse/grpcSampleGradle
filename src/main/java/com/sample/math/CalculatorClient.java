package com.sample.math;

import com.sample.math.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import com.sample.math.CalculatorServiceGrpc.CalculatorServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

  public static void main(String[] args) {
    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 50054)
        .usePlaintext().build();
    //unary api test - sum of two numbers
    unaryApiTest(managedChannel);

    System.out.println("-------------------------");
    //client stream api test
    clientStreamTest(managedChannel);

    System.out.println("-------------------------");
    //server stream api- returns a stream of Responses that represent the prime number decomposition of that number
    serverStreamTest(managedChannel);
  }


  private static void clientStreamTest(ManagedChannel managedChannel) {
    System.out.println("Client Stream test started");
    CalculatorServiceStub calculatorServiceStub = CalculatorServiceGrpc.newStub(managedChannel);
    CountDownLatch latch = new CountDownLatch(1);
    StreamObserver<ComputeAverageRequest> computeAverageRequestStreamObserver = calculatorServiceStub
        .computeAverage(new StreamObserver<ComputeAverageResponse>() {

          @Override
          public void onNext(ComputeAverageResponse computeAverageResponse) {
            System.out.println("Response received !!");
            System.out.println(computeAverageResponse.getNumber());
          }

          @Override
          public void onError(Throwable t) {
            t.printStackTrace();
          }

          @Override
          public void onCompleted() {
            System.out.println("Response completed!!");
            latch.countDown();
          }
        });
    try {
      for (int i = 0; i < 20; i++) {
        computeAverageRequestStreamObserver
            .onNext(ComputeAverageRequest.newBuilder().setNumber(i).build());
      }

      latch.await(13, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    computeAverageRequestStreamObserver.onCompleted();
  }

  private static void unaryApiTest(ManagedChannel managedChannel) {
    System.out.println("unary api test started");
    CalculatorServiceBlockingStub serviceGrpc = CalculatorServiceGrpc
        .newBlockingStub(managedChannel);
    System.out.println("Number needed to be added are 10 and 3 : ");
    CalculatorResponse response = serviceGrpc
        .sum(CalculatorRequest.newBuilder().setA(10).setB(3).build());
    System.out.println("Response received");
    System.out.println(response.getResult());
  }

  private static void serverStreamTest(ManagedChannel managedChannel) {
    System.out.println("server Stream test started");
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
