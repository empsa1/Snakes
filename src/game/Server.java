package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import environment.Board;
import environment.LocalBoard;

public class Server {
	public final static String SERVER_ADDRESS = "127.0.0.1";
     public final static int PORT = 8080;
	private LocalBoard gameBoard;
	@SuppressWarnings("unused")
	private int num_remotePlayers;
	
	public Server(LocalBoard gameBoard) {
		this.gameBoard = gameBoard;
		num_remotePlayers = 0;
	}
	
	private HumanSnake addHumanSnake(Socket clientSocket) {
		HumanSnake s = new HumanSnake(gameBoard.getSnakes().size() + 1, gameBoard, clientSocket);
        gameBoard.addSnake(s);
        s.doInitialPositioning();
        s.start();
        s.getBoard().setChanged();
        System.out.println("New snake was added in server: " + gameBoard.getSnakes().size());
        return s;
	}
	
	public void start(){
		try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            String serverAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Server started at: " + serverAddress + " on port " + PORT);
            while (gameBoard.isFinished() == false) {
                Socket clientSocket = serverSocket.accept();
                num_remotePlayers++;
                System.out.println("New player is connected: " + clientSocket);
                HumanSnake s = addHumanSnake(clientSocket);
                handler handler = new handler(clientSocket, s);
                Thread clientHandler = new Thread(handler);
                clientHandler.start();
            }
            serverSocket.close();
        } catch (IOException e) {
        	System.out.println("Error creating server...aborting!");
			System.exit(1);
        }
		System.out.println("Going back to main");
	}
	class handler implements Runnable{
		private Socket clientSocket;
		private HumanSnake hs;
		
		public handler(Socket clientSocket, HumanSnake hs) {
			this.clientSocket = clientSocket;
			this.hs = hs;
		}
		
		public void moveHumanSnake(String key) throws InterruptedException {
			if (key == null) {
				System.out.println("Something went wrong with getting the key proposed by a users");
				System.exit(-1);
			}
			if (key.equals("UP") && Snake.validSafeMove(hs.cells.getLast().getPosition().getCellAbove())) {
				hs.move(hs.getBoard().getCell(hs.cells.getLast().getPosition().getCellAbove()));
			}
			if (key.equals("DOWN") && Snake.validSafeMove(hs.cells.getLast().getPosition().getCellBelow())) {
				hs.move(hs.getBoard().getCell(hs.cells.getLast().getPosition().getCellBelow()));
			}
			if (key.equals("RIGHT") && Snake.validSafeMove(hs.getCells().getLast().getPosition().getCellRight())) {
				hs.move(hs.getBoard().getCell(hs.cells.getLast().getPosition().getCellRight()));
			}
			if (key.equals("LEFT") && Snake.validSafeMove(hs.cells.getLast().getPosition().getCellLeft())) {
				hs.move(hs.getBoard().getCell(hs.cells.getLast().getPosition().getCellLeft()));
			}
			hs.getBoard().setChanged();
		}
		
		public void run() { //separar os ciclos para nao receber informação inutil economizar threads
			ObjectOutputStream objectOutputStream;
			BufferedReader br;
			while (!gameBoard.isFinished()) {
				try {
					objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
					objectOutputStream.writeObject((Board) gameBoard);
					if (gameBoard.hasStarted()) { //cyclic barrier
						br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						String key = br.readLine();
						moveHumanSnake(key);
					}
				} catch (IOException | InterruptedException e) {
					System.out.println("server: Error sending object");
					System.exit(-1);
				}
				try {
					Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);
				} catch (InterruptedException e) {
					System.out.println("server: Error trying to make stream sender sleep");
					System.exit(-1);
				}	
			}
			try {
				objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
				objectOutputStream.writeObject((Board) gameBoard);
				clientSocket.close();
			} catch (IOException e) {
				System.out.println("server: Error sending object");
				System.exit(-1);
			}
		}
	}
}
