package gui;

import environment.LocalBoard;
import game.Server;

public class Main {
	public static void main(String[] args) {
		LocalBoard board = new LocalBoard();
		SnakeGui game = new SnakeGui(board,600,0);
		game.init();
		Server server = new Server(board);
		server.start();
		System.out.print("The game has end! ");
	}
}
