package ifttt;

public class Shutter {
	private int state;
	public Shutter() {
		state = 0;
	}
	public void setState(int n) {
		state = n;
	}
	public int getState() {
		return state;
	}

}
