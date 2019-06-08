import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {

    private BufferedReader br;

    private JTextArea chat;

    private JTextField newMsg;

    private PrintWriter pw;

    private Socket sock;

    private String username;


    public boolean connect(String host, String username) {
        this.username = username;
        try {
            sock = new Socket(host, 6667);
            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));

            newMsg.setEnabled(true);

            ReceiveMessages recv = new ReceiveMessages();
            recv.start();

            return true;
        } catch (UnknownHostException e) {
            chat.append("Cannot find chat server with specified IP and/or port. Is a firewall running?\n");
            return false;
        } catch (IOException e) {
            chat.append("Unable to connect to chat server. Is the server running?\n");
            return false;
        }
    }

    public void send(String msg) {
        pw.println(msg);
        pw.flush();
    }

    public void closeSocket() {
        try {
            pw.println("s7XUH94y");
            pw.flush();
            br.close();
            pw.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ReceiveMessages extends Thread {

        public void run() {
            while (true) {
                try {
                    chat.append(br.readLine() + "\n");
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}
