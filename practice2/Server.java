import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    public static void main(String[] args) throws IOException {
        try (var server = new ServerSocket(42069)) {
            System.out.println("Server running on port 42069");
            var pool = Executors.newCachedThreadPool();
            while (true) pool.execute(new ClientHandler(server.accept()));
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) { this.socket = socket; }

        public void run() {
            try (var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 var out = new PrintWriter(socket.getOutputStream(), true)) {
                for (String input; (input = in.readLine()) != null; ) {
                    if ("exit".equalsIgnoreCase(input.trim())) { out.println("Goodbye!"); break; }
                    out.println(input.matches("\\d+") ? Integer.parseInt(input) + 1 : input.toUpperCase());
                }
            } catch (IOException e) { System.err.println("Client error: " + e.getMessage()); }
        }
    }
}
