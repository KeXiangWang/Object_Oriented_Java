package taxi;

import java.util.ArrayList;
import java.util.Scanner;

public class TaxiSys {
	static final int SIZE = 80;// size of map
	static final int SQUARESIZE = SIZE * SIZE;// area of map
	static final int TAXINUM = 100;// total number of taxi
	static long MAINTIME;
	static Request GhostRequest;

	public static void main(String args[]) {
		/**
		 * @REQUIRES:None;
		 * @MODIFIES:None;
		 * @EFFECTS: 
		 * 		normal_behavior:
		 * 			Initialize all classes, start the threads, and begin to process the
		 *          input.
		 *      exception_behavior(Throwable e):
		 *			print the wrong information.
		 *			System shut down.
		 */
		try {
			TaxiMap taxiMap = new TaxiMap();
			GhostRequest = new Request(taxiMap.getPoint(20, 0), taxiMap.getPoint(10, 0), System.currentTimeMillis());
			GhostRequest.initRequest(taxiMap);

			ArrayList<Taxi> taxiList = new ArrayList<Taxi>();
			RequestQueue requestQueue = new RequestQueue();
			ArrayList<TaxiChange> initTaxiList = new ArrayList<TaxiChange>();
			ArrayList<FlowChange> initFlowList = new ArrayList<FlowChange>();
			Scanner in = new Scanner(System.in);
			for (int i = 0; i < TaxiSys.TAXINUM; i++)
				taxiList.add(new Taxi(taxiMap));
			Map map = new Map();
			map.Load(requestQueue, taxiMap, in, initTaxiList, initFlowList);
			TaxiGUI taxiGUI = new TaxiGUI();
			taxiGUI.LoadMap(map.intMap, SIZE);
			for (FlowChange flowChange : initFlowList) {
				flowChange.changeFlow();
			}
			MAINTIME = System.currentTimeMillis();
			Scheduler scheduler = new Scheduler(taxiGUI, taxiMap, requestQueue, taxiList);
			for (TaxiChange taxiChange : initTaxiList) {
				taxiChange.changeTaxi(taxiList);
			}
			InputHandler inputHandler = new InputHandler(taxiList, requestQueue, taxiGUI, taxiMap, in);
			new Thread(inputHandler, "Thread-InputHandler").start();
			new Thread(scheduler, "Thread-Scheduler").start();
		}catch (Throwable e) {
			System.out.println("System wrong");
		}
	}
}
