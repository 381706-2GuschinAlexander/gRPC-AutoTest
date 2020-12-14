package app.autotest;
import grpc.*;
import io.grpc.*;

import java.util.ArrayList;
import java.util.Scanner;

import app.teststruct.Question;

public class TestClient {
    static Integer local_id = -1;

    public static void main(String[] args) {
        EchoServiceGrpc.EchoServiceBlockingStub client = createClient("localhost",8080);
        System.out.println("Connected to server");
        Scanner console = new Scanner(System.in);
        String message;
        while((message = console.nextLine())!=null){
            requestForm(client, message, console);
        }
        console.close();
    }
    
    private static int requestForm(EchoServiceGrpc.EchoServiceBlockingStub client, String message, Scanner console){
        String[] req = message.trim().split(" ");
        

        if (req[0].equals("start")){
            int test_id = -1;
            try{
                test_id = Integer.parseInt(req[1]);
            } catch (Exception e){
                return 1;
            }

            StartTestRequest.Builder builder = StartTestRequest.newBuilder();
            builder.setTestId(test_id);
            builder.setClientId(local_id);
            StartTestRequest request = builder.build();
            IdResponse response = client.starttest(request);
            
            local_id = response.getId();
            if(local_id != -1){
                System.out.println("Test started");
            } else {
                System.out.println("Zero test with this id");
            }
        } else if (req[0].equals("seeq")){
            QuestionRequest request = QuestionRequest.newBuilder().setClientId(local_id).build();
            QuestionResponse response = client.seeques(request);

            if(response.getName().equals("null")){
                System.out.println("Wrong id");
            } else {
                System.out.println("Question: " + response.getName());
                int size = response.getAnswNameCount();
                for(int i = 0; i < size; ++i){
                    System.out.println("Id: " + response.getAnswId(i) + " Text: " + response.getAnswName(i));
                }
            }
        } else if (req[0].equals("answer")){
            ArrayList<Integer> array = new ArrayList<Integer>();
            for(int i = 1; i < req.length; ++i){
                try{
                    int tmp = Integer.parseInt(req[i]);
                    array.add(tmp);
                } catch (Exception e){
                    System.out.println("Parse error");
                    return 1;
                }
            }

            AnswerRequest.Builder builder = AnswerRequest.newBuilder();
            builder.setClientId(local_id);
            for(int i = 0; i < array.size(); ++i)
                builder.addAnswId(array.get(i));
            
            AnswerRequest request = builder.build();

            IdResponse response = client.answer(request);
            if(response.getId() != 0)
                System.out.println("Error");
        } else if(req[0].equals("finish")){
            FinishRequest request = FinishRequest.newBuilder().setClientId(local_id).build();
            FinishResponse response = client.finish(request);
            int score = response.getScore();
            if(score == Integer.MIN_VALUE)
                System.out.println("None active test");
            else
                System.out.println("Your score: " + score);
        } else if (req[0].equals("seet")){
            NullRequest request = NullRequest.newBuilder().build();
            TestResponse response = client.seetest(request);
            int size = response.getNameCount();
            for(int i = 0; i < size; ++i){
                System.out.println("Id: " + response.getTestId(i) + " Name: " + response.getName(i));
            }
        }
        return 0;
    }

    private static EchoServiceGrpc.EchoServiceBlockingStub createClient(String host, int port){
        Channel channel = ManagedChannelBuilder.forAddress(host,port)
                .usePlaintext()
                .build();
        return EchoServiceGrpc.newBlockingStub(channel);
    }
}
