package taxi;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public class Request implements Runnable {
	/**
	 * @Overview: Request contains its customer point, its target point and its
	 *            merge time. The class provide a series of method to dispatch taxi
	 *            for it and change the taxi's status.
	 */
	private static int requestnumber;
	private int ID;
	private Point startPoint;
	private Point targetPoint;
	private long realTime;
	private PrintWriter printWriter;
	private long externalTime;
	private ArrayList<Taxi> taxiList;
	private TaxiMap taxiMap;
	private int tickTock;

	/**
	 * @REQUIRES: startPoint!=null; targetPoint!=null;
	 * @MODIFIES: this.ID ; this.targetPoint; this.startPoint ; this.realTime;
	 *            this.externalTime ; this.tickTock ;
	 * @EFFECTS: requestnumber == \old(requestnumber)+1; this.ID == requestnumber;
	 *           this.targetPoint == targetPoint; this.startPoint == startPoint;
	 *           this.realTime == time; this.externalTime == (time -
	 *           TaxiSys.MAINTIME) / 100 * 100; this.tickTock == 0;
	 */
	public Request(Point startPoint, Point targetPoint, long time) {
		this.ID = requestnumber++;
		this.targetPoint = targetPoint;
		this.startPoint = startPoint;
		this.realTime = time;
		this.externalTime = (time - TaxiSys.MAINTIME) / 100 * 100;// ROUNDOWN the time
		this.tickTock = 0;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == this.printWritter;
	 */
	public PrintWriter getPrintWritter() {
		return this.printWriter;
	}

	/**
	 * @REQUIRES: taxiMap!=null;
	 * @MODIFIES: this.taxiList; this.taxiMap; this.printWriter;
	 * @EFFECTS: print the wrong information. this.taxiList == new
	 *           ArrayList<>(); this.taxiMap == taxiMap; this.printWriter == new
	 *           PrintWriter(new BufferedWriter(new FileWriter("request" + ID +
	 *           ".txt")), true); 
	 * 
	 */
	public void initRequest(TaxiMap taxiMap) {
		try {
			this.taxiList = new ArrayList<>();
			this.taxiMap = taxiMap;
			this.printWriter = new PrintWriter(new BufferedWriter(new FileWriter("request" + ID + ".txt")), true);
			this.printWriter.println(this);
		} catch (Exception e) {
			System.out.println("IOException");
		}
	}

	/**
	 * @REQUIRES: o;
	 * @MODIFIES: None;
	 * @EFFECTS: (this.startPoint.x == rq.startPoint.x && this.startPoint.y ==
	 *           rq.startPoint.y && this.targetPoint.x == rq.targetPoint.x &&
	 *           this.targetPoint.y == rq.targetPoint.y && this.externalTime ==
	 *           rq.externalTime)==>\result==true;
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Request)) {
			return false;
		}
		Request rq = (Request) o;
		return (this.startPoint.x == rq.startPoint.x && this.startPoint.y == rq.startPoint.y
				&& this.targetPoint.x == rq.targetPoint.x && this.targetPoint.y == rq.targetPoint.y
				&& this.externalTime == rq.externalTime);
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == this.ID;
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == this.startPoint;
	 */
	public Point getStartPoin() {
		return startPoint;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == this.realTime;
	 */
	public long getRealTime() {
		return realTime;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == this.targetPoint;
	 */
	public Point getTargetPoin() {
		return targetPoint;
	}

	/**
	 * @REQUIRES: taxi;
	 * @MODIFIES: None;
	 * @EFFECTS: normal_behavior: !(taxiList.contains(taxi);
	 *           exception_behavior(Exception e): ;
	 */
	public void removeTaxi(Taxi taxi) {
		try {
			if (taxiList.contains(taxi)) {
				taxiList.remove(taxi);
			}
		} catch (Exception e) {
		}

	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this.taxiList;
	 * @EFFECTS: normal_behavior: (\all Taxi taxi in
	 *           \old(taxiMap.getAmbientTaxi(this)); this.taxiList.contains(taxi);
	 *           exception_behavior(Exception e): ;
	 */
	public void checkTaxiMap() {
		try {
			tickTock++;
			LinkedBlockingDeque<Taxi> tempList = taxiMap.getAmbientTaxi(this);
			for (Taxi taxi : tempList) {
				if (!taxiList.contains(taxi)) {
					taxiList.add(taxi);
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this.taxiList;
	 * @EFFECTS: Pick a nearest accessible taxi, which has the most
	 *           credit, from the robbed-taxiList and dispatch it to this request
	 *           when tickTock == 15. Delete this request from other taxi'
	 *           robbed-requestList. Print the relevant information.
	 * @THREAD_REQUIRES \locked(taxiList);
	 * @THREAD_EFFECTS: \locked(taxiList);
	 */
	public Boolean dispatch() {
		try {
			if (tickTock == 15) {
				synchronized (Request.class) {
					if (taxiList.size() == 0) {
						printWriter
								.println("Failed to dispatch a taxi for Customer " + ID + " at " + (realTime + 7500));
						System.out.println("Failed to dispatch a taxi for Customer " + ID + " at " + (realTime + 7500));
						return true;
					} else {
						int credit = -1;
						ArrayList<Taxi> taxiCredit = new ArrayList<Taxi>();
						for (Taxi taxi : taxiList) {
							printWriter.println(taxi + " \t tried to get the customer");
							if (!taxi.dipatchAble()) {
								continue;
							}
							if (taxi.getCredit() > credit) {
								credit = taxi.getCredit();
								taxiCredit.clear();
								taxiCredit.add(taxi);
							} else if (taxi.getCredit() == credit) {
								taxiCredit.add(taxi);
							}
						}
						printWriter.flush();
						int distance = 1000000;
						ArrayList<Taxi> taxiDistance = new ArrayList<Taxi>();
						for (Taxi taxi : taxiCredit) {
							if (taxi.getDistance(this) < distance) {
								distance = taxi.getDistance(this);
								taxiDistance.clear();
								taxiDistance.add(taxi);
							} else if (taxi.getDistance(this) == distance) {
								taxiDistance.add(taxi);
							}
						}
						if (distance == 1000000) {
							printWriter.println(
									"Failed to dispatch a taxi for Customer " + ID + " at " + (realTime + 7500));
							System.out.println(
									"Failed to dispatch a taxi for Customer " + ID + " at " + (realTime + 7500));
							return true;
						}

						Taxi taxi = taxiDistance.get(0);
						taxi.recieveRequest(this);
						for (Taxi taxi2 : taxiList) {
							if (taxi2.equals(taxi)) {
								taxi.removeRequest(this);
							}
						}
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: true == true;
	 */
	public void run() {
		while (true) {
			try {
				Thread.sleep(TimeCompute.TaxiSleepTime(realTime + tickTock * 500, 500));
				checkTaxiMap();
				if (dispatch()) {
					break;
				}
			} catch (Exception e) {
				System.out.println(ID + "Requesting Wrong");
			}
		}
	}

	/**
	 * @REQUIRES: this;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == "Time: " + realTime + " src: (" + startPoint.x + ", " +
	 *           startPoint.y + ") dst(" + targetPoint.x + ", " + targetPoint.y +
	 *           ")";
	 */
	@Override
	public String toString() {
		return "Time: " + realTime + "  src: (" + startPoint.x + ", " + startPoint.y + ")  dst(" + targetPoint.x + ", "
				+ targetPoint.y + ")";
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return this.targetPoint != null && this.startPoint != null && ID >= 0;
	}
}
