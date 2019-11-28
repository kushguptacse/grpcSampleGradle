package com.sample.math;

import com.sample.math.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import com.sample.math.CalculatorServiceGrpc.CalculatorServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

  public static void main(String[] args) {
    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 50054)
        .usePlaintext().build();
    //unary api test - sum of two numbers
    // unaryApiTest(managedChannel);

    System.out.println("-------------------------");
    //client stream api test - compute avg of numbers
    //clientStreamTest(managedChannel);

    System.out.println("-------------------------");
    //server stream api- returns a stream of Responses that represent the prime number decomposition of that number
    //serverStreamTest(managedChannel);
    System.out.println("-------------------------");
    //bi-directional stream api - from input stream print max number
    bidiStreamTest(managedChannel);

    //error handling sample
    errorHandlingSquareRoot(managedChannel);

    managedChannel.shutdown();
  }

  private static void errorHandlingSquareRoot(ManagedChannel managedChannel) {
    System.out.println("------------------------------------");
    System.out.println("error Handling SquareRoot test started");
    CalculatorServiceBlockingStub calculatorServiceBlockingStub = CalculatorServiceGrpc
        .newBlockingStub(managedChannel);
    int input = 36;
    System.out.println("Square root of " + input + " number is - ");
    SquareRootResponse squareRootResponse = calculatorServiceBlockingStub
        .squareRoot(SquareRootRequest.newBuilder().setNumber(input).build());
    System.out.println(squareRootResponse.getResult());
    input = -4;
    try {
      System.out.println("Square root of " + input + " number is - ");
      squareRootResponse = calculatorServiceBlockingStub
          .squareRoot(SquareRootRequest.newBuilder().setNumber(input).build());
      System.out.println(squareRootResponse.getResult());
    } catch (StatusRuntimeException sre) {
      System.out.println("Exception occurred !!!!!!!");
      sre.printStackTrace();
    }
  }

  private static void bidiStreamTest(ManagedChannel managedChannel) {
    System.out.println("Bi Directional Stream test started");
    CalculatorServiceStub asyncStub = CalculatorServiceGrpc.newStub(managedChannel);
    CountDownLatch latch = new CountDownLatch(1);
    StreamObserver<NumberRequest> streamNumberRequest = asyncStub
        .findMaximum(new StreamObserver<NumberResponse>() {
          @Override
          public void onNext(NumberResponse numberResponse) {
            System.out.println("Current maximum element is : " + numberResponse.getMax());

          }

          @Override
          public void onError(Throwable t) {

          }

          @Override
          public void onCompleted() {
            System.out.println("Response finished");
            latch.countDown();
          }
        });
    streamNumberRequest.onNext(NumberRequest.newBuilder().setNumber(1).build());
    streamNumberRequest.onNext(NumberRequest.newBuilder().setNumber(5).build());
    streamNumberRequest.onNext(NumberRequest.newBuilder().setNumber(3).build());
    streamNumberRequest.onNext(NumberRequest.newBuilder().setNumber(6).build());
    streamNumberRequest.onNext(NumberRequest.newBuilder().setNumber(2).build());
    streamNumberRequest.onNext(NumberRequest.newBuilder().setNumber(20).build());
    streamNumberRequest.onCompleted();
    try {
      latch.await(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
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
