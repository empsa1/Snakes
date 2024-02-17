package environment;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import game.Goal;
import game.Obstacle;
import game.Snake;
import game.AutomaticSnake;
import game.ObstacleMover;

/** Class representing the state of a game running locally
 * 
 * @author luismota
 *
 */
@SuppressWarnings("serial")
public class LocalBoard extends Board{
	
	private static final int NUM_SNAKES = 2; //2
	private static final int NUM_OBSTACLES = 25; //25
	private static final int NUM_SIMULTANEOUS_MOVING_OBSTACLES = 3; //3;
	private static ExecutorService executor = Executors.newFixedThreadPool(NUM_SIMULTANEOUS_MOVING_OBSTACLES);

	

	@SuppressWarnings("unused")
	public LocalBoard() {
		for (int i = 0; i < NUM_SNAKES; i++) {
			AutomaticSnake snake = new AutomaticSnake(i, this);
			snakes.add(snake);
		}
		addObstacles( NUM_OBSTACLES);
		Goal goal = addGoal();
	}

	public void init() {
		for(Snake s:snakes)
			s.start();
		// TODO: launch other threads
		LinkedList<Obstacle> obs = getObstacles();
		for (Obstacle o: obs)
		{
			ObstacleMover oM = new ObstacleMover(o, this);
			executor.submit(oM);
		}
		setChanged();
	}

	

	@Override
	public void handleKeyPress(int keyCode) {
		// do nothing... No keys relevant in local game
	}

	@Override
	public void handleKeyRelease() {
		// do nothing... No keys relevant in local game
	}





}