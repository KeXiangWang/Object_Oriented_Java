package lift;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Scheduler implements Runnable {
	private BlockingQueue<Request> requestQueue;
	private Elevator elevator1, elevator2, elevator3;
	private Request[] unhadledRequest;
	private int[] validation;
	private int unhandleFront;
	private int unhandleRear;
	private Shutter shutter;

	public Scheduler(Elevator elevator1, Elevator elevator2, Elevator elevator3, Shutter shutter) {
		this.elevator1 = elevator1;
		this.elevator2 = elevator2;
		this.elevator3 = elevator3;
		requestQueue = new ArrayBlockingQueue<Request>(500);
		validation = new int[500];
		unhadledRequest = new Request[500];
		unhandleFront = 0;
		unhandleRear = 0;
		this.shutter = shutter;
	}

	public Elevator getElevator(Request request) {

		if (request.getGuest() == Requester.ER) {
			if (request.getNumOfElevator() == 1) {
				Boolean picked1 = elevator1.ableToPick(request);
				if (picked1) {
					return elevator1;
				} else if (elevator1.getArrived()) {
					return elevator1;
				} else {
					return null;
				}
			} else if (request.getNumOfElevator() == 2) {
				Boolean picked2 = elevator2.ableToPick(request);
				if (picked2) {
					return elevator2;
				} else if (elevator2.getArrived()) {
					return elevator2;
				} else {
					return null;
				}
			} else {
				Boolean picked3 = elevator3.ableToPick(request);
				if (picked3) {
					return elevator3;
				} else if (elevator3.getArrived()) {
					return elevator3;
				} else {
					return null;
				}
			}
		} else {
			Boolean picked1 = elevator1.ableToPick(request);
			Boolean picked2 = elevator2.ableToPick(request);
			Boolean picked3 = elevator3.ableToPick(request);
			int workLoad1 = elevator1.getWorkLoad();
			int workLoad2 = elevator2.getWorkLoad();
			int workLoad3 = elevator3.getWorkLoad();
			if (picked1) {
				if (picked2 && workLoad2 < workLoad1) {
					if (picked3 && workLoad3 < workLoad2) {
						return elevator3;
					} else {
						return elevator2;
					}
				} else {
					if (picked3 && workLoad3 < workLoad1) {
						return elevator3;
					} else {
						return elevator1;
					}
				}
			} else {
				if (picked2) {
					if (picked3 && workLoad3 < workLoad2) {
						return elevator3;
					} else {
						return elevator2;
					}
				} else {
					if (picked3) {
						return elevator3;
					} else {
						Boolean arrived1 = elevator1.getArrived();
						Boolean arrived2 = elevator2.getArrived();
						Boolean arrived3 = elevator3.getArrived();

						if (arrived1 || arrived2 || arrived3) {

							if (arrived1) {

								if (arrived2 && workLoad2 < workLoad1) {
									if (arrived3 && workLoad3 < workLoad2) {
										return elevator3;
									} else {
										return elevator2;
									}
								} else {

									if (arrived3 && workLoad3 < workLoad1) {
										return elevator3;
									} else {
										return elevator1;
									}
								}
							} else {
								if (arrived2) {
									if (arrived3 && workLoad3 < workLoad2) {
										return elevator3;
									} else {
										return elevator2;
									}
								} else {
									if (arrived3) {
										return elevator3;
									} else {
										return null;
									}

								}
							}
						} else {
							return null;
						}

					}
				}
			}
		}
	}

	public void dispatchRequest() {
		int i;
		for (i = unhandleFront; i < unhandleRear; i++) {
			if (validation[i] == 0) {
				unhandleFront++;
			} else {
				break;
			}
		}
		for (i = unhandleFront; i < unhandleRear; i++) {
			if (validation[i] == 0) {
				continue;
			} else {
				Request request = unhadledRequest[i];
				Elevator elevatorUsable = getElevator(request);
				if (elevatorUsable == null) {
					validation[i] = 1; // Keep it!
				} else {
					synchronized (elevatorUsable) {
						if (elevatorUsable.getArrived()) {
							elevatorUsable.setMain(request);
						}
						elevatorUsable.catchRequest(request);
						validation[i] = 0; // Dispatch it!
						elevatorUsable.notify();
					}
				}
			}
		}
		for (i = unhandleFront; i < unhandleRear; i++) {
			if (validation[i] == 0) {
				unhandleFront++;
			} else {
				break;
			}
		}
		while (!requestQueue.isEmpty()) {
			Request request = requestQueue.poll();
			Elevator elevatorUsable = getElevator(request);
			if (elevatorUsable == null) {
				validation[unhandleRear] = 1; // Add to unhandle！
				unhadledRequest[unhandleRear++] = request;
			} else {
				synchronized (elevatorUsable) {
					if (elevatorUsable.getArrived()) {
						elevatorUsable.setMain(request);
					}
					elevatorUsable.catchRequest(request); // Dispatch it!
					elevatorUsable.notify();
				}
			}

		}
	}

	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				try {
					this.wait();
					dispatchRequest();
				} catch (InterruptedException ex) {
					System.out.println("schduler syncronized exception");
				}
			}
			if (unhandleFront == unhandleRear && requestQueue.isEmpty() && shutter.getClock() == 1) {
				shutter.setClock(2);
				synchronized (elevator1) {
					elevator1.notify();
				}
				synchronized (elevator2) {
					elevator2.notify();
				}
				synchronized (elevator3) {
					elevator3.notify();
				}
				break;
			}
		}
	}

	public boolean addFR(Requester a, int b, Direction c, long d, int e, int n) {
		synchronized (this) {
			Request request = new FRequest(a, b, c, d, e);
			try {
				requestQueue.put(request);
				return true;
			} catch (InterruptedException exception) {
				return false;
			}
		}
	}

	public boolean addER(Requester a, int b, int c, long d, int e) {
		synchronized (this) {
			Request request = new ERequest(a, b, c, d, e);

			try {
				requestQueue.put(request);
				return true;
			} catch (InterruptedException exception) {
				return false;
			}
		}
	}
}
