import javax.swing.*;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class NetworkConnector {
    private static final int PORT = 8888;

    private String hostname;
    private String username;
    private int userID = -1;
    private Socket s;
    private PrintWriter out;
    private String[] otherPlayers;
    private Grid grid;

    public NetworkConnector(String hostname, String username, Grid grid) {
        this.username = username;
        this.hostname = hostname;
        this.grid = grid;
        connect();
    }

    public void connect() {
        try {
            s = new Socket(hostname, PORT);
            out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            new Thread(new Listener(s)).start();
            Thread.sleep(1000);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot connect to server, restart program");
            System.exit(0);
        }
        sendUsername(username);
    }

    public void sendUsername(String username) {
        sendCommand("set-username", username);
    }

    public void sendLocation(int x, int y) {
        sendCommand("set-location", ("" + x + "," + y));
    }

    public void notifyDeath() {
        sendCommand("set-dead","true");
    }

    private void send(String commandString) {;
        try {
            if (out == null) {
                out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            }
            out.println(commandString);
            out.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String command, String value) {
        send(makeCommandString(command, value));
    }

    public void sendCommand(String command, int value) {
        send(makeCommandString(command, "" + value));
    }

    private String makeCommandString(String command, String value) {
        return command + ":" + value + ";";
    }

    public void processCommand(String cmdString) {
        String[] temp = cmdString.split(":");
        String command = temp[0];
        String value = temp[1];

        switch (command) {
            case "rsp-user-id":
            setUserID(value);
            break;

            case "rsp-game-start":
            startGame(value);
            break;

            case "rsp-username-list":
            setPlayers(value);
            break;

            case "rsp-update-location":
            updateLocation(value);
            break;

            case "rsp-dead":
            grid.stop();
            grid.won();
            break;
        }
    }

    // Methods to process different commands from the server

    private void startGame(String value) {
        grid.startGame(userID);
    }

    private void setPlayers(String value) {
        otherPlayers = value.split(",");
    }

    private void updateLocation(String value) {
        String[] pair = value.split(",");
        grid.getServerBike().setLocation(Integer.parseInt(pair[0]), Integer.parseInt(pair[1]));
    }

    private void setUserID(String value) {
        userID = Integer.parseInt(value);
    }

    public int getUserID(){
    	return userID;
    }

    class Listener implements Runnable {

        private Scanner scan;
        private Socket s;

        public Listener(Socket s) {
            this.s = s;
            try {
                scan = new Scanner(s.getInputStream());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                while (true) {
                    String line = scan.nextLine();
                    parseCommands(line);
                }
            }
            catch (Exception e) {
                System.out.println("Lost the server connection");
            }
        }

        private void parseCommands(String line) {
            String[] commands = line.split(";");
            for (String command : commands) {
                processCommand(command);
            }
        }
    }
}
