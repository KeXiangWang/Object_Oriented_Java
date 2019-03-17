package lift;
import java.util.Scanner;

enum Direction{UP,DOWN}
enum Requester{FR,ER}

public class Scheduler {
	private Elevator elevator;
	private Queue queue;
	private double clock;
	private Request lastRq;
	public Scheduler(){
		elevator = new Elevator();
		queue = new Queue();
		lastRq = null;
		clock = 0.0;
	}
	
	public void run() {
		while(!queue.end()) {
			command();
		}
	}
	
	public void command() {
		Request rq=schedule();
		if(rq!=null) {
			clock = elevator.move(rq);
			lastRq = rq;
		}

	}
	
	public Request schedule() {
		Request rq; 
		if(!queue.end()) {
			queue.wash(lastRq, clock);
		}
		while(!queue.end() && queue.frontVal()==false ) {
			if(queue.frontRq().getGt()==Requester.FR)
				System.out.println("#Invalid Request");
			else
				System.out.println("#Invalid Request");
			queue.moveFront(1);
		}
		rq= queue.frontRq();
		queue.moveFront(1); 
		return rq;
	}
	
	public void parse(String str) {
		int wrong;
		try {
			wrong = queue.parseRq(str);
			if(wrong==0) ;
        	if(wrong==1) System.out.println("ERROR\n#Wrong Input"); 
        	if(wrong==3) System.out.println("ERROR\n#The first request must start from relative time 0");
        	if(wrong==5) System.out.println("ERROR\n#There is no DOWN button ont the Floor 1");
        	if(wrong==6) System.out.println("ERROR\n#There is no UP button ont the Floor 10");
        	if(wrong==7) System.out.println("ERROR\n#Time Never Goes Back");
		}catch(Exception e) {
			System.out.println("ERROR\n#Wrong Input");
		}
	}
	
	public static void main(String args[]) {
		try{
			Scheduler scheduler = new Scheduler();
			Scanner in =  new Scanner(System.in);
			int rqNum=0;
			while(in.hasNextLine()){    
				rqNum++; 
	            String str = in.nextLine();
	            String str2 = str.replaceAll(" ", ""); 
	            if(str2.equals("RUN") || rqNum>=101) {
	            	scheduler.run();
	            	break;
	            }else{
	            	scheduler.parse(str2);
	            }
	        }
			in.close();
		} catch (Throwable e) {
			System.out.println("ERROR\n#Wrong");
		}
	}
}
