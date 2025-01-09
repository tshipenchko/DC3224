package dev.distributed;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.distributed.dto.*;

public class Server {
    private static final int PORT = 12345;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket socket = serverSocket.accept();
                executor.execute(new ClientHandler(socket));
            }
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private static final CopyOnWriteArrayList<PrintWriter> clients = new CopyOnWriteArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            clients.add(out);
            String message;

            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);

                try {
                    Task task = objectMapper.readValue(message, Task.class);
                    int randomClientIndex = ThreadLocalRandom.current().nextInt(clients.size());
                    clients.get(randomClientIndex).println(objectMapper.writeValueAsString(task));
                } catch (Exception e) {
                    try {
                        Result result = objectMapper.readValue(message, Result.class);
                        // Broadcast result to all clients
                        for (PrintWriter client : clients) {
                            if (client != out) {
                                client.println(objectMapper.writeValueAsString(result));
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println("Error processing message: " + message);
                        ex.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }
    }
}
