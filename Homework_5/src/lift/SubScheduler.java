package lift;

public class SubScheduler {
	private Request lastRq;

	public SubScheduler() {
		lastRq = null;
	}

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

	public Request schedule(Queue queue, Elevator elevator) {
		Request rq;
		if (!queue.end()) {
			queue.wash(lastRq, elevator.getClock());
		}
		while (!queue.end()) {
			if (queue.frontVal() == 0) {
				System.out.println("#SAME" + "[" + queue.frontRq(lastRq, elevator).toString() + "]");
				queue.moveFront(1);
			} else if (queue.frontVal() == 2) {
				queue.moveFront(1);
			} else {
				break;
			}
		}
		rq = queue.frontRq(lastRq, elevator);
		lastRq = rq;
		return rq;
	}

}
