package game;

import java.io.Serializable;
import java.net.Socket;

import environment.Board;
 /** Class for a remote snake, controlled by a human 
  * 
  * @author luismota
  *
  */
@SuppressWarnings("serial")
public class HumanSnake extends Snake implements Serializable{
	@SuppressWarnings("unused")
	private transient final Socket clientSocket;

	public HumanSnake(int id,Board board, Socket clientSocket) {
		super(id,board);
		this.clientSocket = clientSocket;
		System.out.println("FInished processing the snake creation");
	}
	
	@Override
	public void start() {
	}

}
