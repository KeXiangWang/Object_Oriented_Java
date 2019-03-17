package ifttt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Summary implements Runnable {
	private PrintWriter printWriter;
	private int renamed;
	private int modified;
	private int pathChanged;
	private int sizeChanged;
	private Shutter shutter;

	public Summary(Shutter shutter) {
		renamed = 0;
		modified = 0;
		pathChanged = 0;
		sizeChanged = 0;
		this.shutter = shutter;
		try {
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter("summary.txt")));
		} catch (Exception e) {
			System.out.println("failed to creat summary.txt");
		}
	}

	public void setSummmary(Trigger trigger) {
		if (trigger == Trigger.RENAMED) {
			renamed++;
		} else if (trigger == Trigger.MODIFIED) {
			modified++;
		} else if (trigger == Trigger.PATH_CHANGED) {
			pathChanged++;
		} else {
			sizeChanged++;
		}
	}

	public void printSummary() {
		printWriter.println("renamed: " + renamed + "  modified: " + modified + "  pathchanged: " + pathChanged
				+ "  sizeChanged: " + sizeChanged);
		printWriter.flush();
	}

	public void run() {
		while (true) {
			synchronized (this) {
				try {
					this.wait();
					if(shutter.getState() == 1 ) {
						Thread.sleep(300);
						break;
					}
				} catch (InterruptedException e) {
				}
				printSummary();

			}
		}

	}

}
