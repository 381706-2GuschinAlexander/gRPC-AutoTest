package app.autotest;

import app.teststruct.ActiveTest;
import app.teststruct.Answer;
import app.teststruct.Question;
import app.teststruct.Test;

import java.util.ArrayList;
import java.util.HashMap;

import io.grpc.*;
import grpc.*;

public class TestServer extends EchoServiceGrpc.EchoServiceImplBase {
    String log = "";
    Integer global_id = 0;
    Integer test_count = 1;
    HashMap<Integer, Test> test_map;
    HashMap<Integer, Question> que_map;
    HashMap<Integer, ActiveTest> active_test;

    TestServer(){
        test_map = new HashMap<Integer, Test>();
        que_map = new HashMap<Integer, Question>();
        active_test = new HashMap<Integer, ActiveTest>();
    }

    public static void main(String[] args) throws Exception{
        Server server = ServerBuilder
                .forPort(8080)
                .addService(new TestServer()).build();
        server.start();
        System.out.println("Server started");
        server.awaitTermination();
    }


    @Override
    public void addtest(AddTestRequest request, io.grpc.stub.StreamObserver<IdResponse> responseObserver){
        int client_id = request.getId();
        int return_id = global_id;
        if(client_id == 0){
            test_map.put(global_id, new Test(request.getName()));
            global_id++;
        } else {
           return_id = -1;
        }

       //System.out.println(test_map);

        IdResponse response = IdResponse.newBuilder().setId(return_id).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addques(AddQuesRequest request, io.grpc.stub.StreamObserver<IdResponse> responseObserver) {
        int client_id = request.getId();
        int return_id = -1;
        int test_id = request.getTestId();
        if(client_id == 0 && test_map.containsKey(test_id) == true){
            que_map.put(global_id, new Question(request.getName()));
            test_map.get(test_id).add(global_id);
            return_id = global_id;
            global_id++;
        } else{
            return_id = -1;
        }
        IdResponse response = IdResponse.newBuilder().setId(return_id).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addansw(AddAnswRequest request, io.grpc.stub.StreamObserver<IdResponse> responseObserver){
        int client_id = request.getId();
        int question_id = request.getQuestionId();
        int return_id = 0;
        if(client_id != 0 || que_map.containsKey(question_id) == false)
            return_id = -1;
        else{
            int size = request.getNameCount();
            for(int i = 0; i < size; ++i){
                String name = request.getName(i);
                boolean isTrue = request.getIsTrue(i);
                int pointTaken = request.getPointTaken(i);
                int pointSkip = request.getPointSkiped(i);
                que_map.get(question_id).add(new Answer(name,isTrue,pointTaken,pointSkip));
            }
        }
        IdResponse response = IdResponse.newBuilder().setId(return_id).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void seetest(NullRequest request, io.grpc.stub.StreamObserver<TestResponse> responseObserver){
        TestResponse.Builder testbuild = TestResponse.newBuilder();
        test_map.forEach((k,v)->{
            testbuild.addTestId(k);
            testbuild.addName(v.getName());
        });
        TestResponse response = testbuild.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void starttest(StartTestRequest request, io.grpc.stub.StreamObserver<IdResponse> responseObserver){
        int id = request.getTestId();
        int client_id = request.getClientId();
        int return_id = -1;
        if(test_map.containsKey(id) == true && active_test.containsKey(client_id) == false){
            return_id = test_count;
            ActiveTest new_test = new ActiveTest();
            ArrayList<Integer> iter = test_map.get(id).getArray();
            iter.forEach(i -> {
                //System.out.println("added questin");
                Question tmp = que_map.get(i);
                new_test.addQuestion(tmp);
            });
            active_test.put(test_count, new_test);
            test_count++;
        }
        IdResponse response = IdResponse.newBuilder().setId(return_id).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void seeques(QuestionRequest request, io.grpc.stub.StreamObserver<QuestionResponse> responseObserver){
        int id = request.getClientId();
        QuestionResponse.Builder builder = QuestionResponse.newBuilder();
        if(active_test.containsKey(id) == true){
            ActiveTest ir = active_test.get(id);
            while(ir.peekQuestion().getVariants().size() == 0 && ir.peekQuestion() != null)
                ir.nextQuestion();
            Question tmp = active_test.get(id).peekQuestion();
            
            if(tmp == null){
                builder.setName("No question left behind");
            } else {
                builder.setName(tmp.toString());
                int k = tmp.getVariants().size();
                for(int i = 0; i < k; ++i){
                    builder.addAnswName(tmp.getVariants().get(i).toString());
                    builder.addAnswId(i);
                }
            }
        } else {
            builder.setName("null");
        }
        QuestionResponse response = builder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void answer(AnswerRequest request, io.grpc.stub.StreamObserver<IdResponse> responseObserver){
        int size = request.getAnswIdCount();
        int id = request.getClientId();
        int return_id = 0;

        if(active_test.containsKey(id) == false || size == 0)
            return_id = -2;
        else{
            ArrayList<Integer> array = new ArrayList<Integer>(size);
            for(int i = 0; i < size; ++i){
                int tmp = request.getAnswId(i);
                if(tmp < 0){
                    return_id = -1;
                    break;
                }
                array.add(tmp);
            }
            if(return_id == 0)
                active_test.get(id).answerQuestion(array);
        }

        IdResponse response = IdResponse.newBuilder().setId(return_id).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void finish(FinishRequest request, io.grpc.stub.StreamObserver<FinishResponse> responseObserver){
        int client_id = request.getClientId();
        int score = Integer.MIN_VALUE;
        if(active_test.containsKey(client_id) == true){
            active_test.get(client_id).EarlyFinish();
            score = active_test.get(client_id).getScore();
            active_test.remove(client_id);
        }

        FinishResponse response = FinishResponse.newBuilder().setScore(score).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
