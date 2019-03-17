package taxi;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Map {
	private String possibleNumber = "0123";
	protected int[][] intMap;
	private RequestQueue requestQueue;
	private TaxiMap taxiMap;
	private ArrayList<TaxiChange> initTaxiList;
	private ArrayList<FlowChange> initFlowList;
	private Boolean mapLoaded;

	public Map() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: this.intMap;
		 * @EFFECTS: intMap == new int[TaxiSys.SIZE][TaxiSys.SIZE];
		 */
		intMap = new int[TaxiSys.SIZE][TaxiSys.SIZE];
		mapLoaded = null;
	}

	public void Load(RequestQueue requestQueue, TaxiMap taxiMap, Scanner in, ArrayList<TaxiChange> initTaxiList,
			ArrayList<FlowChange> initFlowList) {
		/**
		 * @REQUIRES: requestQueue!=null; taxiMap!=null; in!=null; initTaxiList!=null;
		 *            initFlowList!=null;
		 * @MODIFIES: this.requestQueue ; this.taxiMap ; this.initTaxiList ;
		 *            this.initFlowList ; this.intMap;
		 * @EFFECTS: normal_behavior: this.requestQueue == requestQueue; this.taxiMap ==
		 *           taxiMap; this.initTaxiList == initTaxiList; this.initFlowList ==
		 *           initFlowList; this.intMap loaded
		 *           requestQueue.size()>=\old(requestQueue.size());
		 *           initFlowList.size()>=\old(initFlowList.size());
		 *           initTaxiList.size()>=\old(initTaxiList.size());
		 *           exception_behavior(Throwable e): print the wrong information. the
		 *           System shut down.
		 */
		this.requestQueue = requestQueue;
		this.taxiMap = taxiMap;
		this.initTaxiList = initTaxiList;
		this.initFlowList = initFlowList;

		System.out.println("The system initing!");
		try {
			while (in.hasNextLine()) {
				String str = in.nextLine();
				String strs[] = str.split(" ", 2);
				if (strs[0].equals("Load")) {
					if (parseFile(strs[1])) {
						break;
					}
				}
				System.out.println("Invalid LoadRequest: " + str);
			}
			System.out.println("Load finished");
		} catch (Throwable e) {
			System.out.println("Load Wrong");
			System.exit(1);
		}

	}

	public Boolean parseFile(String filename) {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: this.requestQueue ; this.taxiMap ; this.initTaxiList ;
		 *            this.initFlowList ; this.intMap;
		 * @EFFECTS: normal_behavior: this.requestQueue == requestQueue; this.taxiMap ==
		 *           taxiMap; this.initTaxiList == initTaxiList; this.initFlowList ==
		 *           initFlowList; this.intMap loaded
		 *           requestQueue.size()>=\old(requestQueue.size());
		 *           initFlowList.size()>=\old(initFlowList.size());
		 *           initTaxiList.size()>=\old(initTaxiList.size());
		 *           exception_behavior(Throwable e): print the wrong information. the
		 *           System shut down.
		 */
		try {
			String fileName = filename.trim();
			File file = new File(fileName);
			if (!file.exists()) {
				return false;
			} else {
				InputStream inputStream = new FileInputStream(file);
				Scanner in = new Scanner(inputStream);
				while (in.hasNextLine()) {
					String str = in.nextLine();
					str = str.replaceAll(" ", "");
					if (str.equals(""))
						continue;
					if (str.equals("#map")) {
						parseMap(in);
						if(!mapLoaded) {
							Scanner oriIn = new Scanner(new FileInputStream("map_ori.txt"));
							parseMap(oriIn);
							oriIn.close();
						}
					}
					if (str.equals("#taxi")) {
						parseTaxi(in);
					}
					if (str.equals("#flow")) {
						parseFlow(in);
					}
					if (str.equals("#request")) {
						parseRequest(in);
					}
				}
			}
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	private void parseMap(Scanner in) {
		/**
		 * @REQUIRES: in;
		 * @MODIFIES: this.intMap;
		 * @EFFECTS: normal_behavior: this.intMap loaded.
		 * 			 exception_behavior(Throwable e): 
		 * 					print the wrong information. the System shut down.
		 */
		try {
			int line = 0;
			while (in.hasNextLine()) {
				String str = in.nextLine();

				str = str.replaceAll(" ", "").replaceAll("\t", "");
				if (str.equals(""))
					continue;
				if (str.equals("#end_map") && line == TaxiSys.SIZE) {
					mapLoaded = true;
					break;
				}
				if (str.equals("#end_map") && line == 0) {
					mapLoaded = false;
					break;
				}
				if (str.length() != TaxiSys.SIZE) {
					throw new Exception("Too many columns");
				}
				for (int i = 0; i < TaxiSys.SIZE; i++) {
					if (possibleNumber.indexOf(str.charAt(i)) == -1) {
						throw new Exception("Illegal number");
					}
					intMap[line][i] = possibleNumber.indexOf(str.charAt(i));
				}
				line++;
				if (line > TaxiSys.SIZE) {
					throw new Exception("Too many lines or invalid #end_map");
				}

			}

		} catch (Exception e) {
			System.out.println("Wrong map file input so that the program ends!" + " : " + e);
			System.exit(1);
		}
	}

	private void parseTaxi(Scanner in) {
		/**
		 * @REQUIRES: in;
		 * @MODIFIES: initTaxiList;
		 * @EFFECTS: normal_behavior: 
							initTaxiList.size()>=\old(initTaxiList.size());
		 * 			 exception_behavior(Throwable e): 
		 * 					print the wrong information. the System shut down.
		 */
		try {
			int count = 0;
			while (in.hasNextLine()) {
				String str = in.nextLine();
				String strNew = str.replaceAll(" ", "").replaceAll("\t", "");
				if (strNew.equals("#end_taxi")) {
					break;
				}
				if (strNew.equals("") || count > 100)
					continue;
				if (!str.matches("^No.\\d+ \\d+ \\d+ \\(\\d+,\\d+\\)$")) {
					System.out.println("Wrong taxiSet command");
					continue;
				}
				String strs[] = str.split("[ \\(),No.]");
				int taxiNumber = Integer.parseInt(strs[3]);
				int taxiState = Integer.parseInt(strs[4]);
				int taxiCredit = Integer.parseInt(strs[5]);
				int taxiX = Integer.parseInt(strs[7]);
				int taxiY = Integer.parseInt(strs[8]);
				if(taxiNumber<0||taxiNumber>99||taxiCredit<0||taxiCredit>100000) {
					continue;
				}
				if(taxiState!=0&&taxiState!=1&&taxiState!=2&&taxiState!=3) {
					continue;
				}
				count++;
				initTaxiList.add(new TaxiChange(taxiNumber, taxiState, taxiCredit, taxiX, taxiY));
			}
		} catch (Exception e) {
			System.out.println("Wrong taxi input so that the program ends!" + " : " + e);
			System.exit(1);
		}
	}

	private void parseFlow(Scanner in) {
		/**
		 * @REQUIRES: in;
		 * @MODIFIES: initFlowList;
		 * @EFFECTS: normal_behavior: 
		 *					initFlowList.size()>=\old(initFlowList.size());
		 * 			 exception_behavior(Throwable e): 
		 * 					print the wrong information. the System shut down.
		 */
		try {
			while (in.hasNextLine()) {
				String str = in.nextLine();
				String strNew = str.replaceAll(" ", "").replaceAll("\t", "");
				if (strNew.equals(""))
					continue;
				if (strNew.equals("#end_flow")) {// (x1,y1) (x2,y2) value
					break;
				}
				if (!str.matches("^\\(\\+?\\d+,\\+?\\d+\\) \\(\\+?\\d+,\\+?\\d+\\) \\+?\\d+$")) {
					System.out.println("Invalid Flow: " + str);
					continue;
				}
				String strs[] = strNew.split("[\\(), ]");
				int[] position = { Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), Integer.parseInt(strs[4]),
						Integer.parseInt(strs[5]) };
				int value = Integer.parseInt(strs[6]);
				// System.out.println(value);
				for (int number : position) {
					// System.out.println(number);
					if (number > 79 || number < 0) {
						System.out.println("Invalid Flow: " + str);
						continue;
					}
				}
				if (Math.abs(position[0] - position[2]) + Math.abs(position[3] - position[1]) != 1) {
					System.out.println("Invalid Flow: " + str);
					continue;
				}
				initFlowList.add(new FlowChange(position[0], position[1], position[2], position[3], value));
			}
		} catch (Exception e) {
			System.out.println("Wrong flow input so that the program ends!" + " : " + e);
			System.exit(1);
		}
	}

	private void parseRequest(Scanner in) {
		/**
		 * @REQUIRES: in;
		 * @MODIFIES: requestQueue;
		 * @EFFECTS: normal_behavior: 
		 *					requestQueue.size()>=\old(requestQueue.size());
		 * 			 exception_behavior(Exception e): 
		 * 					print the wrong information. the System shut down.
		 */
		try {
			int totalRequest = 0;
			while (in.hasNextLine()) {
				String str = in.nextLine();
				String strNew = str.replaceAll(" ", "").replaceAll("\t", "");
				if (strNew.equals("#end_request")) {
					break;
				}
				if (totalRequest > 100 || strNew.equals("")) {
					continue;
				}
				if (!str.matches("^\\[CR,\\(\\+?\\d+,\\+?\\d+\\)\\(\\+?\\d+,\\+?\\d+\\)\\]$")) {
					System.out.println("Invalid Request: " + str);
					continue;
				}
				String strs[] = strNew.split("[\\(),\\[\\]]");
				int[] position = { Integer.parseInt(strs[3]), Integer.parseInt(strs[4]), Integer.parseInt(strs[6]),
						Integer.parseInt(strs[7]) };
				for (int number : position) {
					// System.out.println(number);
					if (number > 79 || number < 0) {
						System.out.println("Invalid Request: " + str);
						continue;
					}
				}
				Point startPoint = taxiMap.getPoint(position[0], position[1]);
				Point endPoint = taxiMap.getPoint(position[2], position[3]);
				if (startPoint.equals(endPoint)) {
					System.out.println("Invalid Request: " + str);
					continue;
				}
				Request request = new Request(startPoint, endPoint, System.currentTimeMillis());
				synchronized (requestQueue) {
					if (requestQueue.contains(request)) {
						System.out.println("A homogenous request " + request.getID());
					} else {
						totalRequest++;
						request.initRequest(taxiMap);
						// taxiGUI.RequestTaxi(startPoint, endPoint);// It's better not to use it;
						requestQueue.offer(request);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Wrong request input so that the program ends!" + " : " + e);
			System.exit(1);
		}
	}

}
