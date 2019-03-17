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

public abstract class Taxi implements Runnable {
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
	 *            this.executeState this.path; this.flow;this.messages;
	 * @EFFECTS: this.taxiMap == taxiMap; this.ID == number++; this.credit == 0;
	 *           this.state == State.WANDERING; this.location == null;
	 *           this.requestList != null; this.arrivedTarget == false;
	 *           this.gotCustomer == false; this.executingRequest == null;
	 *           this.customerPoint == null; this.targetPoint == null;
	 *           this.executeState == 0; this.path == null; this.flow == null;
	 *           this.messages != null;
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
	 * @REQUIRES: taxiGUI!=null; lightSystem != null;
	 * @MODIFIES: this.location; this.taxiTime; this.tickTock; this.taxiMap;
	 *            this.taxiGUI ;
	 * @EFFECTS: this.taxiTime == TaxiSys.MAINTIME; this.taxiGUI == taxiGUI;
	 *           this.location == taxiMap.getPoint(random.nextInt(TaxiSys.SIZE),
	 *           random.nextInt(TaxiSys.SIZE)); this.tickTock == 0;
	 *           this.taxiMap.addTaxi(location, this);
	 */
	public abstract void initTaxi(TaxiGUI taxiGUI, LightSystem lightSystem);

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
	public abstract int getDistance(Request request);

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
	 *            requestMessage;
	 * @EFFECTS: give the taxi its dispatched request preset its status. Print the
	 *           information. printWriter ==
	 *           request.getPrintWritter();this.requestMessage != null;
	 *           executingRequest == request; requestList.size()==0; customerPoint
	 *           == request.getStartPoin(); targetPoint == request.getTargetPoin();
	 *           (\all Request request2 in requestList;
	 *           !request2.taxiList.contains(this)); executeState == 1;
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
	abstract protected void setRequest();

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
	 * @EFFECTS: Move the taxi to words point, change the path if
	 *           the road disappears. Change the taxiList of point on the taxiMap.
	 */
	protected abstract void driveToPoint(Point point);

	/**
	 * @REQUIRES: point!=null;
	 * @MODIFIES: this.taxiMap; this.location;
	 * @EFFECTS: Move the taxi without a target. Change the taxiList of point on the
	 *           taxiMap.
	 */
	protected abstract void wander();

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this;
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
	protected abstract void switchState();

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this.tickTock; this.taxiTime; this.executeState;
	 * @EFFECTS: Move the taxi. Switch the status. if there is a request not
	 *           handled, set it to the taxi.
	 */
	public abstract void run();

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: requestMessage;
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
