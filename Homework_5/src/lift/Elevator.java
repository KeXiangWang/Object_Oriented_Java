package lift;

public class Elevator implements Runnable {
	private final static int STILLTIME = 6000;
	private final static int RUNTIME = 3000;
	private final static int PRECISION = 20;
	private int tag;
	private Request[] requestList;
	private int[] validation;
	private int front;
	private int rear;
	private Scheduler scheduler;
	private int currentFloor;
	private Direction direction;
	private int workLoad;
	private int targetFloor;
	private long clock;
	private Boolean arrived;
	private Request mainRequest;
	private Shutter shutter;
	private Boolean catchedUnUpgrade;
	private Request unUpgrade;

	public Elevator(int tag, Shutter shutter) {
		this.tag = tag;
		requestList = new Request[500];
		validation = new int[500];
		front = 0;
		rear = 0;
		currentFloor = 1;
		workLoad = 0;
		clock = 0;
		arrived = true;
		this.shutter = shutter;
		catchedUnUpgrade = false;
	}

	public Boolean getArrived() {
		return arrived;
	}

	public long getSleepTime(long sleepTime) {
		long time = clock + sleepTime - System.currentTimeMillis() + ElevatorSys.startTime;
		if (time < 0)
			time = sleepTime;
		return time;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public int getWorkLoad() {
		return workLoad;
	}

	public void catchScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void catchRequest(Request request) {
		validation[rear] = 1;
		requestList[rear++] = request;
	}

	public void setMain(Request request) {
		targetFloor = request.getTarget();
		mainRequest = request;
		arrived = false;
		if (request.getTarget() > currentFloor) {
			direction = Direction.UP;
		} else if (request.getTarget() < currentFloor) {
			direction = Direction.DOWN;
		} else {
			direction = Direction.STILL;
		}
	}

	public void setClock() {
		clock = (System.currentTimeMillis() - ElevatorSys.startTime);
	}

	public Boolean between(int n) {
		if (n > currentFloor && n <= targetFloor) {
			return true;
		} else if (n < currentFloor && n >= targetFloor) {
			return true;
		} else {
			return false;

		}
	}

	public Boolean farther(int n) {
		if (direction == Direction.UP && n > targetFloor) {
			return true;
		} else if (direction == Direction.DOWN && n < targetFloor) {
			return true;
		} else {
			return false;

		}
	}

	public Boolean ableToPick(Request request) {
		if (currentFloor == targetFloor && request.getTarget() == targetFloor
				&& request.getGuest() == mainRequest.getGuest() && arrived == false) {
			if (request.getGuest() == Requester.FR && request.getDirection() == mainRequest.getDirection()) {
				return true;
			}
			if (request.getGuest() == Requester.ER) {
				return true;
			}
		}
		if (request.getGuest() == Requester.FR && request.getTarget() == targetFloor
				&& request.getGuest() == mainRequest.getGuest() && arrived == false
				&& request.getDirection() == mainRequest.getDirection()) {
			return true;
		}
		if (between(request.getTarget()) && arrived == false) {
			// System.out.println(request.toString()+" 22222");
			if (request.getGuest() == Requester.FR && request.getDirection() != direction) {
				return false;
			}
			return true;
		} else if (farther(request.getTarget()) && request.getGuest() == Requester.ER && arrived == false) {
			if (catchedUnUpgrade == false) {
				unUpgrade = request;
				catchedUnUpgrade = true;
			}
			return false;
		} else {
			return false;
		}
	}

	public void move(Request request) {
	}

	public double predictTime(int n, Request request) {
		return 0;
	}

	public int getClock() {
		return 1;
	}

	public void move() {
		if (currentFloor == targetFloor) {
			direction = Direction.STILL;
		} else {
			currentFloor = currentFloor + (direction == Direction.UP ? 1 : direction == Direction.DOWN ? -1 : 0);
			workLoad++;
		}
	}

	public void washRequestList(int washFront, Request request) {
		int i;
		for (i = washFront; i < rear; i++) {
			if (validation[i] == 1 && request.getGuest() == requestList[i].getGuest()
					&& request.getTarget() == requestList[i].getTarget()) {
				if (request.getGuest() == Requester.FR && request.getDirection() != requestList[i].getDirection())
					continue;
				String stime = String.format("%.1f", (double) requestList[i].getTime() / 1000);
				System.out.println(
						"#" + System.currentTimeMillis() + ":SAME[" + requestList[i].toString() + "," + stime + "]");
				validation[i] = 0;
			}
		}
	}

	public void execute() {
		move();
		WashList washlist = new WashList();
		int stoped = 0;
		int i;
		for (i = front; i < rear; i++) {
			if (validation[i] == 0) {
				front++;
			} else {
				break;
			}
		}
		for (i = front; i < rear; i++) {
			if (validation[i] == 0)
				continue;
			Request request = requestList[i];
			if (request.getTarget() == currentFloor) {
				if (request.getGuest() == Requester.FR && currentFloor != targetFloor
						&& request.getDirection() != direction) {
					continue;
				}
				String sClock = String.format("%.1f", (double) clock / 1000);
				String stime = String.format("%.1f", (double) request.getTime() / 1000);
				System.out.println(System.currentTimeMillis() + ":[" + request.toString() + "," + stime + "]/(#" + tag
						+ "," + currentFloor + "," + direction + "," + workLoad + "," + sClock + ")");
				validation[i] = 0;
				
				stoped++;
				washlist.add(i, request);
				washRequestList(i, request);
			}
		}
		if (stoped > 0 && direction != Direction.STILL) {
			try {
				long sleepTime = getSleepTime(STILLTIME);
				Thread.sleep(sleepTime);
				clock += STILLTIME;
			} catch (InterruptedException ex) {
				System.out.println("after not sleep");
			}
		}
		while(washlist.end()!=1) {
			int k =washlist.geti();
			Request rq = washlist.getrq();
			washlist.move();
			washRequestList(k, rq);
		}
		if (currentFloor == targetFloor) {
			if (catchedUnUpgrade == true) {
				setMain(unUpgrade);
				catchedUnUpgrade = false;
			} else {
				arrived = true;
			}
		}
		for (i = front; i < rear; i++) {
			if (validation[i] == 0) {
				front++;
			} else {
				break;
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (arrived) {
					if (shutter.getClock() == 2 && catchedUnUpgrade == false && arrived == true) {
						break;
					}
					synchronized (this) {
						this.wait();
						if (mainRequest != null && mainRequest.getTime() > clock) {
							setClock();
						}
					}
					if (front == rear) {
						continue;
					}
				}

				if (currentFloor == targetFloor) {
					long sleepTime = getSleepTime(STILLTIME - PRECISION);
					Thread.sleep(sleepTime);
					clock += STILLTIME;
				} else {
					long sleepTime = getSleepTime(RUNTIME - PRECISION);
					Thread.sleep(sleepTime);
					clock += RUNTIME;
				}

				execute();
				Thread.sleep(PRECISION);
				synchronized (scheduler) {
					scheduler.notify();
				}

			} catch (InterruptedException ex) {
				System.out.println("Elevator syncronized exception");
			}
		}
	}
}
