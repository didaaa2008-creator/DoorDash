package game.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import game.engine.Role;

public class Main extends Application {
	private Stage primaryStage;
	private LogCapture logCapture;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.logCapture = new LogCapture();
		this.logCapture.start();

		primaryStage.setTitle("DooR DasH: Scare vs Laugh Touchdown");
		primaryStage.setMinWidth(Theme.WINDOW_WIDTH);
		primaryStage.setMinHeight(Theme.WINDOW_HEIGHT);
		primaryStage.setWidth(Theme.WINDOW_WIDTH);
		primaryStage.setHeight(Theme.WINDOW_HEIGHT);
		primaryStage.setResizable(true);
		primaryStage.setOnCloseRequest(e -> {
			logCapture.stop();
			javafx.application.Platform.exit();
			System.exit(0);
		});

		showStartScene();
		primaryStage.show();
	}

	public void showStartScene() {
		StartScene scene = new StartScene(this);
		Scene s = new Scene(scene.getRoot(), Theme.WINDOW_WIDTH, Theme.WINDOW_HEIGHT);
		s.getStylesheets().add("data:text/css," + Theme.INLINE_CSS);
		primaryStage.setScene(s);
	}

	public void showGameScene(Role role) {
		try {
			logCapture.clear();
			GameScene scene = new GameScene(this, role, logCapture);
			Scene s = new Scene(scene.getRoot(), Theme.WINDOW_WIDTH, Theme.WINDOW_HEIGHT);
			s.getStylesheets().add("data:text/css," + Theme.INLINE_CSS);
			primaryStage.setScene(s);
			scene.startGame();
		} catch (Exception e) {
			ErrorDialog.show("Failed to start game", e.getMessage());
		}
	}

	public void showGameOverScene(String winnerName, String winnerRole, int playerEnergy,
								  String playerName, int opponentEnergy, String opponentName) {
		GameOverScene scene = new GameOverScene(this, winnerName, winnerRole,
				playerName, playerEnergy, opponentName, opponentEnergy);
		Scene s = new Scene(scene.getRoot(), Theme.WINDOW_WIDTH, Theme.WINDOW_HEIGHT);
		s.getStylesheets().add("data:text/css," + Theme.INLINE_CSS);
		primaryStage.setScene(s);
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
