package taxi;

import java.awt.Point;
import java.util.Random;

public class NormalTaxi extends Taxi {
	/**
	 * @Overview: NormalTaxi contains its credit, ID, location, and when got a
	 *            request, it record the request's information. It provide method to
	 *            move and rob the requests. NormalTaxi run on a map which does not
	 *            ignores the facts some roads were broken.
	 */
	public NormalTaxi(TaxiMap taxiMap) {
		super(taxiMap);
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
	 * @EFFECTS: \result == Flow.getFastestDistance(request.getStartPoin(),
	 *           location);
	 */
	public int getDistance(Request request) {
		return Flow.getFastestDistance(request.getStartPoin(), location);
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
	 * @REQUIRES: None;
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
	 * @EFFECTS: Switch the status. if there is a request not handled, set it to the
	 *           taxi.
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
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	@Override
	public boolean repOK() {
		return this.taxiMap != null;
	}
}
