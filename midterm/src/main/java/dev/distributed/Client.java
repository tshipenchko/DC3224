package dev.distributed;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.distributed.dto.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                    while (true) {
                        System.out.println("Choose task type (1: TaskSum, 2: TaskMultiply): ");
                        String taskType = reader.readLine();

                        Task task;
                        if ("1".equals(taskType)) {
                            task = new TaskSum();
                        } else if ("2".equals(taskType)) {
                            task = new TaskSum();
                        } else {
                            System.out.println("Invalid task type. Please try again.");
                            continue;
                        }

                        task.setId(UUID.randomUUID().toString());
                        task.setPartId(-1);

                        System.out.println("Enter numbers (comma separated): ");
                        String[] numbersStr = reader.readLine().split(",");
                        int[] numbers = Arrays.stream(numbersStr).mapToInt(Integer::parseInt).toArray();
                        ((TaskSum) task).setNumbers(numbers);

                        Task[] subtasks = task.distribute(4);

                        for (Task subtask : subtasks) {
                            String subtaskJson = objectMapper.writeValueAsString(subtask);
                            out.println(subtaskJson);
                            System.out.println("Sent: " + subtaskJson);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
    }
}
