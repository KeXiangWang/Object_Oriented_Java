
public class ServiceWindow {
/*@Overview: ServiceWindow is the window offering services to customer who holds a matched token. Each service window has 
 * unique identifier, and the type of service is predefined. It manages the status of serving, and records the token in serving,
 * and the number of tokens served from the beginning, and the total service time for all tokens.*/
	private int winID;
	private ServiceType type;
	private int tokensServed;
	private long servingTime; //records the length of time of serving
	private long openTimePoint;//records the time when the servingToken was served 
	private ServiceWindowStatus status;
	Token servingToken;
	TokenMachine t_machine;
	
	public ServiceWindow(int wID, ServiceType t,TokenMachine tm){
	//Requires: tm is not null
		winID = wID;
		type = t;
		tokensServed = 0;
		servingTime=0;
		openTimePoint = 0;
		servingToken = null;
		status = ServiceWindowStatus.closed;
		t_machine = tm;
		
	}
	
	public int RequestsServed(){
		return tokensServed;
	}
	
	public void Open(){
		if (status == ServiceWindowStatus.closed) status = ServiceWindowStatus.idle;
		servingTime = System.currentTimeMillis();
	}
	
	public boolean Close(){
		if (status == ServiceWindowStatus.idle){
			status = ServiceWindowStatus.closed;
			servingTime += System.currentTimeMillis()-openTimePoint;
			return true;
		}
		return false;
	}
	
	public boolean Serve(Token token){
	/**@Requires: token£¡=null && token.type == this.type
	    *@Modifies: this
	    *@Effects: \result==false ==> \old(this).status != idle;
	                        \result == true ==> \old(this).status == idle && this.status == serving && this.RequestsServed == (\old(this).RequestsServed+1) && \invoked(this.Serving) */
		if (status == ServiceWindowStatus.idle){
			servingToken = token;
			tokensServed ++;
			status = ServiceWindowStatus.serving;
			Serving();
			return true;
		}
		return false;
	}
	
	private void Serving(){
		System.out.println("Is Serving:" + servingToken.toString());
	}
	
	public Token FinishCurrService(){
	//Requires: none
	//Modifies: this
	//Effects: returns null if this is not in serving status, otherwise returns the served token, changes the status to idle,
	//         and notify TokenMachine that the current token has been satisfied.
		if(status != ServiceWindowStatus.serving)return null;
		else{
			status = ServiceWindowStatus.idle;
			t_machine.tokenSatisfied(servingToken);
			System.out.println("Serving Finished for:" + servingToken.toString());
			return servingToken;
		}	
	}
	
	public ServiceType getServiceType(){
		return type;
	}

	public ServiceWindowStatus getStatus() {
		// TODO Auto-generated method stub
		return status;
	}

	public int getID() {
		// TODO Auto-generated method stub
		return winID;
	}
	
	public long getTotalServingTime(){
		if(servingTime == 0) servingTime = System.currentTimeMillis()-openTimePoint;
		return servingTime;
	}
}
