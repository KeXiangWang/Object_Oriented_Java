package lift;

public class Request {
	/**
	 * @Overview: Request record all the information of a request. And it provides
	 *            methods to check information.
	 */

	private Requester guest;
	private int askfloor;
	private int target;
	private Direction direction;
	private long time;
	private long ftime;
	private int motion;
	private int order;

	/**
	 * @REQUIRES: a!=null; c!=null;
	 * @MODIFIES: this;
	 * @EFFECTS: construct(this);
	 */
	public Request(Requester a, int b, Direction c, long d, int e) {
		guest = a;
		askfloor = b;
		direction = c;
		time = d;
		ftime = 0;
		motion = 1;
		order = e;
	}

	/**
	 * @REQUIRES: a!=null;
	 * @MODIFIES: this;
	 * @EFFECTS: construct(this);
	 */
	public Request(Requester a, int b, long d, int e) {
		guest = a;
		target = b;
		time = d;
		ftime = 0;
		motion = 1;
		order = e;
	}

	public Requester getGt() {
		return guest;
	}

	public int getTg() {
		return target;
	}

	public int getAf() {
		return askfloor;
	}

	public Direction getDr() {
		return direction;
	}

	public long getTime() {
		return time;
	}

	public int getMt() {
		return motion;
	}

	public void setMt(int n) {
		motion = n;
	}

	public long getFt() {
		return ftime;
	}

	public void setFt(long n) {
		ftime = n;
	}

	/**
	 * @REQUIRES: a!=null;
	 * @MODIFIES: this;
	 * @EFFECTS: construct(this);
	 */
	public int getFloor() {
		if (guest == Requester.FR)
			return askfloor;
		else
			return target;
	}

	public int getOrder() {
		return order;
	} 

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: (o instanceof Request)&&(guest == rq.getGt() && ((guest ==
	 *           Requester.FR && askfloor == rq.getAf() && direction == rq.getDr()
	 *           && rq.getTime() >= time) || (guest == Requester.FR && target ==
	 *           rq.getTg() && rq.getTime() >= time))) ==> \result == true; (other
	 *           conditions) ==> \result == false;
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true; 
		if (!(o instanceof Request)) {
			return false;
		}

		Request rq = (Request) o;
		if (guest == rq.getGt()) {
			if (guest == Requester.FR) { 
				if (askfloor == rq.getAf() && direction == rq.getDr() && rq.getTime() >= time) {
					return true; 
				}
			} else {
				if (target == rq.getTg() && rq.getTime() >= time) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: (o instanceof Request)&&(guest == rq.getGt() && ((guest ==
	 *           Requester.FR && askfloor == rq.getAf() && direction == rq.getDr()
	 *           && rq.getTime() >= time) || (guest == Requester.FR && target ==
	 *           rq.getTg() && rq.getTime() >= time))) ==> \result == true; (other
	 *           conditions) ==> \result == false;
	 */
	@Override
	public String toString() {
		String string;
		String stime = String.format("%d", time);
		if (guest == Requester.FR) {
			string = guest + "," + askfloor + "," + direction + "," + stime;
			return string;
		} else {
			string = guest + "," + target + "," + stime;
			return string;
		}
	}
}
