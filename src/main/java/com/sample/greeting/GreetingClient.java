package com.sample.greeting;

import com.sample.greet.GreetEveryOneRequest;
import com.sample.greet.GreetEveryOneResponse;
import com.sample.greet.GreetManyTimesRequest;
import com.sample.greet.GreetManyTimesResponse;
import com.sample.greet.GreetRequest;
import com.sample.greet.GreetResponse;
import com.sample.greet.GreetServiceGrpc;
import com.sample.greet.GreetServiceGrpc.GreetServiceBlockingStub;
import com.sample.greet.GreetServiceGrpc.GreetServiceStub;
import com.sample.greet.GreetWithDeadlineRequest;
import com.sample.greet.GreetWithDeadlineResponse;
import com.sample.greet.Greeting;
import com.sample.greet.LongGreetRequest;
import com.sample.greet.LongGreetResponse;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

  public static void main(String[] args) {
    System.out.println("inside Greeting client main method");
    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 50052)
        .usePlaintext().build();
    System.out.println("creating Blocking stub");
    GreetServiceBlockingStub greetServiceBlockingStub = GreetServiceGrpc
        .newBlockingStub(managedChannel);
    testUnaryDeadlineTest(greetServiceBlockingStub);
    //testUnaryApi(greetServiceBlockingStub);
    //testServerStreamingApi(greetServiceBlockingStub);
    //testClientStreamingApi(managedChannel);
    testBiDiApi(managedChannel);
    managedChannel.shutdown();
  }

  private static void testUnaryDeadlineTest(GreetServiceBlockingStub greetServiceBlockingStub) {
    System.out.println("Deadline test started !!");

    GreetWithDeadlineRequest request = GreetWithDeadlineRequest.newBuilder().setGreeting(
        Greeting.newBuilder().setFirstName("kush").build()).build();

    try {
      System.out.println("Calling rpc with deadline of 5000 milli seconds");
      GreetWithDeadlineResponse response = greetServiceBlockingStub
          .withDeadline(Deadline.after(5000, TimeUnit.MILLISECONDS))
          .greetWithDeadline(request);
      System.out.println("Response :" + response.getResultId());
    } catch (StatusRuntimeException sre) {
      if (Status.DEADLINE_EXCEEDED.getCode().equals(sre.getStatus().getCode())) {
        System.out.println("deadline exceeded and hence we dont want response ");
      } else {
        sre.printStackTrace();
      }
    }

    System.out.println("------------------------------------------------------");

    try {
      System.out.println("Calling rpc with deadline of 200 milli seconds");
      GreetWithDeadlineResponse response = greetServiceBlockingStub
          .withDeadline(Deadline.after(200, TimeUnit.MILLISECONDS))
          .greetWithDeadline(request);
      System.out.println("Response :" + response.getResultId());
    } catch (StatusRuntimeException sre) {
      if (Status.DEADLINE_EXCEEDED.getCode().equals(sre.getStatus().getCode())) {
        System.out.println("deadline exceeded and hence we dont want response ");
      } else {
        sre.printStackTrace();
      }
    }

  }

  private static void testBiDiApi(ManagedChannel managedChannel) {
    System.out.println("Inside Bi Directional streaming api");
    CountDownLatch latch = new CountDownLatch(1);
    GreetServiceStub asyncStub = GreetServiceGrpc.newStub(managedChannel);
    StreamObserver<GreetEveryOneRequest> greetEveryOneRequestStreamObserver = asyncStub
        .greetEveryone(new StreamObserver<GreetEveryOneResponse>() {

          @Override
          public void onNext(GreetEveryOneResponse greetEveryOneResponse) {
            System.out.println(
                " Server Response received : " + greetEveryOneResponse.getResult());
          }

          @Override
          public void onError(Throwable t) {
            t.printStackTrace();
            latch.countDown();
          }

          @Override
          public void onCompleted() {
            System.out.println("Server is done sending data");
            latch.countDown();
          }
        });

    greetEveryOneRequestStreamObserver.onNext(GreetEveryOneRequest.newBuilder().setGreeting(
        Greeting.newBuilder().setFirstName("kush").build()).build());
    greetEveryOneRequestStreamObserver.onNext(GreetEveryOneRequest.newBuilder().setGreeting(
        Greeting.newBuilder().setFirstName("luv").build()).build());
    greetEveryOneRequestStreamObserver.onNext(GreetEveryOneRequest.newBuilder().setGreeting(
        Greeting.newBuilder().setFirstName("rahul").build()).build());
    greetEveryOneRequestStreamObserver.onCompleted();
    try {
      latch.await(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void testClientStreamingApi(ManagedChannel managedChannel) {
    System.out.println("Inside Client streaming api");
    //initialize countdown latch with 1
    CountDownLatch latch = new CountDownLatch(1);
    GreetServiceStub stub = GreetServiceGrpc.newStub(managedChannel);
    StreamObserver<LongGreetRequest> requestStreamObserver = stub
        .longGreet(new StreamObserver<LongGreetResponse>() {
          @Override
          public void onNext(LongGreetResponse value) {
            // we get the response from the server
            //onNext method will be called only once here
            System.out.println("Received response from server - ");
            System.out.println(value.getResult());
          }

          @Override
          public void onError(Throwable t) {
            //when server sends error
            System.out.println(t.getStackTrace());
          }

          @Override
          public void onCompleted() {
            //server done with sending us the data.
            //it will be called right after onNext().
            System.out.println("Response completed!!");
            //decrement countdown latch by 1.
            latch.countDown();
          }
        });
    //send first stream request
    requestStreamObserver.onNext(LongGreetRequest.newBuilder()
        .setGreeting(Greeting.newBuilder().setFirstName("kush").setLastName("gupta").build())
        .build());
    //send second stream request
    requestStreamObserver.onNext(LongGreetRequest.newBuilder()
        .setGreeting(Greeting.newBuilder().setFirstName("luv").setLastName("gupta").build())
        .build());
    // send on completed
    requestStreamObserver.onCompleted();
    try {
      //wait for countdown latch to set to 0 for 3 seconds. i.e. it waits for server to send only response here
      //otherwise main method would have finsihed without waiting server response.
      latch.await(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

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

  private static void testUnaryApi(GreetServiceBlockingStub greetServiceBlockingStub) {
    GreetResponse response = greetServiceBlockingStub.greet(GreetRequest.newBuilder()
        .setGreeting(Greeting.newBuilder().setFirstName("kush").setLastName("gupta").build())
        .build());
    System.out.println("Unary Response received");
    System.out.println(response.getResultId());
  }

}
