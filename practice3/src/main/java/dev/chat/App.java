package dev.chat;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.Scanner;

public class App {
    private static final String BROKER_URL = "tcp://localhost:1883";
    private static final String TOPIC = "chat/general";
    private static String username;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter your username: ");
            username = scanner.nextLine();

            MqttClient client = new MqttClient(BROKER_URL, "ChatClient_" + System.currentTimeMillis(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            client.connect(options);
            System.out.println("Connected to the local MQTT broker.");

            // Subscribe to the chat topic
            client.subscribe(TOPIC, (topic, message) -> {
                String receivedMessage = new String(message.getPayload());
                // Ignore own message
                if (!receivedMessage.startsWith(username + ">")) {
                    displayMessage(receivedMessage);  // Print message if not sent by the current user
                }
            });

            System.out.println("Type 'exit' to quit.");
            System.out.print("> ");  // Initial input prompt
            while (true) {
                String userInput = scanner.nextLine();
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
                String message = username + "> " + userInput;
                client.publish(TOPIC, new MqttMessage(message.getBytes()));
                System.out.print("\r> ");  // Reprint the input prompt on the same line
            }

            client.disconnect();
            System.out.println("\nDisconnected from the broker.");
        } catch (MqttException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Print the incoming message, ensuring the prompt remains fixed at the bottom
    private static void displayMessage(String message) {
        System.out.println("\r" + message);  // Print message with a new line
        System.out.print("> ");  // Ensure the prompt stays at the bottom after message display
    }
}
