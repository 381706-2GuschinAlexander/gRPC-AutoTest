package app.autotest;
import grpc.*;
import io.grpc.*;

import java.util.ArrayList;
import java.util.Scanner;

import app.teststruct.Question;

public class AdminClient {
    //static Integer local_id = -1;

    public static void main(String[] args) {
        EchoServiceGrpc.EchoServiceBlockingStub client = createClient("localhost",8080);
        System.out.println("Connected to server(Admin)");
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
            System.out.println("Test id: "+ response.getId());
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
            System.out.println("Question id: "+ response.getId());
        }  else if (req[0].equals("adda")){
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
                    pTaken = Integer.parseInt(args[0]);
                    pSkiped = Integer.parseInt(args[1]);
                } catch (Exception e){
                    System.out.println("Parse error");
                    --i;
                    continue;
                }
                builder.addName(name).addPointTaken(pTaken).addPointSkiped(pSkiped);
            }

            AddAnswRequest request = builder.build();
            IdResponse response = client.addansw(request);
            if(response.getId() == 0)
                System.out.println("Added");
            else
                System.out.println("Error");
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
