package game.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import game.engine.Board;

/**
 * A visual stack of card backs representing the draw pile.
 * Shows up to STACK_DEPTH overlapping cards plus the current count.
 * Call refresh() after each draw to update; call animateDraw() to
 * play a brief "card flies off the top" animation.
 */
public class CardPileView extends VBox {
	private static final int    STACK_DEPTH = 5;
	private static final double CARD_W      = 86;
	private static final double CARD_H      = 116;
	private static final double STAGGER     = 4;

	private final Pane stack = new Pane();
	private final Label countLabel = new Label();
	private final Label titleLabel = new Label("Card Pile");
	private final StackPane[] layers = new StackPane[STACK_DEPTH];

	public CardPileView() {
		setSpacing(6);
		setAlignment(Pos.CENTER);
		setPadding(new Insets(6));

		titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + Theme.COLOR_ACCENT_GOLD + ";");

		// Build the visual stack: STACK_DEPTH layered card backs.
		double areaW = CARD_W + (STACK_DEPTH - 1) * STAGGER;
		double areaH = CARD_H + (STACK_DEPTH - 1) * STAGGER;
		stack.setMinSize(areaW, areaH);
		stack.setPrefSize(areaW, areaH);
		stack.setMaxSize(areaW, areaH);

		for (int i = 0; i < STACK_DEPTH; i++) {
			StackPane card = makeCardBack(i);
			card.setLayoutX(i * STAGGER);
			card.setLayoutY((STACK_DEPTH - 1 - i) * STAGGER);
			layers[i] = card;
			stack.getChildren().add(card);
		}

		countLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + Theme.COLOR_TEXT_DARK + ";");

		getChildren().addAll(titleLabel, stack, countLabel);
		refresh();
	}

	private StackPane makeCardBack(int depth) {
		StackPane sp = new StackPane();
		sp.setMinSize(CARD_W, CARD_H);
		sp.setPrefSize(CARD_W, CARD_H);
		sp.setMaxSize(CARD_W, CARD_H);

		// Top-card color + per-layer darken step come from the active theme
		int[] top = parseRgb(Theme.COLOR_CARD_BACK_TOP);
		int[] step = parseRgb(Theme.COLOR_CARD_BACK_STEP);
		int red   = Math.max(0, top[0] - depth * step[0]);
		int green = Math.max(0, top[1] - depth * step[1]);
		int blue  = Math.max(0, top[2] - depth * step[2]);
		String fill = String.format("rgb(%d,%d,%d)", red, green, blue);
		sp.setStyle("-fx-background-color: " + fill + ";"
				+ " -fx-border-color: #000000; -fx-border-width: 2; -fx-border-radius: 10;"
				+ " -fx-background-radius: 10;"
				+ " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 4, 0, 1, 2);");

		// Only the topmost card shows the mark
		if (depth == 0) {
			Label mark = new Label("?");
			mark.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + Theme.COLOR_ACCENT_GOLD + ";"
					+ " -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.7), 2, 0, 0, 1);");
			Label sub = new Label("CARD");
			sub.setStyle("-fx-font-size: 10px; -fx-text-fill: " + Theme.COLOR_ACCENT_GOLD + "; -fx-font-weight: bold;");
			VBox v2 = new VBox(2, mark, sub);
			v2.setAlignment(Pos.CENTER);
			sp.getChildren().add(v2);
		}
		return sp;
	}

	/** Refresh the visible card count and the depth of the visible stack. */
	public void refresh() {
		int remaining = Board.getCards() == null ? 0 : Board.getCards().size();
		int total = computeOriginalSize();
		countLabel.setText(remaining + " / " + total + " cards remaining");

		// Hide bottom layers as the pile shrinks so the user can see it deplete.
		int visible = Math.max(1, Math.min(STACK_DEPTH, (int) Math.ceil(remaining * STACK_DEPTH / (double) Math.max(1, total))));
		for (int i = 0; i < STACK_DEPTH; i++) {
			layers[i].setVisible(i < visible);
		}
	}

	private int computeOriginalSize() {
		java.util.ArrayList<game.engine.cards.Card> orig = Board.getOriginalCards();
		return orig != null ? orig.size() : 0;
	}

	private static int[] parseRgb(String csv) {
		String[] parts = csv.split(",");
		return new int[]{
			Integer.parseInt(parts[0].trim()),
			Integer.parseInt(parts[1].trim()),
			Integer.parseInt(parts[2].trim())
		};
	}

	/**
	 * Play a brief animation suggesting the top card was drawn off the pile.
	 * Call this BEFORE refresh() (so the top card is still visible).
	 */
	public void animateDraw() {
		StackPane top = layers[0];
		// Take a snapshot of the top card as a floating clone so we don't
		// permanently move it - the real layer is then re-shown via refresh().
		final double startX = top.getLayoutX();
		final double startY = top.getLayoutY();

		TranslateTransition tt = new TranslateTransition(Duration.millis(420), top);
		tt.setByX(40);
		tt.setByY(-20);

		FadeTransition ft = new FadeTransition(Duration.millis(420), top);
		ft.setFromValue(1.0);
		ft.setToValue(0.0);

		ParallelTransition pt = new ParallelTransition(tt, ft);
		pt.setOnFinished(e -> {
			top.setTranslateX(0);
			top.setTranslateY(0);
			top.setOpacity(1.0);
			top.setLayoutX(startX);
			top.setLayoutY(startY);
			refresh();
		});
		pt.play();
	}
}
