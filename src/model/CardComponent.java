package model;

import javax.swing.*;
import java.awt.*;

public class CardComponent extends JComponent {
    private Card card;

    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 120;

    public CardComponent(Card card) {
        this.card = card;
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibuja el fondo de la carta
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);

        // Dibuja el borde de la carta
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, CARD_WIDTH - 1, CARD_HEIGHT - 1);

        // Dibuja el valor y el palo de la carta
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString(card.getRank().toString(), 10, 30);

        // Dibuja el símbolo del palo de la carta
        int symbolX = CARD_WIDTH / 2 - 10;
        int symbolY = CARD_HEIGHT / 2 + 10;
        g2d.setFont(new Font("Arial", Font.BOLD, 40));

        switch (card.getSuit()) {
            case HEARTS:
                g2d.setColor(Color.RED);
                drawHeartSymbol(g2d, symbolX, symbolY);
                break;
            case DIAMONDS:
                g2d.setColor(Color.RED);
                drawDiamondSymbol(g2d, symbolX, symbolY);
                break;
            case CLUBS:
                g2d.setColor(Color.BLACK);
                drawClubSymbol(g2d, symbolX, symbolY);
                break;
            case SPADES:
                g2d.setColor(Color.BLACK);
                drawSpadeSymbol(g2d, symbolX, symbolY);
                break;
        }
    }

    private void drawHeartSymbol(Graphics2D g2d, int x, int y) {
        g2d.fillOval(x/2 + 42/2, y/2 + 70/2, 35/2, 35/2);
        g2d.fillOval(x/2 + 73/2, y/2 + 70/2, 35/2, 35/2);
        g2d.fillArc(x/2 + 30/2, y/2 + 90/2, 90/2, 90/2, 51, 78);
    }


    private void drawClubSymbol(Graphics2D g2d, int x, int y) {
        g2d.fillOval(x/2 + 40/2, y/2 + 90/2, 35/2, 35/2);
        g2d.fillOval(x/2 + 75/2, y/2 + 90/2, 35/2, 35/2);
        g2d.fillOval(x/2 + 58/2, y/2 + 62/2, 35/2, 35/2);
        g2d.fillRect(x/2 + 70/2, y/2 + 75/2, 10/2, 70/2);
    }

    private void drawSpadeSymbol(Graphics2D g2d, int x, int y) {
        g2d.fillOval(x/2 + 21, y/2 + 45, 18, 18);
        g2d.fillOval(x/2 + 36, y/2 + 45, 18, 18);
        g2d.fillArc(x/2 + 15, y/2 + 8, 45, 45, 51 + 180, 78);
        g2d.fillRect(x/2 + 35, y/2 + 50, 5, 20);
    }
    private void drawDiamondSymbol(Graphics2D g2d, int x, int y) {
        int[] polyX = {x/2 + 75/2, x/2 + 50/2, x/2 + 75/2, x/2 + 100/2};
        int[] polyY = {y/2 + 60/2, y/2 + 100/2, y/2 + 140/2, y/2 + 100/2};
        g2d.fillPolygon(polyX, polyY, 4);
    }

}