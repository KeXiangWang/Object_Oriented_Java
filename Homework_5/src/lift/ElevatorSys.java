package lift;

public class ElevatorSys {
	protected static long startTime;

	public static void main(String args[]) {
		Shutter shutter = new Shutter();
		Elevator elevator1 = new Elevator(1, shutter);
		Elevator elevator2 = new Elevator(2, shutter);
		Elevator elevator3 = new Elevator(3, shutter);
		Scheduler scheduler = new Scheduler(elevator1, elevator2, elevator3, shutter);
		InputHandler inputHandler = new InputHandler(scheduler, shutter);
		elevator1.catchScheduler(scheduler);
		elevator2.catchScheduler(scheduler);
		elevator3.catchScheduler(scheduler);
		Thread input = new Thread(inputHandler, "Thread-inputHandler");
		Thread sch = new Thread(scheduler, "Thread-scheduler");
		Thread ele1 = new Thread(elevator1, "Thread-elevator1");
		Thread ele2 = new Thread(elevator2, "Thread-elevator2");
		Thread ele3 = new Thread(elevator3, "Thread-elevator3");
		input.start();
		sch.start();
		ele1.start();
		ele2.start();
		ele3.start();
	}
}
