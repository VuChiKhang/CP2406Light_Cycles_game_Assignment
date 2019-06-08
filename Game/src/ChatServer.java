import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer implements Runnable {

    // Attributes
    private int clientCounter = 0;
    private ArrayList<ConnectedClient> clients;
    private JTextArea chat;

    public ChatServer(JTextArea chat) {
        this.chat = chat;
    }

    @Override
    public void run() {

        // Local variables
        ServerSocket srvSock = null;

        // Initialize attributes
        clients = new ArrayList<>();

        // Create the server socket
        try {
            srvSock = new ServerSocket(6667);
        } catch (IOException e) {
            chat.append("Error creating server socket\n");
        }

        // Create the socket and wait for a client to connect; once connected, run on its own thread
        try {
            while (true) {
                chat.append("Waiting for a client.\n");
                Socket sock = srvSock.accept();
                chat.append("Client found:\n" + sock + "\n");
                clients.add(new ConnectedClient(sock, chat));
                clients.get(clientCounter).start();
                clientCounter++;
            }
        } catch (IOException e) {
            chat.append("Error with server socket connections\n");
            try {
                srvSock.close();
            } catch (IOException e1) {
                chat.append("Error closing server socket\n");
            }
        }

    }
