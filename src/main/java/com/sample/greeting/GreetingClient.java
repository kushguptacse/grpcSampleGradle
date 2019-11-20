package com.sample.greeting;

import com.sample.greet.GreetManyTimesRequest;
import com.sample.greet.GreetManyTimesResponse;
import com.sample.greet.GreetRequest;
import com.sample.greet.GreetResponse;
import com.sample.greet.GreetServiceGrpc;
import com.sample.greet.GreetServiceGrpc.GreetServiceBlockingStub;
import com.sample.greet.GreetServiceGrpc.GreetServiceFutureStub;
import com.sample.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Iterator;
import java.util.List;

public class GreetingClient {

  public static void main(String[] args) {
    System.out.println("inside Greeting client main method");
    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 50052)
        .usePlaintext().build();
    System.out.println("creating Blocking stub");
    GreetServiceBlockingStub greetServiceBlockingStub = GreetServiceGrpc
        .newBlockingStub(managedChannel);
    testUnaryApi(greetServiceBlockingStub);
    testServerStreamingApi(greetServiceBlockingStub);
    // testServerStreamingApi(GreetServiceGrpc.newFutureStub(managedChannel));

    managedChannel.shutdown();
  }

  private static void testServerStreamingApi(GreetServiceBlockingStub greetServiceBlockingStub) {
    System.out.println("Server Response received");
    Iterator<GreetManyTimesResponse> responseIterator = greetServiceBlockingStub
        .greetManyService(GreetManyTimesRequest.newBuilder().setGreeting(
            Greeting.newBuilder().setFirstName("kush").setLastName("Gupta").build()).build());
    while (responseIterator.hasNext()) {
      System.out.println(responseIterator.next().getResult());
    }
  }

  //private static void testServerStreamingApi(GreetServiceFutureStub greetServiceFutureStub) {
  //  System.out.println("Server Response received");
  //  GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder().setGreeting(
  //      Greeting.newBuilder().setFirstName("kush").setLastName("Gupta").build();
  //  greetServiceFutureStub
  //      .greetManyService(greetManyTimesRequest).build().forEachRemaining(obj-> System.out.println(obj.getResult()));
  // }

  private static void testUnaryApi(GreetServiceBlockingStub greetServiceBlockingStub) {
    GreetResponse response = greetServiceBlockingStub.greet(GreetRequest.newBuilder()
        .setGreeting(Greeting.newBuilder().setFirstName("kush").setLastName("gupta").build())
        .build());
    System.out.println("Unary Response received");
    System.out.println(response.getResultId());
  }

}
