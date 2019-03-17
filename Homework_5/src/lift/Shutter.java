package lift;

public class Shutter {
	private int shutterClock;

	public Shutter() {
		shutterClock = 0;
	}

	public int getClock() {
		return shutterClock;
	}

	public void setClock(int n) {
		shutterClock = n;
	}
}
