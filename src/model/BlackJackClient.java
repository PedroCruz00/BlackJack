package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BlackJackClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8081;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public BlackJackClient() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Conectado al servidor: " + socket);

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            // Lógica del cliente para enviar y recibir mensajes con el servidor
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            String userInput;

            while (true) {
                // Leer entrada del usuario
                userInput = consoleInput.readLine();

                // Enviar comando al servidor
                output.println(userInput);

                // Esperar respuesta del servidor
                String response = input.readLine();
                System.out.println("Respuesta del servidor: " + response);

                // Salir del bucle si se recibe el mensaje de salida del servidor
                if (response.equalsIgnoreCase("Hasta luego")) {
                    break;
                }
            }

            // Cerrar conexiones
            consoleInput.close();
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BlackJackClient client = new BlackJackClient();
        client.start();
    }
}
