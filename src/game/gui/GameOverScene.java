package game.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Interpolator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class GameOverScene {
	private final Main main;
	private final StackPane root;

	public GameOverScene(Main main, String winnerName, String winnerRole,
						 String playerName, int playerEnergy,
						 String opponentName, int opponentEnergy) {
		this.main = main;
		this.root = new StackPane();
		this.root.getStyleClass().add("bg-dark");
		this.root.setStyle("-fx-background-color: " + Theme.COLOR_BG_GRADIENT + ";");
		buildUI(winnerName, winnerRole, playerName, playerEnergy, opponentName, opponentEnergy);
	}

	public Parent getRoot() {
		return root;
	}

	private void buildUI(String winnerName, String winnerRole,
						 String playerName, int playerEnergy,
						 String opponentName, int opponentEnergy) {
		Label trophy = new Label("TROPHY");
		trophy.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + Theme.COLOR_ACCENT_GOLD + ";"
				+ " -fx-background-color: rgba(231,184,138,0.4); -fx-padding: 12 22; -fx-background-radius: 32;");

		Label gameOver = new Label("GAME WON!");
		gameOver.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: " + Theme.COLOR_ACCENT_GOLD + ";");

		Label winner = new Label(winnerName + " WINS!");
		winner.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + Theme.COLOR_TEXT_DARK + ";");

		boolean isPlayerWinner = winnerName.equals(playerName);
		Label whoWon = new Label(isPlayerWinner ? "YOU WON!" : "OPPONENT WON");
		whoWon.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: "
				+ (isPlayerWinner ? Theme.COLOR_DELTA_POS : Theme.COLOR_DELTA_NEG) + ";"
				+ " -fx-padding: 4 18; -fx-background-radius: 8;"
				+ " -fx-background-color: " + (isPlayerWinner ? "rgba(100,200,100,0.15)" : "rgba(200,80,80,0.15)") + ";");

		// Use the active palette's door color so the role announcement matches the board
		String roleColor = winnerRole.equals("SCARER") ? Theme.COLOR_DOOR_SCARER : Theme.COLOR_DOOR_LAUGHER;
		Label role = new Label("Role: " + winnerRole);
		role.setStyle("-fx-font-size: 18px; -fx-text-fill: " + roleColor + "; -fx-font-weight: bold;");

		VBox energyBox = new VBox(8);
		energyBox.setAlignment(Pos.CENTER);
		Label energyTitle = new Label("Final Energy");
		energyTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: " + Theme.COLOR_ACCENT_GOLD + "; -fx-font-weight: bold;");
		Label playerLine = new Label(playerName + ": " + playerEnergy + " energy");
		playerLine.setStyle("-fx-font-size: 16px; -fx-text-fill: " + Theme.COLOR_TEXT_DARK + ";");
		Label oppLine = new Label(opponentName + ": " + opponentEnergy + " energy");
		oppLine.setStyle("-fx-font-size: 16px; -fx-text-fill: " + Theme.COLOR_TEXT_DARK + ";");
		energyBox.getChildren().addAll(energyTitle, playerLine, oppLine);
		energyBox.setStyle("-fx-background-color: " + Theme.COLOR_BG_PANEL + ";"
				+ " -fx-padding: 16; -fx-background-radius: 10;"
				+ " -fx-border-color: " + Theme.COLOR_BORDER + "; -fx-border-radius: 10; -fx-border-width: 2;");
		energyBox.setMaxWidth(420);

		Button startOver = new Button("Return to Start");
		startOver.getStyleClass().add("action-button");
		startOver.setStyle("-fx-background-color: " + Theme.COLOR_ACCENT_GOLD + ";"
				+ " -fx-text-fill: white; -fx-font-weight: bold;"
				+ " -fx-padding: 10 22; -fx-background-radius: 8; -fx-font-size: 16px;");
		startOver.setOnAction(e -> main.showStartScene());

		Button quit = new Button("Quit");
		quit.getStyleClass().add("action-button");
		quit.setStyle("-fx-background-color: " + Theme.COLOR_QUIT_BUTTON + ";"
				+ " -fx-text-fill: white; -fx-font-weight: bold;"
				+ " -fx-padding: 10 22; -fx-background-radius: 8; -fx-font-size: 16px;");
		quit.setOnAction(e -> {
			javafx.application.Platform.exit();
			System.exit(0);
		});

		HBox btns = new HBox(14, startOver, quit);
		btns.setAlignment(Pos.CENTER);

		VBox box = new VBox(18, trophy, gameOver, whoWon, winner, role, energyBox, btns);
		box.setAlignment(Pos.CENTER);
		box.setPadding(new Insets(40));
		box.setMaxWidth(640);
		box.setStyle("-fx-background-color: " + Theme.COLOR_BG_PANEL + "; -fx-background-radius: 14;"
				+ " -fx-border-color: " + Theme.COLOR_BORDER + "; -fx-border-width: 2; -fx-border-radius: 14;"
				+ " -fx-effect: dropshadow(gaussian, rgba(180,150,180,0.4), 14, 0, 0, 4);");

		root.getChildren().add(box);

		// Entrance animations
		box.setOpacity(0);
		box.setScaleX(0.7);
		box.setScaleY(0.7);
		FadeTransition fadeIn = new FadeTransition(Duration.millis(420), box);
		fadeIn.setFromValue(0); fadeIn.setToValue(1);
		ScaleTransition scaleIn = new ScaleTransition(Duration.millis(520), box);
		scaleIn.setFromX(0.7); scaleIn.setFromY(0.7);
		scaleIn.setToX(1.0);   scaleIn.setToY(1.0);
		scaleIn.setInterpolator(Interpolator.EASE_OUT);
		ParallelTransition entrance = new ParallelTransition(fadeIn, scaleIn);

		// Trophy bounce after entrance
		ScaleTransition bounceUp = new ScaleTransition(Duration.millis(300), trophy);
		bounceUp.setFromX(1.0); bounceUp.setFromY(1.0);
		bounceUp.setToX(1.18);  bounceUp.setToY(1.18);
		ScaleTransition bounceDown = new ScaleTransition(Duration.millis(300), trophy);
		bounceDown.setFromX(1.18); bounceDown.setFromY(1.18);
		bounceDown.setToX(1.0);    bounceDown.setToY(1.0);
		SequentialTransition seq = new SequentialTransition(entrance, bounceUp, bounceDown);
		seq.play();
	}
}
