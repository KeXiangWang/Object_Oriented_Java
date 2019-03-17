package ifttt;

import java.io.File;
import java.util.ArrayList;

public class RenamedMonitor extends Monitor implements Runnable {
	private Request request;
	private Summary summary;
	private Detail detail;
	private Recover recover;
	private Task task;
	private String location;
	private ArrayList<File> fileList;
	public RenamedMonitor(Request request, Summary summary, Detail detail, Recover recover) {
		this.request = request;
		this.summary = summary;
		this.detail = detail;
		this.recover = recover;
	}
	public Boolean checkTrigger() {
		for(File file: fileList) {
			if(file.exists()) {
				return false;
			}else {
				File parentFile = file.getParentFile();
				File[] files = parentFile.listFiles();
				for (File f : files) {
					if (f.isFile()) {
						if(f.lastModified()==file.lastModified()||f.length()==file.length()) {
							return true;
						}
					} 
				}
			}
		}
		return false;
	}
	public Boolean executeTask() {
		System.out.println("renamed");
		return true;
	}
	
	public void run() {
		parseRequest(request);
		while(true) {
			try {
				Thread.sleep(100);
				if(checkTrigger()) {
					executeTask();
				}else {
					continue;
				}
			}catch(InterruptedException e) {
				
			}
		}
	}
}
