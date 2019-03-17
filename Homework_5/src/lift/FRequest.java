package lift;

public class FRequest extends Request {
	private Requester guest;
	private int target;
	private Direction direction;
	private long time;

	public FRequest(Requester a, int b, Direction c, long d, int e) {
		guest = a;
		target = b;
		direction = c;
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

	public Direction getDirection() {
		return direction;
	}

	public String toString() {
		String string;
		string = guest + "," + target + "," + direction;
		return string;
	}
}
