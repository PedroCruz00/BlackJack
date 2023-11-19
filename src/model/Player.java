package model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;
    private String playerState;
    private boolean inGame;

    public Player(String name) {
        this.name = name;
        hand = new ArrayList<>();
        playerState = "Jugando";
        inGame = true;
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }

    public int getScore() {
        int score = 0;
        int numAces = 0;

        for (Card card : hand) {
            if (card.getRank() == Rank.ACE) {
                numAces++;
                score += 11;
            } else {
                score += card.getRank().getValue();
            }
        }

        while (score > 21 && numAces > 0) {
            score -= 10;
            numAces--;
        }

        return score;
    }

    public String getName() {
        return name;
    }

    public int getHandValue() {
        return getScore();
    }

    public List<Card> getHand() {
        return hand;
    }

    public String getPlayerState() {
        return playerState;
    }

    public void setPlayerState(String state) {
        playerState = state;
    }

    public boolean getInGame(){
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }
    public void resetHand() {hand.clear();
    }
}