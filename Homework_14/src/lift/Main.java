package lift;

import java.util.Scanner;

public class Main {
	/**
	 * @Overview: The class is the start of the whole program. It provides main
	 *            method.
	 */

	/**
	 * @MODIFIES:None;
	 * @EFFECTS: The start of the whole elevator system, it states a SubScheduler, a
	 *           queue of request, a elevator and a Scanner, then use them to get
	 *           requests and execute them;
	 */
	public static void main(String args[]) {
		try {
			SubScheduler scheduler = new SubScheduler();
			Scanner in = new Scanner(System.in);
			Queue queue = new Queue();
			Elevator elevator = new Elevator();
			int rqNum = 0;
			while (in.hasNextLine()) {
				rqNum++;
				String str = in.nextLine();
				String str2 = str.replaceAll(" ", "");
				if (str2.equals("RUN") || rqNum >= 101) {
					while (!queue.end()) {
						scheduler.command(queue, elevator);
					}
					break;
				} else {
					queue.parse(str2);
				}
			}
			in.close();
		} catch (Throwable e) {
		}
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:\result == true;
	 */
	public boolean repOK() {
		return true;
	}
}
