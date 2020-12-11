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
        

        if(req[0].equals("addt") && req.length == 2){
            AddTestRequest request = AddTestRequest.newBuilder().setId(0).setName(req[1]).build();
            IdResponse response = client.addtest(request);
            System.out.println("test id: "+ response.getId());
        } else if (req[0].equals("addq") && req.length == 2){
            int test_id = -1;
            try{
                test_id = Integer.parseInt(req[1]);
            } catch (Exception e){
                return 1;
            }
            System.out.print("Enter question text: ");
            String ques_text = console.nextLine();
            AddQuesRequest request = AddQuesRequest.newBuilder().setId(0).setTestId(test_id).setName(ques_text).build();
            IdResponse response = client.addques(request);
            System.out.println("question id: "+ response.getId());
        } else if (req[0].equals("seet")){
            NullRequest request = NullRequest.newBuilder().build();
            TestResponse response = client.seetest(request);
            int size = response.getNameCount();
            for(int i = 0; i < size; ++i){
                System.out.println("name: " + response.getName(i) + " id: " + response.getTestId(i));
            }
        } else if (req[0].equals("adda")){
            int quest_id = -1;
            try{
                quest_id = Integer.parseInt(req[1]);
            } catch (Exception e){
                return 1;
            }
            if(quest_id < 0)
                return 1;

            System.out.println("Enter number of answers: ");
            int size = 0;
            try{
                size = Integer.parseInt(console.nextLine());
            } catch (Exception e){
                return 1;
            }

            if(size <= 0)
                return 1;
            String tmp = "";
            AddAnswRequest.Builder builder = AddAnswRequest.newBuilder().setId(0).setQuestionId(quest_id);
            for(int i = 0; i < size; ++i){
                System.out.println("Enter name");
                String name;
                name = console.nextLine();
                System.out.println("Enter args");
                tmp = console.nextLine();
                String[] args = tmp.trim().split(" ");
                boolean isTrue = true;
                int pTaken = 0;
                int pSkiped = 0;
                try{
                    isTrue = Boolean.parseBoolean(args[0]);
                    pTaken = Integer.parseInt(args[1]);
                    pSkiped = Integer.parseInt(args[2]);
                } catch (Exception e){
                    System.out.println("Parse error");
                    --i;
                    continue;
                }
                builder.addName(name).addIsTrue(isTrue).addPointTaken(pTaken).addPointSkiped(pSkiped);
            }

            AddAnswRequest request = builder.build();
            IdResponse response = client.addansw(request);
            System.out.println("response: "+ response.getId());
        } else if (req[0].equals("start")){
            int test_id = -1;
            try{
                test_id = Integer.parseInt(req[1]);
            } catch (Exception e){
                return 1;
            }

            StartTestRequest.Builder builder = StartTestRequest.newBuilder();
            builder.setTestId(test_id);
            StartTestRequest request = builder.build();
            IdResponse response = client.starttest(request);
            
            local_id = response.getId();
            if(local_id != -1){
                System.out.println(test_id);
                System.out.println("Test started");
            }
        } else if(req[0].equals("seeq")){
            QuestionRequest request = QuestionRequest.newBuilder().setClientId(local_id).build();
            QuestionResponse response = client.seeques(request);

            if(response.getName().equals("null")){
                System.out.println("Wrong id");
            } else {
                System.out.println("Question: " + response.getName());
                int size = response.getAnswNameCount();
                for(int i = 0; i < size; ++i){
                    System.out.println(response.getAnswName(i) + " " + response.getAnswId(i));
                }
            }
        } else if(req[0].equals("answer")){
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
                System.out.println("Negative number");
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
