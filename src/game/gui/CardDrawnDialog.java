package game.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import game.engine.cards.Card;
import game.engine.cards.ConfusionCard;
import game.engine.cards.EnergyStealCard;
import game.engine.cards.ShieldCard;
import game.engine.cards.StartOverCard;
import game.engine.cards.SwapperCard;

public class CardDrawnDialog {

	public static void show(Stage owner, Card card, String drawerName) {
		Stage dialog = new Stage();
		dialog.initOwner(owner);
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setTitle("Card Drawn");

		Label header = new Label(drawerName + " landed on a Card Cell!");
		header.setStyle("-fx-font-size: 18px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");

		Label name = new Label(card.getName());
		name.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Theme.COLOR_ACCENT_GOLD + ";");

		Label type = new Label("Type: " + cardTypeLabel(card));
		type.setStyle("-fx-text-fill: " + Theme.COLOR_TEXT_DARK + "; -fx-font-size: 14px;");

		Label desc = new Label(card.getDescription());
		desc.setWrapText(true);
		desc.setMaxWidth(380);
		desc.setStyle("-fx-text-fill: " + Theme.COLOR_TEXT_DARK + "; -fx-font-size: 13px;");

		Label luckLabel = new Label(card.isLucky() ? "Lucky for the drawer" : "Unlucky for the drawer");
		luckLabel.setStyle("-fx-text-fill: " + (card.isLucky() ? Theme.COLOR_DELTA_POS : Theme.COLOR_DELTA_NEG)
				+ "; -fx-font-weight: bold; -fx-font-size: 12px;");

		Label effectLine = new Label("Effect: " + cardEffectSummary(card));
		effectLine.setWrapText(true);
		effectLine.setMaxWidth(380);
		effectLine.setStyle("-fx-text-fill: " + Theme.COLOR_TEXT_DARK + "; -fx-font-size: 13px;");

		Button ok = new Button("OK");
		ok.getStyleClass().add("action-button");
		ok.setDefaultButton(true);
		ok.setStyle("-fx-background-color: " + Theme.COLOR_ACCENT_GOLD + "; -fx-text-fill: white;"
				+ " -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 8;");
		ok.setOnAction(e -> dialog.close());

		HBox btns = new HBox(ok);
		btns.setAlignment(Pos.CENTER);

		VBox box = new VBox(10, header, name, type, luckLabel, desc, effectLine, btns);
		box.setAlignment(Pos.CENTER);
		box.setPadding(new Insets(20));
		box.setStyle("-fx-background-color: " + Theme.COLOR_DIALOG_BG + ";"
				+ " -fx-border-color: " + Theme.COLOR_BORDER + "; -fx-border-width: 2;"
				+ " -fx-border-radius: 12; -fx-background-radius: 12;");

		StackPane root = new StackPane(box);
		root.setPadding(new Insets(8));
		root.setStyle("-fx-background-color: transparent;");

		Scene scene = new Scene(root, 460, 320);
		scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
		dialog.setScene(scene);

		// Entrance animation
		box.setOpacity(0);
		box.setScaleX(0.85);
		box.setScaleY(0.85);
		dialog.setOnShown(e -> {
			FadeTransition ft = new FadeTransition(Duration.millis(220), box);
			ft.setFromValue(0); ft.setToValue(1);
			ScaleTransition st = new ScaleTransition(Duration.millis(260), box);
			st.setFromX(0.85); st.setFromY(0.85);
			st.setToX(1.0);    st.setToY(1.0);
			new ParallelTransition(ft, st).play();
		});

		dialog.showAndWait();
	}

	private static String cardTypeLabel(Card card) {
		if (card instanceof SwapperCard)     return "Swapper";
		if (card instanceof EnergyStealCard) return "Energy Steal";
		if (card instanceof StartOverCard)   return "Start Over";
		if (card instanceof ShieldCard)      return "Shield";
		if (card instanceof ConfusionCard)   return "Confusion";
		return "Unknown";
	}

	private static String cardEffectSummary(Card card) {
		if (card instanceof SwapperCard)     return "Swap positions with the opponent (only if you are behind).";
		if (card instanceof EnergyStealCard) return "Steal " + ((EnergyStealCard) card).getEnergy() + " energy from the opponent. Shield blocks the entire steal.";
		if (card instanceof StartOverCard)   return ((StartOverCard) card).isLucky() ? "Send the opponent back to cell 0." : "You go back to cell 0.";
		if (card instanceof ShieldCard)      return "Activate a shield that blocks the next negative energy effect to your team.";
		if (card instanceof ConfusionCard)   return "Both monsters' roles are swapped for " + ((ConfusionCard) card).getDuration() + " turns.";
		return card.getDescription();
	}
}
