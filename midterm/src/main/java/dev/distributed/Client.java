package dev.distributed;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.distributed.dto.*;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            ObjectMapper objectMapper = new ObjectMapper();

            executor.execute(() -> {
                TaskSum task = new TaskSum();
                task.setId("task1");
                task.setPartId(-1);
                task.setNumbers(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});

                try {
                    String taskJson = objectMapper.writeValueAsString(task);
                    Task[] subtasks = task.distribute(4);

                    while (true) {
                        Thread.sleep(5000);
                        for (Task subtask : subtasks) {
                            String subtaskJson = objectMapper.writeValueAsString(subtask);
                            out.println(subtaskJson);
                            System.out.println("Sent: " + subtaskJson);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.contains("Task")) {
                        Task task = objectMapper.readValue(message, Task.class);
                        executor.execute(() -> {
                            Result result = task.compute();
                            try {
                                out.println(objectMapper.writeValueAsString(result));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (message.contains("Result")) {
                        Result result = objectMapper.readValue(message, Result.class);
                        System.out.println("Received: " + result);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}