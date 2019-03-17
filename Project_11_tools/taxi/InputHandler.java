package taxi;

import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class InputHandler implements Runnable {
	/**
	 * @Overview: This class handle the input from console and can change the
	 *            request queue, the status of roads. And it also provide a method
	 *            to check the status of a taxi.
	 */
	private RequestQueue requestQueue;
	private ArrayList<Taxi> taxiList;
	private TaxiMap taxiMap;
	private TaxiGUI taxiGUI;
	private Scanner in;
	private int totalRequest;
	private Biterator biterator;
	private PrintWriter printWriter;
	private RequestMessage requestMessage;

	/**
	 * @REQUIRES: taxiList!=null; requestQueue!=null; taxiGUI!=null; taxiMap!=null;
	 *            in!=null;
	 * @MODIFIES: this.taxiList; this.requestQueue; this.taxiMap; this.taxiGUI;
	 *            this.totalRequest;
	 * @EFFECTS: this.taxiList == taxiList; this.requestQueue == requestQueue;
	 *           this.taxiMap == taxiMap; this.taxiGUI == taxiGUI; this.totalRequest
	 *           == 0;
	 */
	public InputHandler(ArrayList<Taxi> taxiList, RequestQueue requestQueue, TaxiGUI taxiGUI, TaxiMap taxiMap,
			Scanner in) {
		try {
			this.taxiList = taxiList;
			this.requestQueue = requestQueue;
			this.taxiMap = taxiMap;
			this.taxiGUI = taxiGUI;
			this.totalRequest = 0;
			this.printWriter = new PrintWriter(new BufferedWriter(new FileWriter("iterator.txt")), true);
			this.requestMessage = null;
			this.in = in;
		} catch (Exception e) {
		}
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this.totalRequest; this.taxiGUI; this.requestQueue;
	 * @EFFECTS: normal_behavior:
	 *           this.requestQueue.size()>=\old(this.requestQueue.size()); the
	 *           status of some roads changed.; exception_behavior(Throwable e):
	 *           print the wrong information.
	 */
	public void run() {
		try {
			System.out.println("The system ready!");
			while (in.hasNextLine()) {
				String str = in.nextLine();
				String strNew = str.replaceAll(" ", "");
				if (strNew.equals("")) {
					System.out.println("Invalid Request: " + str);
					continue;
				}
				if (str.charAt(0) == '#') {
					if (!parseSetRoad(str)) {
						System.out.println("Invalid Set Road: " + str);
					}
				} else if (str.charAt(0) == '%') {
					if (!parseBiterator(str)) {
						System.out.println("Invalid Iterator Request: " + str);
					}
				} else if (str.charAt(0) == '$') {
					if (!parseCheckTaxi(str)) {
						System.out.println("Invalid Taxi Check: " + str);
					}
				} else {
					if (!parseRequest(strNew)) {
						System.out.println("Invalid Request: " + str);
					}
				}
				if (totalRequest > 300) {
					break;
				}
			}
			in.close();
		} catch (Throwable e) {
			System.out.println("InputHandler Wrong");
		}
	}

	/**
	 * @MODIFIES: this.totalRequest; this.requestQueue;
	 * @EFFECTS: normal_behavior:
	 *           this.requestQueue.size()>=\old(this.requestQueue.size()); str is a
	 *           valid request ==> \result=true; str is not a valid request ==>
	 *           \result=false; exception_behavior(Throwable e): \result=false;
	 * @THREAD_REQUIRES None;
	 * @THREAD_EFFECTS: \locked(requestQueue);
	 */
	public Boolean parseRequest(String str) {
		try {
			if (!str.matches("^\\[CR,\\(\\+?\\d+,\\+?\\d+\\)\\(\\+?\\d+,\\+?\\d+\\)\\]$")) {
				return false;
			}
			String strs[] = str.split("[\\(),\\[\\]]");
			int[] position = { Integer.parseInt(strs[3]), Integer.parseInt(strs[4]), Integer.parseInt(strs[6]),
					Integer.parseInt(strs[7]) };
			for (int number : position) {
				if (number > 79 || number < 0) {
					return false;
				}
			}

			Point startPoint = taxiMap.getPoint(position[0], position[1]);
			Point endPoint = taxiMap.getPoint(position[2], position[3]);
			if (startPoint.equals(endPoint)) {
				return false;
			}
			Request request = new Request(startPoint, endPoint, System.currentTimeMillis());
			synchronized (requestQueue) {
				if (requestQueue.contains(request)) {
					System.out.println("A homogenous request " + request.getID());
				} else {
					totalRequest++;
					request.initRequest(taxiMap);
					requestQueue.offer(request);
					new Thread(request).start();
				}
			}

			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	/**
	 * @MODIFIES: this.totalRequest; this.requestQueue;
	 * @EFFECTS: normal_behavior: (The road exsits)==>
	 *           (this.requestQueue.size()>=\old(this.requestQueue.size())); (str is
	 *           a valid setRoad request) ==> (the road'status changed) ==>
	 *           \result=true; str is not a valid setRoad request==> \result=false;
	 *           exception_behavior(Throwable e): \result=false;
	 */
	public Boolean parseSetRoad(String str) {
		try {
			if (!str.matches("^#\\(\\+?\\d+,\\+?\\d+\\) \\(\\+?\\d+,\\+?\\d+\\) [10]$")) {
				return false;
			}
			String strs[] = str.split("[#\\(), ]");
			int[] position = { Integer.parseInt(strs[2]), Integer.parseInt(strs[3]), Integer.parseInt(strs[6]),
					Integer.parseInt(strs[7]) };
			int status = Integer.parseInt(strs[9]);
			for (int number : position) {
				if (number > 79 || number < 0) {
					return false;
				}
			}
			if (Math.abs(position[0] - position[2]) + Math.abs(position[3] - position[1]) != 1) {
				return false;
			}
			if (TaxiMap.matrix[position[0] * 80 + position[1]][position[2] * 80 + position[3]] == 1) {
				taxiGUI.SetRoadStatus(taxiMap.getPoint(position[0], position[1]),
						taxiMap.getPoint(position[2], position[3]), status);
				if (status == 0) {
					FlowMap.clearFlow(position[0], position[1], position[2], position[3]);
				}
			} else {
				System.out.println("Wrong RoadSet :" + str);
			}

			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	/**
	 * @MODIFIES:
	 * @EFFECTS: normal_behavior: print the taxi information; str is a valid check
	 *           request ==> the road'status changed ==> \result=true; str is not a
	 *           valid setRoad request==> \result=false;
	 *           exception_behavior(Throwable e): \result=false;
	 */
	public Boolean parseCheckTaxi(String str) {
		try {

			if (!str.matches("^\\$\\d+$")) {
				return false;
			}

			String strs[] = str.split("\\$");
			int number = Integer.parseInt(strs[1]);
			if (number < 0 || number > 99) {
				return false;
			}
			Taxi taxi = taxiList.get(number);
			System.out.println(taxi.ckeckPrint());
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	/**
	 * @MODIFIES:
	 * @EFFECTS: (str is a valid biterator request) ==> (exert the request and print
	 *           to the designated file);(str is not a valid biterator request) ==>
	 *           (print the feed back in the console);
	 */
	public Boolean parseBiterator(String str) {
		try {
			if (str.matches("^%\\d+$")) {
				String strs[] = str.split("\\%");
				int number = Integer.parseInt(strs[1]);
				if (number < 0 || number > 29) {
					return false;
				}
				TraceableTaxi taxi = (TraceableTaxi) taxiList.get(number);
				biterator = taxi.iterator();
			} else if (str.matches("^%hasNext$")) {
				if (biterator == null) {
					return false;
				} else {
					System.out.println(biterator.hasNext());
				}
			} else if (str.matches("^%next$")) {
				if (biterator == null) {
					return false;
				} else {
					try {
						requestMessage = biterator.next();
					} catch (Exception e) {
						requestMessage = null;
						return false;
					}
				}
			} else if (str.matches("^%hasPrevious$")) {
				if (biterator == null) {
					return false;
				} else {
					System.out.println("hasNext: " + biterator.hasPrevious());
				}
			} else if (str.matches("^%previous$")) {
				if (biterator == null) {
					return false;
				} else {
					try {
						requestMessage = biterator.previous();
					} catch (Exception e) {
						requestMessage = null;
						return false;
					}
				}
			} else if (str.matches("^%print$")) {
				if (requestMessage == null) {
					return false;
				} else {
					requestMessage.printMessage(printWriter);
				}
			} else {
				return false;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return (taxiList != null && requestQueue != null && taxiMap != null && taxiGUI != null);
	}
}
