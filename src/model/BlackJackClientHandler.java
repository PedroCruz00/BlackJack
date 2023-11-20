package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BlackJackClientHandler implements Runnable {
    private final Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private BlackJackServer server;
    private Hand hand;
    private Player player;
    private String lastAction;
    private Game game;

    public BlackJackClientHandler(Socket socket, BlackJackServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.game = server.getGame();
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            hand = new Hand();
            player = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para verificar si un cliente está desconectado
    private boolean isClientDisconnected() {
        try {
            input.ready(); // Verifica si hay datos listos para ser leídos
            return false; // El cliente sigue conectado
        } catch (IOException e) {
            return true; // Error al leer datos, el cliente está desconectado
        }
    }

    @Override
    public void run() {
        try {
            String playerName = input.readLine();

            // Verificar si el nombre del jugador ya está en uso
            if (server.isPlayerNameTaken(playerName)) {
                output.println("El nombre de jugador ya está en uso. Por favor, elige otro nombre.");
                playerName = input.readLine();
                clientSocket.close();
                return;
            }

            server.addPlayerName(playerName);

            while (!server.isGameStarted()) {
                String message = input.readLine();

                if (message.equalsIgnoreCase("¡La partida ha comenzado!")) {
                    startGame();
                    break;
                }

                System.out.println(message);
            }

            while (server.isGameStarted()) {
                String command = input.readLine();

                if (isClientDisconnected()) {
                    handleDisconnection();
                    break;
                }

                processCommand(command);
                lastAction = command;

                if (!server.isGameStarted()) {
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
                server.removeClient(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleDisconnection() {
        player.setInGame(false);
        server.passTurn();

        if (server.allPlayersDisconnected()) {
            server.endGame();
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    public void startGame() {
        // Repartir cartas iniciales
        hand.addCard(server.getDeck().dealCard());
        hand.addCard(server.getDeck().dealCard());

        sendMessage("Tus cartas iniciales: " + hand);
        sendMessage("Puntuación actual: " + hand.getScore());

        // Solicitar acción al jugador
        sendMessage("¿Quieres PEDIR o PARAR?");
    }

    public void resetGame() {
        hand = new Hand();
    }

    public String waitForAction() {
        // Esperar hasta que el jugador envíe una acción
        while (lastAction == null) {
            try {
                Thread.sleep(100); // Evitar uso intensivo de CPU
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Obtener la última acción y restablecerla a null
        String action = lastAction;
        lastAction = null;
        return action;
    }

    private void processCommand(String command) {
        switch (command.toUpperCase()) {
            case "PEDIR":
                // ... (resto del código)
                break;
            case "PARAR":
                game.endHand();
                server.decrementConnectedPlayerCount();

                // Enviar resultados finales al cliente
                sendFinalResults();
                break;
            default:
                // ... (resto del código)
                break;
        }
    }

    // Método para enviar resultados finales al cliente
    public void sendFinalResults() {
        StringBuilder resultMessage = new StringBuilder("Resultados finales:\n");

        for (BlackJackClientHandler clientHandler : server.getConnectedClients()) {
            Player player = clientHandler.getPlayer();
            resultMessage.append(player.getName()).append(": ").append(player.getScore());

            if (player.getPlayerState().equalsIgnoreCase("Winner")) {
                resultMessage.append(" - ¡Ganador!\n");
            } else if (player.getPlayerState().equalsIgnoreCase("Loser")) {
                resultMessage.append(" - Perdedor\n");
            } else {
                resultMessage.append(" - Empate\n");
            }
        }

        // Enviar los resultados finales al cliente
        sendMessage(resultMessage.toString());
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Hand getHand() {
        return hand;
    }
}