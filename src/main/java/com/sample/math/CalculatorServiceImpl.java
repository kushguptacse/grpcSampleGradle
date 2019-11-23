package com.sample.math;

import com.sample.math.CalculatorServiceGrpc.CalculatorServiceImplBase;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceImplBase {

  @Override
  public void sum(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
    int a = request.getA();
    int b = request.getB();
    responseObserver.onNext(CalculatorResponse.newBuilder().setResult(a + b).build());
    responseObserver.onCompleted();
  }

  @Override
  public void primeNumberDecomposition(PrimeNumberDecompositionRequest request,
      StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
    int n = request.getNumber();
    int k = 2;
    try {
      while (n > 1) {
        if (n % k == 0) {
          responseObserver
              .onNext(PrimeNumberDecompositionResponse.newBuilder().setResult(k).build());
          Thread.sleep(1000);
          n = n / k;
        } else {
          k++;
        }
      }
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    } finally {
      responseObserver.onCompleted();
    }
  }

  @Override
  public StreamObserver<ComputeAverageRequest> computeAverage(
      StreamObserver<ComputeAverageResponse> responseObserver) {
    StreamObserver<ComputeAverageRequest> StreamObserverRequest = new StreamObserver<ComputeAverageRequest>() {
      int sum = 0;
      double count = 0;

      @Override
      public void onNext(ComputeAverageRequest computeAverageRequest) {
        sum = sum + computeAverageRequest.getNumber();
        count++;
      }

      @Override
      public void onError(Throwable t) {
        t.printStackTrace();
      }

      @Override
      public void onCompleted() {
        responseObserver
            .onNext(ComputeAverageResponse.newBuilder().setNumber(sum / count).build());
        responseObserver.onCompleted();
      }
    };
    return StreamObserverRequest;
  }

  @Override
  public StreamObserver<NumberRequest> findMaximum(
      StreamObserver<NumberResponse> responseObserver) {

    StreamObserver<NumberRequest> numberRequestStreamObserver = new StreamObserver<NumberRequest>() {
      int max = 0;

      @Override
      public void onNext(NumberRequest numberRequest) {
        System.out.println("Received Request "+ numberRequest.getNumber());
        if (numberRequest.getNumber() > max) {
          max = numberRequest.getNumber();
          responseObserver.onNext(NumberResponse.newBuilder().setMax(max).build());
        }
      }

      @Override
      public void onError(Throwable t) {
        t.printStackTrace();
      }

      @Override
      public void onCompleted() {
        System.out.println("All Request received");
        responseObserver.onCompleted();
      }
    };
    return numberRequestStreamObserver;
  }
}
