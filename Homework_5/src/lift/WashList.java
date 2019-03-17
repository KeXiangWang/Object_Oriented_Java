package lift;


public class WashList {
	private Request[] list;
	private int[] poslist;
	private int front;
	private int rear;

	public WashList() {
		list = new Request[50];
		poslist = new int[50];
		front = 0;
		rear = 0;
	}
	public int end() {
		if(front==rear) {
			return 1;
		}
		return 0;
	}
	public void add(int i, Request request) {
		poslist[rear] = i;
		list[rear]= request;
		rear++;
	}
	public int geti() {
		return poslist[front];
	}
	public Request getrq() {
		return list[front];
	}
	public void move() {
		front++;
	}
}
