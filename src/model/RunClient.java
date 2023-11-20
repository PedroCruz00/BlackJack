package model;

import javax.swing.*;

public class RunClient {
    public static void main(String[] args) {
        BlackJackClient client = new BlackJackClient("127.0.0.1", "8080");
        client.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        client.runClient();

    }
}
