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
	// private Boolean odd;

	public Request(Point startPoint, Point targetPoint, long time) {
		this.ID = requestnumber++;
		this.targetPoint = targetPoint;
		this.startPoint = startPoint;
		this.realTime = time;
		this.externalTime = (time - TaxiSys.MAINTIME) / 100 * 100;// ROUNDOWN the time
		this.tickTock = 0;
	}

	public PrintWriter getPrintWritter() {
		return this.printWriter;
	}

	public void initRequest(TaxiMap taxiMap) {
		try {
			this.taxiList = new ArrayList<>();
			this.taxiMap = taxiMap;
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter("request" + ID + ".txt")), true);
			printWriter.println(this);
		} catch (Exception e) {
			System.out.println("IOException");
		}
	}

	public int getID() {
		return ID;
	}
	@Override
	public boolean equals(Object o) {
        if (o == this) return true;  
        if (!(o instanceof Request)) {  
            return false;  
        }  
        Request rq = (Request) o; 
		return (this.startPoint.x == rq.startPoint.x && this.startPoint.y == rq.startPoint.y
				&& this.targetPoint.x == rq.targetPoint.x && this.targetPoint.y == rq.targetPoint.y
				&& this.externalTime == rq.externalTime);
	}

	public Point getStartPoin() {
		return startPoint;
	}

	public long getRealTime() {
		return realTime;
	}

	public Point getTargetPoin() {
		return targetPoint;
	}

	public void removeTaxi(Taxi taxi) {
		if (taxiList.contains(taxi)) {
			taxiList.remove(taxi);
		}
	}

	public void checkTaxiMap() {
		tickTock++;
		ArrayList<Taxi> tempList = taxiMap.getAmbientTaxi(this);
		for (Taxi taxi : tempList) {
			if (!taxiList.contains(taxi)) {
				taxiList.add(taxi);
			}
		}
	}
	

	public Boolean dispatch() {
		printWriter.println(tickTock+""+System.currentTimeMillis());
		if (tickTock == 15) {
			if (taxiList.size() == 0) {
				printWriter.println("Failed to dispatch a taxi for Customer " + ID + " at " + (realTime + 3000));
				System.out.println("Failed to dispatch a taxi for Customer " + ID + " at " + (realTime + 3000));
				return true;
			} else {
				int credit = -1;
				ArrayList<Taxi> taxiCredit = new ArrayList<Taxi>();
				for (Taxi taxi : taxiList) {
					printWriter.println(taxi + " \ttried to get the customer");
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
					printWriter
							.println("Failed to dispatch a taxi for Customer " + ID + " at " + (realTime + 3000));
					System.out.println("Failed to dispatch a taxi for Customer " + ID + " at " + (realTime + 3000));
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
	}

	public String toString() {
		return "Time: " + realTime + "  src: (" + startPoint.x + ", " + startPoint.y + ")  dst(" + targetPoint.x + ", "
				+ targetPoint.y + ")";
	}

}
