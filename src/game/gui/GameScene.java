package game.gui;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Game;
import game.engine.Role;
import game.engine.cards.Card;
import game.engine.cards.StartOverCard;
import game.engine.cards.SwapperCard;
import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Monster;

public class GameScene {
	private final Main main;
	private final Role chosenRole;
	private final LogCapture logCapture;

	private Game game;
	private BoardView boardView;
	private MonsterPanel playerPanel;
	private MonsterPanel opponentPanel;
	private CardPileView cardPile;
	private TextArea logArea;

	private Label turnLabel;
	private Label currentLabel;
	private Label diceLabel;
	private Button rollBtn;
	private Button powerupBtn;
	private Label freezeBanner;

	private final BorderPane root = new BorderPane();

	private boolean turnInProgress = false;
	// Keyed by Monster (not name) so two monsters with the same name would still
	// be tracked independently - safer than relying on name uniqueness.
	private final Map<Monster, Integer> lastEnergy = new HashMap<>();

	public GameScene(Main main, Role chosenRole, LogCapture logCapture) {
		this.main = main;
		this.chosenRole = chosenRole;
		this.logCapture = logCapture;
	}

	public Parent getRoot() {
		return root;
	}

	public void startGame() {
		try {
			this.game = new Game(chosenRole);
			lastEnergy.put(game.getPlayer(), game.getPlayer().getEnergy());
			lastEnergy.put(game.getOpponent(), game.getOpponent().getEnergy());
			for (Monster m : game.getAllMonsters()) lastEnergy.put(m, m.getEnergy());
		} catch (Exception e) {
			ErrorDialog.show("Failed to start game", e.getMessage());
			main.showStartScene();
			return;
		}

		buildUI();
		refreshAll();
		appendLog("=== Game started! ===");
		appendLog("You are playing as " + chosenRole + " (" + game.getPlayer().getName() + ").");
		appendLog("Opponent: " + game.getOpponent().getName() + " (" + game.getOpponent().getRole() + ").");
		appendLog("Both monsters start at cell 0. First to reach cell 99 with >= 1000 energy wins!");
	}

	private void buildUI() {
		root.getStyleClass().add("bg-dark");
		root.setStyle("-fx-background-color: " + Theme.COLOR_BG_GRADIENT + ";");
		root.setPadding(new Insets(10));

		// Top bar — turn info on the left, quit button on the right
		turnLabel = new Label("Turn 1");
		turnLabel.getStyleClass().add("turn-label");
		currentLabel = new Label("Current: " + game.getCurrent().getName());
		currentLabel.setStyle("-fx-text-fill: " + Theme.COLOR_TEXT_DARK + "; -fx-font-weight: bold;");

		Button quitBtn = new Button("Main Menu");
		quitBtn.getStyleClass().add("action-button");
		quitBtn.setOnAction(e -> {
			logCapture.clearListeners();
			main.showStartScene();
		});

		HBox turnRow = new HBox(12, turnLabel, currentLabel);
		turnRow.setAlignment(Pos.CENTER_LEFT);
		Region topSpacer = new Region();
		HBox.setHgrow(topSpacer, Priority.ALWAYS);

		HBox topBar = new HBox(10, turnRow, topSpacer, quitBtn);
		topBar.setAlignment(Pos.CENTER_LEFT);
		topBar.setPadding(new Insets(4, 8, 8, 8));
		root.setTop(topBar);

		// Center: title above the board
		Label boardTitle = new Label("DooR DasH: Scare vs Laugh Touchdown");
		boardTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + Theme.COLOR_ACCENT_GOLD + ";");

		boardView = new BoardView(game.getBoard());
		StackPane boardWrap = new StackPane(boardView);
		boardWrap.setPadding(new Insets(8));

		VBox centerBox = new VBox(6, boardTitle, boardWrap);
		centerBox.setAlignment(Pos.TOP_CENTER);
		root.setCenter(centerBox);

		// Left panel: player info + controls
		playerPanel = new MonsterPanel("PLAYER", true);
		playerPanel.bind(game.getPlayer());

		diceLabel = new Label("-");
		diceLabel.getStyleClass().add("dice-label");
		Label diceCaption = new Label("Last Roll");
		diceCaption.getStyleClass().add("info-label");
		VBox diceBox = new VBox(diceCaption, diceLabel);
		diceBox.setAlignment(Pos.CENTER);
		diceBox.setStyle("-fx-background-color: " + Theme.COLOR_BG_PANEL + "; -fx-padding: 8; -fx-background-radius: 10;"
				+ " -fx-border-color: " + Theme.COLOR_BORDER + "; -fx-border-width: 2; -fx-border-radius: 10;");

		rollBtn = new Button("Roll Dice");
		rollBtn.getStyleClass().add("action-button");
		rollBtn.setMaxWidth(Double.MAX_VALUE);
		rollBtn.setOnAction(e -> handleRoll());

		powerupBtn = new Button("Use Powerup (-500)"); // label updated dynamically in setControlsEnabled
		powerupBtn.getStyleClass().add("action-button");
		powerupBtn.setMaxWidth(Double.MAX_VALUE);
		powerupBtn.setOnAction(e -> handlePowerup());

		freezeBanner = new Label("FROZEN! Turn skipped.");
		freezeBanner.getStyleClass().add("freeze-banner");
		freezeBanner.setStyle("-fx-background-color: " + Theme.COLOR_FREEZE_BG + "; -fx-padding: 8;"
				+ " -fx-text-fill: " + Theme.COLOR_BADGE_TEXT + "; -fx-font-weight: bold; -fx-background-radius: 6;");
		freezeBanner.setVisible(false);
		freezeBanner.setManaged(false);

		VBox controls = new VBox(8,
				new Label("Turn Controls"),
				diceBox,
				powerupBtn,
				rollBtn,
				freezeBanner
		);
		controls.getChildren().get(0).setStyle("-fx-text-fill: " + Theme.COLOR_ACCENT_GOLD + ";"
				+ " -fx-font-weight: bold; -fx-font-size: 14px;");
		controls.getStyleClass().add("panel");

		Label logTitle = new Label("Action Log");
		logTitle.getStyleClass().add("panel-title");
		logArea = new TextArea();
		logArea.setEditable(false);
		logArea.setPrefRowCount(8);
		logArea.setWrapText(true);
		logArea.getStyleClass().add("log-area");
		logArea.setStyle("-fx-control-inner-background: " + Theme.COLOR_LOG_BG + "; -fx-text-fill: #ECEFF1;"
				+ " -fx-font-family: 'Consolas', monospace; -fx-font-size: 12px;");
		VBox.setVgrow(logArea, Priority.ALWAYS);

		VBox logPanel = new VBox(6, logTitle, logArea);
		logPanel.getStyleClass().add("panel");
		VBox.setVgrow(logPanel, Priority.ALWAYS);

		VBox leftBox = new VBox(10,
				headerLabel("YOU"),
				playerPanel,
				controls,
				logPanel
		);
		leftBox.setPadding(new Insets(0, 8, 0, 0));
		leftBox.setMinWidth(290);
		leftBox.setMaxWidth(310);
		root.setLeft(leftBox);

		// Right panel: opponent info + card pile + legend
		opponentPanel = new MonsterPanel("OPPONENT", false);
		opponentPanel.bind(game.getOpponent());

		cardPile = new CardPileView();
		VBox cardPilePanel = new VBox(4, cardPile);
		cardPilePanel.setAlignment(Pos.CENTER);
		cardPilePanel.getStyleClass().add("panel");

		Label legendTitle = new Label("Legend");
		legendTitle.getStyleClass().add("panel-title");
		VBox legend = legendBox();
		VBox legendPanel = new VBox(6, legendTitle, legend);
		legendPanel.getStyleClass().add("panel");

		VBox rightBox = new VBox(10,
				headerLabel("OPPONENT"),
				opponentPanel,
				cardPilePanel,
				legendPanel
		);
		rightBox.setPadding(new Insets(0, 0, 0, 8));
		rightBox.setMinWidth(310);
		rightBox.setMaxWidth(330);
		root.setRight(rightBox);

		// Clear any stale listeners; engine messages are drained explicitly after each game action
		logCapture.clearListeners();

		// Secret cheat: Shift+W — monster with more energy wins instantly
		root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if (e.isShiftDown() && e.getCode() == KeyCode.W) {
				forceWin();
			}
		});
	}

	private Label headerLabel(String text) {
		Label l = new Label(text);
		l.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + Theme.COLOR_TEXT_MUTED + ";");
		return l;
	}

	private VBox legendBox() {
		VBox v = new VBox(4);
		v.getChildren().addAll(
				legendItem(Theme.COLOR_DOOR_SCARER, "Scarer Door"),
				legendItem(Theme.COLOR_DOOR_LAUGHER, "Laugher Door"),
				legendItem(Theme.COLOR_DOOR_EXHAUSTED, "Exhausted Door"),
				legendItem(Theme.COLOR_MONSTER_CELL, "Monster Cell"),
				legendItem(Theme.COLOR_CARD_CELL, "Card Cell"),
				legendItem(Theme.COLOR_CONVEYOR, "Conveyor Belt"),
				legendItem(Theme.COLOR_SOCK, "Contamination Sock"),
				legendItem(Theme.COLOR_NORMAL, "Normal"),
				legendItem(Theme.COLOR_PLAYER, "P = Player"),
				legendItem(Theme.COLOR_OPP, "O = Opponent")
		);
		return v;
	}

	private HBox legendItem(String color, String label) {
		Region swatch = new Region();
		swatch.setMinSize(14, 14);
		swatch.setMaxSize(14, 14);
		swatch.setStyle("-fx-background-color: " + color + ";"
				+ " -fx-border-color: rgba(120,100,140,0.4); -fx-border-width: 1;");
		Label l = new Label(label);
		l.setStyle("-fx-text-fill: " + Theme.COLOR_TEXT_DARK + "; -fx-font-size: 11px;");
		HBox h = new HBox(6, swatch, l);
		h.setAlignment(Pos.CENTER_LEFT);
		return h;
	}

	private void appendLog(String line) {
		if (logArea == null) return;
		logArea.appendText(line + "\n");
	}

	// ============================================================
	// Game flow
	// ============================================================

	private void handleRoll() {
		if (turnInProgress || game.getWinner() != null) return;
		turnInProgress = true;
		setControlsEnabled(false);

		final Monster current = game.getCurrent();
		final Monster opp = game.getCurrentOpponentMonster();
		final boolean isPlayerActor = (current == game.getPlayer());

		// Snapshot energies of all monsters before turn so we can announce changes
		snapshotEnergy();

		final int oldPos = current.getPosition();

		appendLog("--- " + current.getName() + "'s turn ---");

		try {
			game.playTurn();
			// Flush engine System.out messages (shield blocks, momentum, etc.) to the log
			logCapture.drainLines().forEach(this::appendLog);

			boolean wasFreeze = game.wasLastTurnSkippedDueToFreeze();
			if (wasFreeze) {
				diceLabel.setText("-");
				appendLog(current.getName() + " was frozen and skipped the turn.");
				flashFreeze(current);
				finishTurnAfterAnimation();
				return;
			}

			final int roll = game.getLastRoll();
			final int newPos = current.getPosition();

			// If a teleport card moved the monster, animate as a direct glide
			// instead of a cell-by-cell walk so the visual matches engine semantics.
			Card drawnNow = Board.getLastDrawnCard();
			final boolean isTeleport = (drawnNow instanceof StartOverCard)
					|| (drawnNow instanceof SwapperCard);

			// Spin the dice, then animate token movement.
			animateDiceRoll(roll, () -> {
				appendLog(current.getName() + " rolled a " + roll + ".");
				boardView.animateMove(isPlayerActor, oldPos, newPos, isTeleport, () -> {
					// Defer post-animation work to the next pulse - showAndWait
					// (used by the card popup) is illegal during an animation
					// onFinished callback.
					Platform.runLater(() -> {
						boardView.flashCell(newPos, flashColor());
						announceMovement(current, oldPos, newPos);
						announceLandedCell(current);
						announceCardIfDrawn(current);
						announceEnergyChanges(true);
						finishTurnAfterAnimation();
					});
				});
			});

		} catch (InvalidMoveException ex) {
			// Cell effects already fired before position was reverted - still show them.
			announceCardIfDrawn(current);
			announceEnergyChanges(true);
			ErrorDialog.show("Invalid Move",
				ex.getMessage()
				+ "\n" + current.getName() + " cannot land on " + opp.getName() + "."
				+ "\nRoll the dice again.");
			appendLog("Invalid move: " + ex.getMessage() + ". " + current.getName() + " must roll again.");
			refreshAll();
			turnInProgress = false;
			setControlsEnabled(true);
		} catch (Exception ex) {
			ErrorDialog.show("Unexpected error", ex.getMessage() != null ? ex.getMessage() : ex.toString());
			appendLog("ERROR: " + ex);
			turnInProgress = false;
			setControlsEnabled(true);
		}
	}

	private void finishTurnAfterAnimation() {
		refreshAll();
		Monster winner = game.getWinner();
		if (winner != null) {
			goToGameOver(winner);
			return;
		}
		Monster next = game.getCurrent();
		currentLabel.setText("Current: " + next.getName());
		if (next.isFrozen()) {
			freezeBanner.setText(next.getName() + " is FROZEN! Their next turn will be skipped.");
			freezeBanner.setVisible(true);
			freezeBanner.setManaged(true);
		}
		// Don't hide the banner here - flashFreeze() runs a 1.4s PauseTransition
		// to hide the "was FROZEN! Turn skipped" message after a skipped turn.
		// Hiding it here would clobber that timer in the same JavaFX pulse.
		turnInProgress = false;
		setControlsEnabled(true);
	}

	/** Cell-flash color — looked up live so theme changes between games take effect. */
	private static Color flashColor() {
		return Color.web(Theme.COLOR_ACCENT_GOLD);
	}

	/** Cycle the dice number rapidly for ~600ms before settling on the actual roll. */
	private void animateDiceRoll(int finalRoll, Runnable onDone) {
		Timeline tl = new Timeline();
		int frames = 8;
		double total = 480;
		for (int i = 0; i < frames; i++) {
			final int n = (int)(Math.random() * 6) + 1;
			double t = (i + 1) * (total / frames);
			tl.getKeyFrames().add(new KeyFrame(Duration.millis(t), e -> diceLabel.setText(String.valueOf(n))));
		}
		tl.getKeyFrames().add(new KeyFrame(Duration.millis(total + 80),
				e -> diceLabel.setText(String.valueOf(finalRoll))));
		tl.setOnFinished(e -> {
			if (onDone != null) onDone.run();
		});
		tl.play();
	}

	private void handlePowerup() {
		if (turnInProgress || game.getWinner() != null) return;
		Monster current = game.getCurrent();
		try {
			snapshotEnergy();
			appendLog(current.getName() + " activates their powerup!");
			game.usePowerup();
			// Flush engine System.out messages (powerup effects, chain attack, etc.) to the log
			logCapture.drainLines().forEach(this::appendLog);

			MonsterPanel actorPanel = (current == game.getPlayer()) ? playerPanel : opponentPanel;
			actorPanel.pulse();

			announceEnergyChanges(true);
			refreshAll();

			Monster opp = game.getCurrentOpponentMonster();
			if (opp.isFrozen()) {
				freezeBanner.setText(opp.getName() + " is FROZEN! Their next turn will be skipped.");
				freezeBanner.setVisible(true);
				freezeBanner.setManaged(true);
			}

			Monster winner = game.getWinner();
			if (winner != null) goToGameOver(winner);
		} catch (OutOfEnergyException ex) {
			ErrorDialog.show("Cannot use powerup", ex.getMessage()
					+ "\nYou need at least " + game.getCurrent().getEffectivePowerupCost() + " energy.");
			appendLog("Powerup blocked: " + ex.getMessage());
		} catch (Exception ex) {
			ErrorDialog.show("Unexpected error", ex.getMessage());
		}
	}

	private void setControlsEnabled(boolean enabled) {
		rollBtn.setDisable(!enabled);
		int cost = game.getCurrent().getEffectivePowerupCost();
		powerupBtn.setText("Use Powerup (-" + cost + ")");
		powerupBtn.setDisable(!enabled || game.getCurrent().getEnergy() < cost);
	}

	private void snapshotEnergy() {
		lastEnergy.put(game.getPlayer(), game.getPlayer().getEnergy());
		lastEnergy.put(game.getOpponent(), game.getOpponent().getEnergy());
		for (Monster m : game.getAllMonsters()) lastEnergy.put(m, m.getEnergy());
	}

	private void announceEnergyChanges(boolean withFloaters) {
		announceEnergyChange(game.getPlayer(), withFloaters);
		announceEnergyChange(game.getOpponent(), withFloaters);
		for (Monster m : game.getAllMonsters()) announceEnergyChange(m, false);
	}

	private void announceEnergyChange(Monster m, boolean withFloaters) {
		Integer prev = lastEnergy.get(m);
		if (prev == null) return;
		int diff = m.getEnergy() - prev;
		if (diff != 0) {
			String sign = diff > 0 ? "+" : "";
			appendLog("  -> " + m.getName() + " energy " + sign + diff + " (now " + m.getEnergy() + ")");
			if (withFloaters) {
				if (m == game.getPlayer()) playerPanel.flashEnergyDelta(diff);
				else if (m == game.getOpponent()) opponentPanel.flashEnergyDelta(diff);
			}
		}
		lastEnergy.put(m, m.getEnergy());
	}

	private void announceMovement(Monster mover, int oldPos, int newPos) {
		appendLog("  -> Moved from cell " + oldPos + " to cell " + newPos + ".");
	}

	private void announceLandedCell(Monster mover) {
		Cell c = game.getBoard().getCellAt(mover.getPosition());
		String desc;
		if (c instanceof DoorCell) {
			DoorCell d = (DoorCell) c;
			desc = "Door (" + d.getRole() + ", " + d.getEnergy() + " energy)" + (d.isActivated() ? " - exhausted" : "");
		} else if (c instanceof MonsterCell) {
			Monster cm = ((MonsterCell) c).getCellMonster();
			desc = "Monster Cell with " + (cm == null ? "?" : (cm.getName() + " (" + cm.getRole() + ")"));
		} else if (c instanceof ConveyorBelt) {
			desc = "Conveyor Belt (already moved on land)";
		} else if (c instanceof ContaminationSock) {
			desc = "Contamination Sock (already moved on land)";
		} else if (c instanceof CardCell) {
			desc = "Card Cell";
		} else {
			desc = "Normal Cell";
		}
		appendLog("  -> Landed on cell " + mover.getPosition() + " - " + desc);
	}

	private void announceCardIfDrawn(Monster mover) {
		Card drawn = Board.getLastDrawnCard();
		if (drawn != null) {
			appendLog("  -> CARD DRAWN: " + drawn.getName() + " - " + drawn.getDescription());
			if (cardPile != null) cardPile.animateDraw();
			CardDrawnDialog.show(main.getPrimaryStage(), drawn, mover.getName());
			Board.clearLastDrawnCard();
		}
		if (cardPile != null) cardPile.refresh();
	}

	private void flashFreeze(Monster m) {
		freezeBanner.setText(m.getName() + " was FROZEN! Turn skipped.");
		freezeBanner.setVisible(true);
		freezeBanner.setManaged(true);
		PauseTransition pt = new PauseTransition(Duration.seconds(1.4));
		pt.setOnFinished(e -> {
			freezeBanner.setVisible(false);
			freezeBanner.setManaged(false);
		});
		pt.play();
	}

	private void refreshAll() {
		boardView.refresh(game.getPlayer(), game.getOpponent());
		playerPanel.refresh();
		opponentPanel.refresh();
		playerPanel.setActive(game.getCurrent() == game.getPlayer());
		opponentPanel.setActive(game.getCurrent() == game.getOpponent());
		turnLabel.setText("Turn " + game.getTurnNumber());
		currentLabel.setText("Current: " + game.getCurrent().getName());
		setControlsEnabled(true);

		if (game.getCurrent().isFrozen()) {
			freezeBanner.setText(game.getCurrent().getName() + " is FROZEN! Their next turn will be skipped.");
			freezeBanner.setVisible(true);
			freezeBanner.setManaged(true);
		}
	}

	private void forceWin() {
		if (game == null || game.getWinner() != null || turnInProgress) return;
		Monster player = game.getPlayer();
		Monster opponent = game.getOpponent();
		Monster winner = player.getEnergy() >= opponent.getEnergy() ? player : opponent;
		goToGameOver(winner);
	}

	private void goToGameOver(Monster winner) {
		appendLog("=== " + winner.getName() + " WINS! ===");
		main.showGameOverScene(
				winner.getName(),
				winner.getOriginalRole().name(),
				game.getPlayer().getEnergy(),
				game.getPlayer().getName(),
				game.getOpponent().getEnergy(),
				game.getOpponent().getName()
		);
	}
}
