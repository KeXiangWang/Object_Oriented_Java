package taxi;

public class FlowChange {
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int value;

	public FlowChange(int x1, int y1, int x2, int y2, int value) {
		/**
		 * @MODIFIES: this.x1; this.y1; this.x2; this.y2; this.value;
		 * @EFFECTS: this.x1 == x1; this.y1 == y1; this.x2 == x2; this.y2 == y2;
		 *           this.value == value;
		 */
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.value = value;
	}

	public void changeFlow() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: guigv.flowmap;
		 * @EFFECTS: (guigv.flowmap.get(Key(x1, y1, x2, y2)) == \old(guigv.flowmap.get(Key(x1, y1, x2, y2))) + n);
		 *           (guigv.flowmap.get(Key(x2, y2, x1, y1)) == \old(guigv.flowmap.get(Key(x2, y2, x1, y1))) + n);
		 */
		guigv.AddFlowN(x1, y1, x2, y2, value);
	}
}
