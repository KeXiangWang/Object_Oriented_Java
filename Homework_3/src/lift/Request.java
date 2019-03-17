package lift;


public class Request {
	private Requester guest; 
	private int askfloor; 
	private int target; 
	private Direction direction; 
	private long time; 
	private long ftime; 
	private int motion; 
	private int order; 
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
	public int getFloor() {
		if(guest == Requester.FR)
			return askfloor ;
		else 
			return target;
	}
	public Request(Requester a, int b, Direction c, long d, int e) {
		guest=a; 
		askfloor=b; 
		direction=c; 
		time=d; 
		ftime=0;
		motion=1;
		order=e;
	}
	public Request(Requester a, int b, long d, int e) {
		guest=a; 
		target=b; 
		time=d; 
		ftime=0;
		motion=1;
		order=e;
	}
	public int getOrder() {
		return order;
	}
	public String toString() {
		String string;
		String stime = String.format("%d", time);
		if(guest==Requester.FR) {
			string = guest+","+askfloor+","+direction+","+stime;
			return string;
		} else {
			string = guest+","+target+","+stime;
			return string;
		}
	}
}
