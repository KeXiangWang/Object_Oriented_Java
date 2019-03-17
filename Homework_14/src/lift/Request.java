package lift;

public class Request {
	/**
	 * @Overview: Request record all the information of a request, including its
	 *            guest. A FR request's information consist of its floor where it
	 *            comes from, the move direction, the time it merges, time time to
	 *            be set as a picked request, whether it is a main request and its
	 *            merging order. An ER request's information consist of its floor it
	 *            aiming at, the time it merges, time time to be set as a picked
	 *            request, whether it is a main request and its merging order. It
	 *            provides methods to check information.
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
	 * @REQUIRES: a!=null; c!=null; 0 < b < 11; d >= 0; e >= 0;
	 * @MODIFIES: this;
	 * @EFFECTS: guest == a; askfloor == b; direction == c; time == d; ftime == 0;
	 *           motion == 1; order == e;
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
	 * @REQUIRES: a!=null; 0 < b < 11; d >= 0; e >= 0;
	 * @MODIFIES: this;
	 * @EFFECTS: guest == a; target == b; time == d; ftime == 0; motion == 1; order
	 *           == e;
	 */
	public Request(Requester a, int b, long d, int e) {
		guest = a;
		target = b;
		time = d;
		ftime = 0;
		motion = 1;
		order = e;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == guest;
	 */
	public Requester getGt() {
		return guest;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == target;
	 */
	public int getTg() {
		return target;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == askfloor;
	 */
	public int getAf() {
		return askfloor;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == direction;
	 */
	public Direction getDr() {
		return direction;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == time;
	 */
	public long getTime() {
		return time;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == motion;
	 */
	public int getMt() {
		return motion;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: motion == n;
	 */
	public void setMt(int n) {
		motion = n;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == ftime;
	 */
	public long getFt() {
		return ftime;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: ftime == n;
	 */
	public void setFt(long n) {
		ftime = n;
	}

	/**
	 * @REQUIRES: a!=null;
	 * @MODIFIES: this;
	 * @EFFECTS: guest == Requester.FR ==> \result == askfloor; guest ==
	 *           Requester.ER ==> \result == target;
	 */
	public int getFloor() {
		if (guest == Requester.FR)
			return askfloor;
		else
			return target;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == order;
	 */
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
	 * @EFFECTS: guest == Requester.FR ==> \result == guest + "," + askfloor + "," +
	 *           direction + "," + String.format("%d", time); guest == Requester.ER
	 *           ==> \result == guest + "," + target + "," + String.format("%d",
	 *           time);
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

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS: guest != null && time >= 0 && ftime >= 0 && (motion == 1 || motion
	 *           == 0) && order >= 0 && (guest == Requester.FR && askfloor < 11 &&
	 *           askfloor > 0 && direction != null || guest == Requester.ER &&
	 *           target < 11 && target > 0) ==> \result == true;
	 */
	public boolean repOK() {
		return guest != null && time >= 0 && ftime >= 0 && (motion == 1 || motion == 0) && order >= 0
				&& (guest == Requester.FR && askfloor < 11 && askfloor > 0 && direction != null
						|| guest == Requester.ER && target < 11 && target > 0);
	}
}
