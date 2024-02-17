package game;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("serial")
public abstract class GameElement implements Serializable{
	protected transient Lock lock = new ReentrantLock();
}