package game;

import java.io.Serializable;
import java.util.LinkedList;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
/** Base class for representing Snakes.
 * Will be extended by HumanSnake and AutomaticSnake.
 * Common methods will be defined here.
 * @author luismota
 *
 */
@SuppressWarnings("serial")
public abstract class Snake extends Thread implements Serializable{
	@SuppressWarnings("unused")
	private static final int DELTA_SIZE = 10;
	protected LinkedList<Cell> cells = new LinkedList<Cell>();
	protected int size = 5;
	private int id;
	private Board board;

	
	public Snake(int id,Board board) {
		this.id = id;
		this.board=board;
	}

	public int getSize() {
		return size;
	}

	public int getIdentification() {
		return id;
	}

	public int getLength() {
		return cells.size();
	}
	
	public LinkedList<Cell> getCells() {
		return cells;
	}
	
	enum MOVEMENT
	{
		LEFT,
		RIGHT,
		UP,
		DOWN,;
	}
	
	public void move(Cell cell) throws InterruptedException{
		if (cell == null)
			return ;
	    if (this.isHuman() && cell.isOcupied()) {
	        return ;
	    }
	    cell.request(this);
		cells.add(cell);
	    if (cell.getGameElement() instanceof Goal) {
	        Goal goal = cell.getGoal();
	        System.out.println("Snake: " + this.id + " is going to capture goal: " + goal.getValue());
	        size += goal.captureGoal();
	    }
	    if (cells.size() == size) {
	        cells.getFirst().release();
	        cells.removeFirst();
	    }
	    getBoard().setChanged();
	}

	private boolean isHuman() {
	    return this instanceof HumanSnake;
	}
	
	public static boolean validSafeMove(BoardPosition bp)
	{
		if (bp.x < Board.NUM_ROWS && bp.y < Board.NUM_COLUMNS && bp.x >= 0 && bp.y >= 0) {
			return true;
		}
		return false;
	}
	
	public LinkedList<BoardPosition> getPath() {
		LinkedList<BoardPosition> coordinates = new LinkedList<BoardPosition>();
		for (Cell cell : cells) {
			coordinates.add(cell.getPosition());
		}
		return coordinates;
	}
	
	protected void doInitialPositioning() {
		int posX = 0;
		int posY = (int) (Math.random() * Board.NUM_ROWS);
		BoardPosition at = new BoardPosition(posX, posY);
		if (board.getCell(at).isOcupied()) {
			doInitialPositioning();
		}
		else {
			try {
				board.getCell(at).request(this);
				cells.add(board.getCell(at));
			} catch (InterruptedException e) {
				System.out.println("There was an interruption while setting up initial snake position!");
				System.exit(-1);
			}
		}
	}
	
	public Board getBoard() {
		return board;
	}
	
	
}
