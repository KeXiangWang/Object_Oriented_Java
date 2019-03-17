package taxi;

import java.util.ArrayList;

public class TaxiChange {
	/**
	 * @Overview: TaxiChange record a flow-set when initializing, and able to set
	 *            this flow-set to the flows counting class. A flow-set consist its
	 *            position and it value to set.
	 */
	private int taxiNumber;
	private int taxiState;
	private int taxiCredit;
	private int taxiX;
	private int taxiY;

	/**
	 * @MODIFIES: this.taxiNumber; this.taxiState; this.taxiCredit; this.taxiX;
	 *            this.taxiY;
	 * @EFFECTS: this.taxiNumber==taxiNumber; this.taxiCredit==taxiCredit;
	 *           this.taxiState==taxiState; this.taxiX==taxiX; this.taxiY==taxiY;
	 */
	public TaxiChange(int taxiNumber, int taxiState, int taxiCredit, int taxiX, int taxiY) {

		this.taxiNumber = taxiNumber;
		this.taxiCredit = taxiCredit;
		this.taxiState = taxiState;
		this.taxiX = taxiX;
		this.taxiY = taxiY;
	}

	/**
	 * @REQUIRES: taxiList!=null;
	 * @MODIFIES: taxiList;
	 * @EFFECTS: taxiList.get(taxiNumber).state==taxiState;
	 *           taxiList.get(taxiNumber).credit==credit;
	 *           taxiList.get(taxiNumber).location.x==taxiX;
	 *           taxiList.get(taxiNumber).locatioin.y==taxiY;
	 */
	public void changeTaxi(ArrayList<Taxi> taxiList) {
		Taxi taxi = taxiList.get(taxiNumber);
		taxi.setTaxiInfo(taxiState, taxiCredit, taxiX, taxiY);
	}

	/**
	 * @REQUIRES:None;
	 * @MODIFIES:None;
	 * @EFFECTS:(all the conditions satisfied) ==> \result == true;
	 */
	public boolean repOK() {
		return true;
	}
}
