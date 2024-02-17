package environment;

import java.io.Serializable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import game.GameElement;
import game.Goal;
import game.Obstacle;
import game.Snake;
/** Main class for game representation. 
 * 
 * @author luismota
 *
 */
@SuppressWarnings("serial")
public class Cell implements Serializable{
	private BoardPosition position;
	private Snake ocuppyingSnake = null;
	private GameElement gameElement=null;
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	
	public GameElement getGameElement() {
			return (gameElement);
	}

	public Cell(BoardPosition position) {
		super();
		this.position = position;
	}

	public BoardPosition getPosition() {
		return position;
	}

	public void request(Snake snake) throws InterruptedException{
	    lock.lock();
	    try {
	        while (isOcupiedBySnake() || (gameElement != null && gameElement instanceof Obstacle)) {
	                condition.await();
	        }
	        ocuppyingSnake = snake;
	    } finally {
	        lock.unlock();
	    }
	}

	public void release() {
	    lock.lock();
	    try {
	        if (ocuppyingSnake != null ) {
	            ocuppyingSnake = null;
	            condition.signalAll();
	        }
	    } finally {
	        lock.unlock();
	    }
	}

	public boolean isOcupiedBySnake() {
		return ocuppyingSnake!=null;
	}

	public  void setGameElement(GameElement element) {
		lock.lock();
		try {
				while (isOcupiedBySnake() || gameElement != null) {
					try {
						condition.await();
					} catch (InterruptedException e) {
						System.err.println("The game crashed trying to set a new gameElement");
						System.exit(-1);
					}
				}
				gameElement = element;
				condition.signalAll();
		} finally {
			lock.unlock();
		}

	}

	public boolean isOcupied() {
		return isOcupiedBySnake() || (gameElement!=null && gameElement instanceof Obstacle);
	}

	public Snake getOcuppyingSnake() {
		return ocuppyingSnake;
	}

	public  Goal removeGoal() {
		lock.lock();
		try {
			if (gameElement != null)
				gameElement = null;
			condition.signalAll();
		} finally {
			lock.unlock();
		}
		return null;
	}
	
	public void removeObstacle() {
		lock.lock();
		try {
			if (gameElement != null)
					gameElement = null;
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public Goal getGoal() {
		return (Goal)gameElement;
	}

	public boolean isOcupiedByGoal() {
		return (gameElement!=null && gameElement instanceof Goal);
	}
}
