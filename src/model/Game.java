package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private List<Player> players;
    private int currentPlayerIndex;
    private Deck deck;

    public Game() {
        players = new ArrayList<>();
        currentPlayerIndex = 0;
        deck = new Deck();
        deck.shuffle(); // Baraja las cartas antes de comenzar el juego
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void playerHit(Player player) {
        Card card = deck.drawCard(); // Obtiene una carta de la baraja
        player.addCardToHand(card);

        if (player.getScore() > 21) {
            player.setPlayerState("Fuera");
            player.setInGame(false);
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void playerStand(Player player) {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        player.setInGame(false);
    }

    public void determineWinner() {
        int maxScore = 0;
        Player winner = null;

        for (Player player : players) {
            if (player.getScore() <= 21 && player.getScore() > maxScore) {
                maxScore = player.getScore();
                winner = player;
            }
        }

        if (winner != null) {
            winner.setPlayerState("Ganador");
        }
    }
}