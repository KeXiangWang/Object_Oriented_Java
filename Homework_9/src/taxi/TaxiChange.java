package taxi;

import java.util.ArrayList;

public class TaxiChange {
	private int taxiNumber;
	private int taxiState;
	private int taxiCredit;
	private int taxiX;
	private int taxiY;

	public TaxiChange(int taxiNumber, int taxiState, int taxiCredit, int taxiX, int taxiY) {
		/**
		 * @MODIFIES: this.taxiNumber; this.taxiState; this.taxiCredit; this.taxiX;
		 *            this.taxiY;
		 * @EFFECTS: this.taxiNumber==taxiNumber; this.taxiCredit==taxiCredit;
		 *           this.taxiState==taxiState; this.taxiX==taxiX; this.taxiY==taxiY;
		 */
		this.taxiNumber = taxiNumber;
		this.taxiCredit = taxiCredit;
		this.taxiState = taxiState;
		this.taxiX = taxiX;
		this.taxiY = taxiY;
	}

	public void changeTaxi(ArrayList<Taxi> taxiList) {
		/**
		 * @REQUIRES: taxiList!=null;
		 * @MODIFIES: taxiList;
		 * @EFFECTS: taxiList.get(taxiNumber).state==taxiState;
		 *           taxiList.get(taxiNumber).credit==credit;
		 *           taxiList.get(taxiNumber).location.x==taxiX;
		 *           taxiList.get(taxiNumber).locatioin.y==taxiY;
		 */
		Taxi taxi = taxiList.get(taxiNumber);
		taxi.setTaxiInfo(taxiState, taxiCredit, taxiX, taxiY);
	}

}
