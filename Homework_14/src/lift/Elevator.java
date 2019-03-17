package lift;

public class Elevator {
	/**
	 * @Overview: Elevator class is a model of elevator, it contains two possible
	 *            request position and records floor now the elevator is at, time,
	 *            and difference between the floor before between now floor. It
	 *            deals with coming request and stored request. It provides methods
	 *            to check information.
	 */
	private int floorNow;
	private double clock;
	private Request lastRq;
	private Request lastRq2;
	private int diff;

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this;
	 * @EFFECTS: clock == 0.0; floorNow == 1; lastRq == null; lastRq2 == null; diff
	 *           == 0;
	 */
	public Elevator() {
		floorNow = 1;
		clock = 0.0;
		lastRq = null;
		lastRq2 = null;
		diff = 0;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == clock;
	 */
	public double getClock() {
		return clock;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == floorNow;
	 */
	public int getFloor() {
		return floorNow;
	}

	/**
	 * @REQUIRES: mainRq!=null; rq!=null;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == ((double) mainRq.getTime() > clock ? (double)
	 *           mainRq.getTime(): clock) + (Math.abs(floorNow - rq.getFloor())) *
	 *           0.5 + lastRq != null && rq.getFloor() != floorNow &&
	 *           lastRq.getFloor() != rq.getFloor()? 1: 0;
	 */
	public double predictTime(Request mainRq, Request rq) {
		double temClock = 0;
		if ((double) mainRq.getTime() > clock) {
			temClock = (double) mainRq.getTime();
		} else {
			temClock = clock;
		}
		temClock = temClock + (Math.abs(floorNow - rq.getFloor())) * 0.5;
		if (lastRq != null) {
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
	 * @EFFECTS: (According to whether rq is a main request(whether rq.getMt() == 1)
	 *           and whether the elevator has stored one or two request, act the
	 *           request or store it in lastRq or lastRq2 and change relative label
	 *           last and last2)
	 */
	public void move(Request rq) {
		resetClock(rq);
		if (rq.getMt() == 0) {
			if (lastRq == null) {
				lastRq = rq;
			} else { // rq.getMt() == 0 && last == 1
				if (lastRq.getFloor() == rq.getFloor()) {
					lastRq2 = rq;
				} else {
					if (lastRq2 != null) {
						act(lastRq);
						System.out.println(toString(lastRq));
						System.out.println(toString(lastRq2));
						lastRq2 = null;
						lastRq = rq;
					} else {
						act(lastRq);
						System.out.println(toString(lastRq));
						lastRq = rq;
					}
					clock++;
				}
			}
		} else if (lastRq == null) { // rq.getMt() == 1
			act(rq);
			System.out.println(toString(rq));
			clock++;
		} else { // if(rq.getMt()==1 && last==1)
			if (lastRq2 != null) {
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
			lastRq = null;
			lastRq2 = null;
		}
	}

	/**
	 * @REQUIRES: rq != null;
	 * @MODIFIES: diff; floorNow; clock;
	 * @EFFECTS: diff == \old(floorNow) - rq.getFloor(); floorNow == rq.getFloor();
	 *           clock == 0.5 * Math.abs(\old(floorNow) - rq.getFloor()) +
	 *           \old(clock);clock == 0.5 * Math.abs(diff) + \old(clock);
	 */
	private void act(Request rq) {
		diff = floorNow - rq.getFloor();
		floorNow = rq.getFloor();
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
	 * @EFFECTS:(floorNow <= 10 && floorNow >= 0 && clock >= 0.0 && diff >= -9 &&
	 *                    diff <= 9) ==> \result == true;
	 */
	public boolean repOK() {
		return floorNow <= 10 && floorNow >= 0 && clock >= 0.0 && diff >= -9 && diff <= 9;
	}
}
