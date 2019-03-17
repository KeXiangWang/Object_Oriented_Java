package lift;

public class Request {
	private Requester guest; 
	private int askfloor; 
	private int target; 
	private Direction direction; 
	private long time; 
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
	public Request(Requester a, int b, Direction c, long d) {
		guest=a; 
		askfloor=b; 
		direction=c; 
		time=d; 
	}
	public Request(Requester a, int b, long d) {
		guest=a; 
		target=b; 
		time=d; 
	}
}
