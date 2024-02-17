package game;

import java.io.Serializable;

import environment.Board;

@SuppressWarnings("serial")
public class Goal extends GameElement  implements Serializable{
	private int value=1;
	private Board board;
	public static final int MAX_VALUE=10; //10
	public Goal( Board board2) {
		this.board = board2;
	}
	
	public int getValue() {
		return value;
	}
	public void incrementValue() throws InterruptedException {
		lock.lock();
		try {
			value++;
		} finally {
			lock.unlock();
		}
	}

	public int captureGoal() {
		lock.lock();
		int result = 0;
		try {
			board.getCell(board.getGoalPosition()).removeGoal();
			this.incrementValue();
			result = this.value;
			if (value >= MAX_VALUE) {
				board.setFinished();
			} else {
				this.board.setNewGoal();
				board.getCell(board.getGoalPosition()).setGameElement(this);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return result;
	}
}