package model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class BlackJackClient extends JFrame {
    private String serverIp ;
    private int serverPort;
    private Socket socket;
    private JButton hit;
    private JButton stay;
    private JPanel buttons;
    private JTextArea displayArea;
    private String message ;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String chatServer;
    private boolean inGame;

    public BlackJackClient(String ip, String port) {
        super( "Player" );
        this.serverIp = ip;
        this.serverPort = Integer.parseInt(port);
        inGame = true;
        this.message =  "";
        buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,2));
        hit = new JButton("Hit");
        stay = new JButton("Stay");
        hit.addActionListener(

                new ActionListener()
                {
                    @Override
                    public void actionPerformed( ActionEvent event )
                    {
                        sendData( "hit" );
                    }
                }
        );

        stay.addActionListener(
                new ActionListener()
                {
                    // send message to server
                    public void actionPerformed( ActionEvent event )
                    {
                        sendData( "stay" );
                    } // end method actionPerformed
                } // end anonymous inner class
        ); // end call to addActionListener

        buttons.add(hit, BorderLayout.SOUTH);
        buttons.add(stay, BorderLayout.SOUTH);
        buttons.setVisible(true);
        add(buttons,BorderLayout.SOUTH);
        displayArea = new JTextArea(); // create displayArea
        add( new JScrollPane( displayArea ), BorderLayout.CENTER );

        setSize( 300, 300 ); // set size of window
        setVisible( true ); // show window
        try {
            socket = new Socket(serverIp, serverPort);
            System.out.println("Conectado al servidor: " + socket);

            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream( socket.getOutputStream());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            while (inGame) {
                processConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void processConnection() throws IOException
    {


        do
        {
            try
            {
                message = ( String ) input.readObject(); // read new message
                displayMessage( "\n" + message ); // display message
                if (message.contains("Bust!") || message.contains("Please Wait")){
                    buttons.setVisible(false);
                }

            } // end try
            catch ( ClassNotFoundException classNotFoundException )
            {
                displayMessage( "\nUnknown object type received" );
            } // end catch

        } while ( !message.equals( "SERVER>>> TERMINATE" ) );
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
    public void exit(String response){
        if (response.equalsIgnoreCase("Hasta luego")) {
            inGame = false;
        }
    }
    public void sendData( String message )
    {
        try
        {
            output.writeObject(  message );
            output.flush(); // flush data to output

        } // end try
        catch ( IOException ioException ) {

        }
    }
    private void displayMessage( final String messageToDisplay )
    {
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run() // updates displayArea
                    {
                        displayArea.append( messageToDisplay );
                    }
                }
        );
    }
    public boolean getInGame(){
        return inGame;
    }
}