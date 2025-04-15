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

package network.bisq.blnfeepoc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * Main application class for the Bitcoin P2P Trading application.
 * This class initializes the JavaFX application and loads the main UI.
 */
public class MainApp extends Application {
	private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

	/**
	 * Starts the JavaFX application by loading the FXML-based UI.
	 * Sets up the primary stage with the main view.
	 *
	 * @param primaryStage The primary stage for this application
	 * @throws Exception If an error occurs during application startup
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		logger.info("Starting Bitcoin P2P Trading App");
		URL fxml = getClass().getResource("fxml/MainView.fxml");
		Parent root = FXMLLoader.load(fxml);

		Scene scene = new Scene(root, 800, 600);
		primaryStage.setTitle("Bitcoin P2P Trading with Lightning");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Main method that launches the JavaFX application.
	 *
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}