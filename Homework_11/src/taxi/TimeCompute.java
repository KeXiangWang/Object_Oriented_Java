package taxi;

import java.awt.Point;

public class TimeCompute {
	/**
	 * @Overview: This Class is only used for compute something about the time.
	 */

	/**
	 * @MODIFIES: None;
	 * @EFFECTS: \result == (start + period - System.currentTimeMillis())>0? start +
	 *           period - System.currentTimeMillis():0;
	 */
	public static long TaxiSleepTime(long start, long period) {
		long t = start + period - System.currentTimeMillis();
		return t >= 0 ? t : 0;
	}

	/**
	 * @REQUIRES: origin!=null; now!= null;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == (according the direction, relative position and the
	 *           light-change gap judge how long to sleep);
	 */
	public static long getWaitTime(Point origin, Point now, int direction, boolean light, long taxiTime) {
		if (direction == 0 || direction == 2 || light == false) {
			return 0;
		}
		if (origin.x == now.x && LightSystem.color == 1 || origin.y == now.y && LightSystem.color == 2) {
			if (direction == 3) {
				return 0;
			}
			if (direction == 1) {
				long t = LightSystem.time + LightSystem.GAP - taxiTime;
				return t > 0 ? t : 0;
			}
		}
		if (origin.x == now.x && LightSystem.color == 2 || origin.y == now.y && LightSystem.color == 1) {
			if (direction == 1) {
				return 0;
			}
			if (direction == 3) {
				long t = LightSystem.time + LightSystem.GAP - taxiTime;
				return t > 0 ? t : 0;
			}
		}
		return 0;
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return true;
	}
}
