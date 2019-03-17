package taxi;

import java.awt.Point;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

class State {
	// IDLE: the taxi stop to do something;
	// WANDERING: wander, waiting for a request; //serving
	// TOTARGET: has got a customer and driving to the destination;
	// TOCUSTOMER: drive to pick the customer; //serving
	static final int IDLE = 0;
	static final int TOTARGET = 1;
	static final int WANDERING = 2;
	static final int TOCUSTOMER = 4;
}

public class Taxi {
	private TaxiGUI taxiGUI;
	private TaxiMap taxiMap;
	private Point location;
	private int credit;
	private static int number;
	private int ID;
	private Random random;
	private int state; // the current mode
	private int tickTock;
	private PrintWriter printWriter;
	private ArrayList<Request> requestList;
	private Request executingRequest;
	private Point customerPoint;
	private Point targetPoint;
	private Boolean arrivedTarget;
	private Boolean gotCustomer;
	private long taxiTime;
	private int executeState;// record execute state

	public Taxi(TaxiGUI taxiGUI, TaxiMap taxiMap) {
		random = new Random();
		this.ID = number++;
		this.taxiGUI = taxiGUI;
		this.taxiMap = taxiMap;
		this.credit = 0;
		this.state = State.WANDERING;
		// this.location = taxiMap.getPoint(0, 0);
		this.location = taxiMap.getPoint(random.nextInt(TaxiSys.SIZE), random.nextInt(TaxiSys.SIZE));
		// System.out.println(location + " ID: " + ID);
		this.tickTock = 0;
		this.requestList = new ArrayList<>();
		this.arrivedTarget = false;
		this.gotCustomer = false;
		this.executingRequest = null;
		this.customerPoint = null;
		this.targetPoint = null;
		this.taxiMap.addTaxi(location, this);
		this.taxiTime = TaxiSys.MAINTIME;
		this.executeState = 0;
	}

	public int getState() {
		return state;
	}

	public int getCredit() {
		return credit;
	}

	public int getDistance(Request request) {
		return taxiMap.getDistance(location, request.getStartPoin());
	}

	public Boolean dipatchAble() {
		return !(state == State.IDLE || state == State.TOTARGET || state == State.TOCUSTOMER)
				&& executingRequest == null;
	}

	public void recieveRequest(Request request) {
		printWriter = request.getPrintWritter();
		executingRequest = request;
		requestList.clear();
		customerPoint = request.getStartPoin();
		targetPoint = request.getTargetPoin();
		printWriter.println("At the time of " + (executingRequest.getRealTime() + 3000) + "  " + executingRequest
				+ " has been dispatched to " + this);
		System.out.println("At the time of " + (executingRequest.getRealTime() + 3000) + "  " + executingRequest
				+ " has been dispatched to " + this);
		// printWriter.println(this);
		for (Request request2 : requestList) {
			if (!request2.equals(request)) {
				request2.removeTaxi(this);
			}
		}
		executeState = 1;
	}

	private void setRequest() {

		state = State.TOCUSTOMER;
		tickTock = 0;
	}

	public int robRequest(Request request) {
		if (requestList.contains(request)) {
			return 0;
		} else {
			credit++;
			requestList.add(request);
			return 1;
		}
	}

	public void removeRequest(Request request) {
		if (requestList.contains(request)) {
			requestList.remove(request);
		}
	}

	private void driveToPoint(Point point) {
		taxiMap.deleteTaxi(location, this);
		if (location.x + 1 < 80 && taxiMap.judgeAdjacent(location.x, location.y, location.x + 1, location.y) == 1
				&& taxiMap.getFastestDistance(location.x, location.y, point.x,
						point.y) == taxiMap.getFastestDistance(location.x + 1, location.y, point.x, point.y) + 1) {
			location = taxiMap.getPoint(location.x + 1, location.y);
		} else if (location.x - 1 >= 0 && taxiMap.judgeAdjacent(location.x, location.y, location.x - 1, location.y) == 1
				&& taxiMap.getFastestDistance(location.x, location.y, point.x,
						point.y) == taxiMap.getFastestDistance(location.x - 1, location.y, point.x, point.y) + 1) {
			location = taxiMap.getPoint(location.x - 1, location.y);
		} else if (location.y + 1 < 80 && taxiMap.judgeAdjacent(location.x, location.y, location.x, location.y + 1) == 1
				&& taxiMap.getFastestDistance(location.x, location.y, point.x,
						point.y) == taxiMap.getFastestDistance(location.x, location.y + 1, point.x, point.y) + 1) {
			location = taxiMap.getPoint(location.x, location.y + 1);
		} else if (location.y - 1 >= 0 && taxiMap.judgeAdjacent(location.x, location.y, location.x, location.y - 1) == 1
				&& taxiMap.getFastestDistance(location.x, location.y, point.x,
						point.y) == taxiMap.getFastestDistance(location.x, location.y - 1, point.x, point.y) + 1) {
			location = taxiMap.getPoint(location.x, location.y - 1);
		}
		taxiMap.addTaxi(location, this);
		if (location.equals(point) && !gotCustomer) {
			gotCustomer = true;
			printWriter.println(taxiTime + ": " + this + " arrived at where the customer " + executingRequest.getID()+" is");
		} else if (location.equals(point) && gotCustomer) {
			arrivedTarget = true;
		}
	}

	private void wander() {
		taxiMap.deleteTaxi(location, this);
		Point[] pointList = new Point[4];
		int i = 0;
		if (taxiMap.judgeAdjacent(location.x, location.y, location.x + 1, location.y) == 1) {
			pointList[i++] = taxiMap.getPoint(location.x + 1, location.y);
		}
		if (taxiMap.judgeAdjacent(location.x, location.y, location.x, location.y + 1) == 1) {
			pointList[i++] = taxiMap.getPoint(location.x, location.y + 1);
		}
		if (taxiMap.judgeAdjacent(location.x, location.y, location.x - 1, location.y) == 1) {
			pointList[i++] = taxiMap.getPoint(location.x - 1, location.y);
		}
		if (taxiMap.judgeAdjacent(location.x, location.y, location.x, location.y - 1) == 1) {
			pointList[i++] = taxiMap.getPoint(location.x, location.y - 1);
		}
		if (i != 0) {
			location = pointList[random.nextInt(i)];
		}
		taxiMap.addTaxi(location, this);
	}

	private void run() {
		switch (state) {
		case State.IDLE:
			break;
		case State.TOCUSTOMER:
			driveToPoint(customerPoint);
			break;
		case State.TOTARGET:
			driveToPoint(targetPoint);
			break;
		case State.WANDERING:
			wander();
			break;
		}
	}

	private void switchState() {
		switch (state) {
		case State.IDLE:
			if (tickTock == 5 && !gotCustomer) { // go on wandering
				state = State.WANDERING;
				tickTock = 0;
			} else if (tickTock == 5 && gotCustomer) { // driver to target
				System.out.println(taxiTime + ": " + this + " finally get the customer " + executingRequest.getID());
				printWriter.println(taxiTime + ": " + this + " finally get the customer " + executingRequest.getID());
				state = State.TOTARGET;
				tickTock = 0;
			}
			break;
		case State.TOCUSTOMER:
			if (gotCustomer) { // picked the customer
				state = State.IDLE;
				tickTock = 0;
			}
			break;
		case State.TOTARGET:
			if (arrivedTarget) { // arrived the target
				state = State.IDLE;
				credit += 3;
				printFinishedTask();
				gotCustomer = false;
				arrivedTarget = false;
				executingRequest = null;
				customerPoint = null;
				targetPoint = null;
				printWriter = null;
				tickTock = 0;
			}
			break;
		case State.WANDERING:
			if (tickTock == 100) { // stop for one second
				state = State.IDLE;
				tickTock = 0;
			}
			break;
		}
	}

	public void move() {
		try {
			taxiTime += 200;
			tickTock++;
			run();
			switchState();
			if (executeState == 1) {
				setRequest();
				executeState = 0;
			}
			taxiGUI.SetTaxiStatus(ID, location, state % 3);
			if (executingRequest != null && state != State.IDLE ) {
				printWriter.println(taxiTime + ": " + locationString());
			}
		} catch (Exception e) {
			System.out.println("Runiing Wrong");
		}
	}

	private void printFinishedTask() {
		String printState = state == 0 ? "Stopping"
				: state == 4 ? "ToCustomer" : state == 1 ? "Serving" : state == 2 ? "Waiting" : "Wrong";
		printWriter.println(
				"Arrived!!!  Taxi " + ID + "  Locate at: (" + location.x + ", " + location.y + ") " + "  State: "
						+ printState + "  At " + taxiTime + " The taxi picked customer " + executingRequest.getID());
		System.out.println(
				"Arrived!!!  Taxi " + ID + "  Locate at: (" + location.x + ", " + location.y + ") " + "  State: "
						+ printState + "  At " + taxiTime + " The taxi picked customer " + executingRequest.getID());
	}

	public String toString() {
		String printState = state == 0 ? "Stopping"
				: state == 4 ? "ToCustomer" : state == 1 ? "Serving" : state == 2 ? "Waiting" : "Wrong";
		return "Taxi ID: " + ID + "  Location: (" + location.x + ", " + location.y + ") " + "  State: " + printState
				+ " Credit: " + credit;
	}

	private String locationString() {
		String printState = state == 0 ? "Stopping"
				: state == 4 ? "ToCustomer" : state == 1 ? "Serving" : state == 2 ? "Waiting" : "Wrong";
		if (gotCustomer) {
			return "Taxi " + ID + "  Locate at: (" + location.x + ", " + location.y + ") " + "  State: " + printState
					+ "  got the customer";

		} else {
			return "Taxi " + ID + "  Locate at: (" + location.x + ", " + location.y + ") " + "  State: " + printState
					+ "  on the way to get customer";
		}
	}
}
