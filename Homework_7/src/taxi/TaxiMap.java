package taxi;

import java.awt.Point;
import java.util.ArrayList;

public class TaxiMap {
	private Point[] pointMap;
	private ArrayList<Taxi>[] taxiHere;
	private int[][] adjacencyMatrix; // adjacency matrix
	private int[][] fastestDistance; // the fastest distance

	@SuppressWarnings("unchecked")
	public TaxiMap() {
		taxiHere = new ArrayList[TaxiSys.SQUARESIZE];
		pointMap = new Point[TaxiSys.SQUARESIZE];
		this.adjacencyMatrix = guigv.m.graph;
		this.fastestDistance = guigv.m.D;
		for (int i = 0; i < TaxiSys.SQUARESIZE; i++) {
			pointMap[i] = new Point(i / 80, i % 80);
		}
		for (int i = 0; i < TaxiSys.SQUARESIZE; i++) {
			taxiHere[i] = new ArrayList<Taxi>();
		}
	}

	public void addTaxi(Point point, Taxi taxi) {
		taxiHere[point.x * 80 + point.y].add(taxi);
	}

	public void deleteTaxi(Point point, Taxi taxi) {
		taxiHere[point.x * 80 + point.y].remove(taxi);
	}

	public Point getPoint(int x, int y) {
		return pointMap[x * 80 + y];
	}

	public int getDistance(Point location, Point point) {
		return fastestDistance[location.x * 80 + location.y][point.x * 80 + point.y];
	}

	public int judgeAdjacent(int x1, int y1, int x2, int y2) {
		if (x2 >= 80 || x2 < 0 || y2 >= 80 || y2 < 0) {
			return 0;
		}
		return adjacencyMatrix[x1 * 80 + y1][x2 * 80 + y2];
	}

	public int getFastestDistance(int x1, int y1, int x2, int y2) {
		return fastestDistance[x1 * 80 + y1][x2 * 80 + y2];
	}

	public ArrayList<Taxi> getAmbientTaxi(Request request) {
		Point point = request.getStartPoin();
		ArrayList<Taxi> ambientTaxiList = new ArrayList<Taxi>();
		for (int i = point.x - 2; i <= point.x + 2; i++) {
			for (int j = point.y - 2; j <= point.y + 2; j++) {
				if (i <= 79 && i >= 0 && j <= 79 && j >= 0) {
					for (Taxi taxi : taxiHere[i * 80 + j]) {
						if (taxi.dipatchAble() && taxi.robRequest(request) == 1) {// set the request to the taxi
							ambientTaxiList.add(taxi);
						}
					}
				}

			}
		}
		return ambientTaxiList;
	}

}
