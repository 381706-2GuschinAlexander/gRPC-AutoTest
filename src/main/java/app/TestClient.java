package autotest;
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

        if(req[0].equals("insert")){
            AddIdRequest request = AddIdRequest.newBuilder().setId(0).setName(req[1]).build();
            EchoResponse response = client.insert(request);
            System.out.println("response: "+response.getMessage());
        } else {
            EchoRequest request = EchoRequest.newBuilder().setMessage("aaa").build();
            EchoResponse response = client.echo(request);
            System.out.println("response: "+response.getMessage());
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
