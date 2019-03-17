package taxi;

import java.util.ArrayList;

public class Scheduler implements Runnable {
	private ArrayList<Taxi> taxiList;
	private RequestQueue requestQueue;
	private long clock;// use to calculate the sleep time;

	public Scheduler(TaxiGUI taxiGUI, TaxiMap taxiMap, RequestQueue requestQueue, ArrayList<Taxi> taxiList) {
		/**
		 * @REQUIRES: taxiGUI!=null; requestQueue!=null; taxiList!=null; 
		 * @MODIFIES: this.requestQueue; this.taxiList;
		 * @EFFECTS: 	
		 * 		this.requestQueue = requestQueue;
		 * 		this.taxiList = taxiList;
		 * 		for each taxi in taxiList use taxi.initTaxi(taxiGUI) to init it;
		 */
		this.requestQueue = requestQueue;
		this.taxiList = taxiList;
		for (Taxi taxi : taxiList) {
			taxi.initTaxi(taxiGUI);
		}
	}

	private long calculateSleepTime() {
		/**
		 * @REQUIRES: None; 
		 * @MODIFIES: None;
		 * @EFFECTS: 	
		 * 		\result == clock + 500 - System.currentTimeMillis() >= 0 ? clock - System.currentTimeMillis() : 0
		 */
		clock += 500;
		return clock - System.currentTimeMillis() >= 0 ? clock - System.currentTimeMillis() : 0;
	}

	public void run() {
		/**
		 * @REQUIRES: None; 
		 * @MODIFIES: 
		 * 		clock;
		 * 		requestQueue;
		 * @EFFECTS:
		 * 		normal_behavior:
		 * 			The virtual time lapse--clock;
		 * 			Move taxis according their own status and make some status change if necessarily.
		 * 		exception_behavior(Throwable e):
		 * 			print the wrong information.
		 * 		exception_behavior(InterruptedException e):
		 * 			print the wrong information.
		 * @THREAD_REQUIRES None;
		 * @THREAD_EFFECTS \locked(requestQueue): 
		 */
		clock = TaxiSys.MAINTIME;
		while (true) {
			try {
				Thread.sleep(calculateSleepTime());
			} catch (InterruptedException e) {
				System.out.println("Sleeping Interupted");
			}
			try {
				synchronized (requestQueue) {
					for (Taxi taxi : taxiList) {
						taxi.move();
					}
					if (!requestQueue.isEmpty()) {
						requestQueue.checkListAndDisptach();
					}
					guigv.ClearFlow();
				}
			} catch (Throwable e) {
				System.out.println("Scheduler Interupted");
				break;
			}
		}
	}
}
