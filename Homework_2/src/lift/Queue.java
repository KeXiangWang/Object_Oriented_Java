package lift;

public class Queue {
	private static final int MAX = 200;
	private Request[] rqList;
	private Boolean[] validity;
	private int front;
	private int rear;
	public Queue() {
		rqList = new Request[MAX];
		validity = new Boolean[MAX];
		front = 0;
		rear = 0;
	}
	public Request frontRq() {
 		return rqList[front];
	}
	public boolean frontVal() {
		return validity[front];
	}
	public void moveFront(int n) {
		front = front + n;
	}
	public boolean end() {
		if(front>=rear) return true;
		else return false;
	}
	public void wash(Request lastRq, double clock) { 
		int i; 
		for(i = front; i<rear && rqList[i].getTime()<=clock; i++) { 
			if(validity[i]==true && lastRq!=null ) { 
				if(lastRq.getGt()==rqList[i].getGt()) { 
					if(lastRq.getGt()==Requester.FR) { 
						if(lastRq.getAf()==rqList[i].getAf() && lastRq.getDr()==rqList[i].getDr()) validity[i]=false; 
					}else { 
						if(lastRq.getTg()==rqList[i].getTg()) validity[i]=false; 
					}

				}
			}
		}
	}
	public int  parseRq(String str) throws Exception{ 
		Requester from; 
		int askfloor; 
		int target; 
		Direction direction; 
		long time; 
		String[] strs=null; 
		try {
			if(str.equals("")) return 0;
			if(str.matches( "((\\(FR,\\+?[0-9]{1,50},((DOWN)|(UP)),\\+?[0-9]{1,50}\\))|(\\(ER,\\+?[0-9]{1,50},\\+?[0-9]{1,50}\\)))" )) {
				strs=str.split("[\\(\\),]"); 
				if(strs.length==5) { 
					from = Requester.FR; 
					askfloor = Integer.parseInt(strs[2]); 
					direction = strs[3].equals("UP")? Direction.UP:Direction.DOWN; 
					time = Long.parseLong(strs[4]); 
					if(askfloor<1||askfloor>10)						return 1; 
					if(time<0||time>4294967295L) 					return 1; 
					if(rear==0&&time!=0L) 							return 3; 
					if(askfloor==1&&direction==Direction.DOWN) 		return 5; 
					if(askfloor==10&&direction==Direction.UP) 		return 6; 
					if(rear!=0&&time<rqList[rear-1].getTime())		return 7; 
					validity[rear] = true; 
					rqList[rear++] = new Request(from, askfloor, direction, time); 
				} else{ 
					from = Requester.ER; 
					target = Integer.parseInt(strs[2]); 
					time = Long.parseLong(strs[3]); 
					if(target<1||target>10)							return 1; 
					if(time<0||time>4294967295L) 					return 1; 
					if(rear==0&&time!=0L) 							return 3; 
					if(rear!=0&&time<rqList[rear-1].getTime())		return 7; 
					validity[rear] = true; 
					rqList[rear++] = new Request(from, target, time); 
				} 
				return 0; 
			} else { 
				return 1; 
			}
		} catch(Exception e) {
			return 1;
		}
	}
}