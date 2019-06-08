import java.util.concurrent.TimeUnit;

public class Bike {

	/**
	 * Constant to represent logical north.
	 */
	public static final int DIRECTION_NORTH = 0;

	public static final int DIRECTION_EAST = 1;

	public static final int DIRECTION_SOUTH = 2;

	public static final int DIRECTION_WEST = 3;
	private static final int DELAY_IN_MILLS = 80;

	public int xPosition;
	public int yPosition;
	public int[][] gridArray;
	public int player;
	private int direction;
	public boolean gameState;
	private Grid grid;
	private NetworkConnector connector;

	public Bike(int _xPosition, int _yPosition, int[][] _gridArray, int _player, int initialDirection, Grid _grid){
		direction = initialDirection;
		xPosition = _xPosition;
		yPosition = _yPosition;
		gridArray = _gridArray;
		player = _player;
		gameState = true;
		grid = _grid;
		gridArray[xPosition][yPosition] = player;
		grid.repaint();
	}

	public void turnWest(){
		if(direction != DIRECTION_EAST){
			direction = DIRECTION_WEST;
		}
	}

	public void turnEast(){
		if(direction != DIRECTION_WEST){
			direction = DIRECTION_EAST;
		}
	}

	public void turnSouth(){
		if(direction != DIRECTION_NORTH){
			direction = DIRECTION_SOUTH;
		}
	}

	public void turnNorth(){
		if(direction != DIRECTION_SOUTH){
			direction = DIRECTION_NORTH;
		}
	}

	public void stop(){
		gameState = false;
	}

	public boolean checkLocation(int x, int y) {
		return x > 0 && x < gridArray.length && y > 0 && y < gridArray[0].length && gridArray[x][y] == 0;
	}

	public void updateLocation() {
		gridArray[xPosition][yPosition] = player;
		grid.repaint();
	}

	public void setLocation(int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
		updateLocation();
	}

	public boolean getGameState(){
		return gameState;
	}
	public int getXpos() {
		return xPosition;
	}
	public int getYpos() {
		return yPosition;
	}
	public void startGame() {
		new Thread(new Movement()).start();
	}


	class Movement implements Runnable {
		@Override
		public void run() {
			NetworkConnector connector = grid.getConnector();
			while (gameState) {
				switch(direction) {

					case DIRECTION_NORTH:
					gameState = checkLocation(xPosition, yPosition-1);
					if (gameState) {
						yPosition--;
					}
					break;

					case DIRECTION_EAST:
					gameState = checkLocation(xPosition+1, yPosition);
					if (gameState) {
						xPosition++;
					}
					break;

					case DIRECTION_SOUTH:
					gameState = checkLocation(xPosition, yPosition+1);
					if (gameState) {
						yPosition++;
					}
					break;

					case DIRECTION_WEST:
					gameState = checkLocation(xPosition-1, yPosition);
					if (gameState) {
						xPosition--;
					}
					break;
				}

				if (!gameState) {
					grid.stop();
					connector.notifyDeath();
					grid.lost();
					break;
				}

				// Send the server the bike's new location
				connector.sendLocation(xPosition, yPosition);

				// Show the new location on the grid
				updateLocation();

				// Take a break
				try{
					TimeUnit.MILLISECONDS.sleep(DELAY_IN_MILLS);
				}
				catch(InterruptedException ie){
					ie.printStackTrace();
				}
			}
		}
	}
}
