/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package network.bisq.blnfeepoc.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Model class representing a Bitcoin trade between a buyer and seller.
 * Contains trade details, status, and fee payment information.
 */
public class Trade {
	private String id;
	private String sellerId;
	private String buyerId;
	private long amountSats;
	private double pricePerBitcoin;
	private String status;
	private LocalDateTime createdAt;
	private String feeInvoice;
	private boolean feePaid;

	/**
	 * Creates a new trade with the specified parameters.
	 * Generates a random UUID as the trade ID and sets initial status.
	 *
	 * @param sellerId The ID of the seller
	 * @param buyerId The ID of the buyer
	 * @param amountSats The trade amount in Satoshis
	 * @param pricePerBitcoin The price per Bitcoin in USD
	 */
	public Trade(String sellerId, String buyerId, long amountSats, double pricePerBitcoin) {
		this.id = UUID.randomUUID().toString();
		this.sellerId = sellerId;
		this.buyerId = buyerId;
		this.amountSats = amountSats;
		this.pricePerBitcoin = pricePerBitcoin;
		this.status = "CREATED";
		this.createdAt = LocalDateTime.now();
		this.feePaid = false;
	}

	/**
	 * Gets the unique identifier of this trade.
	 *
	 * @return The trade ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the ID of the seller.
	 *
	 * @return The seller ID
	 */
	public String getSellerId() {
		return sellerId;
	}

	/**
	 * Sets the ID of the seller.
	 *
	 * @param sellerId The seller ID to set
	 */
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	/**
	 * Gets the ID of the buyer.
	 *
	 * @return The buyer ID
	 */
	public String getBuyerId() {
		return buyerId;
	}

	/**
	 * Sets the ID of the buyer.
	 *
	 * @param buyerId The buyer ID to set
	 */
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	/**
	 * Gets the trade amount in Satoshis.
	 *
	 * @return The amount in Satoshis
	 */
	public long getAmountSats() {
		return amountSats;
	}

	/**
	 * Sets the trade amount in Satoshis.
	 *
	 * @param amountSats The amount in Satoshis to set
	 */
	public void setAmountSats(long amountSats) {
		this.amountSats = amountSats;
	}

	/**
	 * Gets the price per Bitcoin in USD.
	 *
	 * @return The price per Bitcoin
	 */
	public double getPricePerBitcoin() {
		return pricePerBitcoin;
	}

	/**
	 * Sets the price per Bitcoin in USD.
	 *
	 * @param pricePerBitcoin The price per Bitcoin to set
	 */
	public void setPricePerBitcoin(double pricePerBitcoin) {
		this.pricePerBitcoin = pricePerBitcoin;
	}

	/**
	 * Gets the current status of the trade.
	 *
	 * @return The trade status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status of the trade.
	 *
	 * @param status The status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Gets the creation timestamp of the trade.
	 *
	 * @return The creation timestamp
	 */
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	/**
	 * Gets the Lightning Network invoice for the trade fee.
	 *
	 * @return The fee invoice
	 */
	public String getFeeInvoice() {
		return feeInvoice;
	}

	/**
	 * Sets the Lightning Network invoice for the trade fee.
	 *
	 * @param feeInvoice The fee invoice to set
	 */
	public void setFeeInvoice(String feeInvoice) {
		this.feeInvoice = feeInvoice;
	}

	/**
	 * Checks if the trade fee has been paid.
	 *
	 * @return true if the fee is paid, false otherwise
	 */
	public boolean isFeePaid() {
		return feePaid;
	}

	/**
	 * Sets whether the trade fee has been paid.
	 *
	 * @param feePaid The fee payment status to set
	 */
	public void setFeePaid(boolean feePaid) {
		this.feePaid = feePaid;
	}
}
