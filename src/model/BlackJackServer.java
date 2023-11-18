package model;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BlackJackServer {
    private static final int PORT = 8081;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("BlackJackServer is running and listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Iniciar un nuevo hilo para manejar al cliente
                BlackJackClientHandler clientHandler = new BlackJackClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}