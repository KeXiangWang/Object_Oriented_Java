package taxi;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class LightSystem implements Runnable {
	/**
	 * @Overview: The LightSystem record the color of all the light, and change it
	 *            every once in a while.
	 */
	private ArrayList<Point> lightList;
	private TaxiGUI taxiGUI;
	protected static long time;
	protected static int color;
	protected static long GAP;

	/**
	 * @REQUIRES:lightList!= null;taxiGUI!=null;
	 * @MODIFIES:this.lightList;this.taxiGUI;LightSystem.color;
	 * @EFFECTS: this.taxiGUI == taxiGUI; (\all point p in this.lightList;
	 *           PositionCompute.countDirection(p.x, p.y) <= 2); LightSystem.color ==
	 *           (new Random()).nextInt(2) + 1;
	 */
	public LightSystem(ArrayList<Point> lightList, TaxiGUI taxiGUI) {
		// this.lightList = lightList;
		this.lightList = new ArrayList<Point>();
		for (Point point : lightList) {
			if (PositionCompute.countDirection(point.x, point.y) <= 2) {
				System.out.println("Wrong Light Position (" + point.x + "," + point.y + ")");
			} else {
				this.lightList.add(point);
			}
		}

		this.taxiGUI = taxiGUI;
		LightSystem.color = (new Random()).nextInt(2) + 1;
	}

	/**
	 * @REQUIRES:point!=null;
	 * @MODIFIES:None;
	 * @EFFECTS:\result == lightList.contains(point);
	 */
	public boolean getLight(Point point) {
		return lightList.contains(point);
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:\result == time;
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 * @THREAD_REQUIRES \locked(lightList);
	 * @THREAD_EFFECTS: \locked(lightList);
	 */
	@Override
	public void run() {
		try {
			time = TaxiSys.MAINTIME;
			for (Point light : lightList) {
				taxiGUI.SetLightStatus(light, color);
			}
			while (true) {
				long sleepTime = TimeCompute.TaxiSleepTime(time, GAP);
				Thread.sleep(sleepTime);
				time += GAP;
				synchronized (lightList) {
					color = 3 - color;
					for (Point light : lightList) {
						taxiGUI.SetLightStatus(light, color);
					}
				}
			}
		} catch (InterruptedException e) {
			System.out.println("LightSystem sleep wrong");
		} catch (Exception e) {
			System.out.println("LightSystem ERROR");
		}
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return (this.lightList != null);
	}
}
