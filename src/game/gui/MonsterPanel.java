package game.gui;

import java.io.File;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import game.engine.Constants;
import game.engine.monsters.Dasher;
import game.engine.monsters.Monster;
import game.engine.monsters.MultiTasker;

public class MonsterPanel extends StackPane {
	private final VBox content;
	private final Pane overlay;

	private final Label headerLabel;
	private final ImageView monsterImage;
	private final Label nameLabel;
	private final Label originalRoleLabel;
	private final Label currentRoleLabel;
	private final Label typeLabel;
	private final Label energyLabel;
	private final Label positionLabel;
	private final ProgressBar energyBar;
	private final Label statusLabel;
	private final HBox statusBox;
	private Monster monster;
	private final boolean isPlayer;

	public MonsterPanel(String header, boolean isPlayer) {
		this.isPlayer = isPlayer;
		setMinWidth(240);
		setPrefWidth(260);
		setAlignment(Pos.TOP_LEFT);

		content = new VBox(6);
		content.setPadding(new Insets(10));
		content.getStyleClass().add("monster-panel");

		overlay = new Pane();
		overlay.setMouseTransparent(true);
		overlay.setPickOnBounds(false);

		headerLabel = new Label(header);
		String headerColor = isPlayer ? Theme.COLOR_PLAYER_HEADER : Theme.COLOR_OPP_HEADER;
		headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + headerColor + ";");

		monsterImage = new ImageView();
		monsterImage.setFitWidth(110);
		monsterImage.setFitHeight(110);
		monsterImage.setPreserveRatio(true);

		nameLabel = makeValue("");
		nameLabel.setStyle(nameLabel.getStyle() + "-fx-font-size: 16px;");

		originalRoleLabel = makeValue("");
		currentRoleLabel = makeValue("");
		typeLabel = makeValue("");
		energyLabel = makeValue("");
		positionLabel = makeValue("");

		energyBar = new ProgressBar(0);
		energyBar.setMaxWidth(Double.MAX_VALUE);
		energyBar.setStyle("-fx-accent: " + Theme.COLOR_ACCENT_GOLD + ";");

		statusLabel = new Label("");
		statusLabel.setStyle("-fx-text-fill: " + Theme.COLOR_TEXT_MUTED + "; -fx-font-size: 12px;");
		statusLabel.setWrapText(true);

		statusBox = new HBox(6);
		statusBox.setAlignment(Pos.CENTER_LEFT);

		content.getChildren().addAll(
				headerLabel,
				monsterImage,
				nameLabel,
				row("Original Role:", originalRoleLabel),
				row("Current Role:", currentRoleLabel),
				row("Type:", typeLabel),
				row("Energy:", energyLabel),
				energyBar,
				row("Position:", positionLabel),
				statusBox,
				statusLabel
		);

		applyBaseStyle();

		getChildren().addAll(content, overlay);
	}

	private HBox row(String label, Label value) {
		Label l = new Label(label);
		l.getStyleClass().add("info-label");
		l.setMinWidth(105);
		l.setStyle("-fx-text-fill: " + Theme.COLOR_TEXT_MUTED + "; -fx-font-size: 12px;");
		HBox h = new HBox(6, l, value);
		h.setAlignment(Pos.CENTER_LEFT);
		return h;
	}

	private Label makeValue(String text) {
		Label l = new Label(text);
		l.setStyle("-fx-text-fill: " + Theme.COLOR_TEXT_DARK + "; -fx-font-weight: bold;");
		return l;
	}

	private void applyBaseStyle() {
		content.setStyle("-fx-background-color: " + Theme.COLOR_BG_PANEL + ";"
				+ " -fx-padding: 10; -fx-background-radius: 10;"
				+ " -fx-border-color: " + Theme.COLOR_BORDER + "; -fx-border-width: 2; -fx-border-radius: 10;");
	}

	public void setActive(boolean active) {
		if (active) {
			content.setStyle("-fx-background-color: " + Theme.COLOR_ACTIVE_PANEL_BG + ";"
					+ " -fx-padding: 10; -fx-background-radius: 10;"
					+ " -fx-border-color: " + Theme.COLOR_ACCENT_ROSE + "; -fx-border-width: 2; -fx-border-radius: 10;");
		} else {
			applyBaseStyle();
		}
	}

	public void bind(Monster m) {
		this.monster = m;
		try {
			// Try classpath resource first (works when packaged in a JAR),
			// then fall back to a relative file path (works from Eclipse).
			java.io.InputStream is = getClass().getResourceAsStream("/imgs/" + m.getName() + ".png");
			if (is != null) {
				monsterImage.setImage(new Image(is, 110, 110, true, true));
			} else {
				File f = new File("imgs/" + m.getName() + ".png");
				if (f.exists()) {
					monsterImage.setImage(new Image(f.toURI().toString(), 110, 110, true, true));
				}
			}
		} catch (Exception ignored) {}
		refresh();
	}

	public Monster getMonster() {
		return monster;
	}

	public void refresh() {
		if (monster == null) return;
		nameLabel.setText(monster.getName());
		originalRoleLabel.setText(monster.getOriginalRole().name());
		currentRoleLabel.setText(monster.getRole().name());
		if (monster.getRole() != monster.getOriginalRole())
			currentRoleLabel.setStyle("-fx-text-fill: " + Theme.COLOR_ACCENT_ROSE + "; -fx-font-weight: bold;");
		else
			currentRoleLabel.setStyle("-fx-text-fill: " + Theme.COLOR_TEXT_DARK + "; -fx-font-weight: bold;");
		typeLabel.setText(monster.getClass().getSimpleName());
		energyLabel.setText(String.valueOf(monster.getEnergy()));
		double progress = Math.min(1.0, monster.getEnergy() / (double) Constants.WINNING_ENERGY);
		energyBar.setProgress(progress);
		positionLabel.setText(String.valueOf(monster.getPosition()));

		statusBox.getChildren().clear();
		if (monster.isShielded()) statusBox.getChildren().add(badge("SHIELD", Theme.COLOR_SHIELD_BG));
		if (monster.isFrozen()) statusBox.getChildren().add(badge("FROZEN (next turn)", Theme.COLOR_FREEZE_BG));
		if (monster.isConfused()) statusBox.getChildren().add(badge("CONFUSED " + monster.getConfusionTurns() + "t", Theme.COLOR_CONFUSED_BG));
		if (monster instanceof Dasher && ((Dasher) monster).getMomentumTurns() > 0)
			statusBox.getChildren().add(badge("MOMENTUM " + ((Dasher) monster).getMomentumTurns() + "t", Theme.COLOR_MOMENTUM_BG));
		if (monster instanceof MultiTasker && ((MultiTasker) monster).getNormalSpeedTurns() > 0)
			statusBox.getChildren().add(badge("FOCUS " + ((MultiTasker) monster).getNormalSpeedTurns() + "t", Theme.COLOR_FOCUS_BG));

		statusLabel.setText(powerupHint());
	}

	private Label badge(String text, String bgColor) {
		Label l = new Label(text);
		l.setStyle("-fx-padding: 3 8; -fx-background-radius: 6; -fx-text-fill: " + Theme.COLOR_BADGE_TEXT + ";"
				+ " -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-color: " + bgColor + ";");
		return l;
	}

	private String powerupHint() {
		if (monster == null) return "";
		String type = monster.getClass().getSimpleName();
		switch (type) {
			case "Dasher":      return "Powerup: Momentum Rush - 3x speed for 3 turns.";
			case "Dynamo":      return "Powerup: Energy Freeze - skip opponent's next turn.";
			case "MultiTasker": return "Powerup: Focus Mode - normal speed for 2 turns.";
			case "Schemer":     return "Powerup: Chain Attack - steal 10 from every monster (ignores shield).";
			default:            return "";
		}
	}

	public boolean isPlayerPanel() {
		return isPlayer;
	}

	// ============================================================
	// Animations
	// ============================================================

	/** Floating "+50" / "-100" delta that rises and fades. */
	public void flashEnergyDelta(int delta) {
		if (delta == 0) return;
		String sign = delta > 0 ? "+" : "";
		String color = delta > 0 ? Theme.COLOR_DELTA_POS : Theme.COLOR_DELTA_NEG;
		String shadow = "rgba(255,255,255,0.7)";

		Label floater = new Label(sign + delta);
		floater.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 22px; -fx-font-weight: bold;"
				+ " -fx-effect: dropshadow(gaussian, " + shadow + ", 3, 0, 0, 1);");
		floater.setMouseTransparent(true);

		double w = getWidth() > 0 ? getWidth() : 240;
		floater.setLayoutX(w * 0.55);
		floater.setLayoutY(120);
		overlay.getChildren().add(floater);

		TranslateTransition tt = new TranslateTransition(Duration.millis(900), floater);
		tt.setByY(-50);
		FadeTransition ft = new FadeTransition(Duration.millis(900), floater);
		ft.setFromValue(1.0);
		ft.setToValue(0.0);
		ParallelTransition pt = new ParallelTransition(tt, ft);
		pt.setOnFinished(e -> overlay.getChildren().remove(floater));
		pt.play();
	}

	/** Pulse the panel briefly (used when the player activates a powerup). */
	public void pulse() {
		ScaleTransition st = new ScaleTransition(Duration.millis(220), content);
		st.setFromX(1.0); st.setFromY(1.0);
		st.setToX(1.06);  st.setToY(1.06);
		st.setAutoReverse(true);
		st.setCycleCount(2);
		st.play();
	}
}
