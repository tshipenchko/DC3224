package dev.distributed.elements;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.distributed.dto.*;

public class MessageHandler {
    private final BufferedReader socketIn;
    private final PrintWriter socketOut;
    private final BlockingQueue<Result> resultQueue;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public MessageHandler(BufferedReader socketIn, PrintWriter socketOut, BlockingQueue<Result> resultQueue) {
        this.socketIn = socketIn;
        this.socketOut = socketOut;
        this.resultQueue = resultQueue;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void start() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                process();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void process() throws IOException {
        String message = socketIn.readLine();

        try {
            Task task = objectMapper.readValue(message, Task.class);

            executor.execute(() -> {
                Result result = task.compute();
                try {
                    socketOut.println(objectMapper.writeValueAsString(result));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (JsonMappingException e) {
            try {
                Result result = objectMapper.readValue(message, Result.class);
                resultQueue.add(result);

            } catch (JsonMappingException ex) {
                System.out.println("Failed to map a message.");
                ex.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Error processing message.");
            e.printStackTrace();
        }
    }
}
