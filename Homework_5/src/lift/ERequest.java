package lift;

public class ERequest extends Request {
	private Requester guest;
	private int target;
	private int numOfElevator;
	private long time;

	public ERequest(Requester a, int b, int c, long d, int e) {
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

	public String toString() {
		String string;
		string = guest + ",#" + numOfElevator + "," + target;
		return string;
	}
}
