import javax.swing.*;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Player implements Runnable {

    private String username = "None";
    private int playerID;
    private GameServer gameServer;
    private PrintWriter out;
    private Socket s;
    public JTextArea playerOutput;

    public Player(Socket s, int playerID, GameServer gameServer, JTextArea playerOutput) {
        this.s = s;
        this.playerID = playerID;
        this.gameServer = gameServer;
        this.playerOutput = playerOutput;
    }

    public void run() {
        try {
            out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            new Thread(new Listener(s, this)).start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        playerOutput.append("Sending player id: " + playerID + " to " + s + "\n");
        sendPlayerID();

        if (playerID == gameServer.MAX_CLIENTS) {
            gameServer.startGame();
        }
    }

    public void push(String line) {
        try {
            if (out == null) {
                out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            }
            out.println(line);
            out.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void push(String command, String value) {
        push(makeCommandString(command, value));
    }
    private void pushToOthers(String command, String value) {
        gameServer.pushToOthers(playerID, makeCommandString(command, value));
    }

    private void pushToAll(String command, String value) {
        gameServer.pushToAll(makeCommandString(command, value));
    }

    private String makeCommandString(String command, String value) {
        return command + ":" + value + ";";
    }

    public void processCommand(String cmdString) {
        String[] temp = cmdString.split(":");
        String command = temp[0];
        String value = temp[1];

        playerOutput.append("Processing " + cmdString + "\n");

        switch (command) {

            case "set-username":
            setUsername(value);
            break;

            case "set-location":
            pushToOthers("rsp-update-location", value);
            break;

            case "set-dead":
            pushToOthers("rsp-dead", value);
            break;
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void sendPlayerID() {
        push("rsp-user-id", ""+playerID);
    }

}

class Listener implements Runnable {

    Scanner scan;
    Socket s;
    Player p;

    public Listener(Socket s, Player p) {
        p.playerOutput.append("Listener started for " + s + "\n");
        this.s = s;
        this.p = p;
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
            p.playerOutput.append("Connection to client " + s + " lost.\n");
        }
    }

    private void parseCommands(String line) {
        String[] commands = line.split(";");
        for (String command : commands) {
            p.processCommand(command);
        }
    }
}
