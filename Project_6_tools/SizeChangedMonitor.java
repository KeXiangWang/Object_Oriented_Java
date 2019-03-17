package ifttt;

import java.io.File;
import java.util.ArrayList;

public class SizeChangedMonitor extends Monitor implements Runnable{
	private Request request;
	private Summary summary;
	private Detail detail;
	private Recover recover;
	private Task task;
	private String location;
	private ArrayList<File> fileList;
	public SizeChangedMonitor(Request request, Summary summary, Detail detail, Recover recover) {
		this.request = request;
		this.summary = summary;
		this.detail = detail;
		this.recover = recover;
	}

	public void run() {

	}
}
