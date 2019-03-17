package taxi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestQueue {
	private BlockingQueue<Request> requestQueue;
	private BlockingQueue<Request> deleteList;

	public RequestQueue() {
		requestQueue = new LinkedBlockingQueue<Request>();
		deleteList = new LinkedBlockingQueue<Request>();
	}

	public Boolean offer(Request request) {
		return requestQueue.offer(request);
	}

	public Boolean isEmpty() {
		return requestQueue.isEmpty();
	}

	public Boolean contains(Request request) {
		return requestQueue.contains(request);
	}

	public void checkListAndDisptach() {
		try {
			synchronized (this) {
				for (Request request : requestQueue) {
					request.checkTaxiMap();
					if (request.dispatch()) {
						deleteList.add(request);
					}
				}
				requestQueue.removeAll(deleteList);
				deleteList.clear();
			}
		} catch (Exception e) {
			System.out.println("Request Dispatching Wrong");

		}

	}
}
