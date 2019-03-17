package lift;
import java.util.Scanner;


public class Scheduler { 
	private double clock; 
	private Request lastRq;
	public Scheduler(){ 
		lastRq = null; 
		clock = 0.0; 
	}
	
	public void command(Queue queue, Elevator elevator) {
		Request rq=schedule(queue,elevator);
		if(rq!=null) {
			clock = elevator.move(rq);
			lastRq = rq;
		}
	}
	
	public Request schedule(Queue queue, Elevator elevator) {
		Request rq; 
		if(!queue.end()) {
			queue.wash(lastRq, clock);
		}
		while(!queue.end() && queue.frontVal()==0 ) { 
			if(queue.frontRq(lastRq, elevator).getGt()==Requester.FR)
				System.out.println("Invalid Request");
			else
				System.out.println("Invalid Request");
			queue.moveFront(1);
		}
		rq= queue.frontRq(lastRq, elevator);
		queue.moveFront(1); 
		return rq;
	}

	
	public static void main(String args[]) {
		try{
			SubScheduler scheduler = new SubScheduler();
			Scanner in =  new Scanner(System.in);
			Queue queue = new Queue();
			Elevator elevator = new Elevator();
			int rqNum=0;
			while(in.hasNextLine()){    
				rqNum++; 
	            String str = in.nextLine();
	            String str2 = str.replaceAll(" ", ""); 
	            if(str2.equals("RUN") || rqNum>=101) {
	            	while(!queue.end()) {
	        			scheduler.command(queue, elevator);
	        		}
	            	break;
	            }else{
	            	queue.parse(str2);
	            }
	        }
			in.close();
		} catch (Throwable e) {
			System.out.println("#ERROR");
		}
	}
}
