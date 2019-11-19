package com.sample.greeting;

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
}
