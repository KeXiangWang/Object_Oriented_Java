package lift;

enum Requester {
	FR, ER
}

enum Direction {
	UP, DOWN
}

public class Queue {
	/**
	 * @Overview: Queue is a queue of request, it provides methods to add, wash,
	 *            judge, justify, change information and get information. It
	 *            consists of a list of request with a list of corresponding
	 *            validity, the front and rear of the list, a request able to be
	 *            upgrade with its position in the list and a main request. And it
	 *            Defines a constant to restrict the length of the list.
	 */

	private static final int MAX = 2000;
	private Request[] rqList;
	private int[] validity;
	private int front;
	private int rear;
	private Request unhandle;
	private Request mainRq;
	private int unhandlePosition;

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this;
	 * @EFFECTS: rqList!=null; validity!=null; front == 0; rear == 0; unhandle ==
	 *           null; mainRq == null; unhandlePosition == -1;
	 */
	public Queue() {
		rqList = new Request[MAX];
		validity = new int[MAX];
		front = 0;
		rear = 0;
		unhandle = null;
		mainRq = null;
		unhandlePosition = -1;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == mainRq;
	 */
	public Request getMain() {
		return mainRq;
	}

	/**
	 * @EFFECTS: (front >= rear) ==> (\result == true); (front < rear) ==> (\result
	 *           == false);
	 */
	public boolean end() {
		if (front >= rear)
			return true;
		else
			return false;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this;
	 * @EFFECTS: (According to the information from lastRq and elevator, choose a
	 *           proper request on the basic of the information of this queue. And
	 *           change some fields if necessary) ==> \result == request;
	 */
	public Request frontRq(Request lastRq, Elevator elevator) {
		if (validity[front] == 0) {
			return rqList[front];
		} else {
			Request nearRequest = null;
			int nearPosition = -1;
			mainRq = rqList[front];
			double newClock = elevator.predictTime(mainRq, mainRq);
			for (int i = front + 1; i < rear; i++) {
				if (rqList[i].getTime() <= newClock + 1 && mainRq.equals(rqList[i])) {
					validity[i] = 0;
					continue;
				}
				if (rqList[i].getTime() >= newClock) {
					break;
				}
				if (validity[i] == 0 || validity[i] == 2)
					continue;
				int judgeResult = judge(mainRq, rqList[i], elevator);
				if (judgeResult == 1) {
					if (nearPosition == -1) { // BEST?
						if (!(lastRq.getMt() == 0 && Math.abs(lastRq.getFloor() - elevator.getFloor()) > Math
								.abs(rqList[i].getFloor() - elevator.getFloor()))) {
							nearRequest = rqList[i];
							nearPosition = i;
						}
					} else {
						if (Math.abs(nearRequest.getFloor() - elevator.getFloor()) > Math
								.abs(rqList[i].getFloor() - elevator.getFloor())) {
							if (!(lastRq.getMt() == 0 && Math.abs(lastRq.getFloor() - elevator.getFloor()) > Math
									.abs(rqList[i].getFloor() - elevator.getFloor()))) {
								nearRequest = rqList[i];
								nearPosition = i;
							}
						}
					}
				} else if (judgeResult == 2) {
					if (unhandlePosition == -1) {
						unhandle = rqList[i];
						unhandlePosition = i;
					}
				} else {
				}
			}
			if (nearRequest == null) {
				front++;
				while (validity[front] == 2)
					front++;
				if (unhandlePosition != -1) {
					justifyUnhandle();
					unhandlePosition = -1;
				}
				return mainRq;
			} else {
				rqList[nearPosition].setMt(0);
				rqList[nearPosition].setFt(mainRq.getTime());
				validity[nearPosition] = 2;
				nearPosition = -1;
				return nearRequest;
			}

		}
	}

	/**
	 * @MODIFIES: 0<a,b<11;
	 * @EFFECTS: a > b ==> \result == Direction.DOWN; a < b ==> \result ==
	 *           Direction.UP; a == b ==> \result == null;
	 */
	private Direction getRelativeDirection(int a, int b) {
		if (a > b) {
			return Direction.DOWN;
		} else if (a < b) {
			return Direction.UP;
		} else {
			return null;
		}
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this;
	 * @EFFECTS: (rq is pickup-able for mainRq and elevator but not scalable) ==
	 *           \result == 1; (rq is scalable for mainRq and elevator) == \result
	 *           == 2; (other conditions) ==> \result == 0;
	 */
	private int judge(Request mainRq, Request rq, Elevator elevator) {
		double newClock = elevator.predictTime(mainRq, rq);
		Direction direction = getRelativeDirection(elevator.getFloor(), mainRq.getFloor());
		if (direction == null) {
			return 0;
		}
		if (rq.getGt() == Requester.FR) {
			if (direction != rq.getDr())
				return 0;
			if (rq.getFloor() <= mainRq.getFloor() && rq.getFloor() > elevator.getFloor() && newClock > rq.getTime()) {
				return 1;
			} else if (rq.getFloor() >= mainRq.getFloor() && rq.getFloor() < elevator.getFloor()
					&& newClock > rq.getTime()) {
				return 1;
			} else {
				return 0;
			}
		} else {
			if (rq.getFloor() <= mainRq.getFloor() && rq.getFloor() > elevator.getFloor()) {
				if (newClock > rq.getTime()) {
					return 1;
				} else {
					return 0;
				}
			} else if (rq.getFloor() >= mainRq.getFloor() && rq.getFloor() < elevator.getFloor()) {
				if (newClock > rq.getTime()) {
					return 1;
				} else {
					return 0;
				}
			} else if (rq.getFloor() > elevator.getFloor() && direction == Direction.UP
					|| rq.getFloor() < elevator.getFloor() && direction == Direction.DOWN) {
				return 2;
			} else {
				return 0;
			}
		}
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this;
	 * @EFFECTS: (\all int i; unhandlePosition >= i > front; validity[i] ==
	 *           old(validity[i - 1])&&rqList[i] == \old(rqList[i - 1]));
	 *           validity[front] == \old(validity[unhandlePosition]); rqList[front]
	 *           == unhandle;
	 */
	private void justifyUnhandle() {
		int temp = validity[unhandlePosition];
		for (int i = unhandlePosition; i > front; i--) {
			validity[i] = validity[i - 1];
			rqList[i] = rqList[i - 1];
		}
		validity[front] = temp;
		rqList[front] = unhandle;
	}
	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == validity[front];
	 */
	public int getFrontVal() {
		return validity[front];
	}

	/**
	 * @MODIFIES: front;n>0;
	 * @EFFECTS: front == \old(front) + n;
	 */
	public void moveFront(int n) {
		front = front + n;
	}

	/**
	 * @REQUIRES: clock>=0;
	 * @MODIFIES: validity;
	 * @EFFECTS: (\all int i; rear > i >= front && rqList[i].getTime() <= clock;
	 *           (validity[i] == 1 && lastRq != null && lastRq.equals(rqList[i]))
	 *           ==> validity[i] == 0;);
	 */
	public void wash(Request lastRq, double clock) {
		for (int i = front; i < rear && rqList[i].getTime() <= clock; i++) {
			if (validity[i] == 1 && lastRq != null) {
				if (lastRq.equals(rqList[i])) {
					validity[i] = 0;
				}
			}
		}
	}

	/**
	 * @MODIFIES: System.out; this;
	 * @EFFECTS: (parse the input str and according to the parsing result print
	 *           relative text);
	 */
	public void parse(String str) {
		if (parseRq(str) != 0) {
			System.out.println("INVALID[" + str + "]");
		}
	}

	/**
	 * @MODIFIES: this;
	 * @EFFECTS: (str is a valid request) ==> (rear == \old(rear) + 1) &&
	 *           validity[rear] == 1 && \result == 0; (str is not a valid request)
	 *           ==> \result != 0;
	 */
	private int parseRq(String str) {
		try {
			if (str.matches(
					"((\\(FR,\\+?[0-9]{1,50},((DOWN)|(UP)),\\+?[0-9]{1,50}\\))|(\\(ER,\\+?[0-9]{1,50},\\+?[0-9]{1,50}\\)))")) {
				String[] strs = str.split("[\\(\\),]");
				if (strs.length == 5) {
					Requester from = Requester.FR;
					int askfloor = Integer.parseInt(strs[2]);
					Direction direction = strs[3].equals("UP") ? Direction.UP : Direction.DOWN;
					long time = Long.parseLong(strs[4]);
					if (askfloor < 1 || askfloor > 10)
						return 1;
					if (time > 4294967295L)
						return 1;
					if (rear == 0 && time != 0L)
						return 3;
					if (askfloor == 1 && direction == Direction.DOWN)
						return 5;
					if (askfloor == 10 && direction == Direction.UP)
						return 6;
					if (rear != 0 && time < rqList[rear - 1].getTime())
						return 7;
					validity[rear] = 1;
					rqList[rear] = new Request(from, askfloor, direction, time, rear);
					if (rear == 0 && !rqList[rear].toString().equals("FR,1,UP,0")) {
						validity[rear] = 0;
						return 1;
					}
					rear++;
				} else {
					Requester from = Requester.ER;
					int target = Integer.parseInt(strs[2]);
					long time = Long.parseLong(strs[3]);
					if (target < 1 || target > 10)
						return 1;
					if (time > 4294967295L)
						return 1;
					if (rear == 0 && time != 0L)
						return 3;
					if (rear != 0 && time < rqList[rear - 1].getTime())
						return 7;
					validity[rear] = 1;
					rqList[rear] = new Request(from, target, time, rear);
					if (rear == 0) {
						validity[rear] = 0;
						return 1;
					}
					rear++;
				}
				return 0;
			} else {
				return 1;
			}
		} catch (Exception e) {
			return 1;
		}
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(rqList != null && validity != null && front >= 0 && rear >= 0 &&
	 *                  unhandlePosition >= -1) ==> \result == true;
	 */
	public boolean repOK() {
		return rqList != null && validity != null && front >= 0 && rear >= 0 && unhandlePosition >= -1;
	}
}