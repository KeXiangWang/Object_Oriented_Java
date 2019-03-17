package taxi;

import java.awt.Point;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class TaxiMap {
	/**
	 * @Overview: TaxiMap contains all the points, and record every car in the each
	 *            point. It provides some method to get the taxis and points.
	 */
	private Point[] pointMap;
	private BlockingQueue<Taxi>[] taxiHere;
	public static int[][] matrix;

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this.pointMap; this.taxiHere;
	 * @EFFECTS: taxiHere != null; pointMap != null; (\all int i;
	 *           0<=i<TaxiSys.SQUARESIZE; pointMap[i] != null); (\all int i;
	 *           0<=i<TaxiSys.SQUARESIZE; taxiHere[i] != null);
	 */
	@SuppressWarnings("unchecked")
	public TaxiMap() {
		taxiHere = new LinkedBlockingQueue[TaxiSys.SQUARESIZE];
		pointMap = new Point[TaxiSys.SQUARESIZE];
		matrix = new int[6405][6405];
		for (int i = 0; i < TaxiSys.SQUARESIZE; i++) {
			pointMap[i] = new Point(i / 80, i % 80);
		}
		for (int i = 0; i < TaxiSys.SQUARESIZE; i++) {
			taxiHere[i] = new LinkedBlockingQueue<Taxi>();
		}
	}

	/**
	 * @REQUIRES: point！=null; taxi!=null;
	 * @MODIFIES: taxiHere[point.x * 80 + point.y];
	 * @EFFECTS: taxiHere[point.x * 80 + point.y].contains(taxi);
	 */
	public void addTaxi(Point point, Taxi taxi) {
		taxiHere[point.x * 80 + point.y].add(taxi);
	}

	/**
	 * @REQUIRES: point！=null; taxi!=null;
	 * @MODIFIES: taxiHere[point.x * 80 + point.y];
	 * @EFFECTS: !taxiHere[point.x * 80 + point.y].contains(taxi);
	 */
	public void deleteTaxi(Point point, Taxi taxi) {
		taxiHere[point.x * 80 + point.y].remove(taxi);
	}

	/**
	 * @MODIFIES: None;
	 * @EFFECTS: \result == pointMap[x * 80 + y];
	 */
	public Point getPoint(int x, int y) {
		return pointMap[x * 80 + y];
	}

	/**
	 * @REQUIRES: request!=null;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == ambientTaxiList; AmbientTaxiList consists of all the
	 *           dispatchable taxi for the request in the ambient 4*4-square.
	 */
	public LinkedBlockingDeque<Taxi> getAmbientTaxi(Request request) {
		Point point = request.getStartPoin();
		LinkedBlockingDeque<Taxi> ambientTaxiList = new LinkedBlockingDeque<Taxi>();
		for (int i = point.x - 2; i <= point.x + 2; i++) {
			for (int j = point.y - 2; j <= point.y + 2; j++) {
				if (i <= 79 && i >= 0 && j <= 79 && j >= 0) {
					for (Taxi taxi : taxiHere[i * 80 + j]) {
						synchronized (taxi) {
							if (taxi.dipatchAble() && taxi.robRequest(request) == 1) {// set the request to the taxi
								ambientTaxiList.add(taxi);
							}
						}
					}
				}

			}
		}
		return ambientTaxiList;
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return pointMap != null;
	}
}
