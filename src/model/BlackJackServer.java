package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlackJackServer {
    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private List<BlackJackClientHandler> connectedClients;
    private int connectedPlayerCount;
    private Set<String> playerNames;
    private Deck deck;
    private boolean gameStarted;
    private int currentPlayerIndex;

    private Game game;

    public BlackJackServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            this.playerNames = new HashSet<>();
            this.currentPlayerIndex = 0;
            connectedClients = new ArrayList<>();
            connectedPlayerCount = 0;
            deck = new Deck();
            gameStarted = false;
            game = new Game(); // Crear instancia de Game
            System.out.println("Servidor en ejecución. Esperando conexiones en el puerto " + PORT + "...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nueva conexión entrante: " + clientSocket);
                BlackJackClientHandler clientHandler = new BlackJackClientHandler(clientSocket, this);
                connectedClients.add(clientHandler);
                executorService.execute(clientHandler);
                incrementConnectedPlayerCount();
                updateConnectedPlayerCount();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void gameLoop() {
        while (gameStarted) {
            // Obtener el jugador actual
            BlackJackClientHandler currentPlayer = connectedClients.get(currentPlayerIndex);

            // Solicitar acción al jugador actual
            currentPlayer.sendMessage("Es tu turno. ¿Quieres PEDIR o PARAR?");

            // Esperar la respuesta del jugador actual
            String playerAction = currentPlayer.waitForAction();

            // Procesar la acción del jugador actual
            processPlayerAction(currentPlayer, playerAction);

            // Pasar al siguiente jugador
            currentPlayerIndex = (currentPlayerIndex + 1) % connectedPlayerCount;
        }

        // La partida ha terminado, realizar acciones de finalización
        endGame();
    }

    public boolean allPlayersStand() {
        for (BlackJackClientHandler player : connectedClients) {
            if (!player.getPlayer().getPlayerState().equalsIgnoreCase("Stand")) {
                return false;
            }
        }
        return true;
    }

    public boolean allPlayersBusted() {
        for (BlackJackClientHandler player : connectedClients) {
            if (!player.getPlayer().getPlayerState().equalsIgnoreCase("Busted")) {
                return false;
            }
        }
        return true;
    }

    public synchronized void broadcastMessage(String message) {
        for (BlackJackClientHandler clientHandler : connectedClients) {
            clientHandler.sendMessage(message);
        }
    }

    public void startGame() {
        // Repartir cartas iniciales
        for (BlackJackClientHandler clientHandler : connectedClients) {
            Hand hand = clientHandler.getHand();
            hand.addCard(deck.dealCard());
            hand.addCard(deck.dealCard());

            clientHandler.sendMessage("Tus cartas iniciales: " + hand);
            clientHandler.sendMessage("Puntuación actual: " + hand.getScore());
        }

        // Comenzar el bucle de juego
        gameStarted = true;
        gameLoop();
    }

    public synchronized void processPlayerAction(BlackJackClientHandler player, String action) {
        switch (action.toUpperCase()) {
            case "PEDIR":
                player.getHand().addCard(deck.dealCard());
                player.sendMessage(
                        "Carta recibida: " + player.getHand().getCards().get(player.getHand().getCards().size() - 1));
                player.sendMessage("Puntuación actual: " + player.getHand().getScore());
                if (player.getHand().isBust()) {
                    player.sendMessage("Has superado 21. ¡Perdiste!");
                    decrementConnectedPlayerCount();
                    checkEndGame();
                }
                break;
            case "PARAR":
                player.getPlayer().setPlayerState("Stand");
                decrementConnectedPlayerCount();
                checkEndGame();
                break;
            default:
                player.sendMessage("Comando no válido. ¿Quieres PEDIR o PARAR?");
                break;
        }
    }

    private void checkEndGame() {
        if (allPlayersStand() || allPlayersBusted()) {
            endGame();
        }
    }

    public synchronized void removeClient(BlackJackClientHandler clientHandler) {
        connectedClients.remove(clientHandler);
        decrementConnectedPlayerCount();
        updateConnectedPlayerCount();
    }

    public synchronized void incrementConnectedPlayerCount() {
        connectedPlayerCount++;
        if (connectedPlayerCount == 3 && !gameStarted) {
            gameStarted = true;
            broadcastMessage("¡La partida ha comenzado!");
            startGame();
        } else {
            broadcastMessage("Esperando a que se conecten más jugadores...");
        }
    }

    public synchronized void decrementConnectedPlayerCount() {
        connectedPlayerCount--;
        if (connectedPlayerCount == 0) {
            resetGame();
        }
    }

    public synchronized void updateConnectedPlayerCount() {
        broadcastMessage("Jugadores conectados: " + connectedPlayerCount);
    }

    public boolean isPlayerNameTaken(String playerName) {
        return playerNames.contains(playerName);
    }

    public synchronized void addPlayerName(String playerName) {
        playerNames.add(playerName);
    }

    private void resetGame() {
        gameStarted = false;
        deck = new Deck();
        game.reset();
        for (BlackJackClientHandler clientHandler : connectedClients) {
            clientHandler.resetGame();
        }
    }

    public Deck getDeck() {
        return deck;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public Game getGame() {
        return game;
    }

    public List<BlackJackClientHandler> getConnectedClients() {
        return connectedClients;
    }

    public synchronized void passTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % connectedPlayerCount;
        // Aquí podrías enviar un mensaje al nuevo jugador activo
    }

    public synchronized boolean allPlayersDisconnected() {
        for (BlackJackClientHandler player : connectedClients) {
            if (player.getPlayer().getInGame()) {
                return false; // Al menos un jugador sigue conectado
            }
        }
        return true; // Todos los jugadores están desconectados
    }

    public synchronized void endGame() {
        // Verifica si todos los jugadores están desconectados
        boolean allPlayersDisconnected = true;

        for (BlackJackClientHandler player : connectedClients) {
            if (player.getPlayer().getInGame()) {
                allPlayersDisconnected = false;
                break;
            }
        }

        // Si todos los jugadores están desconectados, finaliza la partida
        if (allPlayersDisconnected) {
            resetGame(); // Reiniciar el juego
            // Notifica a todos los jugadores que la partida ha terminado
            broadcastMessage("La partida ha terminado. Todos los jugadores están desconectados.");
        }
    }

    public static void main(String[] args) {
        BlackJackServer server = new BlackJackServer();
        server.start();
    }
}