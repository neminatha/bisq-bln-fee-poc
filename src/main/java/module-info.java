module network.bisq.blnfeepoc {
	requires javafx.controls;
	requires javafx.fxml;
	requires org.slf4j;
	// requires org.bitcoinj.core;
	requires lightningj;

	exports network.bisq.blnfeepoc;
	exports network.bisq.blnfeepoc.service;

	opens network.bisq.blnfeepoc to javafx.fxml;
	opens network.bisq.blnfeepoc.service to javafx.fxml;
	opens network.bisq.blnfeepoc.controller to javafx.fxml;
}