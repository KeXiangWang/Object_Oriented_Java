package taxi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestQueue {
	private BlockingQueue<Request> requestQueue;
	private BlockingQueue<Request> deleteList;

	public RequestQueue() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: 
		 * 		this.requestQueue;
		 * 		this.deleteList;
		 * @EFFECTS: 
		 *		this.requestQueue = new LinkedBlockingQueue<Request>();
		 *		this.deleteList = new LinkedBlockingQueue<Request>();
		 */
		this.requestQueue = new LinkedBlockingQueue<Request>();
		this.deleteList = new LinkedBlockingQueue<Request>();
	}

	public Boolean offer(Request request) {
		/**
		 * @REQUIRES: request！=null;
		 * @MODIFIES: this.request;
		 * @EFFECTS: this.requestQueue.contains(request);
		 */
		return requestQueue.offer(request);
	}

	public Boolean isEmpty() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: \result == requestQueue.isEmpty();
		 */
		return requestQueue.isEmpty();
	}

	public Boolean contains(Request request) {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: None;
		 * @EFFECTS: \result ==  requestQueue.contains(request);
		 */
		return requestQueue.contains(request);
	}

	public void checkListAndDisptach() {
		/**
		 * @REQUIRES: None;
		 * @MODIFIES: 
		 * 		requestQueue;
		 * 		deleteList;
		 * @EFFECTS: 
		 * 		For each request in the requestQueue, make a try to dispatch it a taxi.
		 * 		When successfully, remove the request from requestQueue.  
		 */
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
