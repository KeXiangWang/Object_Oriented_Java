package lift;

import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Matcher;

public class InputHandler extends SubScheduler implements Runnable {
	private final static Pattern PATTERNFR = Pattern.compile("\\(FR,\\+?[0-9]{1,50},(DOWN|UP)\\)");
	private final static Pattern PATTERNER = Pattern.compile("\\(ER,#\\+?[0-9]{1,50},\\+?[0-9]{1,50}\\)");
	private Scheduler scheduler;
	private int order;
	private Shutter shutter;

	public InputHandler() {

	}

	public InputHandler(Scheduler scheduler, Shutter shutter) {
		this.scheduler = scheduler;
		order = 0;
		this.shutter = shutter;
	}

	public void printInvalid(long currentTime, String string) {
		String stime = String.format("%.1f", (double) (currentTime - ElevatorSys.startTime) / 1000);
		System.out.println(currentTime + ":INVALID[" + string + "," + stime + "]");
	}

	public void parse(String str) {
		int i;
		String[] strs = str.split(";", 11);
		long currentTime = System.currentTimeMillis();
		try {
			synchronized (scheduler) {
				for (i = 0; i < strs.length && i < 10; i++) {
					try {
						Matcher mFR = PATTERNFR.matcher(strs[i]);
						Matcher mER = PATTERNER.matcher(strs[i]);
						if (mFR.matches()) {
							String[] strs2 = strs[i].split("[\\(\\),]");
							int target = Integer.parseInt(strs2[2]);
							Direction direction = strs2[3].equals("UP") ? Direction.UP : Direction.DOWN;
							if (target == 1 && direction == Direction.DOWN || target == 20 && direction == Direction.UP
									|| target <= 0 || target > 20) {
								printInvalid(currentTime, strs[i]);
								continue;
							}
							scheduler.addFR(Requester.FR, target, direction, currentTime - ElevatorSys.startTime, order,
									1);
							order++;

						} else if (mER.matches()) {
							String[] strs2 = strs[i].split("[\\(\\),#]");

							int target = Integer.parseInt(strs2[4]);
							int numElevator = Integer.parseInt(strs2[3]);
							if (target <= 0 || target > 20 || numElevator <= 0 || numElevator > 3) {
								printInvalid(currentTime, strs[i]);
								continue;
							}
							scheduler.addER(Requester.ER, numElevator, target, currentTime - ElevatorSys.startTime,
									order);
							order++;
						} else {
							printInvalid(currentTime, strs[i]);
						}
					} catch (Exception e) {
						printInvalid(currentTime, strs[i]);
						continue;
					}
				}
				if (i == 10 && strs.length == 11) {
					printInvalid(currentTime, strs[i]);
				}
				scheduler.notify();
			}
		} catch (Exception e) {
			printInvalid(currentTime, str);
		}
	}

	@Override
	public void run() {
		File file = new File("result.txt");
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			PrintStream printStream = new PrintStream(fileOutputStream);
			System.setOut(printStream);
		} catch (FileNotFoundException e) {
			System.out.println("wrongFile");
		}
		Scanner in = new Scanner(System.in);
		int rqNum = 0;
		while (in.hasNextLine()) {
			if (rqNum == 0) {
				ElevatorSys.startTime = System.currentTimeMillis();
			}
			rqNum++;
			String str = in.nextLine();
			String str2 = str.replaceAll(" ", "");
			if (str2.equals("END") || rqNum >= 51) {
				shutter.setClock(1);
				try {
					synchronized (scheduler) {
						scheduler.notify();
					}
				} catch (Exception e) {

				}
				break;
			} else {
				parse(str2);
			}
		}
		in.close();
	}
}
