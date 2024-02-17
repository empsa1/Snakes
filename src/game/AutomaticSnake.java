package game;

import java.io.Serializable;
import environment.LocalBoard;
import environment.Cell;
import environment.Board;
import environment.BoardPosition;

@SuppressWarnings("serial")
public class AutomaticSnake extends Snake implements Serializable{
	public boolean difMove;
	public AutomaticSnake(int id, LocalBoard board) {
		super(id,board);
		difMove = false;
	}
	
	private Cell moveSnake(MOVEMENT direction) {
	    Cell nextCell = null;
	    switch (direction) {
	        case RIGHT:
	        	if (validSafeMove(cells.getLast().getPosition().getCellRight()))	
	        		nextCell = getBoard().getCell(cells.getLast().getPosition().getCellRight());
	            break;
	        case LEFT:
	        	if (validSafeMove(cells.getLast().getPosition().getCellLeft()))
	        		nextCell = getBoard().getCell(cells.getLast().getPosition().getCellLeft());
	            break;
	        case UP:
	        	if (validSafeMove(cells.getLast().getPosition().getCellAbove()))
	        		nextCell = getBoard().getCell(cells.getLast().getPosition().getCellAbove());
	            break;
	        case DOWN:
	        	if (validSafeMove(cells.getLast().getPosition().getCellBelow()))	
	        		nextCell = getBoard().getCell(cells.getLast().getPosition().getCellBelow());
	            break;
	    }
	    if (nextCell != null)
	    	return (nextCell);
	    return null;
	}
	
	public double isValidMovement(BoardPosition bp, double currentDistance) {
		if (bp.x < Board.NUM_ROWS && bp.y < Board.NUM_COLUMNS && bp.x >= 0 && bp.y >= 0) {
			Cell cell = getBoard().getCell(bp);
			if (!cell.isOcupiedBySnake() && !(cell.getGameElement() instanceof Obstacle) && currentDistance > bp.distanceTo(getBoard().getGoalPosition()))
				return bp.distanceTo(getBoard().getGoalPosition());
		}
		return Board.NUM_ROWS * Board.NUM_COLUMNS;
	}
	
	@Override
	public void run() {
	    doInitialPositioning();
	    if (!this.getBoard().hasStarted()) {
	    	try {
            	// Sleep for 10 seconds (10000 milliseconds)
            	Thread.sleep(10000);
            	this.getBoard().setStarted();
        	} catch (InterruptedException e) {
        		e.printStackTrace();
        	}
	    }
	    while (!getBoard().isFinished()) {
	        MOVEMENT flag = null;
	        double distance = Board.NUM_ROWS * Board.NUM_COLUMNS + 1;
	        double rightDistance = isValidMovement(cells.getLast().getPosition().getCellRight(), distance);
	        double leftDistance = isValidMovement(cells.getLast().getPosition().getCellLeft(), distance);
	        double aboveDistance = isValidMovement(cells.getLast().getPosition().getCellAbove(), distance);
	        double belowDistance = isValidMovement(cells.getLast().getPosition().getCellBelow(), distance);
	        double minDistance = Math.min(rightDistance, Math.min(leftDistance, Math.min(aboveDistance, belowDistance)));
	        if (minDistance < distance) {
	            distance = minDistance;
	            if (minDistance == rightDistance)
	                flag = MOVEMENT.RIGHT;
	            else if (minDistance == leftDistance)
	                flag = MOVEMENT.LEFT;
	            else if (minDistance == aboveDistance)
	                flag = MOVEMENT.UP;
	            else if (minDistance == belowDistance)
	                flag = MOVEMENT.DOWN;
	            try {
	            	if (!difMove)
	            		this.move(moveSnake(flag));
	            	else {
	            		System.out.println("Diff move");
	            		this.move(moveSafe(this.getCells().getLast().getPosition()));
	            		difMove = false;
	            	}
	            	sleep(Board.PLAYER_PLAY_INTERVAL);
	            } catch (InterruptedException e) {
	            		System.out.println("Snake: " + this.getIdentification()  + " was interrupted");
	            }
	        }
	    }
	    System.err.println(this.getIdentification() + " has died!" + getBoard().getGoalPosition());
	}

	public Cell moveSafe(BoardPosition position) {
		System.out.println("Inside moveSafe");
		if (validSafeMove(position.getCellRight())) {
			if (getBoard().getCell(position.getCellRight()).isOcupied())
				return(getBoard().getCell(position.getCellRight()));
		}
		if (validSafeMove(position.getCellAbove())) {
			if (getBoard().getCell(position.getCellAbove()).isOcupied())
				return(getBoard().getCell(position.getCellAbove()));
		}
		if (validSafeMove(position.getCellLeft())) {
			if (getBoard().getCell(position.getCellLeft()).isOcupied())
				return(getBoard().getCell(position.getCellLeft()));
		}
		if (validSafeMove(position.getCellBelow())) {
			if (getBoard().getCell(position.getCellBelow()).isOcupied())
				return(getBoard().getCell(position.getCellBelow()));
		}
		System.out.println("Leaving moveSafe");
		return null;
	}
}
