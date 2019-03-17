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
	private ArrayList<Point> path;
	private Flow flow;

	public Taxi(TaxiMap taxiMap) {
		/**
		 * @REQUIRES: taxiMap!=null;
		 * @MODIFIES: this.taxiMap; this.ID; this.credit; this.state; this.location;
		 *            this.requestList; this.arrivedTarget; this.gotCustomer;
		 *            this.executingRequest; this.customerPoint; this.targetPoint;
		 *            this.executeState this.path; this.flow;
		 * @EFFECTS: this.taxiMap == taxiMap; this.ID == number++; this.credit == 0;
		 *           this.state == State.WANDERING; this.location == null;
		 *           this.requestList == !=null; this.arrivedTarget == false;
		 *           this.gotCustomer == false; this.executingRequest == null;
		 *           this.customerPoint == null; this.targetPoint == null;
		 *           this.executeState == 0; this.path == null; this.flow == null;
		 */
		this.taxiMap = taxiMap;
		this.ID = number++;
		this.credit = 0;
		this.state = State.WANDERING;
		this.location = null;
		this.requestList = new ArrayList<>();
		this.arrivedTarget = false;
		this.gotCustomer = false;
		this.executingRequest = null;
		this.customerPoint = null;
		this.targetPoint = null;
		this.executeState = 0;
		this.path = null;
		this.flow = null;
	}

	public void setTaxiInfo(int taxiState, int taxiCredit, int taxiX, int taxiY) {
		/**
		 * @MODIFIES: this.taxiMap; this.state; this.location; this.arrivedTarget;
		 *            this.gotCustomer; this.executingRequest; this.customerPoint;
		 *            this.targetPoint; this.path; this.flow; this.taxiMap
		 * @EFFECTS: According taxiState, give the taxi a request or just make it wander
		 *           or stop. At the same time, init some
		 */
		taxiMap.deleteTaxi(location, this);
		this.location = taxiMap.getPoint(taxiX, taxiY);
		switch (taxiState) {
		case 0:
			this.executingRequest = TaxiSys.GhostRequest;
			this.printWriter = executingRequest.getPrintWritter();
			this.customerPoint = executingRequest.getStartPoin();
			this.targetPoint = executingRequest.getTargetPoin();
			this.flow = new Flow(taxiMap);
			this.flow.BFS(targetPoint);
			this.path = flow.getPath(location);
			this.gotCustomer = true;
			this.state = State.TOTARGET;
			if (path.size() == 0) {
				arrivedTarget = true;
				switchState();
			}
			break;
		case 1:
			this.executingRequest = TaxiSys.GhostRequest;
			this.printWriter = executingRequest.getPrintWritter();
			this.customerPoint = executingRequest.getStartPoin();
			this.targetPoint = executingRequest.getTargetPoin();
			this.flow = new Flow(taxiMap);
			this.flow.BFS(customerPoint);
			this.path = flow.getPath(location);
			this.state = State.TOCUSTOMER;
			if (path.size() == 0) {
				gotCustomer = true;
				printWriter.println(
						taxiTime + ": " + this + " arrived at where the customer " + executingRequest.getID() + " is");
				switchState();
			}
			break;
		case 2:
			this.state = State.WANDERING;
			break;
		case 3:
			this.state = State.IDLE;
			break;
		}
		this.tickTock = 0;
		this.credit = taxiCredit;
		this.taxiGUI.SetTaxiStatus(ID, location, state % 3);
		this.taxiMap.addTaxi(location, this);
	}

	public void initTaxi(TaxiGUI taxiGUI) {
		/**
		 * @REQUIRES: taxiGUI!=null;
		 * @MODIFIES: this.location; this.taxiTime; this.tickTock; this.taxiMap;
		 *            this.taxiGUI ;
		 * @EFFECTS: this.taxiTime == TaxiSys.MAINTIME; this.taxiGUI == taxiGUI;
		 *           this.location == taxiMap.getPoint(random.nextInt(TaxiSys.SIZE),
		 *           random.nextInt(TaxiSys.SIZE)); this.tickTock == 0;
		 *           this.taxiMap.addTaxi(location, this);
		 */
		random = new Random();
		this.taxiTime = TaxiSys.MAINTIME;
		this.taxiGUI = taxiGUI;
		this.location = taxiMap.getPoint(random.nextInt(TaxiSys.SIZE), random.nextInt(TaxiSys.SIZE));
		this.tickTock = 0;
		this.taxiMap.addTaxi(location, this);
	}

	public int getState() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: this.result == state;
		 */
		return state;
	}

	public int getCredit() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: this.result == credit;
		 */
		return credit;
	}

	public int getDistance(Request request) {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: this.result == Flow.getFastestDistance(request.getStartPoin(),
		 *           location);
		 */
		return Flow.getFastestDistance(request.getStartPoin(), location);
	}

	public Boolean dipatchAble() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: (!(state == State.IDLE || state == State.TOTARGET || state ==
		 *           State.TOCUSTOMER) && executingRequest == null) ==> \result == true;
		 */
		return !(state == State.IDLE || state == State.TOTARGET || state == State.TOCUSTOMER)
				&& executingRequest == null;
	}

	public void recieveRequest(Request request) {
		/**
		 * @REQUIRES: request!=null;
		 * @MODIFIES: this.printWriter; this.executingRequest; this.requestList;
		 *            this.customerPoint; this.targetPoint; this.executeState;
		 * @EFFECTS: give the taxi its dispatched request preset its status. Print the
		 *           information. printWriter == request.getPrintWritter();
		 *           executingRequest == request; requestList.size()==0; customerPoint
		 *           == request.getStartPoin(); targetPoint == request.getTargetPoin();
		 *           (\all Request request2 in requestList;
		 *           !request2.taxiList.contains(this)); executeState == 1;
		 */
		printWriter = request.getPrintWritter();
		executingRequest = request;
		requestList.clear();
		customerPoint = request.getStartPoin();
		targetPoint = request.getTargetPoin();
		printWriter.println("At the time of " + (executingRequest.getRealTime() + 7500) + "  " + executingRequest
				+ " has been dispatched to " + this);
		System.out.println("At the time of " + (executingRequest.getRealTime() + 7500) + "  " + executingRequest
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
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: this.flow; this.path; this.state; this.tickTock; this.gotCustomer;
		 * @EFFECTS: Set its status. Get its path towards its target. Print the relevant
		 *           information.
		 */
		this.flow = new Flow(taxiMap);
		flow.BFS(customerPoint);
		path = flow.getPath(location);
		state = State.TOCUSTOMER;
		tickTock = 0;
	}

	public int robRequest(Request request) {
		/**
		 * @REQUIRES: request!=null;
		 * @MODIFIES: this.requestList; this.credit;
		 * @EFFECTS: credit == \old(credit) + 1; requestList.contains(request);
		 *           \old(requestList).contains(request) ==> \result == 1 ;
		 *           !\old(requestList).contains(request) ==> \result == 0 ;
		 */
		if (requestList.contains(request)) {
			return 0;
		} else {
			credit++;
			requestList.add(request);
			return 1;
		}
	}

	public void removeRequest(Request request) {
		/**
		 * @REQUIRES: request!=null;
		 * @MODIFIES: this.requestList;
		 * @EFFECTS: !requestList.contains(request);
		 */
		if (requestList.contains(request)) {
			requestList.remove(request);
		}
	}

	private void driveToPoint(Point point) {
		/**
		 * @REQUIRES: point!=null;
		 * @MODIFIES: this.taxiMap; this.path; this.location; this.flow;
		 * @EFFECTS: normal_behavior: Move the taxi to words point, change the path if
		 *           the road disappears. Change the taxiList of point on the taxiMap.
		 *           exception_behavior(Exception e): print the wrong information.
		 */
		try {
			taxiMap.deleteTaxi(location, this);
			Point next = path.get(0);
			if (guigv.m.graph[location.x * 80 + location.y][next.x * 80 + next.y] != 1) {
				this.flow = new Flow(taxiMap);
				this.flow.BFS(point);
				this.path = flow.getPath(location);
				next = path.get(0);
			}
			path.remove(next);
			location = next;
			taxiMap.addTaxi(location, this);
			if (location.equals(point) && !gotCustomer) {
				gotCustomer = true;
				printWriter.println(
						taxiTime + ": " + this + " arrived at where the customer " + executingRequest.getID() + " is");
			} else if (location.equals(point) && gotCustomer) {
				arrivedTarget = true;
			}
		} catch (Exception e) {
			System.out.println("drive to point wrong");
		}
	}

	private void wander() {
		/**
		 * @REQUIRES: point!=null;
		 * @MODIFIES: this.taxiMap; this.location;
		 * @EFFECTS: Move the taxi without a target. Change the taxiList of point on the
		 *           taxiMap.
		 */
		taxiMap.deleteTaxi(location, this);
		Point[] pointList = new Point[4];
		int i = 0, j = 0;
		int flowTemp = 200;
		int[][] directions = getDirections(location.x, location.y);
		int[] directionFlow = new int[4];
		for (i = 0; i < 4; i++) {
			if (directions[i][0] * 80 + directions[i][1] < 0 || directions[i][0] * 80 + directions[i][1] >= 6400) {
				continue;
			}
			if (guigv.m.graph[location.x * 80 + location.y][directions[i][0] * 80 + directions[i][1]] == 1) {
				directionFlow[i] = guigv.GetFlow(location.x, location.y, directions[i][0], directions[i][1]);
				if (directionFlow[i] < flowTemp) {
					flowTemp = directionFlow[i];
				}
			}
		}
		for (i = 0; i < 4; i++) {
			if (directions[i][0] * 80 + directions[i][1] < 0 || directions[i][0] * 80 + directions[i][1] >= 6400) {
				continue;
			}
			if (guigv.m.graph[location.x * 80 + location.y][directions[i][0] * 80 + directions[i][1]] == 1) {
				if (directionFlow[i] == flowTemp) {
					pointList[j++] = taxiMap.getPoint(directions[i][0], directions[i][1]);
				}
			}
		}
		if (j != 0) {
			location = pointList[random.nextInt(j)];
		}
		taxiMap.addTaxi(location, this);
	}

	public int[][] getDirections(int x, int y) {
		/**
		 * @MODIFIES: None;
		 * @EFFECTS: return the four directions arounding the point(x,y).
		 */
		int[][] directions = new int[4][2];
		directions[0][0] = x + 1;
		directions[0][1] = y;
		directions[1][0] = x - 1;
		directions[1][1] = y;
		directions[2][0] = x;
		directions[2][1] = y + 1;
		directions[3][0] = x;
		directions[3][1] = y - 1;
		return directions;
	}

	private void run() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: state;
		 * @EFFECTS: move the taxi according to the state.
		 */
		switch (state) {
		case State.IDLE:
			break;
		case State.TOCUSTOMER:
			if (path.size() == 0) {
				gotCustomer = true;
				System.out.println("wired customerpoint " + executingRequest.getID());
				printWriter.println((taxiTime - 500) + ": " + this + " arrived at where the customer "
						+ executingRequest.getID() + " is");
				switchState();
				tickTock++;
			} else {
				driveToPoint(customerPoint);
			}
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
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: this.state; this.flow; this.path; this.printWriter; this.state;
		 *            this.credit; this.tickTock; this.gotCustomer; this.arrivedTarget;
		 *            this.executingRequest; this.customerPoint;
		 * @EFFECTS: Switch the state of the taxi according to the \old(state) and
		 *           tickTock. Set the taxi its target and other status, if it is going
		 *           to serve.
		 */
		switch (state) {
		case State.IDLE:
			if (tickTock == 2 && !gotCustomer) { // go on wandering
				state = State.WANDERING;
				tickTock = 0;
			} else if (tickTock == 2 && gotCustomer) { // driver to target
				System.out.println(taxiTime + ": " + this + " finally get the customer " + executingRequest.getID());
				printWriter.println(taxiTime + ": " + this + " finally get the customer " + executingRequest.getID());
				state = State.TOTARGET;
				flow = new Flow(taxiMap);
				flow.BFS(targetPoint);
				path = flow.getPath(location);
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
				path = null;
				flow = null;
				tickTock = 0;
			}
			break;
		case State.WANDERING:
			if (tickTock == 40) { // stop for one second
				state = State.IDLE;
				tickTock = 0;
			}
			break;
		}
	}

	public void move() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: this.tickTock; this.taxiTime; this.executeState;
		 * @EFFECTS: normal_behavior: Move the taxi. Switch the status. if there is a
		 *           request not handled, set it to the taxi.
		 *           exception_behavior(Exception e): print the wrong information.
		 */
		try {
			taxiTime += 500;
			tickTock++;
			run();
			switchState();
			if (executeState == 1) {
				setRequest();
				executeState = 0;
			}
			taxiGUI.SetTaxiStatus(ID, location, state % 3);
			if (executingRequest != null && state != State.IDLE) {
				printWriter.println(taxiTime + ": " + locationString());
			}
		} catch (Exception e) {
			System.out.println(location);
			System.out.println(ID + "Runiing Wrong");
		}
	}

	private void printFinishedTask() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: return the print form of finished request.
		 */
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
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: return the print form of this taxi.
		 */
		String printState = state == 0 ? "Stopping"
				: state == 4 ? "ToCustomer"
						: state == 1 ? "Serving"
								: state == 2 && executingRequest == null ? "Waiting"
										: state == 2 && executingRequest != null ? "ToCustomer" : "Wrong";
		return "Taxi ID: " + ID + "  Location: (" + location.x + ", " + location.y + ") " + "  State: " + printState
				+ " Credit: " + credit;
	}

	public String ckeckPrint() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: return the print form for check this taxi.
		 */
		String printState = state == 0 ? "Stopping"
				: state == 4 ? "ToCustomer"
						: state == 1 ? "Serving"
								: state == 2 && executingRequest == null ? "Waiting"
										: state == 2 && executingRequest != null ? "ToCustomer" : "Wrong";
		return System.currentTimeMillis() + ":  " + taxiTime + ":  " + "Taxi ID: " + ID + "  Location: (" + location.x
				+ ", " + location.y + ") " + "  State: " + printState + " Credit: " + credit;
	}

	private String locationString() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: return the print form of the location of this taxi.
		 */
		String printState = state == 0 ? "Stopping"
				: state == 4 ? "ToCustomer"
						: state == 1 ? "Serving"
								: state == 2 && executingRequest == null ? "Waiting"
										: state == 2 && executingRequest != null ? "ToCustomer" : "Wrong";
		if (gotCustomer) {
			return "Taxi " + ID + "  Locate at: (" + location.x + ", " + location.y + ") " + "  State: " + printState
					+ "  got the customer";

		} else {
			return "Taxi " + ID + "  Locate at: (" + location.x + ", " + location.y + ") " + "  State: " + printState
					+ "  on the way to get customer";
		}
	}
}
