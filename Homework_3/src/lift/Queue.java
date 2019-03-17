package lift;

enum Requester{FR,ER}
enum Direction{UP,DOWN}
public class Queue {
	private static final int MAX = 200;
	private Request[] rqList;
	private int[] validity;
	private int front;
	private int rear;
	private Request unhandle; 	
	private Request mainRq; 
	private int unhandlePosition;
	public Queue() {
		rqList = new Request[MAX];
		validity = new int[MAX];
		front = 0;
		rear = 0;
		unhandle = null;
		mainRq = null;
		unhandlePosition = -1;
	}
	public Request getMain() {
		return mainRq; 
	}
	public Request frontRq(Request lastRq, Elevator elevator) {
		if(validity[front]==0) {
			return rqList[front];
		} else if(validity[front]==2) {
			return rqList[front]; 
		} else {
			int i;
			Request nearRequest =null;
			int nearPosition = -1;
			mainRq = rqList[front];
			double newClock = elevator.predictTime(mainRq, mainRq); 
			for(i = front+1;i<rear;i++) {
				if(rqList[i].getTime()<=newClock+1&&judgeSame(mainRq, rqList[i])==1) {
					validity[i]=0;
					continue;
				}
				if(rqList[i].getTime()>=newClock) break;
				if(validity[i]==0) continue;
				if(validity[i]==2) continue;
				if(judgeSame(mainRq, rqList[i])==1) {
					validity[i]=0;
					continue;
				}
				int judgeResult = judge(mainRq, rqList[i], elevator);
				if(judgeResult ==1) {
					if(nearPosition==-1) {				//BEST?
						if(lastRq.getMt()==0&&Math.abs(lastRq.getFloor()-elevator.getFloor())>Math.abs(rqList[i].getFloor()-elevator.getFloor()))
							continue;
						else {
							nearRequest=rqList[i];
							nearPosition=i;
						}
					}else {
						if(Math.abs(nearRequest.getFloor()-elevator.getFloor())>Math.abs(rqList[i].getFloor()-elevator.getFloor())) {
							if(lastRq.getMt()==0&&Math.abs(lastRq.getFloor()-elevator.getFloor())>Math.abs(rqList[i].getFloor()-elevator.getFloor()))
								continue;
							else {
								nearRequest=rqList[i];
								nearPosition=i;
							}
						}
					}
				}else if(judgeResult ==2){
					if(unhandlePosition==-1) {
						unhandle = rqList[i];
						unhandlePosition = i;
					}
				}else {
					continue;
				}
			}
			if(nearRequest==null) {
				front++;
				while(validity[front]==2)
					front++;
				if(unhandlePosition!=-1) {
					justify();
					unhandlePosition=-1;
				}
				return mainRq;
			}else {
				rqList[nearPosition].setMt(0);
				rqList[nearPosition].setFt(mainRq.getTime());
				validity[nearPosition]=2;
				nearPosition=-1;
				return nearRequest;
			}

		}
	}

	public int judge(Request mainRq, Request rq, Elevator elevator) {
		double newClock = elevator.predictTime(mainRq, rq); 
		State state = elevator.getState(elevator.getFloor(),mainRq.getFloor());
		Direction direction;
		if(state == State.STILL) return 0;
		else if(state == State.UP) direction = Direction.UP;
		else direction = Direction.DOWN;
		if(rq.getGt()==Requester.FR) {
			if(direction!=rq.getDr()) return 0;
			if(rq.getFloor()<=mainRq.getFloor() && rq.getFloor()>elevator.getFloor() && state==State.UP) {
				if(newClock>rq.getTime()) {
					return 1;
				}else {
					return 0;
				}
			}else if(rq.getFloor()>=mainRq.getFloor() && rq.getFloor()<elevator.getFloor() && state==State.DOWN){
				if(newClock>rq.getTime()) {
					return 1;
				}else {
					return 0;
				}
			}else {
				return 0;
			}
		}else {
			if(rq.getFloor()<=mainRq.getFloor() && rq.getFloor()>elevator.getFloor()  && state==State.UP ) {
				if(newClock>rq.getTime()) {
					return 1;
				}else {
					return 0;
				}
			}else if(rq.getFloor()>=mainRq.getFloor() && rq.getFloor()<elevator.getFloor() && state==State.DOWN){
				if(newClock>rq.getTime()) {
					return 1;
				}else {
					return 0;
				}
			} else if(rq.getFloor()>elevator.getFloor() && state==State.UP) {
				return 2;
			} else if(rq.getFloor()<elevator.getFloor() && state==State.DOWN) {
				return 2;
			} else {
				return 0;
			}
		}
	}
	public void justify() {
		int i;
		int temp = validity[unhandlePosition]; 
		for(i=unhandlePosition;i>front;i--) {
			validity[i]=validity[i-1];
			rqList[i]=rqList[i-1];
		}
		validity[front]=temp;
		rqList[front]=unhandle;
	}
	
	public int tryUnhandle() {
		return unhandlePosition;
	}
	public Request getUnhandle() {
		return unhandle;
	}
	
	public int frontVal() {
		return validity[front];
	}
	public void moveFront(int n) { 
		front = front + n;
	}
	public boolean end() {
		if(front>=rear) return true;
		else return false;
	} 
	public int judgeSame(Request A, Request B) {
		if(A.getGt()==B.getGt()) { 
			if(A.getGt()==Requester.FR) { 
				if(A.getAf()==B.getAf() && A.getDr()==B.getDr() && B.getTime()>=A.getTime()) {
					return 1;
				}
			}else { 
				if(A.getTg()==B.getTg() && B.getTime()>=A.getTime()) {
					return 1;
				}
			}
		}
		return 0;
	}
	public void wash(Request lastRq, double clock) { 
		int i; 
		for(i = front; i<rear && rqList[i].getTime()<=clock; i++) { 
			if(validity[i]==1&& lastRq!=null ) { 
				if(lastRq.getGt()==rqList[i].getGt()) { 
					if(lastRq.getGt()==Requester.FR) { 
						if(lastRq.getAf()==rqList[i].getAf() && lastRq.getDr()==rqList[i].getDr() && rqList[i].getTime()>=lastRq.getTime()) {
							validity[i]=0; 
						}
					}else { 
						if(lastRq.getTg()==rqList[i].getTg() && rqList[i].getTime()>=lastRq.getTime()) {
							validity[i]=0; 
						}
					}
				}
			}
		}
	}	
	
	public void parse(String str) {
		int wrong;
		try {
			wrong = parseRq(str);
			if(wrong==0) ; 
			else {
				System.out.println("INVALID["+str+"]");
			}
		}catch(Exception e) {
			System.out.println("INVALID["+str+"]");
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
					validity[rear] = 1; 
					rqList[rear] = new Request(from, askfloor, direction, time,rear); 
					if(rear==0&&!rqList[rear].toString().equals("FR,1,UP,0")){
						validity[rear] = 0;
						return 1;
					}
					rear++;
				} else{ 
					from = Requester.ER; 
					target = Integer.parseInt(strs[2]); 
					time = Long.parseLong(strs[3]); 
					if(target<1||target>10)							return 1; 
					if(time<0||time>4294967295L) 					return 1; 
					if(rear==0&&time!=0L) 							return 3; 
					if(rear!=0&&time<rqList[rear-1].getTime())		return 7; 
					validity[rear] = 1; 
					rqList[rear] = new Request(from, target, time,rear); 
					if(rear==0&&!rqList[rear].toString().equals("FR,1,UP,0")){
						validity[rear] = 0;
						return 1;
					}
					rear++;
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