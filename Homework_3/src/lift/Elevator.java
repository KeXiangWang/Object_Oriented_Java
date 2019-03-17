package lift;


enum State{UP,DOWN,STILL}

public class Elevator implements Equipment{
	private int floorNow; 
	private double clock;  
	private Request lastRq;
	private int last; 
	private Request lastRq2;
	private int last2;
	private int diff; 
	public Elevator() {
		floorNow = 1; 
		clock=0.0;  
		last=0;
		lastRq=null;
		last2=0;
		lastRq2=null;
	}
	public double getClock() {
		return clock;
	}
	public int getFloor() { 
		return floorNow ; 
	}
	public State getState(int a, int b) {
		if(a>b) {
			return State.DOWN;
		} else if(a<b) {
			return State.UP;
		} else {
			return State.STILL;
		}
	}
	public double predictTime(Request mainRq, Request rq) {
		double temClock = 0;
		if((double)mainRq.getTime()>clock) { 
			temClock=(double)mainRq.getTime(); 
		} else {
			temClock = clock;
		}
		temClock = temClock+(Math.abs(floorNow-rq.getFloor()))*0.5;
		if(last!=0) {
			if(rq.getFloor()!=floorNow&&lastRq.getFloor()!=rq.getFloor()) {
				temClock++;
			}
		}
		return temClock;
	}
	public double move(Request rq) {
		if((double)rq.getTime()>clock&&rq.getMt()==1) { //mainRq
			clock=(double)rq.getTime(); 
		} 
		if((double)rq.getTime()>clock&&rq.getMt()==0) { //byRq
			if((double)rq.getFt()>clock)
				clock=(double)rq.getFt(); 
		} 
		if(rq.getMt()==0 && last==0) {
			lastRq=rq;
			last=1;
			return clock;
		}else if(rq.getMt()==1 && last==0) {
			act(rq);
			System.out.println(toString(rq));
			clock++;
		}else if(rq.getMt()==0 && last==1) { 
			if(lastRq.getFloor()==rq.getFloor()) {
				if(lastRq.getOrder()<rq.getOrder()) {
					last2=1;
					lastRq2=rq;
					return clock;
				}else {
					last2=1;
					lastRq2=lastRq;
					lastRq=rq;
					return clock;
				}
			}else {
				if(last2==1) {
					act(lastRq);
					System.out.println(toString(lastRq));
					System.out.println(toString(lastRq2));
					last2=0;
					lastRq=rq;
					last=1;
				}
				else {
					act(lastRq);
					System.out.println(toString(lastRq));
					lastRq=rq;
					last=1;
				}
				clock++;
			}
		}else {// if(rq.getMt()==1 && last==1) 
			if(last2==1) {
				if(lastRq.getFloor()==rq.getFloor()) {
					if(lastRq2.getOrder()<rq.getOrder()) {
						act(lastRq);
						System.out.println(toString(lastRq));
						System.out.println(toString(lastRq2));
						System.out.println(toString(rq));
					} else if(lastRq.getOrder()<rq.getOrder()) {
						act(lastRq);
						System.out.println(toString(lastRq));
						System.out.println(toString(rq));
						System.out.println(toString(lastRq2));
					} else{
						act(lastRq);
						System.out.println(toString(rq));
						System.out.println(toString(lastRq));
						System.out.println(toString(lastRq2));
					}
					clock++;
					last=0;
					last2=0;
				} else {
					act(lastRq);
					System.out.println(toString(lastRq));
					System.out.println(toString(lastRq2));
					clock++;
					act(rq); 
					System.out.println(toString(rq));
					clock++;
					last=0;
					last2=0;
				}
			}else {
				if(lastRq.getFloor()==rq.getFloor()) {
					if(lastRq.getOrder()>rq.getOrder()) {
						act(rq);
						System.out.println(toString(rq));
						System.out.println(toString(lastRq));
					} else {
						act(lastRq);
						System.out.println(toString(lastRq));
						System.out.println(toString(rq));
					}
					clock++;
					last=0;
				}else {
					act(lastRq);
					System.out.println(toString(lastRq));
					clock++;
					act(rq);
					System.out.println(toString(rq));
					clock++;
					last=0;
				}
				
			}
		}
		return clock;
	}
	public void act(Request rq) {
		int askfloor = rq.getFloor(); 
		diff = floorNow-askfloor; 
		floorNow = askfloor; 
		clock = 0.5*Math.abs(diff) + clock; 
	}
	public String toString(Request rq) {
		String rqori =rq.toString();
		if(diff == 0) { 
			return "["+rqori+"]/"+"("+floorNow+String.format(",STILL,%.1f)", clock+1);
		} else if(diff > 0) { 
			return "["+rqori+"]/"+"("+floorNow+String.format(",DOWN,%.1f)", clock);
		} else{ 
			return "["+rqori+"]/"+"("+floorNow+String.format(",UP,%.1f)", clock);
		} 
	}
}
