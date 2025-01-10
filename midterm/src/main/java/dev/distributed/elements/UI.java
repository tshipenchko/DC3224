package dev.distributed.elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.distributed.dto.*;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class UI {
    private final PrintWriter socketOut;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Scanner scanner = new Scanner(System.in);
    private final BlockingQueue<Result> resultQueue;

    private Task currentTask;
    private int distributionFactor;
    private Result[] results;

    public UI(PrintWriter socketOut, BlockingQueue<Result> resultQueue) {
        this.socketOut = socketOut;
        this.resultQueue = resultQueue;
    }

    public void start() {
        //noinspection InfiniteLoopStatement
        while (true) {
            process();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void process() {
        System.out.println("1: TaskSum");
        System.out.println("2: TaskAverage");

        System.out.print("Choose task type: ");
        String taskType = scanner.nextLine();

        try {
            switch (taskType) {
                case "1":
                    processTaskSum();
                    waitForResults();
                    break;
                case "2":
                    processTaskAverage();
                    waitForResults();
                    break;
                default:
                    System.out.println("Invalid task type. Please try again.");
            }
        } catch (JsonProcessingException e) {
            System.out.println("Error processing task.");
            e.printStackTrace();
        }
    }

    private Task processTaskSum() throws JsonProcessingException {
        TaskSum task = new TaskSum();
        task.setId(UUID.randomUUID().toString());
        task.setPartId(-1);

        int[] numbers = getNumbers();
        task.setNumbers(numbers);

        distributeTask(task);

        return task;
    }

    private Task processTaskAverage() throws JsonProcessingException {
        TaskAverage task = new TaskAverage();
        task.setId(UUID.randomUUID().toString());
        task.setPartId(-1);

        int[] numbers = getNumbers();
        task.setNumbers(numbers);

        distributeTask(task);

        return task;
    }

    private void distributeTask(Task task) throws JsonProcessingException {
        System.out.println("Enter the distribution factor: ");
        int factor = Integer.parseInt(scanner.nextLine());
        if (factor <= 0) factor = 4;

        Task[] subtasks = task.distribute(factor);

        currentTask = task;
        results = new Result[factor];
        distributionFactor = factor;

        for (Task subtask : subtasks) {
            String subtaskJson = objectMapper.writeValueAsString(subtask);
            socketOut.println(subtaskJson);
            System.out.println("Sent: " + subtaskJson);
        }
    }

    private int[] getNumbers() {
        System.out.println("Enter numbers (comma separated): ");
        String[] numbersStr = scanner.nextLine().split(",");
        return Arrays.stream(numbersStr).mapToInt(Integer::parseInt).toArray();
    }

    private void waitForResults() {
        long start = System.currentTimeMillis();
        System.out.println("Waiting for results...");

        try {
            Result result;
            int received = 0;

            while (received < distributionFactor) {
                result = resultQueue.take();

                System.out.println("Received: " + result);

                if (!Objects.equals(result.getId(), currentTask.getId())) {
                    continue;
                }

                results[result.getPartId()] = result;
                received++;
            }

            Result finalResult = currentTask.join(results);
            System.out.println("Final Result: " + finalResult);

            long end = System.currentTimeMillis();
            System.out.println("Total execution time: " + (end - start) + "ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
