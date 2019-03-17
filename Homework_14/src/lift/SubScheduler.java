package lift;

public class SubScheduler {
	/**
	 * @Overview: SubSchduler is a scheduler which get request and send to elevator.
	 *            And it provides methods to use.
	 */
	private Request lastRq;

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this;
	 * @EFFECTS: construct(this);
	 */
	public SubScheduler() {
		lastRq = null;
	}

	/**
	 * @REQUIRES: queue != null; elevator ！= null;
	 * @MODIFIES: None;
	 * @EFFECTS: (drive the elevator, get a request for it and wash the queue)
	 */
	public void command(Queue queue, Elevator elevator) {
		Request rq = schedule(queue, elevator);
		if (rq != null) {
			elevator.move(rq);
			lastRq = rq; 
			if (lastRq.getMt() == 0) {
				queue.wash(lastRq, elevator.predictTime(queue.getMain(), lastRq) + 1);
			} else {
				queue.wash(lastRq, elevator.predictTime(queue.getMain(), lastRq));
			}
		}
	}

	/**
	 * @REQUIRES: queue != null; elevator ！= null;
	 * @MODIFIES: System.out;
	 * @EFFECTS: (schedule the queue when queue not empty, print the information of
	 *           homogeneous request and get a proper Request as rq) ==> \request ==
	 *           rq;
	 */
	public Request schedule(Queue queue, Elevator elevator) {
		if (!queue.end()) {
			queue.wash(lastRq, elevator.getClock());
		}
		while (!queue.end()) {
			if (queue.getFrontVal() == 0) {
				System.out.println("#SAME" + "[" + queue.frontRq(lastRq, elevator).toString() + "]");
				queue.moveFront(1);
			} else if (queue.getFrontVal() == 2) {
				queue.moveFront(1);
			} else {
				break;
			}
		}
		Request rq = queue.frontRq(lastRq, elevator);
		lastRq = rq;
		return rq;
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:\result == true;
	 */
	public boolean repOK() {
		return true;
	}
}
