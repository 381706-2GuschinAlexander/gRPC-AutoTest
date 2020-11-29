package app.autotest;
import grpc.*;
import io.grpc.*;
import java.util.Scanner;

public class TestClient {
    Integer local_id = -1;

    public static void main(String[] args) {
        EchoServiceGrpc.EchoServiceBlockingStub client = createClient("localhost",8080);
        System.out.println("Connected to server");
        Scanner console = new Scanner(System.in);
        String message;
        while((message = console.nextLine())!=null){
            requestForm(client, message);
        }
        console.close();
    }

    private static int requestForm(EchoServiceGrpc.EchoServiceBlockingStub client, String message){
        String[] req = message.trim().split(" ");

        if(req[0].equals("addt") && req.length == 2){
            AddTestRequest request = AddTestRequest.newBuilder().setId(0).setName(req[1]).build();
            IdResponse response = client.addtest(request);
            System.out.println("response: "+ response.getId());
        } else if (req[0].equals("addq") && req.length == 3){
            int test_id = -1;
            try{
                test_id = Integer.parseInt(req[1]);
            } catch (Exception e){
                return 1;
            }
            AddQuesRequest request = AddQuesRequest.newBuilder().setId(0).setTestId(test_id).setName(req[2]).build();
            IdResponse response = client.addques(request);
            System.out.println("response: "+ response.getId());
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
