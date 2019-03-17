package taxi;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

public class Flow {
	/**
	 * @Overview: Serve for point. For each point on the map , recording the
	 *            shortest distance and flow-least shortest distance between it and
	 *            this point.
	 */
	private Point target;
	private int[] flowSum;
	private int[] depth;
	private TaxiMap taxiMap;

	/**
	 * @REQUIRES: taxiMap!=null;
	 * @MODIFIES: this.taxiMap; this.depth; this.flowSum;
	 * @EFFECTS: this.taxiMap == taxiMap; this.depth == new int[6400]; this.flowSum
	 *           == new int[6400];
	 */
	public Flow(TaxiMap taxiMap) {
		this.flowSum = new int[6400];
		this.depth = new int[6400];
		this.taxiMap = taxiMap;
	}

	/**
	 * @REQUIRES: target!=null;
	 * @MODIFIES: this.target; this.depth; this.flowSum;
	 * @EFFECTS: this.target == target; (\all int i in \old(depth); (depth[i]== the
	 *           min of depth between (i/80,i%80) and target)); (\all int i in
	 *           \old(flowSum); (flowSum[i]== the min of flowSum between (i/80,i%80)
	 *           and target));
	 */
	public void BFS(Point target, long time) {
		this.target = target;
		int[] offset = new int[] { 0, 1, -1, 80, -80 };
		Vector<Integer> queue = new Vector<Integer>();
		int x = target.x * 80 + target.y;
		flowSum[x] = 0;
		depth[x] = 0;
		queue.add(x);
		while (queue.size() > 0) {
			int n = queue.get(0);
			for (int i = 1; i <= 4; i++) {
				int next = n + offset[i];
				if (next >= 0 && next < 6400 && guigv.m.graph[n][next] == 1 && x != next) {
					if (depth[next] == 0) {
						flowSum[next] = flowSum[n] + FlowMap.getFlow(n / 80, n % 80, next / 80, next % 80, time);
						depth[next] = depth[n] + 1;
						queue.add(next);
					} else {
						if (depth[next] == depth[n] + 1) {
							int flow = FlowMap.getFlow(n / 80, n % 80, next / 80, next % 80, time);
							if (flowSum[next] > flowSum[n] + flow) {
								flowSum[next] = flowSum[n] + flow;
							}
						}
					}
				}
			}
			queue.remove(0);// 退出队列
		}
	}

	/**
	 * @REQUIRES: start!=null;
	 * @MODIFIES None:
	 * @EFFECTS:(the path is the shortest. path between the Point start and the
	 *               Point this.target) ==> \result == path;
	 */
	public ArrayList<Point> getPath(Point start, long time) {
		ArrayList<Point> path = new ArrayList<Point>();
		Point temp = start;
		Point next = null;
		if (start.equals(target)) {
			return path;
		}
		while (true) {
			if (temp.x + 1 < 80
					&& flowSum[temp.x * 80 + temp.y] == FlowMap.getFlow(temp.x, temp.y, temp.x + 1, temp.y, time)
							+ flowSum[(temp.x + 1) * 80 + temp.y]
					&& depth[temp.x * 80 + temp.y] == depth[(temp.x + 1) * 80 + temp.y] + 1
					&& guigv.m.graph[temp.x * 80 + temp.y][(temp.x + 1) * 80 + temp.y] == 1) {
				next = taxiMap.getPoint(temp.x + 1, temp.y);
				path.add(next);
				temp = next;
			} else if (temp.x - 1 >= 0
					&& flowSum[temp.x * 80 + temp.y] == FlowMap.getFlow(temp.x, temp.y, temp.x - 1, temp.y, time)
							+ flowSum[(temp.x - 1) * 80 + temp.y]
					&& depth[temp.x * 80 + temp.y] == depth[(temp.x - 1) * 80 + temp.y] + 1
					&& guigv.m.graph[temp.x * 80 + temp.y][(temp.x - 1) * 80 + temp.y] == 1) {
				next = taxiMap.getPoint(temp.x - 1, temp.y);
				path.add(next);
				temp = next;
			} else if (temp.y + 1 < 80
					&& flowSum[temp.x * 80 + temp.y] == FlowMap.getFlow(temp.x, temp.y, temp.x, temp.y + 1, time)
							+ flowSum[temp.x * 80 + temp.y + 1]
					&& depth[temp.x * 80 + temp.y] == depth[temp.x * 80 + temp.y + 1] + 1
					&& guigv.m.graph[temp.x * 80 + temp.y][temp.x * 80 + temp.y + 1] == 1) {
				next = taxiMap.getPoint(temp.x, temp.y + 1);
				path.add(next);
				temp = next;
			} else if (temp.y - 1 >= 0
					&& flowSum[temp.x * 80 + temp.y] == FlowMap.getFlow(temp.x, temp.y, temp.x, temp.y - 1, time)
							+ flowSum[temp.x * 80 + temp.y - 1]
					&& depth[temp.x * 80 + temp.y] == depth[temp.x * 80 + temp.y - 1] + 1
					&& guigv.m.graph[temp.x * 80 + temp.y][temp.x * 80 + temp.y - 1] == 1) {
				next = taxiMap.getPoint(temp.x, temp.y - 1);
				path.add(next);
				temp = next;
			}
			if (next.equals(target)) {
				break;
			}
		}
		return path;
	}

	/**
	 * @REQUIRES: A!=null; B!=null;
	 * @MODIFIES None:
	 * @EFFECTS: (dep[next] is the shortest distance between the Point A and the
	 *           Point B) ==> (\result == dep[next]);
	 */
	public static int getFastestDistance(Point A, Point B) {
		if (A.equals(B)) {
			return 0;
		}
		int end = B.x * 80 + B.y;
		int[] offset = new int[] { 0, 1, -1, 80, -80 };
		Vector<Integer> queue = new Vector<Integer>();
		int x = A.x * 80 + A.y;
		int[] dep = new int[6405];
		dep[x] = 0;
		queue.add(x);
		while (queue.size() > 0) {
			int n = queue.get(0);
			for (int i = 1; i <= 4; i++) {
				int next = n + offset[i];
				if (next >= 0 && next < 6400 && guigv.m.graph[n][next] == 1 && x != next) {
					if (dep[next] == 0) {
						dep[next] = dep[n] + 1;
						queue.add(next);
					}
					if (next == end) {
						return dep[next];
					}
				}
			}
			queue.remove(0);
		}
		return dep[end];
	}

	/**
	 * @REQUIRES: target!=null;
	 * @MODIFIES: this.target; this.depth; this.flowSum;
	 * @EFFECTS: this.target == target; (\all int i in \old(depth); (depth[i]== the
	 *           min of depth between (i/80,i%80) and target)); (\all int i in
	 *           \old(flowSum); (flowSum[i]== the min of flowSum between (i/80,i%80)
	 *           and target));
	 */
	public void BFSTraceable(Point target, long time) {
		this.target = target;
		int[] offset = new int[] { 0, 1, -1, 80, -80 };
		Vector<Integer> queue = new Vector<Integer>();
		int x = target.x * 80 + target.y;
		flowSum[x] = 0;
		depth[x] = 0;
		queue.add(x);
		while (queue.size() > 0) {
			int n = queue.get(0);
			for (int i = 1; i <= 4; i++) {
				int next = n + offset[i];
				if (next >= 0 && next < 6400 && TaxiMap.matrix[n][next] == 1 && x != next) {
					if (depth[next] == 0) {
						flowSum[next] = flowSum[n] + FlowMap.getFlow(n / 80, n % 80, next / 80, next % 80, time);
						depth[next] = depth[n] + 1;
						queue.add(next);
					} else {
						if (depth[next] == depth[n] + 1) {
							int flow = FlowMap.getFlow(n / 80, n % 80, next / 80, next % 80, time);
							if (flowSum[next] > flowSum[n] + flow) {
								flowSum[next] = flowSum[n] + flow;
							}
						}
					}
				}
			}
			queue.remove(0);// 退出队列
		}
	}

	/**
	 * @REQUIRES: start!=null;
	 * @MODIFIES None:
	 * @EFFECTS:(the path is the shortest. path between the Point start and the
	 *               Point this.target) ==> \result == path;(And it can only be used
	 *               for traceableTaxi);
	 */
	public ArrayList<Point> getPathTraceable(Point start, long time) {
		ArrayList<Point> path = new ArrayList<Point>();
		Point temp = start;
		Point next = null;
		if (start.equals(target)) {
			return path;
		}
		while (true) {
			if (temp.x + 1 < 80
					&& flowSum[temp.x * 80 + temp.y] == FlowMap.getFlow(temp.x, temp.y, temp.x + 1, temp.y, time)
							+ flowSum[(temp.x + 1) * 80 + temp.y]
					&& depth[temp.x * 80 + temp.y] == depth[(temp.x + 1) * 80 + temp.y] + 1
					&& TaxiMap.matrix[temp.x * 80 + temp.y][(temp.x + 1) * 80 + temp.y] == 1) {
				next = taxiMap.getPoint(temp.x + 1, temp.y);
				path.add(next);
				temp = next;
			} else if (temp.x - 1 >= 0
					&& flowSum[temp.x * 80 + temp.y] == FlowMap.getFlow(temp.x, temp.y, temp.x - 1, temp.y, time)
							+ flowSum[(temp.x - 1) * 80 + temp.y]
					&& depth[temp.x * 80 + temp.y] == depth[(temp.x - 1) * 80 + temp.y] + 1
					&& TaxiMap.matrix[temp.x * 80 + temp.y][(temp.x - 1) * 80 + temp.y] == 1) {
				next = taxiMap.getPoint(temp.x - 1, temp.y);
				path.add(next);
				temp = next;
			} else if (temp.y + 1 < 80
					&& flowSum[temp.x * 80 + temp.y] == FlowMap.getFlow(temp.x, temp.y, temp.x, temp.y + 1, time)
							+ flowSum[temp.x * 80 + temp.y + 1]
					&& depth[temp.x * 80 + temp.y] == depth[temp.x * 80 + temp.y + 1] + 1
					&& TaxiMap.matrix[temp.x * 80 + temp.y][temp.x * 80 + temp.y + 1] == 1) {
				next = taxiMap.getPoint(temp.x, temp.y + 1);
				path.add(next);
				temp = next;
			} else if (temp.y - 1 >= 0
					&& flowSum[temp.x * 80 + temp.y] == FlowMap.getFlow(temp.x, temp.y, temp.x, temp.y - 1, time)
							+ flowSum[temp.x * 80 + temp.y - 1]
					&& depth[temp.x * 80 + temp.y] == depth[temp.x * 80 + temp.y - 1] + 1
					&& TaxiMap.matrix[temp.x * 80 + temp.y][temp.x * 80 + temp.y - 1] == 1) {
				next = taxiMap.getPoint(temp.x, temp.y - 1);
				path.add(next);
				temp = next;
			}
			if (next.equals(target)) {
				break;
			}
		}
		return path;
	}

	/**
	 * @REQUIRES: A!=null; B!=null;
	 * @MODIFIES None:
	 * @EFFECTS: (dep[next] is the shortest distance between the Point A and the
	 *           Point B) ==> (\result == dep[next]);(And it can only be used for
	 *           traceableTaxi);
	 */
	public static int getFastestDistanceTraceable(Point A, Point B) {
		if (A.equals(B)) {
			return 0;
		}
		int end = B.x * 80 + B.y;
		int[] offset = new int[] { 0, 1, -1, 80, -80 };
		Vector<Integer> queue = new Vector<Integer>();
		int x = A.x * 80 + A.y;
		int[] dep = new int[6405];
		dep[x] = 0;
		queue.add(x);
		while (queue.size() > 0) {
			int n = queue.get(0);
			for (int i = 1; i <= 4; i++) {
				int next = n + offset[i];
				if (next >= 0 && next < 6400 && TaxiMap.matrix[n][next] == 1 && x != next) {
					if (dep[next] == 0) {
						dep[next] = dep[n] + 1;
						queue.add(next);
					}
					if (next == end) {
						return dep[next];
					}
				}
			}
			queue.remove(0);
		}
		return dep[end];
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return (flowSum != null && depth != null);
	}
}
