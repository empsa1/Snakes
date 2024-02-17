package remote;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import environment.LocalBoard;
import environment.Board;
import game.Server;
import gui.SnakeGui;

/** Remote representation of the game, no local threads involved.
 * Game state will be changed when updated info is received from Server.
 * Only for part II of the project.
 * @author luismota
 *
 */
@SuppressWarnings("serial")
public class RemoteBoard extends Board{
	private  transient Socket socket = null;
	private ObjectInputStream objectInputStream;
    private SnakeGui gui;
    private String hostName;
    private String key = "EMPTY";

	public RemoteBoard() {
		super();
		try {
			connectToServer();
		} catch (IOException e) {
			System.out.println("Client was not able to connect to the server");
			System.exit(-1);
		}
	}
	
	public void boardLooper() {
			getData();
			this.setChanged();
			sendStream();
	}
	
	public void connectToServer() throws IOException {
        try {
            socket = new Socket(InetAddress.getByName(hostName), Server.PORT);
            System.out.println("client: Connected to server.");
        } catch (IOException e) {
            System.err.println("client: Error connecting to server...aborting");
            System.exit(1);
        }
    }
	
	@Override
	    public void handleKeyPress(int keyCode) {
			if (keyCode == 38)
				key = "UP";
			else if (keyCode == 39)
				key = "RIGHT";
			else if (keyCode == 40)
				key = "DOWN";
			else if (keyCode == 37)
				key = "LEFT";
			else {
				return ;
			}
	    }

	@Override
	public void handleKeyRelease() {
		key = "EMPTY";
	}
	
	public void buildGUI() {
		if (gui == null)
		{
			gui = new SnakeGui(this, 600, 0);
			gui.init();
		}
	}
	
	public void getData() {
		LocalBoard b;
		try {
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			b = (LocalBoard) objectInputStream.readObject();
			this.cells = b.getCells();
			this.snakes = b.getSnakes();
			this.hasStarted = b.hasStarted();
			this.isFinished = b.isFinished();
			this.obstacles = b.getObstacles();
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Couldnt receive more data from server");
			System.exit(-1);
		}
	}
	
	public String getKey() {
		return key;
	}
	
	public void sendStream() {
		while (!this.hasStarted) {
			getData();
			continue;
		}
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter ( new OutputStreamWriter ( socket.getOutputStream())) , true );
			out.println(key);
			out.flush();
			//key = "EMPTY";
		} catch (IOException e) {
			System.out.println("client: Error sending data");
			System.exit(-1);
		}
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("client: error closing socket");
			System.exit(-1);
		}
	}
	
	@Override
	public void init() {
		buildGUI();
	}
}
