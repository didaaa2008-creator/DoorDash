package game.gui;

import java.io.File;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import game.engine.Role;

public class StartScene {
	private final Main main;
	private final StackPane root;

	public StartScene(Main main) {
		this.main = main;
		this.root = new StackPane();
		this.root.getStyleClass().add("bg-dark");
		buildUI();
	}

	public Parent getRoot() {
		return root;
	}

	private void buildUI() {
		// Background image - try classpath resource first (works in a JAR),
		// then fall back to a relative file path (works from Eclipse).
		ImageView bg = new ImageView();
		try {
			java.io.InputStream is = getClass().getResourceAsStream("/imgs/background.png");
			if (is != null) {
				bg.setImage(new Image(is));
			} else {
				File f = new File("imgs/background.png");
				if (f.exists())
					bg.setImage(new Image(f.toURI().toString()));
			}
		} catch (Exception ignored) {}
		bg.setPreserveRatio(false);
		bg.fitWidthProperty().bind(root.widthProperty());
		bg.fitHeightProperty().bind(root.heightProperty());

		// Role buttons — vertically stacked, positioned under the banner on the left
		Button scarerBtn = new Button("SCARER");
		scarerBtn.getStyleClass().addAll("side-button", "side-button-scarer");
		scarerBtn.setOnAction(e -> main.showGameScene(Role.SCARER));

		Button laugherBtn = new Button("LAUGHER");
		laugherBtn.getStyleClass().addAll("side-button", "side-button-laugher");
		laugherBtn.setOnAction(e -> main.showGameScene(Role.LAUGHER));

		Button instrBtn = new Button("INSTRUCTIONS");
		instrBtn.getStyleClass().addAll("side-button");
		instrBtn.setOnAction(e -> showInstructions());
		VBox.setMargin(instrBtn, new Insets(20, 0, 0, 0));

		VBox buttonBox = new VBox(16, scarerBtn, laugherBtn, instrBtn);
		buttonBox.setAlignment(Pos.TOP_LEFT);
		StackPane.setAlignment(buttonBox, Pos.TOP_LEFT);
		StackPane.setMargin(buttonBox, new Insets(400, 0, 0, 275));

		root.getChildren().addAll(bg, buttonBox);

		// Entrance: fade-in on the buttons
		buttonBox.setOpacity(0);
		FadeTransition fade = new FadeTransition(Duration.millis(500), buttonBox);
		fade.setFromValue(0); fade.setToValue(1);
		fade.play();
	}

	private void showInstructions() {
		Stage dialog = new Stage();
		dialog.initOwner(main.getPrimaryStage());
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setTitle("How to Play");

		Label header = new Label("How to Play — DooR DasH");
		header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

		Label body = new Label(getInstructionText());
		body.setWrapText(true);
		body.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Theme.COLOR_TEXT_DARK + ";");
		body.setMaxWidth(620);

		ScrollPane scroll = new ScrollPane(body);
		scroll.setFitToWidth(true);
		scroll.setPrefHeight(440);
		scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

		Button close = new Button("CLOSE");
		close.setDefaultButton(true);
		close.setStyle("-fx-background-color: " + Theme.COLOR_ACCENT_GOLD
				+ "; -fx-text-fill: white; -fx-font-weight: bold;"
				+ " -fx-padding: 8 24; -fx-background-radius: 8;");
		close.setOnAction(e -> dialog.close());

		VBox box = new VBox(16, header, scroll, close);
		box.setAlignment(Pos.TOP_CENTER);
		box.setPadding(new Insets(24));
		box.setStyle("-fx-background-color: " + Theme.COLOR_DIALOG_BG + ";"
				+ " -fx-border-color: " + Theme.COLOR_BORDER + "; -fx-border-width: 2;"
				+ " -fx-border-radius: 12; -fx-background-radius: 12;");

		Scene scene = new Scene(box, 680, 540);
		scene.setFill(javafx.scene.paint.Color.web(Theme.COLOR_DIALOG_BG));
		dialog.setScene(scene);
		dialog.showAndWait();
	}

	private String getInstructionText() {
		return
			"OBJECTIVE\n" +
			"  Be the first to land exactly on Boo's Door (cell 99) with at least 1000 energy.\n\n" +
			"BOARD (100 cells in a zigzag layout)\n" +
			"  - 50 Door Cells (alternating SCARER & LAUGHER) - role-match earns energy, mismatch loses energy.\n" +
			"  - 6 Monster Cells - same role triggers your powerup for free; mismatch swaps energy if you have more.\n" +
			"  - 5 Conveyor Belts - move forward immediately.\n" +
			"  - 5 Contamination Socks - move backward and lose 100 energy (shield blocks energy loss only).\n" +
			"  - 10 Card Cells - draw a random card from the card pile and apply its effect.\n" +
			"  - Remaining cells are normal corridors.\n\n" +
			"TURN SEQUENCE\n" +
			"  1. Optionally use your powerup (costs 500 energy).\n" +
			"  2. Roll the dice (1-6) and move forward. If you land on the opponent the move is invalid - roll again.\n" +
			"  3. Cell effect applies. Movement caused by cards/transports does NOT trigger the new cell's effect.\n" +
			"  4. Turn passes to the other monster.\n\n" +
			"MONSTER TYPES\n" +
			"  - Dasher: doubles dice movement (passive). Powerup: 3x speed for 3 turns.\n" +
			"  - Dynamo: doubles all energy gains AND losses. Powerup: freezes opponent for 1 turn.\n" +
			"  - MultiTasker: halves dice movement but +200 to every energy change. Powerup: normal speed for 2 turns.\n" +
			"  - Schemer: every energy change gets a +10 bonus. Powerup: steals 10 from every monster (ignores shields).\n\n" +
			"CARDS\n" +
			"  - Position Swap (Swapper): swap with opponent if you are behind.\n" +
			"  - Contamination Code / 2319 Alert (Start Over): send player or opponent back to cell 0.\n" +
			"  - Small Snatcher / Sneaky Thief / Mega Drain (Energy Steal): take 50/100/150 energy from opponent.\n" +
			"  - Super Shield (Shield): blocks the next negative energy effect to your team.\n" +
			"  - Mind Scramble / Total Confusion (Confusion): swap roles for 2 or 3 turns.\n\n" +
			"WIN: stand exactly on cell 99 with at least 1000 energy. Otherwise the game continues.";
	}
}
