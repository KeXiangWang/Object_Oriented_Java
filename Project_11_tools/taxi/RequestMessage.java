package taxi;

import java.io.PrintWriter;
import java.util.ArrayList;

public class RequestMessage {
	/**
	 * @Overview: This class is to record all the messages produced when a taxi
	 *            serve a request. And it provide method to add information and
	 *            print all the information.
	 */
	private ArrayList<String> message;

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: this.message;
	 * @EFFECTS: this.message == new ArrayList<String>();
	 */
	public RequestMessage() {
		message = new ArrayList<String>();
	}

	/**
	 * @REQUIRES: string != null;
	 * @MODIFIES: this.message;
	 * @EFFECTS: this.message.contains(string);
	 */
	public void addMessage(String string) {
		message.add(string);
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: Print all the message recorded in this class;
	 */
	public void printMessage(PrintWriter out) {
		for (String string : message) {
			out.println(string);
		}
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return this.message != null;
	}
}
