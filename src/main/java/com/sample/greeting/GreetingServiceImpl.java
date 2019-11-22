package com.sample.greeting;

import com.sample.greet.GreetEveryOneRequest;
import com.sample.greet.GreetEveryOneResponse;
import com.sample.greet.GreetManyTimesRequest;
import com.sample.greet.GreetManyTimesResponse;
import com.sample.greet.GreetRequest;
import com.sample.greet.GreetResponse;
import com.sample.greet.GreetServiceGrpc.GreetServiceImplBase;
import com.sample.greet.LongGreetRequest;
import com.sample.greet.LongGreetResponse;
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

  @Override
  public StreamObserver<LongGreetRequest> longGreet(
      StreamObserver<LongGreetResponse> responseObserver) {
    StreamObserver<LongGreetRequest> requestStreamObserver = new StreamObserver<LongGreetRequest>() {
      String result = "Hi from";

      @Override
      public void onNext(LongGreetRequest value) {
        //when clients send message this is invoked.
        result =
            result + " " + value.getGreeting().getFirstName() + "-" + value.getGreeting()
                .getLastName();
      }

      @Override
      public void onError(Throwable t) {
        //when clients send error.
      }

      @Override
      public void onCompleted() {
        //when client is done with all the request.
        responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());
        responseObserver.onCompleted();
      }
    };
    return requestStreamObserver;
  }

  @Override
  public StreamObserver<GreetEveryOneRequest> greetEveryone(
      StreamObserver<GreetEveryOneResponse> responseObserver) {
    StreamObserver<GreetEveryOneRequest> streamObserver = new StreamObserver<GreetEveryOneRequest>() {
      int c=0;
      @Override
      public void onNext(GreetEveryOneRequest greetEveryOneRequest) {
        c++;
        responseObserver.onNext(
            GreetEveryOneResponse.newBuilder()
                .setResult(c+": Hi " + greetEveryOneRequest.getGreeting().getFirstName()).build());
      }

      @Override
      public void onError(Throwable t) {
        t.printStackTrace();
      }

      @Override
      public void onCompleted() {
        System.out.println("All request received !!");
        responseObserver.onCompleted();
      }
    };
    return streamObserver;
  }
}
