package lift;


public class Elevator {
	private int floorNow; 
	private double clock; 
	public Elevator() {
		floorNow = 1; 
		clock=0.0;  
	}
	public double move(Request rq) {
		Requester guest = rq.getGt();
		if(guest==Requester.FR) {
			int askfloor = rq.getAf(); 
			int diff = floorNow-askfloor; 
			floorNow = askfloor; 
			if((double)rq.getTime()>clock) {
				clock=(double)rq.getTime(); 
			} 
			clock = 0.5*Math.abs(diff) + clock; 
			if(diff == 0) { 
				System.out.printf("("+floorNow+",STILL,%.1f)",clock+1); 
			} 
			if(diff > 0) { 
				System.out.printf("("+floorNow+",DOWN,%.1f)",clock); 
			} 
			if(diff < 0) { 
				System.out.printf("("+floorNow+",UP,%.1f)",clock); 
			} 
			System.out.println(""); 
			clock++; 
		}else {
			int target = rq.getTg();
			int diff = floorNow-target;
			floorNow = target; 
			if(rq.getTime()>clock) clock=rq.getTime();
			clock = 0.5*Math.abs(diff) + clock;
			if(diff == 0) {
				System.out.printf("("+floorNow+",STILL,%.1f)",clock+1);
			}
			if(diff > 0) {
				System.out.printf("("+floorNow+",DOWN,%.1f)",clock);
			}
			if(diff < 0) {
				System.out.printf("("+floorNow+",UP,%.1f)",clock);
			}
			System.out.println("");
			clock++;
		}
		return clock;
	}
	
}
