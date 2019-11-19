package com.sample.greeting;

import com.sample.greet.GreetRequest;
import com.sample.greet.GreetResponse;
import com.sample.greet.GreetServiceGrpc;
import com.sample.greet.GreetServiceGrpc.GreetServiceBlockingStub;
import com.sample.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

  public static void main(String[] args) {
    System.out.println("inside Greeting client main method");
    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 50052)
        .usePlaintext().build();
    System.out.println("creating stub");
    GreetServiceBlockingStub greetServiceBlockingStub = GreetServiceGrpc
        .newBlockingStub(managedChannel);
    GreetResponse response =  greetServiceBlockingStub.greet(GreetRequest.newBuilder()
        .setGreeting(Greeting.newBuilder().setFirstName("kush").setLastName("gupta").build())
        .build());
    System.out.println("Response received");
    System.out.println(response.getResultId());
  }

}
