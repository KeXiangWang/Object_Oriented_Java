package taxi;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class TaxiSys {
	/**
	 * @Overview: TaxiSys starts the whole system and defines some constant.
	 */
	static final int SIZE = 80;// size of map
	static final int SQUARESIZE = SIZE * SIZE;// area of map
	static final int TAXINUM = 100;// total number of taxi
	static long MAINTIME;
	static Request GhostRequest;

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS: normal_behavior: Initialize all classes, start the threads, and
	 *           begin to process the input. exception_behavior(Throwable e): print
	 *           the wrong information. System shut down.
	 */
	public static void main(String args[]) {
		try {
			TaxiMap taxiMap = new TaxiMap();
			GhostRequest = new Request(taxiMap.getPoint(20, 0), taxiMap.getPoint(10, 0), System.currentTimeMillis());
			GhostRequest.initRequest(taxiMap);

			ArrayList<Taxi> taxiList = new ArrayList<Taxi>();
			RequestQueue requestQueue = new RequestQueue();
			ArrayList<TaxiChange> initTaxiList = new ArrayList<TaxiChange>();
			ArrayList<FlowChange> initFlowList = new ArrayList<FlowChange>();
			ArrayList<Point> lightList = new ArrayList<Point>();

			Scanner in = new Scanner(System.in);
			for (int i = 0; i < 30; i++) {
				taxiList.add(new TraceableTaxi(taxiMap));
			}
			for (int i = 30; i < TaxiSys.TAXINUM; i++) {
				taxiList.add(new Taxi(taxiMap));
			}
				

			FileLoader fileLoader = new FileLoader();

			fileLoader.Load(requestQueue, taxiMap, in, initTaxiList, initFlowList, lightList);
			TaxiGUI taxiGUI = new TaxiGUI();
			taxiGUI.LoadMap(FileLoader.intMap, SIZE);

			LightSystem lightSystem = new LightSystem(lightList, taxiGUI);
			LightSystem.GAP = (new Random()).nextInt(501) + 500;

			MAINTIME = System.currentTimeMillis();
			for (FlowChange flowChange : initFlowList) {
				flowChange.changeFlow();
			}
			InputHandler inputHandler = new InputHandler(taxiList, requestQueue, taxiGUI, taxiMap, in);

			for (Taxi taxi : taxiList) {
				taxi.initTaxi(taxiGUI, lightSystem);
			}
			for (TaxiChange taxiChange : initTaxiList) {
				taxiChange.changeTaxi(taxiList);
			}
			// start the threads.
			new Thread(lightSystem, "LightSystem").start();
			for (Taxi taxi : taxiList) {
				new Thread(taxi).start();
			}
			new Thread(inputHandler, "Thread-InputHandler").start();
		} catch (Throwable e) {
			System.out.println("System wrong");
		}
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return (GhostRequest != null);
	}
}
