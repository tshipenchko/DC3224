package dev.distributed;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.distributed.dto.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
            task.setId("task-1");
            task.setPartId(-1);
            task.setNumbers(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});

            try {
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
            List<Result> partialResults = new ArrayList<>();
            int expectedParts = 4; // This should match the distribution factor used in distribute

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
                    Task task = result.generateTask();
                    partialResults.add(result);

                    System.out.println("Received: " + message);
                    System.out.println("Partial Results: " + partialResults.size());

                    if (partialResults.size() == expectedParts) {
                        Result finalResult = task.join(partialResults.toArray(new Result[0]));
                        System.out.println("Final Result: " + finalResult);
                        partialResults.clear();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}}
