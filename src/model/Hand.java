package model;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> cards;

    public Hand() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public int getScore() {
        int score = 0;
        int numAces = 0;

        for (Card card : cards) {
            if (card.getRank() == Rank.ACE) {
                score += 11;
                numAces++;
            } else if (card.getRank().getValue() > 10) {
                score += 10;
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

    public boolean isBust() {
        return getScore() > 21;
    }

    public boolean isBlackjack() {
        return cards.size() == 2 && getScore() == 21;
    }

    public List<Card> getCards() {
        return cards;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Card card : cards) {
            sb.append(card).append(" ");
        }

        return sb.toString().trim();
    }
}
