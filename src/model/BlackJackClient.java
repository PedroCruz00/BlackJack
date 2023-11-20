package model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class BlackJackClient extends JFrame {
    private String serverIp;
    private int serverPort;
    private Socket socket;
    private JButton hit;
    private JButton stay;
    private JPanel buttons;
    private JTextArea displayArea;
    private String message;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private boolean inGame;

    public BlackJackClient(String ip, String port) {
        super("Player");
        this.serverIp = ip;
        this.serverPort = Integer.parseInt(port);
        inGame = true;
        this.message = "";
        buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 2));
        hit = new JButton("Hit");
        stay = new JButton("Stay");
        hit.addActionListener(

                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        sendData("hit");
                    }
                }
        );

        stay.addActionListener(
                new ActionListener() {
                    // send message to server
                    public void actionPerformed(ActionEvent event) {
                        sendData("stay");
                    }
                }
        );

        buttons.add(hit, BorderLayout.SOUTH);
        buttons.add(stay, BorderLayout.SOUTH);
        buttons.setVisible(true);
        add(buttons, BorderLayout.SOUTH);
        displayArea = new JTextArea(); // create displayArea
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        setSize(300, 300); // set size of window
        setVisible(true); // show window
        try {
            socket = new Socket(serverIp, serverPort);
            System.out.println("Conectado al servidor: " + socket);

            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToServer() throws IOException {
        displayMessage("Attempting connection\n");

        socket = new Socket(serverIp, serverPort);

        displayMessage("Connected to: " +
                socket.getInetAddress().getHostName());
    }

    private void getStreams() throws IOException {
        // set up output stream for objects
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush(); // flush output buffer to send header information

        // set up input stream for objects
        input = new ObjectInputStream(socket.getInputStream());
        displayMessage("\nGot I/O streams\n");
    } // en

    private void processConnection() throws IOException {
        do {
            try {
                message = (String) input.readObject();
                displayMessage("\n" + message);
                if (message.contains("Bust!") || message.contains("Please Wait")) {
                    buttons.setVisible(false);
                }

            } // end try
            catch (ClassNotFoundException classNotFoundException) {
                displayMessage("\nUnknown object type received");
            } // end catch

        } while (!message.equals("SERVER>>> TERMINATE"));
    }

    public void closeConnection() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit(String response) {
        if (response.equalsIgnoreCase("Hasta luego")) {
            inGame = false;
        }
    }

    public void sendData(String message) {
        try {
            output.writeObject(message);
            output.flush();

        } // end try
        catch (IOException ioException) {

        }
    }

    public void runClient() {
        try {
            connectToServer();
            getStreams();
            processConnection();
        } catch (EOFException eofException) {
            displayMessage("\nClient terminated connection");
        } catch (IOException ioException) {
        } finally {
            closeConnection();
        }
    }

    private void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        displayArea.append(messageToDisplay);
                    }
                }
        );
    }

    public boolean getInGame() {
        return inGame;
    }
}