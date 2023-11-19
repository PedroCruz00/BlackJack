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

    @Override
    public void run() {
        try {
            // Obtener el nombre del jugador
            String playerName = input.readLine();

            // Verificar si el nombre del jugador ya está en uso
            if (server.isPlayerNameTaken(playerName)) {
                output.println("El nombre de jugador ya está en uso. Por favor, elige otro nombre.");
                playerName = input.readLine();
                clientSocket.close();
                return;
            }

            server.addPlayerName(playerName);

            // Esperar a que se inicie el juego
            while (!server.isGameStarted()) {
                // Esperar al mensaje de inicio del juego del servidor
                String message = input.readLine();

                // Verificar si el mensaje indica que el juego ha comenzado
                if (message.equalsIgnoreCase("¡La partida ha comenzado!")) {
                    startGame();
                    break;
                }

                // Mostrar el mensaje de espera o información adicional
                System.out.println(message);
            }

            // Procesar los comandos del jugador mientras el juego esté en curso
            while (server.isGameStarted()) {
                // Leer el comando del jugador
                String command = input.readLine();

                // Procesar el comando
                processCommand(command);
                lastAction = command;

                // Verificar si el juego ha terminado
                if (!server.isGameStarted()) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Cerrar conexiones y limpiar recursos
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
