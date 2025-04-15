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

package network.bisq.blnfeepoc.controller;

import network.bisq.blnfeepoc.model.Trade;
import network.bisq.blnfeepoc.service.LightningNetworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller class for the main view of the application.
 * Handles user interactions and connects the UI with the Lightning Network service.
 */
public class MainController {
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	private LightningNetworkService lightningService;
	private Trade currentTrade;

	@FXML private TextField amountField;
	@FXML private TextField priceField;
	@FXML private Label statusLabel;
	@FXML private Label feeLabel;
	@FXML private Label nodeInfoLabel;
	@FXML private Label balanceLabel;
	@FXML private Button createTradeButton;
	@FXML private Button payFeeButton;
	@FXML private Button refreshNodeInfoButton;

	/**
	 * Initializes the controller.
	 * This method is automatically called after the FXML file has been loaded.
	 * Sets up the Lightning Network service and initializes UI components.
	 */
	@FXML
	public void initialize() {
		lightningService = new LightningNetworkService();
		payFeeButton.setDisable(true);

		// Display node info
		updateNodeInfo();
	}

	/**
	 * Updates the node information displayed in the UI.
	 * Retrieves the current node connection status and wallet balance.
	 */
	@FXML
	private void updateNodeInfo() {
		if (lightningService.isConnected()) {
			nodeInfoLabel.setText(lightningService.getNodeInfo());
			long balance = lightningService.getWalletBalance();
			balanceLabel.setText("Wallet Balance: " + balance + " sats");
		} else {
			nodeInfoLabel.setText("Not connected to LND");
			balanceLabel.setText("Wallet Balance: Not available");
			statusLabel.setText("LND Node not connected. Please start LND in Neutrino mode.");
		}
	}

	/**
	 * Handles the creation of a new trade.
	 * Parses input fields, creates a trade object, and generates a fee invoice.
	 */
	@FXML
	private void handleCreateTrade() {
		try {
			if (!lightningService.isConnected()) {
				statusLabel.setText("Not connected to LND Node");
				return;
			}

			long amount = Long.parseLong(amountField.getText());
			double price = Double.parseDouble(priceField.getText());

			// Create demo trade
			currentTrade = new Trade("seller123", "buyer456", amount, price);
			logger.info("Trade created: {}", currentTrade.getId());

			// Calculate fee and create Lightning invoice
			String feeInvoice = lightningService.calculateAndCreateFeeInvoice(amount);
			currentTrade.setFeeInvoice(feeInvoice);

			// Update UI
			statusLabel.setText("Trade created, ID: " + currentTrade.getId());
			feeLabel.setText("Fee Invoice: " + feeInvoice);
			payFeeButton.setDisable(false);

		} catch (Exception e) {
			logger.error("Error creating trade: " + e.getMessage(), e);
			statusLabel.setText("Error: " + e.getMessage());
		}
	}

	/**
	 * Handles payment of the trade fee using Lightning Network.
	 * Updates the trade status upon successful payment.
	 */
	@FXML
	private void handlePayFee() {
		try {
			if (currentTrade != null && currentTrade.getFeeInvoice() != null) {
				boolean success = lightningService.payLightningInvoice(currentTrade.getFeeInvoice());

				if (success) {
					currentTrade.setFeePaid(true);
					currentTrade.setStatus("FEE_PAID");
					statusLabel.setText("Fee successfully paid!");
					updateNodeInfo(); // Update balance
				} else {
					statusLabel.setText("Error paying the fee");
				}
			}
		} catch (Exception e) {
			logger.error("Error paying fee: " + e.getMessage(), e);
			statusLabel.setText("Error: " + e.getMessage());
		}
	}
}