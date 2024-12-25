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

            client.subscribe(TOPIC, (topic, message) -> {
                String receivedMessage = new String(message.getPayload());
                if (!receivedMessage.startsWith(username + ">")) {
                    displayMessage(receivedMessage);
                }
            });

            System.out.println("Type 'exit' to quit.");
            System.out.print("> ");
            while (true) {
                String userInput = scanner.nextLine();
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
                String message = username + "> " + userInput;
                client.publish(TOPIC, new MqttMessage(message.getBytes()));
                System.out.print("\r> ");
            }

            client.disconnect();
            System.out.println("\nDisconnected from the broker.");
        } catch (MqttException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayMessage(String message) {
        System.out.println("\r" + message);
        System.out.print("> ");
    }
}
