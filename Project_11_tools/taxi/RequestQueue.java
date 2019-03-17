package taxi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestQueue {
	/**
	 * @Overview: RequestQueue store all the legal request(not including the
	 *            homogeneous request), and provide some add and delete method.
	 */
	private BlockingQueue<Request> requestQueue;
	private BlockingQueue<Request> deleteList;

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this.requestQueue; this.deleteList;
	 * @EFFECTS: this.requestQueue = new LinkedBlockingQueue<Request>();
	 *           this.deleteList = new LinkedBlockingQueue<Request>();
	 */
	public RequestQueue() {
		this.requestQueue = new LinkedBlockingQueue<Request>();
		this.deleteList = new LinkedBlockingQueue<Request>();
	}

	/**
	 * @REQUIRES: request！=null;
	 * @MODIFIES: this.request;
	 * @EFFECTS: this.requestQueue.contains(request);
	 */
	public Boolean offer(Request request) {
		return requestQueue.offer(request);
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == requestQueue.isEmpty();
	 */
	public Boolean isEmpty() {
		return requestQueue.isEmpty();
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == requestQueue.contains(request);
	 */
	public Boolean contains(Request request) {
		return requestQueue.contains(request);
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: requestQueue; deleteList;
	 * @EFFECTS: For each request in the requestQueue, make a try to dispatch it a
	 *           taxi. When successfully, remove the request from requestQueue.
	 * @THREAD_REQUIRES \locked(this);
	 * @THREAD_EFFECTS: \locked(this);
	 */
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

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return this.requestQueue != null && this.deleteList != null;
	}
}
