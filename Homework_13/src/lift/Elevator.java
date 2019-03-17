package lift;

public class Elevator implements Equipment {
	/**
	 * @Overview: Elevator class is a model of elevator, it deal with the requests
	 *            and store request which is not to be exert right away. It records
	 *            relative information and provide method to check information.
	 */
	private int floorNow;
	private double clock;
	private Request lastRq;
	private int last;
	private Request lastRq2;
	private int last2;
	private int diff;

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this;
	 * @EFFECTS: construct(this);
	 */
	public Elevator() {
		floorNow = 1;
		clock = 0.0;
		last = 0;
		lastRq = null;
		last2 = 0;
		lastRq2 = null;
	}

	public double getClock() {
		return clock;
	}

	public int getFloor() {
		return floorNow;
	}

	/**
	 * @REQUIRES: mainRq!=null; rq!=null;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == (According to the clock and floor of elevator and the
	 *           different of floor between two request, predict a time between two
	 *           request);
	 */
	public double predictTime(Request mainRq, Request rq) {
		double temClock = 0;
		if ((double) mainRq.getTime() > clock) {
			temClock = (double) mainRq.getTime();
		} else {
			temClock = clock;
		}
		temClock = temClock + (Math.abs(floorNow - rq.getFloor())) * 0.5;
		if (last != 0) {
			if (rq.getFloor() != floorNow && lastRq.getFloor() != rq.getFloor()) {
				temClock++;
			}
		}
		return temClock;
	}

	/**
	 * @REQUIRES: rq!=null;
	 * @MODIFIES: clock;
	 * @EFFECTS: ((double) rq.getTime() > clock && rq.getMt() == 1) ==> (clock ==
	 *           (double) rq.getTime()); ((double) rq.getTime() > clock &&
	 *           rq.getMt() == 0 && (double) rq.getFt() > clock) ==> (clock ==
	 *           (double) rq.getFt());
	 */
	private void resetClock(Request rq) {
		if ((double) rq.getTime() > clock) {
			if (rq.getMt() == 1) { // mainRq
				clock = (double) rq.getTime();
			} else { // byRq
				if ((double) rq.getFt() > clock)
					clock = (double) rq.getFt();
			}
		}
	}

	/**
	 * @REQUIRES: rq!=null;
	 * @MODIFIES: this;
	 * @EFFECTS: (According whether rq is a main request(whether rq.getMt() == 1)
	 *           and whether the elevator has store one or two request, act the
	 *           request or store it in lastRq or lastRq2 and change relative label
	 *           last and last2)
	 */
	public void move(Request rq) {
		resetClock(rq);
		if (rq.getMt() == 0) {
			if (last == 0) {
				lastRq = rq;
				last = 1;
			} else { // rq.getMt() == 0 && last == 1
				if (lastRq.getFloor() == rq.getFloor()) {
					// if (lastRq.getOrder() < rq.getOrder()) {
					last2 = 1;
					lastRq2 = rq;
					// } else {
					// last2 = 1;
					// lastRq2 = lastRq;
					// lastRq = rq;
					// }
				} else {
					if (last2 == 1) {
						act(lastRq);
						System.out.println(toString(lastRq));
						System.out.println(toString(lastRq2));
						last2 = 0;
						lastRq = rq;
						last = 1;
					} else {
						act(lastRq);
						System.out.println(toString(lastRq));
						lastRq = rq;
						last = 1;
					}
					clock++;
				}
			}

		} else if (last == 0) { // rq.getMt() == 1 
			act(rq);
			System.out.println(toString(rq));
			clock++;
		} else { // if(rq.getMt()==1 && last==1) 
			if (last2 == 1) {
				act(lastRq);
				if (lastRq.getFloor() == rq.getFloor()) {
					System.out.println(toString(rq));
					System.out.println(toString(lastRq));
					System.out.println(toString(lastRq2));
					// }
				} else {
					System.out.println(toString(lastRq));
					System.out.println(toString(lastRq2));
					clock++;
					act(rq);
					System.out.println(toString(rq));
				}
			} else {
				if (lastRq.getFloor() == rq.getFloor()) {
					if (lastRq.getOrder() > rq.getOrder()) {
						act(rq);
						System.out.println(toString(rq));
						System.out.println(toString(lastRq));
					} else {
						act(lastRq);
						System.out.println(toString(lastRq));
						System.out.println(toString(rq));
					}
				} else {
					act(lastRq);
					System.out.println(toString(lastRq));
					clock++;
					act(rq);
					System.out.println(toString(rq));
				}
			}
			clock++;
			last = 0;
			last2 = 0;
		}
	}

	/**
	 * @REQUIRES: rq != null;
	 * @MODIFIES: diff; floorNow; clock;
	 * @EFFECTS: diff == \old(floorNow) - rq.getFloor(); floorNow == rq.getFloor();
	 *           clock == 0.5 * Math.abs(\old(floorNow) - rq.getFloor()) +
	 *           \old(clock);
	 */
	private void act(Request rq) {
		int askfloor = rq.getFloor();
		diff = floorNow - askfloor;
		floorNow = askfloor;
		clock = 0.5 * Math.abs(diff) + clock;
	}

	/**
	 * @REQUIRES: rq != null;
	 * @MODIFIES: None;
	 * @EFFECTS: diff == 0 ==> \result == "[" + rq.toString() + "]/" + "(" +
	 *           floorNow + String.format(",STILL,%.1f)", clock + 1); diff > 0 ==>
	 *           \result == "[" + rq.toString() + "]/" + "(" + floorNow +
	 *           String.format(",DOWN,%.1f)", clock); diff < 0 ==> \result == "[" +
	 *           rq.toString() + "]/" + "(" + floorNow + String.format(",UP,%.1f)",
	 *           clock);
	 */
	private String toString(Request rq) {
		String rqori = rq.toString();
		if (diff == 0) {
			return "[" + rqori + "]/" + "(" + floorNow + String.format(",STILL,%.1f)", clock + 1);
		} else if (diff > 0) {
			return "[" + rqori + "]/" + "(" + floorNow + String.format(",DOWN,%.1f)", clock);
		} else {
			return "[" + rqori + "]/" + "(" + floorNow + String.format(",UP,%.1f)", clock);
		}
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return true;
	}
}
