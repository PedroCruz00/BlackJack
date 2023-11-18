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

    public BlackJackClientHandler(Socket socket) {
        this.clientSocket = socket;

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

            // Obtener el nombre del jugador desde el cliente
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

    private String processMessage(String message) {
        String response = "";

        if (message.equalsIgnoreCase("HIT")) {
            game.playerHit(player);

            if (player.getPlayerState().equalsIgnoreCase("Fuera")) {
                response = "Has superado 21. ¡Perdiste!";
            } else {
                response = "Carta recibida: " + player.getHand().get(player.getHand().size() - 1);
            }
        } else if (message.equalsIgnoreCase("STAND")) {
            game.playerStand(player);

            if (player.getPlayerState().equalsIgnoreCase("Jugando")) {
                response = "Es el turno del siguiente jugador.";
            } else if (player.getPlayerState().equalsIgnoreCase("Ganador")) {
                response = "¡Felicidades! ¡Eres el ganador!";
            }
        } else if (message.equalsIgnoreCase("QUIT")) {
            response = "Hasta luego";
        } else {
            response = "Comando no válido";
        }

        return response;
    }
}