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

    class ConnectedClient extends Thread {

        private BufferedReader br = null;

        private PrintWriter pw = null;

        private Socket sock;

        private String msg = "";

        private JTextArea chat;

        public ConnectedClient(Socket sock, JTextArea chat) {
            this.sock = sock;
            this.chat = chat;
            try {
                br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
            } catch (IOException e) {
                chat.append("IOException BufferedReader/Print Writer\n");
            }
        }

        public void send(String msg) {
            pw.println(msg);
            pw.flush();
        }

        public void addToChatWindow(String msg) {
            chat.append(msg + "\n");
        }

        public void sendToAll(String msg) {
            for (ConnectedClient cc : clients) {
                cc.send(msg);
            }
        }
        @Override
        public void run() {
            while (!msg.equalsIgnoreCase(null)) {
                try {

                    // Initialize attributes
                    br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));

                    // Read from client
                    msg = br.readLine();
                    sendToAll(msg);
                    // Show message in the chat window
                    addToChatWindow(msg);

                } catch (OptionalDataException e) {
                    chat.append("OptionalDataException occurred.\n");
                } catch (IOException e) {
                }
            }

            sendToAll("A user has disconnected.");
            clients.remove(this);

            // Close all readers, writers, sockets
            try {
                br.close();
                pw.close();
                sock.close();
            } catch (IOException e) {
                chat.append("IOException closing connection\n");
            }
        }
    }
}

