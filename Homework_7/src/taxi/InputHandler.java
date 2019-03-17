package taxi;

import java.util.Scanner;
import java.awt.Point;

public class InputHandler implements Runnable {
	private RequestQueue requestQueue;
	private TaxiMap taxiMap;
	private TaxiGUI taxiGUI;
	private int totalRequest;

	public InputHandler(RequestQueue requestQueue, TaxiGUI taxiGUI, TaxiMap taxiMap) {
		this.requestQueue = requestQueue;
		this.taxiMap = taxiMap;
		this.taxiGUI = taxiGUI;
		this.totalRequest = 0;
	}

	public void run() {
		try {
			Scanner in = new Scanner(System.in);
			System.out.println("The system ready!");
			while (in.hasNextLine()) {
				String str = in.nextLine();
				str = str.replaceAll(" ", "");
				if (!parse(str)) {
					System.out.println("Invalid Request: " + str);
				}
				if (totalRequest > 600) {
					break;
				}
			}
			in.close();
		} catch (Throwable e) {
			System.out.println("InputHandler Wrong");
		}
	}

	public Boolean parse(String str) {
		try {
			if (!str.matches("^\\[CR,\\(\\+?\\d+,\\+?\\d+\\),\\(\\+?\\d+,\\+?\\d+\\)\\]$")) {
				return false;
			}
			String strs[] = str.split("[\\(),\\[\\]]");
			int[] position = { Integer.parseInt(strs[3]), Integer.parseInt(strs[4]), Integer.parseInt(strs[7]),
					Integer.parseInt(strs[8]) };
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
					taxiGUI.RequestTaxi(startPoint, endPoint);// It's better not to use it;
					requestQueue.offer(request);
				}
			}
			return true;
		} catch (Throwable e) {
			return false;
		}
	}
}
