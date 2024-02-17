package remote;

import environment.Board;

/** Remote client, only for part II
 * 
 * @author luismota
 *
 */

public class Client extends Thread{
	public static void main(String[] args) {
		RemoteBoard rm = new RemoteBoard();
		rm.getData();
		rm.init();
		while (!rm.hasStarted()) //wait for the local board to start playing
			rm.getData();
		rm.sendStream();
        while (!rm.isFinished()) {
        	rm.boardLooper();
        	if (!rm.getKey().equals("EMPTY"))
	        try {
				Thread.sleep(Board.PLAYER_PLAY_INTERVAL);
			} catch (InterruptedException e) {
				System.out.println("server: Remote Client was interrupted while sleeping");
				System.exit(-1);
			}
        }
        System.out.println("Game over!");
    }
}
