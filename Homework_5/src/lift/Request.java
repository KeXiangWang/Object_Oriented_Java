package lift;

public class Request {
	private Requester guest;
	private int target;
	private Direction direction;
	private int numOfElevator;
	private long time;

	public Request() {
	}

	public Request(Requester a, int b, Direction c, long d, int e, int nop) {
		guest = a;
		target = b;
		direction = c;
		time = d;
	}

	public Request(Requester a, int b, int c, long d, int e) {
		guest = a;
		numOfElevator = b;
		target = c;
		time = d;
	}

	public long getTime() {
		return time;
	}

	public Requester getGuest() {
		return guest;
	}

	public int getTarget() {
		return target;
	}

	public int getNumOfElevator() {
		return numOfElevator;
	}

	public Direction getDirection() {
		return direction;
	}

	public int getMt() {
		return target;
	}
}
