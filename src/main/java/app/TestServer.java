package autotest;

import java.util.ArrayList;
import io.grpc.*;
import grpc.*;

public class TestServer extends EchoServiceGrpc.EchoServiceImplBase {
    String log = "";
    ArrayList<Integer> active_test_id;

    TestServer(){
        active_test_id = new ArrayList<Integer>();
    }

    @Override
    public void echo(EchoRequest request, io.grpc.stub.StreamObserver<EchoResponse> responseObserver) {
        System.out.println("receive:"+request.getMessage());
        EchoResponse response = EchoResponse.newBuilder().setMessage("hello from server:"+request.getMessage()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void insert(AddIdRequest request, io.grpc.stub.StreamObserver<EchoResponse> responseObserver){
        String response_str = "Added new Test";
        int client_id = request.getId();
        if(client_id != 0)
            response_str = "Access denied";
        else{
            // if there are alredy test with this name
            ;
        }
        log += request.getName() + "\n";
        
        System.out.println(log);

        EchoResponse response = EchoResponse.newBuilder().setMessage(response_str).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public static void main(String[] args) throws Exception{
        Server server = ServerBuilder
                .forPort(8080)
                .addService(new TestServer()).build();
        server.start();
        System.out.println("Server started");
        server.awaitTermination();
    }
}
