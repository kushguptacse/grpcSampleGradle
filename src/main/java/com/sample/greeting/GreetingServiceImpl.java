package com.sample.greeting;

import com.sample.greet.GreetManyTimesRequest;
import com.sample.greet.GreetManyTimesResponse;
import com.sample.greet.GreetRequest;
import com.sample.greet.GreetResponse;
import com.sample.greet.GreetServiceGrpc.GreetServiceImplBase;
import io.grpc.stub.StreamObserver;

public class GreetingServiceImpl extends GreetServiceImplBase {

  @Override
  public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
    //extract fields from request object.
    String firstName = request.getGreeting().getFirstName();
    String lastName = request.getGreeting().getLastName();
    //prepare response object using request fields
    GreetResponse response = GreetResponse.newBuilder()
        .setResultId("hello: " + firstName + " " + lastName).build();
    //send the response
    responseObserver.onNext(response);
    //complete the rpc call
    responseObserver.onCompleted();
  }

  @Override
  public void greetManyService(GreetManyTimesRequest request,
      StreamObserver<GreetManyTimesResponse> responseObserver) {
    //extract fields from request object.
    String firstName = request.getGreeting().getFirstName();
    String lastName = request.getGreeting().getLastName();
    //prepare response object using request fields
    try {
      for (int i = 0; i < 10; i++) {
        GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
            .setResult("hi: " + firstName + " " + lastName + " ,Response " + i).build();
        responseObserver.onNext(response);
        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      responseObserver.onCompleted();
    }
  }
}
