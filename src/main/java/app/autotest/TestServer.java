package app.autotest;

import app.teststruct.Question;

import java.util.ArrayList;
import java.util.HashMap;

import io.grpc.*;
import grpc.*;

public class TestServer extends EchoServiceGrpc.EchoServiceImplBase {
    String log = "";
    Integer global_id = 0;
    HashMap<Integer, String> test_map;
    HashMap<Integer, HashMap<Integer, Question>> que_map;
    ArrayList<Integer> active_test_id;

    TestServer(){
        active_test_id = new ArrayList<Integer>();
        test_map = new HashMap<Integer, String>();
        que_map = new HashMap<Integer, HashMap<Integer, Question>>();
    }



    @Override
    public void addtest(AddTestRequest request, io.grpc.stub.StreamObserver<IdResponse> responseObserver){
        int client_id = request.getId();
        int return_id = global_id;
        if(client_id == 0){
            test_map.put(global_id, request.getName());
            que_map.put(global_id, new HashMap<Integer, Question>());
            global_id++;
        } else {
           return_id = -1;
        }

        System.out.println(test_map);

        IdResponse response = IdResponse.newBuilder().setId(return_id).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addques(AddQuesRequest request, io.grpc.stub.StreamObserver<IdResponse> responseObserver) {
        int client_id = request.getId();
        int return_id = -1;
        int test_id = request.getTestId();
        if(client_id == 0 && test_id < global_id){
            que_map.get(test_id).put(global_id ,new Question(request.getName()));
            return_id = global_id;
            global_id++;
        } else{
            return_id = -1;
        }
        IdResponse response = IdResponse.newBuilder().setId(return_id).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }    


/*    @Override
    public void addansw(AddQuesRequest request, io.grpc.stub.StreamObserver<IdResponse> responseObserver) {
        que_map.forEach((key, hashtable) -> {
            if(hashtable.containsKey())
              return_id = 2;9
        });
    }*/

    public static void main(String[] args) throws Exception{
        Server server = ServerBuilder
                .forPort(8080)
                .addService(new TestServer()).build();
        server.start();
        System.out.println("Server started");
        server.awaitTermination();
    }
}
