package game;

import java.io.Serializable;
import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import environment.LocalBoard;

@SuppressWarnings("serial")
public class ObstacleMover extends Thread implements Serializable{
	private Obstacle obstacle;
	private LocalBoard board;
	
	public ObstacleMover(Obstacle obstacle, LocalBoard board) {
		super();
		this.obstacle = obstacle;
		this.board = board;
	}
	
	
	 private void moveObstacles() {
	        for (int x = 0; x < Board.NUM_COLUMNS; x++) {
	            for (int y = 0; y < Board.NUM_ROWS; y++) {
	                Cell cell = board.getCell(new BoardPosition(x, y));
	                if (cell.getGameElement() != null && cell.getGameElement().equals(obstacle) && !board.isFinished() && obstacle.getRemainingMoves() > 0) {
	                	cell.removeObstacle();
	                	obstacle.reduceRemainingMoves();
	                	board.addGameElement(obstacle);
	                	try {
							sleep(Obstacle.OBSTACLE_MOVE_INTERVAL);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	                }
	           }
	        }
	 }

	@Override
	public void run() {
		// TODO
		while (!board.hasStarted())
			continue;
		while (obstacle.getRemainingMoves() > 0 && !board.isFinished()) {
			moveObstacles();
		}
	}
}