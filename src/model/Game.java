package model;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;
    private Deck deck;
    private boolean isGameOver;
    private int maxHandValue;
    private boolean isStarted;

    public Game() {
        players = new ArrayList<>();
        deck = new Deck();
        isGameOver = false;
        maxHandValue = 21;
        isStarted = false;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void startHand() {
        // Distribuir las cartas iniciales a los jugadores
        for (int i = 0; i < 2; i++) {
            for (Player player : players) {
                dealCard(player);
            }
        }
    }

    public void playerHit(Player player) {
        if (!isGameOver && player.getInGame()) {
            dealCard(player);
            if (player.getHandValue() > maxHandValue) {
                player.setPlayerState("Busted");
                player.setInGame(false);
            }
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void playerStand(Player player) {
        if (!isGameOver && player.getInGame()) {
            player.setPlayerState("Standing");
            player.setInGame(false);
        }
    }

    private void dealCard(Player player) {
        Card card = deck.dealCard();
        player.addCardToHand(card);
    }

    public void endHand() {
        boolean allPlayersStanding = true;

        for (Player player : players) {
            if (player.getInGame()) {
                allPlayersStanding = false;
                break;
            }
        }

        if (allPlayersStanding) {
            determineWinnersAndLosers();
            resetHand();
        }
    }

    private void determineWinnersAndLosers() {
        int highestHandValue = 0;

        for (Player player : players) {
            if (!player.getPlayerState().equalsIgnoreCase("Busted")) {
                highestHandValue = Math.max(highestHandValue, player.getHandValue());
            }
        }

        for (Player player : players) {
            if (!player.getPlayerState().equalsIgnoreCase("Busted")) {
                if (player.getHandValue() == highestHandValue) {
                    player.setPlayerState("Winner");
                } else {
                    player.setPlayerState("Loser");
                }
            }
        }

        isGameOver = true;
    }

    private void resetHand() {
        for (Player player : players) {
            player.resetHand();
            player.setPlayerState("Playing");
            player.setInGame(true);
        }

        deck.shuffle();
        isGameOver = false;
    }
    public void reset() {
        for (Player player : players) {
            player.getHand().clear();
            player.setPlayerState("Playing");
            player.setInGame(true);
        }
        deck.shuffle();
        isGameOver = false;
        isStarted = false;
    }
}



