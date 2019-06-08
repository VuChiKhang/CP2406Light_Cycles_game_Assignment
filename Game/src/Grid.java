import javax.swing.*;
import java.awt.*;

public class Grid extends JPanel {
    private final int GRID_HEIGHT = 100;
    private final int GRID_WIDTH = 100;
    private int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT];
    private final int WIDTH = GRID_WIDTH * 5;
    private final int HEIGHT = GRID_HEIGHT * 5;
    private final Color PLAYER1 = Color.blue;
    private final Color PLAYER2 = Color.red;
    private Bike bike1;
    private Bike bike2;
    private Bike controlledBike;
    private Bike serverBike;

    private NetworkConnector connector;
    private Color userColor;

    public Grid() {
        setPreferredSize(new Dimension(WIDTH + 1, HEIGHT + 1));//Plus one to assure the edge line is shown

        //Set everything to 0
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                grid[x][y] = 0;
            }
        }
        userColor = UIManager.getColor("Panel.background");
    }

    public void startGame(int controlled) {
        try {
            Thread.sleep(1000);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        bike1 = new Bike(25, 75, grid, 1, Bike.DIRECTION_EAST, this);
        bike2 = new Bike(75, 25, grid, 2, Bike.DIRECTION_WEST, this);

        if(controlled == 1) {
            controlledBike = bike1;
            serverBike = bike2;
        }
        else {
            controlledBike = bike2;
            serverBike = bike1;
        }

        controlledBike.startGame();
    }

    public void connect(String hostname, String username) {
        connector = new NetworkConnector(hostname, username, this);
        System.out.println(connector.getUserID());
        if(connector.getUserID() == 1){
            userColor = PLAYER1;
        }else if(connector.getUserID() == 2){
            userColor = PLAYER2;
        }
        repaint();
    }

    public void turnNorth() {
        controlledBike.turnNorth();
    }

    public void turnEast() {
        controlledBike.turnEast();
    }

    public void turnSouth() {
        controlledBike.turnSouth();
    }

    public void turnWest() {
        controlledBike.turnWest();
    }

    public void stop(){
    	controlledBike.stop();
    }

    public void won(){
		JOptionPane.showMessageDialog(this, "You Win!");
	}

	public void lost(){
		JOptionPane.showMessageDialog(this, "You Lost :/");
	}


    public Bike getServerBike() {
        return serverBike;
    }

    public NetworkConnector getConnector() {
        return connector;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //EDGES
        g.drawLine(0, 0, 0, HEIGHT);//left side
        g.drawLine(WIDTH, 0, WIDTH, HEIGHT);//right side
        g.drawLine(0, 0, WIDTH, 0);//top
        g.drawLine(0, HEIGHT, WIDTH, HEIGHT);//bottom

        //Draw snakes on screen here
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (grid[x][y] != 0) {
                    if (grid[x][y] == 1) {
                        g.setColor(PLAYER1);
                    } else if (grid[x][y] == 2) {
                        g.setColor(PLAYER2);
                    }
                    g.fillRect(x * 5, y * 5, 5, 5);
                }
            }
        }
        g.setColor(userColor);
        g.fillRect(0, 501, 501, 505);
    }
}
