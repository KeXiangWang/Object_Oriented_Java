import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class TokenMachine {
/*overview: TokenMachine manages the services offered by ServiceWindow to Customer in terms of tokens. 
 * Each Customer has to request a token to use service. Each Token represents a request of service.  
 * All tokens are managed in queues according to the type of services requested. For each of the requested tokens, 
 * TokenMachine uses the FIFO mode to dispatch whenever a matching service window is ready to serve. 
 * Service window can plug or unplug itself to the TokenMachine to offer or stop its services. 
 * In general, data managed in this class can be defined as (waitingTokenList_Saving, waitingTokenList_Credit, sv_winList, servingTokenList, servedTokenList).
 * 
 * Please note that the following is just for implementing, not for using this class. 
 * Abstract Function can be defined as:
 * AF(tm) = (waitingTokenList_Saving, waitingTokenList_Credit, sv_winList, servedTokenList), where
 * waitingTokenList_Saving=tm.waitingSavingServiceTokens, waitingTokenList_Credit=tm.waitingCreditServiceTokens,
 * sv_winList = tm.pluggedWindows, servedTokenList=tm.servedServiceTokens.
 * The invariant of TokenMachine is:
 * I(tm)=tm.waitingSavingServiceTokens <> null && tm.waitingCreditServiceTokens <> null && tm.servedServiceTokens <> null
 *       && tm.pluggedWindows <> null 
 *       && (for any token in tm.waitingCreditServiceTokens or tm.waitingSavingServiceTokens, token.status <> served)
 *       && (for any token in tm.servedServiceTokens, token.status == served)
 *       && (for any sw in tm.pluggedWindows, sw.status <> closed)
 *       && (tm.num_savingSW == size of the set {sw in tm.pluggedWindows|sw.type == savingService})
 *       && (tm.num_creditSW == size of the set {sw in tm.pluggedWindows|sw.type == creditService})
 *       && (if any token with status changed into served, should be removed from tm.waitingCreditServiceTokens or tm.waitingSavingServiceTokens,
 *           and managed in tm.servedServiceTokens)
 *       && (for any token t1 and t2 in tm.waitingCreditServiceTokens or tm.waitingSavingServiceTokens, t1 <> t2)
 *       && (for any token t1 and t2 in tm.servedServiceTokens, t1 <> t2)
 *       && (for any service window sw1 and sw2 in tm.pluggedWindows, sw1 <> sw2)
 *       && (for any token t1 in tm.servedServiceTokens, and any token t2 in tm.servingTokens, t1.ID < t2.ID if t1.wid == t2.wid)
 *       && (for any token t1 in tm.servingTokens, and any token t2 in tm.waitingCreditServiceTokens or tm.waitingSavingServiceTokens,
 *          t1.ID < t2.ID)
 *          
  
*/
//rep:
	private Queue<Token> waitingSavingServiceTokens;
	private Queue<Token> waitingCreditServiceTokens;
	private Queue<Token> servingTokens;
	private Queue<Token> servedServiceTokens;
	private Vector<ServiceWindow> pluggedWindows;
	private int count;
	private int num_savingSW;
	private int num_creditSW;
	
	public TokenMachine(){
	/**@Requires: none
	    *@Modifies: this
	    *@\this != NULL.	*/
		waitingSavingServiceTokens = new LinkedList<Token>();
		waitingCreditServiceTokens = new LinkedList<Token>();
		servedServiceTokens = new LinkedList<Token>();
		pluggedWindows = new Vector<ServiceWindow>();
		servingTokens = new LinkedList<Token>();
		
		count = 0;
		num_savingSW = 0;
		num_creditSW = 0;
	}
	
	synchronized public Token requestService(ServiceType type)
	//this method is for Customer to request a service token, and wait into the queue.
	/**@Requires: none
	    *@Modifies: this
	    *@Effects: \result!=NULL ==> (\result.type == type) && (\exist ServiceWindow sw; sw.type == type && pluggedWindows.contains(sw)) && (\result.id >  waitingSavingServiceTokens.lastElement.id ||\result.id >  waitingCreditServiceTokens.lastElement.id );
	                        \result == NULL ==> (\all ServiceWinow sw; sw.type == type && pluggedWindows.contains(sw)==false)*/
	{
		int waits = 0;
		Token token = null;
		Queue<Token> queue;
		int num=0;
		
		if(type == ServiceType.creditService){
			queue = waitingCreditServiceTokens;
			num = num_creditSW;
		}
		else{
			queue = waitingSavingServiceTokens;
			num = num_savingSW;
		}
		
		if(num > 0){
			waits = queue.size();
			count++;
			token = new Token(count,type, waits);
			queue.add(token);
		}
		
		return token;
	}
	
	public boolean anyWaitingService(){
	/**@Requires: none
	    *@Modifies: none
	    *@Effects: \result == waitingSavingServiceTokens.size > 0 || waitingCreditServiceTokens.size >0*/
		return (!waitingSavingServiceTokens.isEmpty()||!waitingCreditServiceTokens.isEmpty());
	}
	
	synchronized public void plugServiceWindow(ServiceWindow win){
	//this method is for ServiceWindow to plug itself.
	/**@Requires: win!=NULL
	    *@Modifies: this
	    *@Effects: pluggedWindows.contains(win);
		                   win.type == savingService ==>(num_savingSW == \old(num_savingSW)+1);
						   win.type == creditService ==>(num_creditSW == \old(num_creditSW)+1);*/
		if(!pluggedWindows.contains(win))pluggedWindows.addElement(win);
		if(win.getServiceType()==ServiceType.savingService)num_savingSW++;
		if(win.getServiceType()==ServiceType.creditService)num_creditSW++;
	}
	
	synchronized public void unPlugServiceWindow(ServiceWindow win){
    /**@Requires: win!=NULL
	    *@Modifies: this
	    *@Effects: pluggedWindows.contains(win)==false;
		                   win.type == savingService ==>(num_savingSW == \old(num_savingSW)-1);
						   win.type == creditService ==>(num_creditSW == \old(num_creditSW)-1);*/
		if(pluggedWindows.contains(win)){
			if(win.getServiceType()==ServiceType.savingService)num_savingSW--;
			if(win.getServiceType()==ServiceType.creditService)num_creditSW--;
			pluggedWindows.remove(win);
		}
	}
	
	synchronized public void serveNext(ServiceWindow win){
	//this method is for scheduling thread to select an idle window to assign token to serve.
	/**@Requires: win!=NULL && win.status == idle
	    *@Modifies: this
	    *@Effects: (win.type == creditService && \old(waitingCreditServiceTokens).size > 0) ==> (\exist Token t;  \old(waitingCreditServiceTokens).contains(t) &&  \waitingCreditServiceTokens.contains(t) == false 
		                    && t.type == win.type && servingTokens.contains(t) && t.ServingWindow == win.id) && (\all Token t1;  (servingTokens.contains(t1) && t != t1) ==> t1.ServingWindow ! = win.id) && win.Serve(t) == true;
							(win.type == savingService && \old(waitingSavingServiceTokens).size > 0) ==> (\exist Token t;  \old(waitingSavingServiceTokens).contains(t) &&  \waitingSavingServiceTokens.contains(t) == false 
		                    && t.type == win.type && servingTokens.contains(t) && t.ServingWindow == win.id) && (\all Token t1;  (servingTokens.contains(t1) && t != t1) ==> t1.ServingWindow ! = win.id) && win.Serve(t) == true;
    */
		Token token;
		Queue<Token> queue;
		Iterator<Token> iter=null;
		
		if(win.getStatus() != ServiceWindowStatus.idle)return;
		
		if(win.getServiceType()==ServiceType.creditService)
			queue = waitingCreditServiceTokens;
		else
			queue = waitingSavingServiceTokens;
		
		//here the chance to remove lost tokens.
		iter = servingTokens.iterator();
		while(iter.hasNext()){
			token = iter.next();
			if(token.ServingWindow()==win.getID())  //lost token
				servingTokens.remove(token);
		}
		
		token = queue.poll();  //token will not be managed in the queue any way.
		if(token != null){
			servingTokens.add(token);
			token.AssignWindow(win.getID());
			win.Serve(token);
		}
	}
	
	synchronized public void tokenSatisfied(Token token){
	//this method is for Service Window to call whenever a token has been satisfied	
	/**@Requires: token != NULL
	    *@Modifies: this, token
	    *@Effects: \old(servingTokens).contains(token) ==> token.status == served && servedServiceTokens.contains(token) && servingTokens.contains(token) == false;
	*/
		if(servingTokens.contains(token)){
			token.Satisfied();
			servedServiceTokens.add(token);
			servingTokens.remove(token);
		}
	}
	
	public ServiceWindow pickupWindow(){
	//this method is for scheduling thread to pick up service window to serve customers.	
	/**@Requires: none.
	    *@Modifies: none.
	    *@Effects: \result!=NULL ==> \result.status == idle	*/
		ServiceWindow sw;
		int i=0;
		for(i=0;i<pluggedWindows.size();i++){
			sw = pluggedWindows.get(i);
			if(sw.getStatus()==ServiceWindowStatus.idle)return sw;
		}
		return null;
	}
	
	public int numTokensRequested(){
		return count;
	}
	
	public int numTokensLost(){
		return count - servedServiceTokens.size();
	}
	
	public int numTokensSatisfied(ServiceType type){
		/**@Requires: none.
	    *@Modifies: none.
	    *@Effects: \exist List<Token> tlist; \all Token t; tlist.contains(t) && t.status == served; t.type == type && servedServiceWindows.contains(t)==>\result=tlist.size	*/
		int num=0;
		Token token;
		Iterator<Token> t = servedServiceTokens.iterator();
		while(t.hasNext()){
			token = t.next();
			if(type == token.getServiceType())num++;
		}
		return num;
	}
	
	public long totalWaitingTime(){
		/**@Requires: none.
	    *@Modifies: none.
	    *@Effects: \result==\sum(servedServiceTokens.token.WaitingTime)*/
		long len=0;
		Token token;
		Iterator<Token> t = servedServiceTokens.iterator();
		while(t.hasNext()){
			token = t.next();
			len += token.WaitingTime();
		}
		return len;
	}
}
