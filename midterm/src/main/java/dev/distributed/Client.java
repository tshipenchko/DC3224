package dev.distributed;

import dev.distributed.elements.*;
import dev.distributed.dto.*;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            BlockingQueue<Result> queue = new LinkedBlockingQueue<>();
            UI ui = new UI(out, queue);
            MessageHandler messageHandler = new MessageHandler(in, out, queue);

            executor.execute(messageHandler::start);
            ui.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
