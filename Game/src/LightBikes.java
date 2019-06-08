import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

public class LightBikes extends JFrame implements KeyListener, MouseListener {
    private final String GAME_VERSION = "0.1";
    private JMenuItem jmiExit;
    private JMenuItem jmiAbout;
    private JMenuItem jmiCredit;
    private Grid gameGrid;
    private String hostname;
    private String username;
    public static void main(String[] args){
        new LightBikes();
    }

    public LightBikes(){

        ImageIcon icon = new ImageIcon("/Users/Jace/Downloads/Assgn2/src/Image/18lsqxo3x52a6jpg.jpg");
        int input = JOptionPane.showConfirmDialog(null,
                "Play Light Cycle?", "Light Cycle Game", JOptionPane. YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon );
        if (input == JOptionPane.NO_OPTION ){
            System.exit(0);
        }

        //Add JMenu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu jmFile = new JMenu("File");
        jmiExit = new JMenuItem("Exit");
        jmFile.add(jmiExit);
        menuBar.add(jmFile);
        JMenu jmHelp = new JMenu("Help");
        jmiAbout = new JMenuItem("About");
        jmHelp.add(jmiAbout);
        jmiCredit = new JMenuItem("Credit");
        jmHelp.add(jmiCredit);
        menuBar.add(jmHelp);
        setJMenuBar(menuBar);

        //Create Game grid and add to center
        gameGrid = new Grid();
        add(gameGrid, BorderLayout.CENTER);

        //Action listener for Menu items
        ActionListener menuListener = ae -> {

            Object choice = ae.getSource();

            if(choice == jmiExit){
                System.exit(0);
            }else if(choice == jmiAbout){
                JOptionPane.showMessageDialog(null, "LightBikes is a multiplayer game between two people." +
                        "This game is inspired by the same game from the TRON movie.\n\n" +
                        "CONTROLS\n" +
                        "Use the arrow keys to move. The game will start after your opponent connects.\n"
                );
            }else if(choice == jmiCredit){
                JOptionPane.showMessageDialog(null, "LightBikes v" + GAME_VERSION + "\n" +
                        "Created in June 2019.\n\n" +
                        "Vu Chi Khang:\n" + "13544920\n"
                );
            }
        };

        jmiExit.addActionListener(menuListener);
        jmiAbout.addActionListener(menuListener);
        jmiCredit.addActionListener(menuListener);

        pack();
        setLocationRelativeTo(null);
        setTitle("Light Bikes");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);

        //Get connection info
        hostname = JOptionPane.showInputDialog(null, "Enter the server hostname:");
        username = JOptionPane.showInputDialog(null, "Enter your desired username:");
        gameGrid.connect(hostname, username);
        gameGrid.setFocusable(true);
        gameGrid.addKeyListener(this);
        gameGrid.addMouseListener(this);
        gameGrid.requestFocus();
    }
    /**
     * Key listeners to listen for user input to control the bike.
     */
    @Override
    public void keyPressed(KeyEvent ke){

        if(ke.getKeyCode() == KeyEvent.VK_RIGHT){
            gameGrid.turnEast();
        }
        else if(ke.getKeyCode() == KeyEvent.VK_LEFT) {
            gameGrid.turnWest();
        }
        else if(ke.getKeyCode() == KeyEvent.VK_UP) {
            gameGrid.turnNorth();
        }
        else if(ke.getKeyCode() == KeyEvent.VK_DOWN) {
            gameGrid.turnSouth();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        gameGrid.requestFocus();
    }

    @Override
    public void keyReleased(KeyEvent ke){

    }

    @Override
    public void keyTyped(KeyEvent ke){

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
