package taxi;

import java.util.ArrayList;

public class Scheduler implements Runnable {
	private ArrayList<Taxi> taxiList;
	private RequestQueue requestQueue;
	private long clock;// use to calculate the sleep time;

	public Scheduler(TaxiGUI taxiGUI, TaxiMap taxiMap, RequestQueue requestQueue) {
		this.requestQueue = requestQueue;
		taxiList = new ArrayList<Taxi>();
		for (int i = 0; i < TaxiSys.TAXINUM; i++)
			taxiList.add(new Taxi(taxiGUI, taxiMap));
	}

	private long calculateSleepTime() {
		clock += 200;
		return clock - System.currentTimeMillis() >= 0 ? clock - System.currentTimeMillis() : 0;
	}

	public void run() {
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
				}
			} catch (Throwable e) {
				System.out.println("Scheduler Interupted");
				break;
			}
		}
	}
}
