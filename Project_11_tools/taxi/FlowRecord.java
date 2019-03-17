package taxi;

import java.util.ArrayList;

public class FlowRecord {
	/**
	 * @Overview: FlowRecord record every passed time for the road, which could use
	 *            to get the road's flow. And it provide the method to add and
	 *            count.
	 */
	private ArrayList<Long> stamps;
	private ArrayList<Integer> times;

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:this.stamps;this.times;
	 * @EFFECTS: stamps == new ArrayList<Long>() && stamps.get(0)==time; times ==
	 *           new ArrayList<Integer>() && times.get(0) == num;
	 */
	public FlowRecord(long time, int num) {
		stamps = new ArrayList<Long>();
		times = new ArrayList<Integer>();
		stamps.add(time);
		times.add(num);
	}

	/**
	 * @MODIFIES: None;
	 * @EFFECTS: (\old(stamps.get(n - 1)==long))==>(times.get(n -
	 *           1)==\old(times.get(n - 1))+=num); (\old(stamps.get(n -
	 *           1)==long))==>(stamps.get(n - 1)==long)&&(times.get(n - 1)==num);
	 * @THREAD_REQUIRES \locked(\this);
	 * @THREAD_EFFECTS: \locked(this);
	 */
	public synchronized void add(long time, int num) {
		int n = stamps.size();
		if (stamps.get(n - 1) < time) {
			stamps.add(time);
			times.add(num);
		} else {
			int count = times.get(n - 1);
			times.set(n - 1, count + num);
		}
	}

	/**
	 * @MODIFIES: None;
	 * @EFFECTS: (\result == count) ==> (\all int i, count = 0; 0<=i<stamps.size();
	 *           ((stamps.get(i)>= now-500) &&
	 *           stamps.get(i)!=now)==>count+=times.get(i));
	 * @THREAD_REQUIRES \locked(\this);
	 * @THREAD_EFFECTS: \locked(this);
	 */
	public int count(long now) {
		int count = 0;
		long start = now - 500;
		for (int i = stamps.size() - 1; i >= 0; i--) {
			if (stamps.get(i) >= start && stamps.get(i) != now) {
				count += times.get(i);
			}
			if (stamps.get(i) < start) {
				break;
			}
		}
		return count;
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return (stamps != null && times != null);
	}
}
