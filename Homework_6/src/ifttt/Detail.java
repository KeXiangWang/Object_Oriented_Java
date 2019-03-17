package ifttt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Detail implements Runnable {
	private PrintWriter printWriter;
	private Shutter shutter;

	public Detail(Shutter shutter) {
		this.shutter =shutter;
		try {
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter("detail.txt")));
		} catch (Exception e) {
			System.out.println("failed to creat detail.txt");
		}
	}

	public void setDetail(Trigger trigger, SafeFile originFile, SafeFile newFile) {

		if (trigger == Trigger.RENAMED) {
			printWriter.println("renamed:");
		} else if (trigger == Trigger.MODIFIED) { 
			printWriter.println("modified:");
		} else if (trigger == Trigger.PATH_CHANGED) {
			printWriter.println("pathchanged:");
		} else {
			printWriter.println("sizechanged:");
		}
		printWriter.println("name: "+originFile.getName()+" -> "+ newFile.getName());
		printWriter.println("path: "+originFile.getAbsolutePath()+" -> "+ newFile.getAbsolutePath());
		printWriter.println("time: "+originFile.getOriginTime()+" -> "+ newFile.getOriginTime());
		printWriter.println("size: "+originFile.getOriginLength()+" -> "+ newFile.getOriginLength());
		printWriter.println("---------------------------------------------------------------------");
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

			}
		}

	}
}
