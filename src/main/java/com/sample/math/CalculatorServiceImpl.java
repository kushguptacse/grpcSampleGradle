package com.sample.math;

import com.sample.math.CalculatorServiceGrpc.CalculatorServiceImplBase;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceImplBase {

  @Override
  public void sum(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
    int a = request.getA();
    int b = request.getB();
    responseObserver.onNext(CalculatorResponse.newBuilder().setResult(a+b).build());
    responseObserver.onCompleted();

  }
}
