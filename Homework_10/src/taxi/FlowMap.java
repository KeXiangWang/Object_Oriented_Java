package taxi;

import java.util.HashMap;

public class FlowMap {
	/**
	 * @Overview: this class include all the flow on the roads. And provide the
	 *            method to add and get it according to the input time.
	 */
	private static HashMap<String, FlowRecord> flowMap = new HashMap<String, FlowRecord>();

	/**
	 * @MODIFIES: None;
	 * @EFFECTS: \result = "" + x1 + "," + y1 + "," + x2 + "," + y2;
	 */
	private static String getKey(int x1, int y1, int x2, int y2) {// 生成唯一的Key
		return "" + x1 + "," + y1 + "," + x2 + "," + y2;
	}

	/**
	 * @MODIFIES:this.flowMap;
	 * @EFFECTS: (flowMap.get(getKey(x1, y1, x2, y2))!=null)
	 *           ==>(flowMap.contains(flowMap.get(getKey(x1, y1, x2,
	 *           y2)))&&flowMap.contains(flowMap.get(getKey(x2, y2, x1, y1))););
	 *           (flowMap.get(getKey(x1, y1, x2, y2))==null) ==>
	 *           (flowMap.get(getKey(x1, y1, x2, y2)) = \old(flowMap.get(getKey(x1,
	 *           y1, x2, y2))) + num);
	 * @THREAD_REQUIRES \locked(this);
	 * @THREAD_EFFECTS: \locked(this);
	 */
	public static synchronized void addFlow(int x1, int y1, int x2, int y2, long time, int num) {
		FlowRecord record = flowMap.get(getKey(x1, y1, x2, y2));
		if (record == null) {
			flowMap.put(getKey(x1, y1, x2, y2), (new FlowRecord(time, num)));
			flowMap.put(getKey(x2, y2, x1, y1), (new FlowRecord(time, num)));
		} else {
			record.add(time, num);
			FlowRecord record2 = flowMap.get(getKey(x2, y2, x1, y1));
			record2.add(time, num);
		}
	}

	/**
	 * @MODIFIES: None;
	 * @EFFECTS: \result == flowMap.get(getKey(x1, y1, x2, y2))==null? 0 :
	 *           flowMap.get(getKey(x1, y1, x2, y2)).count(now);
	 * @THREAD_REQUIRES \locked(\this);
	 * @THREAD_EFFECTS: \locked(this);
	 */
	public static synchronized int getFlow(int x1, int y1, int x2, int y2, long now) {
		FlowRecord record = flowMap.get(getKey(x1, y1, x2, y2));
		if (record == null) {
			return 0;
		} else {
			return record.count(now);
		}

	}
/**
		 * @REQUIRES:None;
		 * @MODIFIES:None;
		 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
		 */
	public boolean repOK() {
		return (flowMap != null);
	}
}
