package ifttt;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class InputHandler {
	private BlockingQueue<Request> requestQueue;

	public InputHandler(BlockingQueue<Request> requestQueue) {
		this.requestQueue = requestQueue;
	}

	public void read() {
		Scanner in = new Scanner(System.in);
		while (in.hasNextLine()) {
			String str = in.nextLine();
			String str1 = str.replaceAll(" ", "");
			if (str1.equals("END") ) {
				break;
			} else {
				Boolean got = parse(str);
				if (!got) {
					System.out.println("INVALID Request: " + str);
				}
			}
		}
		in.close();
	}

	public Boolean parse(String str) {
		try {
			String[] strs = str.trim().split("( )+");
			if (!strs[0].equals("IF") || !strs[3].equals("THEN")) {
				return false;
			}
			if(strs[2].equals("modified")&&strs[4].equals("recover")||strs[2].equals("size-changed")&&strs[4].equals("recover")) {
				return false;
			}
			if (strs.length != 5) {
				return false;
			}
			String filePath = strs[1];
			File file = new File(filePath);
			if (!file.exists()) {
				return false;
			}
			Request request = new Request();
			if (request.setRequest(filePath, strs[2], strs[4]) == false) {
				return false;
			}
			try {
				requestQueue.put(request);
				return true;
			} catch (InterruptedException exception) {
				return false;
			}
		}catch (Exception e) {
			return false;
		}
		
	}
}
