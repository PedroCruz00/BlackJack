package model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartClient extends JFrame {

    private JTextField playerNameField;
    private  JTextField serverAddressField;
    private  JTextField serverPortField;
    private JButton connectButton;
    private JButton exitButton;

    public StartClient() {

        setTitle("Blackjack Client");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        setCustomBackground();
    }

    private void setCustomBackground() {
        getContentPane().setBackground(new Color(240, 240, 240));
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);

        JLabel nameLabel = new JLabel("Nombre de Usuario:");
        nameLabel.setFont(labelFont);
        playerNameField = new JTextField();
        playerNameField.setFont(labelFont);

        JLabel addressLabel = new JLabel("Dirección del Servidor:");
        addressLabel.setFont(labelFont);
        serverAddressField = new JTextField();
        serverAddressField.setFont(labelFont);

        JLabel portLabel = new JLabel("Puerto del Servidor:");
        portLabel.setFont(labelFont);
        serverPortField = new JTextField();
        serverPortField.setFont(labelFont);

        connectButton = new JButton("Conectar al Servidor");
        connectButton.setFont(buttonFont);
        exitButton = new JButton("Salir");
        exitButton.setFont(buttonFont);

        // Cambiamos los colores de fondo y texto de los componentes
        nameLabel.setForeground(Color.BLACK);
        addressLabel.setForeground(Color.BLACK);
        portLabel.setForeground(Color.BLACK);
        connectButton.setBackground(new Color(39, 174, 96)); // Color verde lima
        exitButton.setBackground(new Color(39, 174, 96)); // Color rojo naranja

        mainPanel.add(nameLabel);
        mainPanel.add(playerNameField);
        mainPanel.add(addressLabel);
        mainPanel.add(serverAddressField);
        mainPanel.add(portLabel);
        mainPanel.add(serverPortField);
        mainPanel.add(new JLabel());
        mainPanel.add(new JLabel());
        mainPanel.add(connectButton);
        mainPanel.add(exitButton);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String playerName = playerNameField.getText();
                    String serverAddress = serverAddressField.getText();
                    int serverPort = Integer.parseInt(serverPortField.getText());

                    if (playerName.isEmpty() || serverAddress.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Ingresa un nombre de usuario y una dirección de servidor válidos.");
                        return;
                    }

                    JOptionPane.showMessageDialog(null, "Conectando al servidor en " + serverAddress + ":" + serverPort +
                            " como " + playerName);

                }catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingresa un puerto válido.");
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add(mainPanel);
    }

 loadCode
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new StartClient().setVisible(true);
            }
        });
    }
}





}
 master
