package taxi;

import java.awt.Point;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

class State {
	/**
	 * @Overview: State defines states.
	 */
	// IDLE: the taxi stop to do something;
	// WANDERING: wander, waiting for a request; //serving
	// TOTARGET: has got a customer and driving to the destination;
	// TOCUSTOMER: drive to pick the customer; //serving
	static final int IDLE = 0;
	static final int TOTARGET = 1;
	static final int WANDERING = 2;
	static final int TOCUSTOMER = 4;

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return true;
	}
}

public class Taxi implements Runnable {
	/**
	 * @Overview: Taxi contains its credit, ID, location, and when got a request, it
	 *            record the request's information. It provide method to move and
	 *            rob the requests.
	 */
	protected TaxiGUI taxiGUI;
	protected TaxiMap taxiMap;
	protected Point location;
	protected Point lastLocation;
	protected int credit;
	protected static int number;
	protected int ID;
	protected Random random;
	protected int state; // the current mode
	protected int tickTock;
	protected PrintWriter printWriter;
	protected ArrayList<Request> requestList;
	protected Request executingRequest;
	protected Point customerPoint;
	protected Point targetPoint;
	protected Boolean arrivedTarget;
	protected Boolean gotCustomer;
	protected long taxiTime;
	protected int executeState;// record execute state
	protected ArrayList<Point> path;
	protected LightSystem lightSystem;
	protected Flow flow;
	protected ArrayList<RequestMessage> messages;
	protected RequestMessage requestMessage;

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
	public Taxi(TaxiMap taxiMap) {
		this.taxiMap = taxiMap;
		this.ID = number++;
		this.credit = 0;
		this.state = State.WANDERING;
		this.messages = new ArrayList<RequestMessage>();
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

	/**
	 * @MODIFIES: this.taxiMap; this.state; this.location; this.arrivedTarget;
	 *            this.gotCustomer; this.executingRequest; this.customerPoint;
	 *            this.targetPoint; this.path; this.flow; this.taxiMap
	 * @EFFECTS: According taxiState, give the taxi a request or just make it wander
	 *           or stop. At the same time, init some
	 */
	public void setTaxiInfo(int taxiState, int taxiCredit, int taxiX, int taxiY) {
		taxiMap.deleteTaxi(location, this);
		this.location = taxiMap.getPoint(taxiX, taxiY);
		this.lastLocation = location;
		switch (taxiState) {
		case 0:
			this.executingRequest = TaxiSys.GhostRequest;
			this.printWriter = executingRequest.getPrintWritter();
			this.customerPoint = executingRequest.getStartPoin();
			this.targetPoint = executingRequest.getTargetPoin();
			this.flow = new Flow(taxiMap);
			this.flow.BFS(targetPoint, taxiTime);
			this.path = flow.getPath(location, taxiTime);
			this.gotCustomer = true;
			this.state = State.TOTARGET;
			this.requestMessage = new RequestMessage();
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
			this.flow.BFS(customerPoint, taxiTime);
			this.path = flow.getPath(location, taxiTime);
			this.state = State.TOCUSTOMER;
			this.requestMessage = new RequestMessage();
			if (path.size() == 0) {
				gotCustomer = true;
				String string = taxiTime + ": " + this + " arrived at where the customer " + executingRequest.getID()
						+ " is";
				printWriter.println(string);
				requestMessage.addMessage(string);
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
		// this.taxiGUI.SetTaxiStatus(ID, location, state % 3);
		this.taxiMap.addTaxi(location, this);
	}

	/**
	 * @REQUIRES: taxiGUI!=null;
	 * @MODIFIES: this.location; this.taxiTime; this.tickTock; this.taxiMap;
	 *            this.taxiGUI ;
	 * @EFFECTS: this.taxiTime == TaxiSys.MAINTIME; this.taxiGUI == taxiGUI;
	 *           this.location == taxiMap.getPoint(random.nextInt(TaxiSys.SIZE),
	 *           random.nextInt(TaxiSys.SIZE)); this.tickTock == 0;
	 *           this.taxiMap.addTaxi(location, this);
	 */
	public void initTaxi(TaxiGUI taxiGUI, LightSystem lightSystem) {
		random = new Random();
		this.lightSystem = lightSystem;
		this.taxiTime = TaxiSys.MAINTIME;
		this.taxiGUI = taxiGUI;
		this.location = taxiMap.getPoint(random.nextInt(TaxiSys.SIZE), random.nextInt(TaxiSys.SIZE));
		this.lastLocation = location;
		this.tickTock = 0;
		this.taxiMap.addTaxi(location, this);
		taxiGUI.SetTaxiType(this.ID, 0);
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == state;
	 */
	public int getState() {
		return state;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == credit;
	 */
	public int getCredit() {
		return credit;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == Flow.getFastestDistance(request.getStartPoin(),
	 *           location);
	 */
	public int getDistance(Request request) {
		return Flow.getFastestDistance(request.getStartPoin(), location);
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: (!(state == State.IDLE || state == State.TOTARGET || state ==
	 *           State.TOCUSTOMER) && executingRequest == null) ==> \result == true;
	 */
	public Boolean dipatchAble() {
		return !(state == State.IDLE || state == State.TOTARGET || state == State.TOCUSTOMER)
				&& executingRequest == null;
	}

	/**
	 * @REQUIRES: request!=null;
	 * @MODIFIES: this.printWriter; this.executingRequest; this.requestList;
	 *            this.customerPoint; this.targetPoint; this.executeState;
	 * @EFFECTS: give the taxi its dispatched request preset its status. Print the
	 *           information. printWriter ==
	 *           request.getPrintWritter();this.requestMessage == new
	 *           RequestMessage(); executingRequest == request;
	 *           requestList.size()==0; customerPoint == request.getStartPoin();
	 *           targetPoint == request.getTargetPoin(); (\all Request request2 in
	 *           requestList; !request2.taxiList.contains(this)); executeState == 1;
	 */
	public void recieveRequest(Request request) {
		this.printWriter = request.getPrintWritter();
		this.requestMessage = new RequestMessage();
		executingRequest = request;
		requestList.clear();
		customerPoint = request.getStartPoin();
		targetPoint = request.getTargetPoin();
		String string = "At the time of " + (executingRequest.getRealTime() + 7500) + "  " + executingRequest
				+ " has been dispatched to " + this;
		printWriter.println(string);
		System.out.println(string);
		printWriter.println(string);
		requestMessage.addMessage(string);
		// printWriter.println(this);
		for (Request request2 : requestList) {
			if (!request2.equals(request)) {
				request2.removeTaxi(this);
			}
		}
		executeState = 1;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this.flow; this.path; this.state; this.tickTock; this.gotCustomer;
	 * @EFFECTS: Set its status. Get its path towards its target. Print the relevant
	 *           information.
	 */
	protected void setRequest() {

		this.flow = new Flow(taxiMap);
		flow.BFS(customerPoint, taxiTime);
		path = flow.getPath(location, taxiTime);
		state = State.TOCUSTOMER;
		tickTock = 0;
	}

	/**
	 * @REQUIRES: request!=null;
	 * @MODIFIES: this.requestList; this.credit;
	 * @EFFECTS: credit == \old(credit) + 1; requestList.contains(request);
	 *           \old(requestList).contains(request) ==> \result == 1 ;
	 *           !\old(requestList).contains(request) ==> \result == 0 ;
	 */
	public int robRequest(Request request) {

		if (requestList.contains(request)) {
			return 0;
		} else {
			credit++;
			requestList.add(request);
			return 1;
		}
	}

	/**
	 * @REQUIRES: request!=null;
	 * @MODIFIES: this.requestList;
	 * @EFFECTS: !requestList.contains(request);
	 */
	public void removeRequest(Request request) {
		if (requestList.contains(request)) {
			requestList.remove(request);
		}
	}

	/**
	 * @REQUIRES: point!=null;
	 * @MODIFIES: this.taxiMap; this.path; this.location; this.flow;
	 * @EFFECTS: normal_behavior: Move the taxi to words point, change the path if
	 *           the road disappears. Change the taxiList of point on the taxiMap.
	 *           exception_behavior(Exception e): print the wrong information.
	 */
	protected void driveToPoint(Point point) {
		try {
			taxiMap.deleteTaxi(location, this);
			Point next = path.get(0);
			int direction = PositionCompute.getTurningDirection(lastLocation, location, next);
			boolean light = lightSystem.getLight(location);
			long waitingTime = TimeCompute.getWaitTime(lastLocation, location, direction, light, taxiTime);
			try {
				Thread.sleep(TimeCompute.TaxiSleepTime(taxiTime, waitingTime));
				taxiTime += waitingTime;
				if (waitingTime != 0) {
					printWriter.println(ID + " stop for the light" + waitingTime);
					requestMessage.addMessage(ID + " stop for the light" + waitingTime);
				}
			} catch (InterruptedException e) {
				System.out.println(ID + " wander sleeping wrong");
			}
			if (guigv.m.graph[location.x * 80 + location.y][next.x * 80 + next.y] != 1) {
				this.flow = new Flow(taxiMap);
				this.flow.BFS(point, taxiTime);
				this.path = flow.getPath(location, taxiTime);
				next = path.get(0);
			}
			path.remove(next);
			lastLocation = location;
			location = next;
			taxiMap.addTaxi(location, this);
			if (location.equals(point) && !gotCustomer) {
				gotCustomer = true;
				printWriter.println(
						taxiTime + ": " + this + " arrived at where the customer " + executingRequest.getID() + " is");
				requestMessage.addMessage(
						taxiTime + ": " + this + " arrived at where the customer " + executingRequest.getID() + " is");
			} else if (location.equals(point) && gotCustomer) {
				arrivedTarget = true;
			}
		} catch (Exception e) {
			System.out.println("drive to point wrong");
		}
	}

	/**
	 * @REQUIRES: point!=null;
	 * @MODIFIES: this.taxiMap; this.location;
	 * @EFFECTS: Move the taxi without a target. Change the taxiList of point on the
	 *           taxiMap.
	 */
	protected void wander() {
		Point next = PositionCompute.getWanderTarget(location, taxiMap, ID, taxiTime);
		if (next == null) {
			return;
		}
		if (lastLocation.equals(location)) {
			lastLocation = next;
		}
		int direction = PositionCompute.getTurningDirection(lastLocation, location, next);
		boolean light = lightSystem.getLight(location);
		long waitingTime = TimeCompute.getWaitTime(lastLocation, location, direction, light, taxiTime);
		try {
			Thread.sleep(TimeCompute.TaxiSleepTime(taxiTime, waitingTime));
			taxiTime += waitingTime;
			if (guigv.m.graph[location.x * 80 + location.y][next.x * 80 + next.y] == 0) {
				next = PositionCompute.getWanderTarget(location, taxiMap, ID, taxiTime);
			}

		} catch (InterruptedException e) {
			System.out.println(ID + " wander sleeping wrong");
		}
		lastLocation = location;
		location = next;
		taxiMap.deleteTaxi(lastLocation, this);
		taxiMap.addTaxi(location, this);
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: state;
	 * @EFFECTS: move the taxi according to the state.
	 */
	protected void move() {
		switch (state) {
		case State.IDLE:
			break;
		case State.TOCUSTOMER:
			if (path.size() == 0) {
				gotCustomer = true;
				String string = (taxiTime - 500) + ": " + this + " arrived at where the customer "
						+ executingRequest.getID() + " is";
				printWriter.println(string);
				requestMessage.addMessage(string);
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

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this.state; this.flow; this.path; this.printWriter; this.state;
	 *            this.credit; this.tickTock; this.gotCustomer; this.arrivedTarget;
	 *            this.executingRequest; this.customerPoint;
	 * @EFFECTS: Switch the state of the taxi according to the \old(state) and
	 *           tickTock. Set the taxi its target and other status, if it is going
	 *           to serve.
	 */
	protected void switchState() {
		switch (state) {
		case State.IDLE:
			if (tickTock == 2 && !gotCustomer) { // go on wandering
				state = State.WANDERING;
				tickTock = 0;
			} else if (tickTock == 2 && gotCustomer) { // driver to target
				String string = taxiTime + ": " + this + " finally get the customer " + executingRequest.getID();
				System.out.println(string);
				printWriter.println(string);
				requestMessage.addMessage(string);
				state = State.TOTARGET;
				flow = new Flow(taxiMap);
				flow.BFS(targetPoint, taxiTime);
				path = flow.getPath(location, taxiTime);
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
				messages.add(requestMessage);
				requestMessage = null;
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

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this.tickTock; this.taxiTime; this.executeState;
	 * @EFFECTS: normal_behavior: Move the taxi. Switch the status. if there is a
	 *           request not handled, set it to the taxi.
	 *           exception_behavior(Exception e): print the wrong information.
	 */
	public void run() {
		taxiGUI.SetTaxiStatus(ID, location, state % 3);
		while (true) {

			try {
				Thread.sleep(TimeCompute.TaxiSleepTime(taxiTime, 500));
				tickTock++;
				taxiTime += 500;
				move();
				switchState();
				if (executeState == 1) {
					setRequest();
					executeState = 0;
				}
				taxiGUI.SetTaxiStatus(ID, location, state % 3);
				if (executingRequest != null && state != State.IDLE) {
					printWriter.println(taxiTime + ": " + locationString());
					requestMessage.addMessage(taxiTime + ": " + locationString());
				}
				if (!location.equals(lastLocation)) {
					FlowMap.addFlow(location.x, location.y, lastLocation.x, lastLocation.y, taxiTime, 1);
				}
			} catch (Exception e) {
				System.out.println(location);
				System.out.println(ID + "Runiing Wrong");
			}
		}
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: return the print form of finished request.
	 */
	protected void printFinishedTask() {
		String printState = state == 0 ? "Stopping"
				: state == 4 ? "ToCustomer" : state == 1 ? "Serving" : state == 2 ? "Waiting" : "Wrong";
		String string = "Arrived!!!  Taxi " + ID + "  Locate at: (" + location.x + ", " + location.y + ") "
				+ "  State: " + printState + "  At " + taxiTime + " The taxi picked customer "
				+ executingRequest.getID();
		printWriter.println(string);
		System.out.println(string);
		requestMessage.addMessage(string);
		requestMessage
				.addMessage("---------------------------------------------------------------------------------------");
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: return the print form of this taxi.
	 */
	public String toString() {
		String printState = state == 0 ? "Stopping"
				: state == 4 ? "ToCustomer"
						: state == 1 ? "Serving"
								: state == 2 && executingRequest == null ? "Waiting"
										: state == 2 && executingRequest != null ? "ToCustomer" : "Wrong";
		return "Taxi ID: " + ID + "  Location: (" + location.x + ", " + location.y + ") " + "  State: " + printState
				+ " Credit: " + credit;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: return the print form for check this taxi.
	 */
	public String ckeckPrint() {
		String printState = state == 0 ? "Stopping"
				: state == 4 ? "ToCustomer"
						: state == 1 ? "Serving"
								: state == 2 && executingRequest == null ? "Waiting"
										: state == 2 && executingRequest != null ? "ToCustomer" : "Wrong";
		return System.currentTimeMillis() + ":  " + taxiTime + ":  " + "Taxi ID: " + ID + "  Location: (" + location.x
				+ ", " + location.y + ") " + "  State: " + printState + " Credit: " + credit;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: return the print form of the location of this taxi.
	 */
	protected String locationString() {
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

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return this.taxiMap != null && this.requestList != null;
	}
}
