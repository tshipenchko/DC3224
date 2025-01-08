package dev.distributed;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && "server".equalsIgnoreCase(args[0])) {
            Server.main(args);
        } else if (args.length > 0 && "client".equalsIgnoreCase(args[0])) {
            Client.main(args);
        } else {
            System.out.println("Usage: java -jar midterm.jar <server|client>");
        }
    }
}
