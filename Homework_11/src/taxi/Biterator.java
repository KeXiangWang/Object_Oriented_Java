package taxi;

import java.util.ArrayList;

public class Biterator {
	/**
	 * @Overview: This class is a Bi-directional iterator. It consist of a list of
	 *            request the taxi served and a pointer to iterate.
	 */
	private ArrayList<RequestMessage> messages;
	private int index;

	/**
	 * @REQUIRES: messages != null;
	 * @MODIFIES: this.messages;
	 * @EFFECTS: this.messages == messages; index == 0;
	 */
	public Biterator(ArrayList<RequestMessage> messages) {
		this.messages = messages;
		this.index = 0;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: (index < 0 || index >= messages.size()) ==> \result == true;
	 *           (!(index < 0 || index >= messages.size())) ==> \result == false;
	 */
	public boolean hasNext() {
		if (index < 0 || index >= messages.size()) {
			return false;
		}
		return true;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: (index-1 < 0 || index-1 >= messages.size()) ==> \result == true;
	 *           (!(index-1 < 0 || index-1 >= messages.size())) ==> \result ==
	 *           false;
	 */
	public boolean hasPrevious() {
		if (index - 1 < 0 || index - 1 >= messages.size()) {
			return false;
		}
		return true;
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: (messages.get(\old(index))!=null) ==> \result ==
	 *           messages.get(index)&&index == \old(index)+1;
	 */
	public RequestMessage next() throws Exception {
		try {
			RequestMessage requestMessage = messages.get(index);
			index++;
			return requestMessage;
		} catch (Exception e) {
			throw new Exception("Illegal Request");
		}
	}

	/**
	 * @REQUIRES: None;
	 * @MODIFIES: None;
	 * @EFFECTS: (messages.get(index)!=null) ==> \result ==
	 *           messages.get(index)&&index =
	 *           \old(index)-1;exception_behavior(Exception e);
	 */
	public RequestMessage previous() throws Exception {
		index--;
		try {
			RequestMessage requestMessage = messages.get(index);
			return requestMessage;
		} catch (Exception e) {
			index++;
			throw new Exception("Illegal Request");
		}
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return this.messages != null;
	}
}
