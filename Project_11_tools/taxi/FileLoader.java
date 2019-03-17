package taxi;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class FileLoader {
	/**
	 * @Overview: FileLoader is a loader to load map, light, flow, request, taxi.
	 *            According the text of the file, add elements to the requestQueue,
	 *            initTaxiList, initFlowList, lightList. If the necessary list not
	 *            initialized, the system shut down. If the unnecessary get the
	 *            wrong element, ignore it.
	 */
	private String possibleNumber = "0123";
	protected static int[][] intMap;
	private RequestQueue requestQueue;
	private TaxiMap taxiMap;
	private ArrayList<TaxiChange> initTaxiList;
	private ArrayList<FlowChange> initFlowList;
	private ArrayList<Point> lightList;
	private Boolean mapLoaded;
	private Boolean lightLoaded;

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this.intMap;
	 * @EFFECTS: intMap == new int[TaxiSys.SIZE][TaxiSys.SIZE];
	 */
	public FileLoader() {
		intMap = new int[TaxiSys.SIZE][TaxiSys.SIZE];
		mapLoaded = false;
		lightLoaded = false;
	}

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
	public void Load(RequestQueue requestQueue, TaxiMap taxiMap, Scanner in, ArrayList<TaxiChange> initTaxiList,
			ArrayList<FlowChange> initFlowList, ArrayList<Point> lightList) {
		this.requestQueue = requestQueue;
		this.taxiMap = taxiMap;
		this.initTaxiList = initTaxiList;
		this.initFlowList = initFlowList;
		this.lightList = lightList;
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
	public Boolean parseFile(String filename) {
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
						mapSet(in);
					}
					if (str.equals("#light")) {
						while (in.hasNextLine()) {
							String string = in.nextLine();
							string = string.replaceAll(" ", "").replaceAll("\t", "");
							if (string.equals(""))
								continue;
							if (string.equals("#end_light")) {
								break;
							}
							File lightFile = new File(string);
							if (lightFile.exists()) {
								Scanner newIn = new Scanner(new FileInputStream(lightFile));
								parseLight(newIn);
								newIn.close();
								lightLoaded = true;
							}
						}
						if (!lightLoaded) {
							System.out.println("Lack the light File");
							System.exit(1);
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

	/**
	 * @REQUIRES: in!=null;
	 * @MODIFIES: None;
	 * @EFFECTS: (there is a designated map file)==> parseMap(designated map);
	 *           (there is not a designated map file)==> parseMap("map_ori.txt");
	 *           (!(new File(designated map)).exists()&&!(new
	 *           File("map_ori.txt")).exists()) ==> System.exit(1);
	 *           exception_behavior(Throwable e): print the wrong information. the
	 *           System shut down.
	 */
	private void mapSet(Scanner in) {
		try {
			while (in.hasNextLine()) {
				String str = in.nextLine();
				str = str.replaceAll(" ", "").replaceAll("\t", "");
				if (str.equals(""))
					continue;
				if (str.equals("#end_map")) {
					break;
				}
				File file = new File(str);
				if (file.exists()) {
					Scanner newIn = new Scanner(new FileInputStream(file));
					parseMap(newIn);
					newIn.close();
				}
			}

			if (!mapLoaded) {
				for (int i = 0; i < TaxiSys.SIZE; i++) {
					for (int j = 0; j < TaxiSys.SIZE; j++) {
						intMap[i][j] = 0;
					}
				}
				File file = new File("map_ori.txt");
				if (file.exists()) {
					Scanner oriIn = new Scanner(new FileInputStream("map_ori.txt"));
					parseMap(oriIn);
					oriIn.close();
				} else {
					System.out.println("Lack map_ori.txt");
					System.exit(1);
				}
			}

		} catch (Exception E) {
		}

	}

	/**
	 * @REQUIRES: in!=null;
	 * @MODIFIES: this.intMap;
	 * @EFFECTS: normal_behavior: this.intMap loaded. exception_behavior(Throwable
	 *           e): print the wrong information. the System shut down.
	 */
	private void parseMap(Scanner in) {
		try {
			int line = 0;
			while (in.hasNextLine()) {
				String str = in.nextLine();
				str = str.replaceAll(" ", "").replaceAll("\t", "");
				if (str.equals(""))
					continue;
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
					throw new Exception("Too many lines");
				}
				if (line == TaxiSys.SIZE) {
					mapLoaded = true;
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Wrong map file input so that the program ends!" + " : " + e);
			System.exit(1);
		}
	}

	/**
	 * @REQUIRES: in!=null;
	 * @MODIFIES: initTaxiList;
	 * @EFFECTS: normal_behavior: initTaxiList.size()>=\old(initTaxiList.size());
	 *           exception_behavior(Throwable e): print the wrong information. the
	 *           System shut down.
	 */
	private void parseTaxi(Scanner in) {
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
				if (taxiNumber < 0 || taxiNumber > 99 || taxiCredit < 0 || taxiCredit > 100000) {
					continue;
				}
				if (taxiState != 0 && taxiState != 1 && taxiState != 2 && taxiState != 3) {
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

	/**
	 * @REQUIRES: in!=null;
	 * @MODIFIES: initFlowList;
	 * @EFFECTS: normal_behavior: initFlowList.size()>=\old(initFlowList.size());
	 *           exception_behavior(Throwable e): print the wrong information. the
	 *           System shut down.
	 */
	private void parseFlow(Scanner in) {
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
				for (int number : position) {
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

	/**
	 * @REQUIRES: in!=null;
	 * @MODIFIES: requestQueue;
	 * @EFFECTS: normal_behavior: requestQueue.size()>=\old(requestQueue.size());
	 *           exception_behavior(Exception e): print the wrong information. the
	 *           System shut down.
	 */
	private void parseRequest(Scanner in) {
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
				// System.out.println(str);
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
						new Thread(request).start();
						// System.out.println("???");
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Wrong request input so that the program ends!" + " : " + e);
			System.exit(1);
		}
	}

	/**
	 * @REQUIRES: in!=null;
	 * @MODIFIES: this.intMap;
	 * @EFFECTS: normal_behavior: this.intMap loaded. exception_behavior(Throwable
	 *           e): print the wrong information. the System shut down.
	 */
	private void parseLight(Scanner in) {
		try {
			int line = 0;

			while (in.hasNextLine()) {
				String str = in.nextLine();
				str = str.replaceAll(" ", "").replaceAll("\t", "");
				if (str.equals(""))
					continue;
				if (str.length() != TaxiSys.SIZE) {
					throw new Exception("Too many columns");
				}
				for (int i = 0; i < TaxiSys.SIZE; i++) {
					if (str.charAt(i) != '0' && str.charAt(i) != '1') {
						System.out.println(str);
						throw new Exception("Illegal number");
					}
					if (str.charAt(i) == '1') {
						lightList.add(taxiMap.getPoint(line, i));
					}
				}
				line++;
				if (line > TaxiSys.SIZE) {
					throw new Exception("Too many lines or invalid #end_map");
				}
				if (line == TaxiSys.SIZE) {
					break;
				}
			}

		} catch (Exception e) {
			System.out.println("Wrong light input so that the program ends!" + " : " + e);
			System.exit(1);
		}
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return (intMap != null);
	}
}
