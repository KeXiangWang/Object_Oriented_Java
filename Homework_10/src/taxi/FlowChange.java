package taxi;

public class FlowChange {
	/**
	 * @Overview: FlowChange record a flow-set when initializing, and able to set
	 *            this flow-set to the flows counting class. A flow-set consist its
	 *            position and it value to set.
	 */
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int value;

	/**
	 * @MODIFIES: this.x1; this.y1; this.x2; this.y2; this.value;
	 * @EFFECTS: this.x1 == x1; this.y1 == y1; this.x2 == x2; this.y2 == y2;
	 *           this.value == value;
	 */
	public FlowChange(int x1, int y1, int x2, int y2, int value) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.value = value;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: guigv.flowmap;
	 * @EFFECTS: (guigv.flowmap.get(Key(x1, y1, x2, y2)) ==
	 *           \old(guigv.flowmap.get(Key(x1, y1, x2, y2))) + value);
	 *           (guigv.flowmap.get(Key(x2, y2, x1, y1)) ==
	 *           \old(guigv.flowmap.get(Key(x2, y2, x1, y1))) + value);
	 *           (FlowMap.getFlow(x1, y1, x2, y2, TaxiSys.MAINTIME+1) ==
	 *           (FlowMap.getFlow(x1, y1, x2, y2, TaxiSys.MAINTIME) + value);
	 *           (FlowMap.getFlow(x2, y2, x1, y1, TaxiSys.MAINTIME+1)) ==
	 *           (FlowMap.getFlow(x2, y2, x1, y1, TaxiSys.MAINTIME) + value);
	 */
	public void changeFlow() {
		guigv.AddFlowN(x1, y1, x2, y2, value);
		FlowMap.addFlow(x1, y1, x2, y2, TaxiSys.MAINTIME, value);
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
