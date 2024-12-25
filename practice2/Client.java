import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try (var socket = new Socket("localhost", 42069);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var out = new PrintWriter(socket.getOutputStream(), true);
             var console = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Connected to server. Type 'exit' to quit.");
            for (String input; ; ) {
                System.out.print("> ");
                input = console.readLine();
                if (input == null || "exit".equalsIgnoreCase(input.trim())) break;
                out.println(input);
                System.out.println("Server: " + in.readLine());
            }
        } catch (IOException e) { System.err.println("Connection error: " + e.getMessage()); }
    }
}
