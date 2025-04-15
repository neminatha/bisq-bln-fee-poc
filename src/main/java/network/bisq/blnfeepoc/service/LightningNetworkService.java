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

package network.bisq.blnfeepoc.service;

//import org.bitcoinj.core.NetworkParameters;
//import org.bitcoinj.params.TestNet3Params;
import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service class for interacting with the Lightning Network.
 * Provides methods for connecting to an LND node, creating invoices,
 * and making payments via the Lightning Network.
 */
public class LightningNetworkService {
	private static final Logger logger = LoggerFactory.getLogger(LightningNetworkService.class);
	private static final String LND_DIR_PROPERTY = "lnd.directory";
//	private final NetworkParameters networkParameters;
	private SynchronousLndAPI lndAPI;
	private boolean connected = false;

	/**
	 * Constructs a new Lightning Network service.
	 * Initializes network parameters and attempts to connect to the LND node.
	 */
	public LightningNetworkService() {
		// For the PoC we use the Bitcoin testnet
//		this.networkParameters = TestNet3Params.get();
		setupLndConnection();
	}

	/**
	 * Sets up the connection to the local LND node.
	 * Uses either the default directory (~/.lnd) or a custom directory
	 * specified via the lnd.directory system property.
	 */
	private void setupLndConnection() {
		try {
			// Get LND directory from system property or use default
			String homeDir = System.getProperty("user.home");
			String lndDir = System.getProperty(LND_DIR_PROPERTY, homeDir + "/.lnd");

			logger.info("Using LND directory: {}", lndDir);

			String tlsCertPath = lndDir + "/tls.cert";
			String macaroonPath = lndDir + "/data/chain/bitcoin/testnet/admin.macaroon";

			// Check if files exist
			if (!Files.exists(Path.of(tlsCertPath))) {
				logger.error("TLS certificate not found: {}", tlsCertPath);
				return;
			}

			if (!Files.exists(Path.of(macaroonPath))) {
				logger.error("Admin macaroon not found: {}", macaroonPath);
				// Fallback to regtest or other networks
				macaroonPath = lndDir + "/data/chain/bitcoin/regtest/admin.macaroon";
				if (!Files.exists(Path.of(macaroonPath))) {
					logger.error("No macaroon found for testnet or regtest");
					return;
				}
			}

			// Connect to local LND node
			this.lndAPI = new SynchronousLndAPI("localhost", 10009, new File(tlsCertPath), new File(macaroonPath));

			// Test connection
			GetInfoResponse info = lndAPI.getInfo();
			logger.info("Connected to LND Node: {}", info.getAlias());
			logger.info("LND Version: {}", info.getVersion());
			logger.info("Blockchain: {}", info.getChains().get(0).getChain());
			logger.info("Network: {}", info.getChains().get(0).getNetwork());

			connected = true;
		} catch (Exception e) {
			logger.error("Error connecting to LND Node: " + e.getMessage(), e);
			connected = false;
		}
	}

	/**
	 * Checks if the service is connected to an LND node.
	 *
	 * @return true if connected, false otherwise
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Retrieves information about the connected LND node.
	 *
	 * @return A formatted string with node information or an error message
	 */
	public String getNodeInfo() {
		if (!connected) return "Not connected";

		try {
			GetInfoResponse info = lndAPI.getInfo();
			return String.format("Node: %s, Version: %s, Network: %s",
				info.getAlias(),
				info.getVersion(),
				info.getChains().get(0).getNetwork());
		} catch (Exception e) {
			logger.error("Error retrieving node info: " + e.getMessage(), e);
			return "Error: " + e.getMessage();
		}
	}

	/**
	 * Calculates and creates an invoice for the 1% marketplace fee.
	 *
	 * @param tradeAmount The trade amount in Satoshis
	 * @return The Lightning invoice for the fee
	 * @throws Exception If there's an error creating the invoice or if not connected
	 */
	public String calculateAndCreateFeeInvoice(long tradeAmount) throws Exception {
		if (!connected) {
			throw new Exception("Not connected to LND Node");
		}

		// Calculate 1% of the trade amount
		long feeAmount = calculateFee(tradeAmount);
		logger.info("Calculated fee: {} sats for trade amount: {} sats", feeAmount, tradeAmount);

		// Create Lightning invoice
		return createLightningInvoice(feeAmount);
	}

	/**
	 * Calculates the fee amount based on the trade amount.
	 *
	 * @param tradeAmount The trade amount in Satoshis
	 * @return The calculated fee amount (1% of trade amount)
	 */
	private long calculateFee(long tradeAmount) {
		BigDecimal amount = BigDecimal.valueOf(tradeAmount);
		BigDecimal feePercentage = new BigDecimal("0.01"); // 1%
		return amount.multiply(feePercentage).setScale(0, RoundingMode.HALF_UP).longValue();
	}

	/**
	 * Creates a Lightning Network invoice for the specified amount.
	 *
	 * @param amountSats The amount in Satoshis
	 * @return The encoded payment request string
	 * @throws Exception If there's an error creating the invoice
	 */
	private String createLightningInvoice(long amountSats) throws Exception {
		try {
			// Create Lightning invoice
			Invoice invoice = new Invoice();
			invoice.setValue(amountSats);
			invoice.setMemo("BISQ Trading Platform Fee - 1%");

			AddInvoiceResponse response = lndAPI.addInvoice(invoice);
			String paymentRequest = response.getPaymentRequest();

			logger.info("Lightning Invoice created: {}", paymentRequest);
			return paymentRequest;
		} catch (StatusException e) {
			logger.error("Error creating Lightning invoice: " + e.getMessage(), e);
			throw new Exception("Lightning network error: " + e.getMessage(), e);
		}
	}

	/**
	 * Pays a Lightning invoice using the connected LND node.
	 *
	 * @param paymentRequest The Lightning invoice to pay
	 * @return true if successful, otherwise false
	 */
	public boolean payLightningInvoice(String paymentRequest) {
		if (!connected) {
			logger.error("Not connected to LND Node");
			return false;
		}

		try {
			// Decode invoice to show details
			PayReq decodedRequest = lndAPI.decodePayReq(paymentRequest);
			logger.info("Invoice decoded: {} Satoshis to {}",
				decodedRequest.getNumSatoshis(),
				decodedRequest.getDestination());

			// Pay invoice
			SendRequest sendRequest = new SendRequest();
			sendRequest.setPaymentRequest(paymentRequest);

			SendResponse response = lndAPI.sendPaymentSync(sendRequest);

			if (response.getPaymentError() != null && !response.getPaymentError().isEmpty()) {
				logger.error("Payment error: {}", response.getPaymentError());
				return false;
			}

			logger.info("Fee successfully paid via Lightning Network");
			logger.info("Payment ID: {}", bytesToHex(response.getPaymentHash()));
			return true;
		} catch (Exception e) {
			logger.error("Error paying Lightning invoice: " + e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Converts a byte array to a hexadecimal string.
	 *
	 * @param bytes The byte array to convert
	 * @return The hexadecimal representation of the bytes
	 */
	private String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	/**
	 * Retrieves the wallet balance from the connected LND node.
	 *
	 * @return The total balance in Satoshis, or -1 if an error occurs
	 */
	public long getWalletBalance() {
		if (!connected) return 0;

		try {
			WalletBalanceResponse balance = lndAPI.walletBalance((String)null);
			return balance.getTotalBalance();
		} catch (Exception e) {
			logger.error("Error retrieving wallet balance: " + e.getMessage(), e);
			return -1;
		}
	}
}
