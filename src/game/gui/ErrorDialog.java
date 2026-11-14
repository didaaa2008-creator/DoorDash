package game.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ErrorDialog {

	public static void show(String title, String message) {
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setTitle(title);

		Label icon = new Label("!");
		icon.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + Theme.COLOR_DELTA_NEG + ";"
				+ " -fx-background-color: " + Theme.COLOR_BG_PANEL + "; -fx-min-width: 56; -fx-min-height: 56;"
				+ " -fx-alignment: center; -fx-background-radius: 28;");

		Label header = new Label(title);
		header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

		Label body = new Label(message != null ? message : "An error occurred.");
		body.setWrapText(true);
		body.setMaxWidth(360);
		body.setStyle("-fx-text-fill: " + Theme.COLOR_TEXT_DARK + "; -fx-font-size: 13px;");

		Button ok = new Button("OK");
		ok.getStyleClass().add("action-button");
		ok.setDefaultButton(true);
		ok.setStyle("-fx-background-color: " + Theme.COLOR_ACCENT_GOLD + "; -fx-text-fill: white;"
				+ " -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 8;");
		ok.setOnAction(e -> dialog.close());

		HBox btns = new HBox(ok);
		btns.setAlignment(Pos.CENTER);

		VBox box = new VBox(12, icon, header, body, btns);
		box.setAlignment(Pos.CENTER);
		box.setPadding(new Insets(20));
		box.setStyle("-fx-background-color: " + Theme.COLOR_DIALOG_BG + ";"
				+ " -fx-background-radius: 12;");

		Scene scene = new Scene(box, 420, 260);
		scene.setFill(javafx.scene.paint.Color.web(Theme.COLOR_DIALOG_BG));
		dialog.setScene(scene);
		dialog.showAndWait();
	}

	public static void info(String title, String message) {
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setTitle(title);

		Label header = new Label(title);
		header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

		Label body = new Label(message != null ? message : "");
		body.setWrapText(true);
		body.setMaxWidth(380);
		body.setStyle("-fx-text-fill: " + Theme.COLOR_TEXT_DARK + "; -fx-font-size: 13px;");

		Button ok = new Button("OK");
		ok.getStyleClass().add("action-button");
		ok.setDefaultButton(true);
		ok.setStyle("-fx-background-color: " + Theme.COLOR_ACCENT_GOLD + "; -fx-text-fill: white;"
				+ " -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 8;");
		ok.setOnAction(e -> dialog.close());

		HBox btns = new HBox(ok);
		btns.setAlignment(Pos.CENTER);

		VBox box = new VBox(12, header, body, btns);
		box.setAlignment(Pos.CENTER);
		box.setPadding(new Insets(20));
		box.setStyle("-fx-background-color: linear-gradient(to bottom, #f6ecdc, #e2d8ee);"
				+ " -fx-border-color: " + Theme.COLOR_BORDER + "; -fx-border-width: 2;"
				+ " -fx-border-radius: 12; -fx-background-radius: 12;"
				+ " -fx-effect: dropshadow(gaussian, rgba(180,150,180,0.4), 12, 0, 0, 3);");

		Scene scene = new Scene(box, 440, 240);
		dialog.setScene(scene);
		dialog.showAndWait();
	}
}
