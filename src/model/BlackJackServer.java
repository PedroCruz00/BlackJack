package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlackJackServer {
    private static final int PORT = 8081;
    private static final int THREAD_POOL_SIZE = 10;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private Lock lock;
    private List<BlackJackClientHandler> connectedClients;
    private int connectedPlayerCount;

    public BlackJackServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            lock = new ReentrantLock();
            connectedClients = new ArrayList<>();
            connectedPlayerCount = 0;
            System.out.println("Servidor en ejecución. Esperando conexiones en el puerto " + PORT + "...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (connectedPlayerCount < 3) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket);

                lock.lock();
                try {
                    connectedPlayerCount++;
                    notifyWaitingScreen(connectedPlayerCount);
                    updateConnectedPlayerCount();

                    if (connectedPlayerCount == 3) {
                        System.out.println("El juego ha comenzado");
                        startGame();
                        break; // Salir del bucle una vez que se inicie el juego
                    }
                } finally {
                    lock.unlock();
                }

                BlackJackClientHandler clientHandler = new BlackJackClientHandler(clientSocket, this);
                connectedClients.add(clientHandler);
                executorService.execute(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Cerrar el servidor después de que se completen las 3 conexiones
        executorService.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decrementConnectedPlayerCount() {
        lock.lock();
        try {
            connectedPlayerCount--;
        } finally {
            lock.unlock();
        }
    }

    public void broadcastMessage(String message) {
        lock.lock();
        try {
            for (BlackJackClientHandler client : connectedClients) {
                client.sendMessage(message);
            }
        } finally {
            lock.unlock();
        }
    }

    private void notifyWaitingScreen(int connectedPlayerCount) {
        String message = "Esperando a " + (3 - connectedPlayerCount) + " jugadores más para iniciar la partida.";
        broadcastMessage(message);
    }

    private void updateConnectedPlayerCount() {
        String message = "Jugadores conectados: " + connectedPlayerCount;
        broadcastMessage(message);
    }

    private void startGame() {
        String message = "¡La partida ha comenzado!";
        broadcastMessage(message);
    }

    public static void main(String[] args) {
        BlackJackServer server = new BlackJackServer();
        server.start();
    }
}