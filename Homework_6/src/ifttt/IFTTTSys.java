package ifttt;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class IFTTTSys {
	public static void main(String args[]) {
		BlockingQueue<Request> requestQueue = new LinkedBlockingQueue<Request>();
		ArrayList<Monitor> monitorList = new ArrayList<Monitor>();
		ArrayList<String> pathList = new ArrayList<String>();
		InputHandler inputHandler = new InputHandler(requestQueue);
		Shutter shutter = new Shutter();
		Summary summary = new Summary(shutter);
		Detail detail = new Detail(shutter);
		TestThread testThread = new TestThread(shutter,summary,detail );
		inputHandler.read();
		Boolean gotRequest;
		Boolean hadPath;
		try {
			for (Request request : requestQueue) {
				gotRequest = false;
				hadPath = false;
				for (Monitor monitor : monitorList) {
					int judge = monitor.checkRequest(request);
					if (judge == 1) {
						gotRequest = true;
					} else if (judge == 2) {
						gotRequest = true;
					}
				}
				File pathFile = new File(request.getPath());
				for (String path : pathList) {
					if (pathFile.getAbsolutePath().equals(path)) {
						hadPath = true;
					}
				}
				if (!hadPath) {
					pathList.add(pathFile.getAbsolutePath());
				}
				if (pathList.size() > 10) {
					break;
				}
				if (!gotRequest) {
					Monitor requestMoniter = new Monitor(request, summary, detail, shutter);
					monitorList.add(requestMoniter);
					Thread monitor = new Thread(requestMoniter,
							request.getPath() + " " + request.geTrigger() + " " + request.geTask());
					monitor.start();
				}
			}
			new Thread(summary).start();
			new Thread(detail).start();
			new Thread(testThread).start();
		}catch (Throwable e) {
		}
		
	}
}
