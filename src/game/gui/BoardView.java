package game.gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import game.engine.Board;
import game.engine.Constants;
import game.engine.cells.Cell;
import game.engine.monsters.Monster;

public class BoardView extends StackPane {
	private static final double GAP = 2;
	private static final double PADDING = 6;
	private static final double TOKEN_SIZE = 26;

	private final Board board;
	private final GridPane grid = new GridPane();
	private final Pane overlay = new Pane();
	private final CellView[] cellViews = new CellView[Constants.BOARD_SIZE];
	private final Label playerToken = makeToken("P", Theme.COLOR_PLAYER);
	private final Label opponentToken = makeToken("O", Theme.COLOR_OPP);

	public BoardView(Board board) {
		this.board = board;

		grid.setHgap(GAP);
		grid.setVgap(GAP);
		grid.setPadding(new Insets(PADDING));
		grid.setStyle("-fx-background-color: " + Theme.COLOR_GRID_BG + "; -fx-background-radius: 10;");

		buildCells();

		double total = PADDING * 2 + 10 * Theme.CELL_SIZE + 9 * GAP;
		grid.setMinSize(total, total);
		grid.setPrefSize(total, total);
		grid.setMaxSize(total, total);

		overlay.setMouseTransparent(true);
		overlay.setPickOnBounds(false);
		overlay.setMinSize(total, total);
		overlay.setPrefSize(total, total);
		overlay.setMaxSize(total, total);

		playerToken.setVisible(false);
		opponentToken.setVisible(false);
		overlay.getChildren().addAll(playerToken, opponentToken);

		setAlignment(Pos.TOP_LEFT);
		getChildren().addAll(grid, overlay);
		setMinSize(total, total);
		setPrefSize(total, total);
		setMaxSize(total, total);
	}

	private void buildCells() {
		for (int i = 0; i < Constants.BOARD_SIZE; i++) {
			Cell cell = board.getCellAt(i);
			CellView cv = new CellView(i, cell);
			cellViews[i] = cv;
			int[] rc = engineToGridRowCol(i);
			grid.add(cv, rc[1], rc[0]);
		}
	}

	private int[] engineToGridRowCol(int index) {
		int cols = Constants.BOARD_COLS;
		int row = index / cols;
		int col = index % cols;
		if (row % 2 == 1) col = cols - 1 - col;
		int gridRow = (Constants.BOARD_ROWS - 1) - row;
		return new int[]{gridRow, col};
	}

	private Label makeToken(String text, String color) {
		Label l = new Label(text);
		l.setStyle("-fx-background-color: " + color + "; -fx-text-fill: " + Theme.COLOR_DOT_TEXT + ";"
				+ " -fx-font-weight: bold; -fx-padding: 2 7; -fx-background-radius: 14; -fx-font-size: 14px;"
				+ " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 6, 0, 0, 2);");
		l.setMinSize(TOKEN_SIZE, TOKEN_SIZE);
		return l;
	}

	public CellView getCellView(int index) {
		return cellViews[index];
	}

	public Point2D cellCenter(int index) {
		int[] rc = engineToGridRowCol(index);
		double cellPlusGap = Theme.CELL_SIZE + GAP;
		double cx = PADDING + rc[1] * cellPlusGap + Theme.CELL_SIZE / 2.0;
		double cy = PADDING + rc[0] * cellPlusGap + Theme.CELL_SIZE / 2.0;
		return new Point2D(cx, cy);
	}

	/** Refresh all cell visuals (door state, in-cell pieces). */
	public void refresh(Monster player, Monster opponent) {
		for (CellView cv : cellViews) cv.refresh(player, opponent);
	}

	/** Briefly flash the cell at this index. */
	public void flashCell(int index, javafx.scene.paint.Color color) {
		cellViews[index].flash(color);
	}

	/**
	 * Animate a monster token from one cell to another. Hops cell-by-cell
	 * along the actual board path so the token follows the zigzag layout
	 * (rather than cutting diagonally across rows).
	 *
	 * @param isPlayer    true for player, false for opponent
	 * @param fromIndex   starting cell index
	 * @param toIndex     destination cell index
	 * @param isTeleport  true if the move was caused by a teleport card
	 *                    (Swapper / Start Over) - skips the cell-by-cell path
	 *                    and does a direct glide so the animation matches
	 *                    the engine semantics (teleport, not a walk).
	 * @param onDone      callback when animation completes
	 */
	public void animateMove(boolean isPlayer, int fromIndex, int toIndex,
							boolean isTeleport, Runnable onDone) {
		Label tok = isPlayer ? playerToken : opponentToken;

		// Hide the static piece in the source cell while the floating token glides.
		cellViews[fromIndex].setPieceVisible(isPlayer, false);

		double offX = TOKEN_SIZE / 2.0;
		double offY = TOKEN_SIZE / 2.0;

		Point2D start = cellCenter(fromIndex);
		tok.setLayoutX(start.getX() - offX);
		tok.setLayoutY(start.getY() - offY);
		tok.setVisible(true);

		final int MAX_HOPS = 30;
		int[] path; // ordered list of intermediate cell indices ending at toIndex

		if (isTeleport || fromIndex == toIndex) {
			// Card-driven teleport (Swapper / Start Over) or no movement -
			// skip the cell-by-cell walk and glide directly to the destination.
			path = new int[]{ toIndex };
		} else {
			// Natural movement (dice + conveyor / sock). Pick the smaller
			// modular direction - dice rolls and transport effects are
			// short-range, so the smaller side matches the actual path the
			// engine walked (including wrap-around at the end of the board).
			int forwardSteps  = (toIndex - fromIndex + Constants.BOARD_SIZE) % Constants.BOARD_SIZE;
			int backwardSteps = (fromIndex - toIndex + Constants.BOARD_SIZE) % Constants.BOARD_SIZE;

			if (forwardSteps <= MAX_HOPS && forwardSteps <= backwardSteps) {
				path = new int[forwardSteps];
				int idx = fromIndex;
				for (int s = 0; s < forwardSteps; s++) {
					idx = (idx + 1) % Constants.BOARD_SIZE;
					path[s] = idx;
				}
			} else if (backwardSteps <= MAX_HOPS) {
				path = new int[backwardSteps];
				int idx = fromIndex;
				for (int s = 0; s < backwardSteps; s++) {
					idx = (idx - 1 + Constants.BOARD_SIZE) % Constants.BOARD_SIZE;
					path[s] = idx;
				}
			} else {
				// Both directions too long (shouldn't happen for non-teleports
				// with current game data) - fall back to direct glide.
				path = new int[]{ toIndex };
			}
		}

		Timeline tl = new Timeline();
		double perStep = path.length > 6 ? 80 : 120;   // shorter per-hop for long paths
		for (int s = 0; s < path.length; s++) {
			int idx = path[s];
			Point2D p = cellCenter(idx);
			double t = (s + 1) * perStep;
			tl.getKeyFrames().add(new KeyFrame(Duration.millis(t),
					new KeyValue(tok.layoutXProperty(), p.getX() - offX, Interpolator.EASE_OUT),
					new KeyValue(tok.layoutYProperty(), p.getY() - offY, Interpolator.EASE_OUT)));
			// Flash every cell the token passes through (except the final landing
			// cell - the GameScene will flash that one in a brighter way).
			if (s < path.length - 1) {
				final CellView cv = cellViews[idx];
				tl.getKeyFrames().add(new KeyFrame(Duration.millis(t),
						e -> cv.flash(javafx.scene.paint.Color.web(Theme.COLOR_ACCENT_GOLD))));
			}
		}

		tl.setOnFinished(e -> {
			tok.setVisible(false);
			// Do NOT re-show the source piece here - the engine has already moved
			// the monster, and the caller's refresh() will sync the in-cell markers
			// to the new positions.
			if (onDone != null) onDone.run();
		});
		tl.play();
	}
}
