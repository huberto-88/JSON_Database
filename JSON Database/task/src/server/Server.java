package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {
    private final int PORT = 54444;
    private ServerSocket serverSocket;
    private final DataBase data;
    private ReadWriteLock lock;
    private Lock readLock;
    private Lock writeLock;

    public Server() throws IOException {
        this.data = new DataBase();
        this.serverSocket = new ServerSocket(PORT);
        this.lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    public void run()   {
        System.out.println("Server started!");
        while (!serverSocket.isClosed()) {
            try {
                ExecutorService executorService = Executors.
                        newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                executorService.submit(new Session(serverSocket.accept()));
                executorService.shutdown();
            } catch (IOException ignored) {}
        }
    }

    class Session extends Thread {
        private final Socket socket;

        public Session(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            ) {
                while (!socket.isClosed()) {

                    // reading input from clients
                    String jsonFromClient = inputStream.readUTF();

                    JsonElement jsonElementFromClient = JsonParser.parseString(jsonFromClient);
                    String request = jsonElementFromClient.getAsJsonObject().get("type").getAsString();
                    jsonElementFromClient.getAsJsonObject().remove("type");

//-------------------------------------------------------------------------

                    System.out.println("\n\n");

                    System.out.println("request jest: " + request);


                    System.out.println("\n\n");
//-------------------------------------------------------------------------
                    Response response = null;

                    switch (request) {
                        case "get":
                            readLock.lock();
                            response = data.getData(jsonElementFromClient);
                            readLock.unlock();
                            break;
                        case "set":
                            writeLock.lock();
                            response = data.setData(jsonElementFromClient);
                            writeLock.unlock();
                            break;
                        case "delete":
                            writeLock.lock();
                            response = data.deleteData(jsonElementFromClient);
                            writeLock.unlock();
                            break;
                        case "exit":
                            response = new Response.GetResponse("OK")
                                    .build();
                            break;
                    }

                    Gson gson = new Gson();

                    // if user request exit, send ok and close connection
                    if (request.equals("exit")) {
                        outputStream.writeUTF(gson.toJson(response));
                        serverSocket.close();
                    } else {
                        outputStream.writeUTF(gson.toJson(response));
                    }
                    socket.close();
                }
             } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}