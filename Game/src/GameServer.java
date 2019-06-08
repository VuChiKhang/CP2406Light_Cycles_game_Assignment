import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class GameServer extends JFrame {
    private static final int PORT = 8888;

    public static final int MAX_CLIENTS = 2;

    private boolean acceptingPlayers = true;
    private Vector<Player> players = new Vector<Player>();

    public static void main (String[] args) {
        new GameServer();

    }

    /**
     * Builds GUI
     * Connects to players
     */
    public GameServer() {
        System.out.println("Game server test");
        setLayout(new BorderLayout());

        //Add Labels to top
        JPanel topLables = new JPanel(new GridLayout(0, 3));

        JLabel player1Lable = new JLabel("Player 1");
        player1Lable.setHorizontalAlignment(JLabel.CENTER);
        topLables.add(player1Lable);

        JLabel player2Lable = new JLabel("Player 2");
        player2Lable.setHorizontalAlignment(JLabel.CENTER);
        topLables.add(player2Lable);

        add(topLables, BorderLayout.NORTH);

        //Add Text outputs to center
        JPanel centerTexts = new JPanel(new GridLayout(0, 3));

        JTextArea player1 = new JTextArea(35, 25);
        JScrollPane scroll1 = new JScrollPane(player1);
        DefaultCaret caret1 = (DefaultCaret) player1.getCaret();
        caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        player1.setEditable(false);
        centerTexts.add(scroll1);

        JTextArea player2 = new JTextArea(35, 25);
        JScrollPane scroll2 = new JScrollPane(player2);
        DefaultCaret caret2 = (DefaultCaret) player2.getCaret();
        caret2.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        player2.setEditable(false);
        centerTexts.add(scroll2);

        JTextArea chat = new JTextArea(35, 25);
        JScrollPane scrollChat = new JScrollPane(chat);
        DefaultCaret caretChat = (DefaultCaret) chat.getCaret();
        caretChat.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        chat.setEditable(false);
        centerTexts.add(scrollChat);
        add(centerTexts, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setTitle("Light Bikes Server");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        //Connect players to server
        ServerSocket ss = null;
        Socket s = null;
        try {
            ss = new ServerSocket(PORT);
            player1.append("Waiting for client connections...\n");
            while (acceptingPlayers) {
                s = ss.accept();
                Player temp = null;
                if(players.size() == 0) {
                    player1.append("Caught one - " + s + "\n");
                    temp = new Player(s, players.size() + 1, this, player1);
                    player2.append("Waiting for client connections...\n");
                } else if(players.size() == 1)  {
                    player2.append("Caught one - " + s + "\n");
                    temp = new Player(s, players.size() + 1, this, player2);
                }
                players.add(temp);
                new Thread(temp).start();
                acceptingPlayers = players.size() < MAX_CLIENTS;
            }
        }
        catch (IOException ioe) {
        }
    }

    public void pushToAll(String commandString) {
        for (Player player : players) {
            player.push(commandString);
        }
    }

    /**
     * Pushes a message to a single client.
     * @param  playerID         The id of the player to send to.
     * @param  commandString    text to send to the specified
     *                          player.
     */
    public void pushToPlayer(int playerID, String commandString) {
        players.get(playerID-1).push(commandString);
    }

    /**
     * Push a message to all clients except for the specified player.
     * @param  playerID         The player ID to avoid
     * @param  commandString   text to push to players.
     */
    public void pushToOthers(int playerID, String commandString) {
        for (Player player : players) {
            if (player.getPlayerID() != playerID) {
                player.push(commandString);
            }
        }
    }

    private String makeCommandString(String command, String value) {
        return command + ":" + value + ";";
    }

    /**
     * Starts the game by sending a start message to all players connected to
     */
    public void startGame() {
        pushToAll(makeCommandString("rsp-game-start", "true"));
    }
}

