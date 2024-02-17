package game;

import java.io.Serializable;
import environment.Board;

@SuppressWarnings("serial")
public class Obstacle extends GameElement implements Serializable{
	
	
	private static final int NUM_MOVES=3;
	public static final int OBSTACLE_MOVE_INTERVAL = 400;
	private int remainingMoves=NUM_MOVES;
	@SuppressWarnings("unused")
	private Board board;
	public Obstacle(Board board) {
		super();
		this.board = board;
	}
	
	public int getRemainingMoves() {
		return remainingMoves;
	}
	public void reduceRemainingMoves()
	{
		remainingMoves--;
	}
	
}