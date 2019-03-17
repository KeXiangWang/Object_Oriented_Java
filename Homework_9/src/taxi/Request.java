package taxi;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Request {
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

	public Request(Point startPoint, Point targetPoint, long time) {
		/**
		 * @REQUIRES: startPoint!=null; targetPoint!=null;
		 * @MODIFIES: this.ID ; this.targetPoint; this.startPoint ; this.realTime;
		 *            this.externalTime ; this.tickTock ;
		 * @EFFECTS: requestnumber == \old(requestnumber)+1; this.ID == requestnumber;
		 *           this.targetPoint == targetPoint; this.startPoint == startPoint;
		 *           this.realTime == time; this.externalTime == (time - TaxiSys.MAINTIME)
		 *           / 100 * 100; this.tickTock == 0;
		 */
		this.ID = requestnumber++;
		this.targetPoint = targetPoint;
		this.startPoint = startPoint;
		this.realTime = time;
		this.externalTime = (time - TaxiSys.MAINTIME) / 100 * 100;// ROUNDOWN the time
		this.tickTock = 0;
	}

	public PrintWriter getPrintWritter() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: \result == this.printWritter;
		 */
		return this.printWriter;
	}

	public void initRequest(TaxiMap taxiMap) {
		/**
		 * @REQUIRES: taxiMap;
		 * @MODIFIES: 
		 * 		this.taxiList;
		 *		this.taxiMap;
		 *		this.printWriter;
		 * @EFFECTS: 
		 * 	normal_behavior:
		 *		print the wrong information.
		 * 		this.taxiList == new ArrayList<>();
		 *		this.taxiMap == taxiMap;
		 *		this.printWriter == new PrintWriter(new BufferedWriter(new FileWriter("request" + ID + ".txt")), true);
		 *	exception_behavior(Exception e):
		 *		print the wrong information.
		 *		
		 */
		try {
			this.taxiList = new ArrayList<>();
			this.taxiMap = taxiMap;
			this.printWriter = new PrintWriter(new BufferedWriter(new FileWriter("request" + ID + ".txt")), true);
			this.printWriter.println(this);
		} catch (Exception e) {
			System.out.println("IOException");
		}
	}

	@Override
	public boolean equals(Object o) {
		/**
		 * @REQUIRES: o;
		 * @MODIFIES: None;
		 * @EFFECTS: (this.startPoint.x == rq.startPoint.x && this.startPoint.y == rq.startPoint.y
		 *		&& this.targetPoint.x == rq.targetPoint.x && this.targetPoint.y == rq.targetPoint.y
		 *		&& this.externalTime == rq.externalTime)==>\result==true;
		 */
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
	
	public int getID() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: \result == this.ID;
		 */
		return ID;
	}
	public Point getStartPoin() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: \result == this.startPoint;
		 */
		return startPoint;
	}

	public long getRealTime() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: \result == this.realTime;
		 */
		return realTime;
	}

	public Point getTargetPoin() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: \result == this.targetPoint;
		 */
		return targetPoint;
	}

	public void removeTaxi(Taxi taxi) {
		/**
		 * @REQUIRES: taxi;
		 * @MODIFIES: None;
		 * @EFFECTS: 
		 * 	normal_behavior:
		 *		!(taxiList.contains(taxi);
		 *	exception_behavior(Exception e):
		 *		;
		 */
		try {
			if (taxiList.contains(taxi)) {
				taxiList.remove(taxi);
			}
		} catch (Exception e) {
		}

	}

	public void checkTaxiMap() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: this.taxiList;
		 * @EFFECTS: 
		 * 	normal_behavior:
		 *		(\all Taxi taxi in \old(taxiMap.getAmbientTaxi(this)); this.taxiList.contains(taxi);
		 *	exception_behavior(Exception e):
		 *		;
		 */
		try {
			tickTock++;
			ArrayList<Taxi> tempList = taxiMap.getAmbientTaxi(this);
			for (Taxi taxi : tempList) {
				if (!taxiList.contains(taxi)) {
					taxiList.add(taxi);
				}
			}
		} catch (Exception e) {
		}
	}

	public Boolean dispatch() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: this.taxiList;
		 * @EFFECTS: 
		 * 	normal_behavior:
		 *		Pick a nearest accessible taxi, which has the most credit, from the robbed-taxiList and dispatch it to this request when tickTock == 15.
		 *		Delete this request from other taxi' robbed-requestList.
		 *		Print the relevant information. 
		 *	exception_behavior(Exception e):
		 *		;
		 */
		try {
		// printWriter.println(tickTock+""+System.currentTimeMillis());
			if (tickTock == 15) {
				if (taxiList.size() == 0) {
					// System.out.println(System.currentTimeMillis());
					printWriter.println("Failed to dispatch a taxi for Customer " + ID + " at " + (realTime + 7500));
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
						printWriter.println("Failed to dispatch a taxi for Customer " + ID + " at " + (realTime + 7500));
						System.out.println("Failed to dispatch a taxi for Customer " + ID + " at " + (realTime + 7500));
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
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public String toString() {
		/**
		 * @REQUIRES: this;
		 * @MODIFIES: None;
		 * @EFFECTS: 
		 * 		\result == "Time: " + realTime + "  src: (" + startPoint.x + ", " + startPoint.y + ")  dst(" + targetPoint.x + ", "
		 *		+ targetPoint.y + ")";
		 */
		return "Time: " + realTime + "  src: (" + startPoint.x + ", " + startPoint.y + ")  dst(" + targetPoint.x + ", "
				+ targetPoint.y + ")";
	}

}
