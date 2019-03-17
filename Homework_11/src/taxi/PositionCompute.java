package taxi;

import java.awt.Point;
import java.util.Random;

public class PositionCompute {
	/**
	 * @Overview: This Class is only used for compute somethings about the position.
	 *            And it define some constant.
	 */
	public final static int BACK = 0;
	public final static int LEFT = 1;
	public final static int RIGHT = 2;
	public final static int STRAIGHT = 3;

	/**
	 * @REQUIRES: origin!= null;now!=null;next!=null;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == (according to the three points' relative position, judge
	 *           the taxi's move direction);
	 */
	public static int getTurningDirection(Point origin, Point now, Point next) {
		if ((origin.x - now.x == now.x - next.x) || (origin.y - now.y == now.y - next.y)) {
			return STRAIGHT;
		}
		if (origin.equals(next)) {
			return BACK;
		}
		if ((origin.x > now.x && now.y > next.y) || (origin.x < now.x && now.y < next.y)
				|| (origin.y > now.y && now.x < next.x) || (origin.y < now.y && now.x > next.x)) {
			return LEFT;
		}
		if ((origin.x > now.x && now.y < next.y) || (origin.x < now.x && now.y > next.y)
				|| (origin.y > now.y && now.x > next.x) || (origin.y < now.y && now.x < next.x)) {
			return RIGHT;
		}
		return -1;
	}

	/**
	 * @MODIFIES: None;
	 * @EFFECTS: \result==directions ==> (\all int i; 0<=i<4;
	 *           Math.abs(directions[i][0]-x)+Math.abs(directions[i][1]-y)==1);
	 */
	public static int[][] getDirections(int x, int y) {
		int[][] directions = new int[4][2];
		directions[0][0] = x + 1;
		directions[0][1] = y;
		directions[1][0] = x - 1;
		directions[1][1] = y;
		directions[2][0] = x;
		directions[2][1] = y + 1;
		directions[3][0] = x;
		directions[3][1] = y - 1;
		return directions;
	}

	/**
	 * @REQUIRES: location!= null;taxiMap!=null;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == (the next point, which the road between it and the
	 *           location has the least flow);
	 */
	public static Point getWanderTarget(Point location, TaxiMap taxiMap, int ID, long time) {
		Point[] pointList = new Point[4];
		int i = 0, j = 0;
		int flowTemp = 200;
		int[][] directions = PositionCompute.getDirections(location.x, location.y);
		int[] directionFlow = new int[4];
		for (i = 0; i < 4; i++) {
			if (directions[i][0] * 80 + directions[i][1] < 0 || directions[i][0] * 80 + directions[i][1] >= 6400) {
				continue;
			}
			if (guigv.m.graph[location.x * 80 + location.y][directions[i][0] * 80 + directions[i][1]] == 1) {
				directionFlow[i] = FlowMap.getFlow(location.x, location.y, directions[i][0], directions[i][1], time);
				if (directionFlow[i] < flowTemp) {
					flowTemp = directionFlow[i];
				}
			}
		}
		for (i = 0; i < 4; i++) {
			if (directions[i][0] * 80 + directions[i][1] < 0 || directions[i][0] * 80 + directions[i][1] >= 6400) {
				continue;
			}
			if (guigv.m.graph[location.x * 80 + location.y][directions[i][0] * 80 + directions[i][1]] == 1) {
				if (directionFlow[i] == flowTemp) {
					pointList[j++] = taxiMap.getPoint(directions[i][0], directions[i][1]);
				}
			}
		}
		if (j != 0) {
			Point next = pointList[(new Random()).nextInt(j)];
			return next;
		}
		return null;
	}

	/**
	 * @MODIFIES: None;
	 * @EFFECTS: \result == (how many points can be reached from this point through
	 *           just one road);
	 */
	public static int countDirection(int x, int y) {
		int i, count = 0;
		int[][] directions = PositionCompute.getDirections(x, y);
		for (i = 0; i < 4; i++) {
			if (directions[i][0] * 80 + directions[i][1] < 0 || directions[i][0] * 80 + directions[i][1] >= 6400) {
				continue;
			}
			if (guigv.m.graph[x * 80 + y][directions[i][0] * 80 + directions[i][1]] == 1) {
				count++;
			}
		}
		return count;
	}

	/**
	 * @REQUIRES: location!= null;taxiMap!=null;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == (the next point, which the road between it and the
	 *           location has the least flow, and it can only be used for traceableTaxi);
	 */
	public static Point getWanderTargetTraceable(Point location, TaxiMap taxiMap, int ID, long time) {
		Point[] pointList = new Point[4];
		int i = 0, j = 0;
		int flowTemp = 200;
		int[][] directions = PositionCompute.getDirections(location.x, location.y);
		int[] directionFlow = new int[4];
		for (i = 0; i < 4; i++) {
			if (directions[i][0] * 80 + directions[i][1] < 0 || directions[i][0] * 80 + directions[i][1] >= 6400) {
				continue;
			}
			if (TaxiMap.matrix[location.x * 80 + location.y][directions[i][0] * 80 + directions[i][1]] == 1) {
				directionFlow[i] = FlowMap.getFlow(location.x, location.y, directions[i][0], directions[i][1], time);
				if (directionFlow[i] < flowTemp) {
					flowTemp = directionFlow[i];
				}
			}
		}
		for (i = 0; i < 4; i++) {
			if (directions[i][0] * 80 + directions[i][1] < 0 || directions[i][0] * 80 + directions[i][1] >= 6400) {
				continue;
			}
			if (TaxiMap.matrix[location.x * 80 + location.y][directions[i][0] * 80 + directions[i][1]] == 1) {
				if (directionFlow[i] == flowTemp) {
					pointList[j++] = taxiMap.getPoint(directions[i][0], directions[i][1]);
				}
			}
		}
		if (j != 0) {
			Point next = pointList[(new Random()).nextInt(j)];
			return next;
		}
		return null;
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
