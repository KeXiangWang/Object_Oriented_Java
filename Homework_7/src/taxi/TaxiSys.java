package taxi;

import java.io.File;

public class TaxiSys {
	static final int SIZE = 80;// size of map
	static final int SQUARESIZE = SIZE * SIZE;// area of map
	static final int TAXINUM = 100;// total number of taxi
	static long MAINTIME;

	public static void main(String args[]) {
		Map map = new Map(new File("map.txt"));
		TaxiGUI taxiGUI = new TaxiGUI();
		TaxiMap taxiMap = new TaxiMap();
		taxiGUI.LoadMap(map.intMap, SIZE);
//		RequestQueue requestQueue = new RequestQueue(taxiGUI, taxiMap);
		RequestQueue requestQueue = new RequestQueue();
		MAINTIME = System.currentTimeMillis();
		Scheduler scheduler = new Scheduler(taxiGUI, taxiMap,requestQueue);
		InputHandler inputHandler = new InputHandler(requestQueue, taxiGUI, taxiMap);
		new Thread(inputHandler, "Thread-InputHandler").start();
		new Thread(scheduler, "Thread-Scheduler").start();
	}
}
