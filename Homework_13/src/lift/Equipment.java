package lift;

public interface Equipment {
	/**
	 * @Overview: Equipment is a abstract model of elevator. And it provides some
	 *            abstract methods.
	 */

	/**
	 * @REQUIRES: mainRq!=null; rq!=null;
	 * @MODIFIES: None;
	 * @EFFECTS: \result == (According to the information, predict a time between
	 *           two request);
	 */
	public double predictTime(Request mainRq, Request rq);

	/**
	 * @REQUIRES: rq!=null;
	 * @MODIFIES: this;
	 * @EFFECTS: (According to the information, move)
	 */
	public void move(Request rq);

	public int getFloor();
}
