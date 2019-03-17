package lift;

public interface Equipment { 
	public State getState(int a, int b) ; 
	public double predictTime(Request mainRq, Request rq); 
	public double move(Request rq); 
	public void act(Request rq);  
}
