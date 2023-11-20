package model;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dealer extends JFrame {

    private JButton dealButton;
    private JTextArea displayArea;
    private ExecutorService executor;
    private ServerSocket server;
    private List<SockServer> sockServers;
    private List<Player> players;
    private Deck deck;
    private Player dealer;
    private boolean roundOver;

    public Dealer() {
        super("Dealer");

        sockServers = new ArrayList<>();
        executor = Executors.newFixedThreadPool(100);
        deck = new Deck();
        dealer = new Player("Dealer");
        roundOver = true;

        dealButton = new JButton("Deal Cards");

        dealButton.addActionListener(e -> {
            dealButton.setEnabled(false);
            startRound();
        });

        add(dealButton, BorderLayout.SOUTH);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        try {
            server = new ServerSocket(8080, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }

        runServer();
    }

    public void runServer() {
        try {
            while (true) {
                waitForConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForConnection() throws IOException {
        displayMessage("Waiting for connection\n");
        Socket connection = server.accept();
        displayMessage("Connection received from: " + connection.getInetAddress().getHostName() + "\n");

        SockServer sockServer = new SockServer(connection, this);
        sockServers.add(sockServer);
        executor.execute(sockServer);
    }

    public void displayResults() {
        for (SockServer sockServer : sockServers) {
            Player player = sockServer.getPlayer();

            sockServer.sendData("Dealer's hand: " + dealer.getHand() + "\n");

            if (player.getInGame()) {
                if (player.getHandValue() <= 21) {
                    if (dealer.getHandValue() > 21 || dealer.getHandValue() < player.getHandValue()) {
                        sockServer.sendData("You win!\n");
                    } else if (dealer.getHandValue() > player.getHandValue()) {
                        sockServer.sendData("You lose!\n");
                    } else {
                        sockServer.sendData("It's a tie!\n");
                    }
                } else {
                    sockServer.sendData("You bust! You lose!\n");
                }
            } else {
                sockServer.sendData("Please wait for the dealer to finish...\n");
            }
        }
    }

    public void startRound() {
        if (roundOver) {
            roundOver = false;
            deck.shuffle();
            dealer.resetHand();
            dealCards();
            displayMessage("\n\nCARDS DEALT\n\n");
        }
    }

    private void dealCards() {
        dealDealer();
        for (SockServer sockServer : sockServers) {
            dealPlayer(sockServer);
        }
    }

    private void dealDealer() {
        dealer.addCardToHand(deck.dealCard());
        dealer.addCardToHand(deck.dealCard());
        System.out.println(dealer.getHand().toString());
        sendDataToAll("Dealer's face-up card: " + dealer.getHand().get(0).toString() + "\n");
        displayMessage("Dealer's hand: " + dealer.getHand() + "\n");
        displayMessage("Dealer's value: " + dealer.getHandValue() + "\n");

    }

    private void dealPlayer(SockServer sockServer) {
        Player player = sockServer.getPlayer();
        player.resetHand();
        player.addCardToHand(deck.dealCard());
        player.addCardToHand(deck.dealCard());

        sockServer.sendData("You were dealt: " + player.getHand().get(0).toString() +
                " and " + player.getHand().get(1).toString() + "\n");

        sockServer.sendData("Your total: " + player.getHandValue() + "\n");
    }

    public void playerHit(SockServer sockServer) {
        if (!roundOver) {
            Player player = sockServer.getPlayer();
            player.addCardToHand(deck.dealCard());

            sockServer.sendData("You were dealt: " + player.getHand().get(player.getHand().size() - 1).toString() + "\n");
            sockServer.sendData("Your total: " + player.getHandValue() + "\n");

            if (player.getHandValue() > 21) {
                sockServer.sendData("Bust!\n");
                roundOver = true;
                dealerTurn();
            }
        }
    }

    public synchronized void playerStand(SockServer sockServer) {
        if (!roundOver) {
            sockServer.sendData("Please wait for the dealer to finish...\n");
            sockServer.getPlayer().setInGame(false);
            checkAllPlayersStand();
        }
    }

    private synchronized void checkAllPlayersStand() {
        boolean allPlayersStand = sockServers.stream().allMatch(sockServer -> !sockServer.getPlayer().getInGame());
        System.out.println("All players stand: " + allPlayersStand);

        if (allPlayersStand) {
            boolean allPlayersNotInGame = sockServers.stream().allMatch(sockServer -> !sockServer.getPlayer().getInGame());
            if (allPlayersNotInGame && !dealer.getInGame()) {
                System.out.println("Calling dealerTurn()");
                dealerTurn();
            }
        }
    }


    private void dealerTurn() {
        System.out.println("Turno del dealer");
        while (dealer.getHandValue() < 17) {
            Card dealerCard = deck.dealCard();
            if (dealerCard != null) {
                dealer.addCardToHand(dealerCard);
            } else {
                deck = new Deck();
                break;
            }
        }

        displayMessage("Dealer's hand: " + dealer.getHand() + "\n");

        for (SockServer sockServer : sockServers) {
            Player player = sockServer.getPlayer();
            if (player.getInGame()) {
                if (dealer.getHandValue() > 21) {
                    // Dealer busts, player wins
                    sockServer.sendData("Dealer busts! You win!\n");
                } else if (player.getHandValue() > 21) {
                    // Player busts, player loses
                    sockServer.sendData("You bust! You lose!\n");
                } else if (dealer.getHandValue() > player.getHandValue()) {
                    // Dealer wins
                    sockServer.sendData("You lose!\n");
                } else if (dealer.getHandValue() < player.getHandValue()) {
                    // Player wins
                    sockServer.sendData("You win!\n");
                } else {
                    // It's a tie
                    sockServer.sendData("It's a tie!\n");
                }
            }
        }

        roundOver = true;
        dealButton.setEnabled(true);
        sendDataToAll("Round over. You can start a new round.\n");
    }


    public Player getDealer() {
        return dealer;
    }

    public void removeSockServer(SockServer sockServer) {
        sockServers.remove(sockServer);
    }

    private void sendDataToAll(String message) {
        for (SockServer sockServer : sockServers) {
            sockServer.sendData(message);
        }
    }

    public void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(() -> displayArea.append(messageToDisplay));
    }

    public List<SockServer> getSockServers(){
        return sockServers;
    }
    public ServerSocket getServer() {
        return server;
    }

    public static void main(String[] args) {
        new Dealer();
    }

    public class SockServer implements Runnable {

        private ObjectOutputStream output;
        private ObjectInputStream input;
        private Socket connection;
        private Dealer dealer;
        private Player player;


        public SockServer(Socket connection, Dealer dealer) {
            this.connection = connection;
            this.dealer = dealer;
            this.player = new Player("Player" + dealer.getSockServers().size());
        }

        public void run() {
            try {
                getStreams();
                processConnection();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }

        private void getStreams() throws IOException {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();

            input = new ObjectInputStream(connection.getInputStream());
            dealer.displayMessage("Got I/O streams\n");
        }

        private void processConnection() throws IOException {
            dealer.displayMessage("Connection successful\n");
            sendData("Connection successful");

            do {
                try {
                    String message = (String) input.readObject();
                    dealer.displayMessage("Received: " + message + "\n");

                    if (message.equals("hit")) {
                        dealer.playerHit(this);
                    } else if (message.equals("stay")) {
                        dealer.playerStand(this);
                    }

                } catch (ClassNotFoundException e) {
                    dealer.displayMessage("Unknown object type received\n");
                }

            } while (dealer.getDealer().getInGame());

            dealer.removeSockServer(this);
            sendData("Hasta luego");
        }

        private void closeConnection() {
            dealer.displayMessage("Terminating connection\n");

            try {
                output.close();
                input.close();
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendData(String message) {
            try {
                output.writeObject(message);
                output.flush();
            } catch (IOException e) {
                dealer.displayMessage("Error writing object\n");
            }
        }

        public Player getPlayer() {
            return player;
        }
    }
}

