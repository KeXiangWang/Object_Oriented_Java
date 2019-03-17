package taxi;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

public class Flow {
	private Point target;
	private int[] flowSum;
	private int[] depth;
	private TaxiMap taxiMap;

	public Flow(TaxiMap taxiMap) {
		/**
		 * @REQUIRES: taxiMap!=null;
		 * @MODIFIES: this.taxiMap; this.depth; this.flowSum;
		 * @EFFECTS: this.taxiMap == taxiMap; this.depth == new int[6400]; this.flowSum ==
		 *           new int[6400];
		 */
		this.flowSum = new int[6400];
		this.depth = new int[6400];
		this.taxiMap = taxiMap;
	}

	public void BFS(Point target) {
		/**
		 * @REQUIRES: target!=null;
		 * @MODIFIES: this.target; this.depth; this.flowSum;
		 * @EFFECTS: this.target == target; (\all int i in \old(depth); depth[i]== the min
		 *           of depth between (i/80,i%80) and target); (\all int i in
		 *           \old(flowSum); flowSum[i]== the min of flowSum between (i/80,i%80)
		 *           and target);
		 */
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
						flowSum[next] = flowSum[n] + guigv.GetFlow(n / 80, n % 80, next / 80, next % 80);
						depth[next] = depth[n] + 1;
						queue.add(next);
					} else {
						if (depth[next] == depth[n] + 1) {
							int flow = guigv.GetFlow(n / 80, n % 80, next / 80, next % 80);
							if (flowSum[next] > flowSum[n] + flow) {
								flowSum[next] = flowSum[n] + flow;
							}
						}
					}
				}
			}
			// System.out.println(queue.size());
			queue.remove(0);// 退出队列
		}

	}

	public ArrayList<Point> getPath(Point start) {
		/**
		 * @REQUIRES: start!=null;
		 * @MODIFIES None:
		 * @EFFECTS: \result == path; the path is the shortest path between the Point
		 *           start and the Point this.target;
		 */
		ArrayList<Point> path = new ArrayList<Point>();
		Point temp = start;
		Point next = null;
		if (start.equals(target)) {
			return path;
		}
		while (true) {
			if (temp.x + 1 < 80
					&& flowSum[temp.x * 80 + temp.y] == guigv.GetFlow(temp.x, temp.y, temp.x + 1, temp.y)
							+ flowSum[(temp.x + 1) * 80 + temp.y]
					&& depth[temp.x * 80 + temp.y] == depth[(temp.x + 1) * 80 + temp.y] + 1
					&& guigv.m.graph[temp.x * 80 + temp.y][(temp.x + 1) * 80 + temp.y] == 1) {
				next = taxiMap.getPoint(temp.x + 1, temp.y);
				path.add(next);
				temp = next;
			} else if (temp.x - 1 >= 0
					&& flowSum[temp.x * 80 + temp.y] == guigv.GetFlow(temp.x, temp.y, temp.x - 1, temp.y)
							+ flowSum[(temp.x - 1) * 80 + temp.y]
					&& depth[temp.x * 80 + temp.y] == depth[(temp.x - 1) * 80 + temp.y] + 1
					&& guigv.m.graph[temp.x * 80 + temp.y][(temp.x - 1) * 80 + temp.y] == 1) {
				next = taxiMap.getPoint(temp.x - 1, temp.y);
				path.add(next);
				temp = next;
			} else if (temp.y + 1 < 80
					&& flowSum[temp.x * 80 + temp.y] == guigv.GetFlow(temp.x, temp.y, temp.x, temp.y + 1)
							+ flowSum[temp.x * 80 + temp.y + 1]
					&& depth[temp.x * 80 + temp.y] == depth[temp.x * 80 + temp.y + 1] + 1
					&& guigv.m.graph[temp.x * 80 + temp.y][temp.x * 80 + temp.y + 1] == 1) {
				next = taxiMap.getPoint(temp.x, temp.y + 1);
				path.add(next);
				temp = next;
			} else if (temp.y - 1 >= 0
					&& flowSum[temp.x * 80 + temp.y] == guigv.GetFlow(temp.x, temp.y, temp.x, temp.y - 1)
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

	public static int getFastestDistance(Point A, Point B) {
		/**
		 * @REQUIRES: A!=null; B!=null;
		 * @MODIFIES None:
		 * @EFFECTS: \result == dep[next]; dep[next] is the shortest distance between the
		 *           Point A and the Point B;
		 */
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

}
