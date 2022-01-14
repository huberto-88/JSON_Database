package client;

import com.beust.jcommander.JCommander;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 54444;
    private static Request request = new Request();

    public static void main(String[] args) {
        System.out.println("Client started!");
        /*
        parsing request and content from command line
        using JCommander library
         */
        JCommander.newBuilder()
                .addObject(request)
                .build()
                .parse(args);

        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        ) {
            // sending message to server in JSON form
            System.out.println("Sent: " + request.toJson());
            outputStream.writeUTF(request.toJson());

            // receiving and printing message from server in JSON form
            String receivedMsg;
            receivedMsg = inputStream.readUTF();
            System.out.println("Received: " + receivedMsg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}