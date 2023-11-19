package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BlackJackClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private Game game;
    private Player player;
    private BlackJackServer server;

    private boolean disconnected = false;

    public BlackJackClientHandler(Socket socket, BlackJackServer server) {
        this.clientSocket = socket;
        this.server = server;
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            game = new Game();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Lógica para recibir y enviar mensajes con el cliente
            String message;

            String playerName = input.readLine();
            player = new Player(playerName);
            game.addPlayer(player);

            output.println("¡Bienvenido al juego de Blackjack, " + playerName + "!");

            while ((message = input.readLine()) != null) {
                // Procesar el mensaje recibido y enviar una respuesta al cliente
                String response = processMessage(message);
                output.println(response);

                // Salir del bucle si se recibe el mensaje de salida del servidor
                if (response.equalsIgnoreCase("Hasta luego")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
                output.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isClientActive() {
        return !disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    private String processMessage(String message) {
        String response = "";

        if (message.equalsIgnoreCase("HIT")) {
            game.playerHit(player);

            if (player.getPlayerState().equalsIgnoreCase("Busted")) {
                response = "Has superado 21. ¡Perdiste!";
            } else {
                response = "Carta recibida: " + player.getHand().get(player.getHand().size() - 1);
            }
        } else if (message.equalsIgnoreCase("STAND")) {
            game.playerStand(player);
            if (player.getPlayerState().equalsIgnoreCase("Playing")) {
                response = "Es el turno del siguiente jugador.";
            } else if (player.getPlayerState().equalsIgnoreCase("Winner")) {
                response = "¡Felicidades! ¡Eres el ganador!";
            }
        } else if (message.equalsIgnoreCase("QUIT")) {
            response = "Hasta luego";
        } else {
            response = "Comando no válido";
        }

        return response;
    }
    public void sendMessage(String message) {
        output.println(message);
    }
}