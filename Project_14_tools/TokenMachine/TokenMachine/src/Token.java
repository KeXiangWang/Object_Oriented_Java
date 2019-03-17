
public class Token {
	private ServiceType type;
	private long grantTime;
	private long servingTime;
	private int num_before;
	private TokenStatus status;
	private Integer tid;
	private int wid;
	
	public Token(int id, ServiceType t,int waits){
		tid = new Integer(id);
		type = t;
		grantTime = System.currentTimeMillis();
		num_before = waits;
		status = TokenStatus.waiting;
	}

	public TokenStatus getStatus() {
		// TODO Auto-generated method stub
		return status;
	}
	
	public String toString(){
		long waittime = System.currentTimeMillis()-grantTime;
		String token = "Service "+tid.toString()+ "type:"+type.toString()+"; has waited for "+new Integer((int)(waittime/60000)).toString()+
						" minutes";
		
		return token;
	}

	public ServiceType getServiceType() {
		// TODO Auto-generated method stub
		return type;
	}

	public void Satisfied() {
		// TODO Auto-generated method stub
		status = TokenStatus.served;
	}
	
	public void AssignWindow(int winID){
		wid = winID;
		servingTime = System.currentTimeMillis();
	}

	public int ServingWindow() {
		// TODO Auto-generated method stub
		return wid;
	}
	
	public long WaitingTime(){
		return servingTime - grantTime;
	}
}
