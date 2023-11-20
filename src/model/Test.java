package model;

import javax.swing.*;
import java.awt.*;

public class Test {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Crear una instancia de la carta
            Card card = new Card(Suit.HEARTS, Rank.ACE);
            Card card1 = new Card(Suit.DIAMONDS, Rank.ACE);
            Card card2 = new Card(Suit.SPADES, Rank.ACE);
            Card card3 = new Card(Suit.CLUBS, Rank.ACE);

            // Crear una instancia del componente gráfico de la carta
            CardComponent cardComponent = new CardComponent(card);
            CardComponent cardComponent1 = new CardComponent(card2);
            CardComponent cardComponent2 = new CardComponent(card3);
            CardComponent cardComponent3 = new CardComponent(card1);

            // Crear un JFrame y agregar el componente gráfico
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(200, 300); // Ajusta el tamaño del JFrame según tus necesidades
            frame.setLayout(new GridLayout());
            frame.getContentPane().add(cardComponent);
            frame.getContentPane().add(cardComponent1);
            frame.getContentPane().add(cardComponent2);
            frame.getContentPane().add(cardComponent3);
            frame.setVisible(true);
        });
    }
}