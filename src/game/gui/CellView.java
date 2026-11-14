package game.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import game.engine.Role;
import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.monsters.Monster;

public class CellView extends StackPane {
	private final int index;
	private final Cell cell;
	private Label monsterMarker;
	private Label exhaustedMark;
	private VBox content;
	private Label playerDot;
	private Label oppDot;

	public CellView(int index, Cell cell) {
		this.index = index;
		this.cell = cell;
		setPrefSize(Theme.CELL_SIZE, Theme.CELL_SIZE);
		setMinSize(Theme.CELL_SIZE, Theme.CELL_SIZE);
		setMaxSize(Theme.CELL_SIZE, Theme.CELL_SIZE);
		getStyleClass().add("cell-base");
		buildContent();
		applyBaseStyle();
	}

	public int getIndex() {
		return index;
	}

	public Cell getCell() {
		return cell;
	}

	private void applyBaseStyle() {
		String color = colorFor(cell);
		setStyle("-fx-background-color: " + color + "; -fx-border-color: " + Theme.COLOR_BORDER + "; -fx-border-width: 2;");
	}

	private String colorFor(Cell c) {
		if (c instanceof DoorCell) {
			DoorCell d = (DoorCell) c;
			if (d.isActivated()) return Theme.COLOR_DOOR_EXHAUSTED;
			return d.getRole() == Role.SCARER ? Theme.COLOR_DOOR_SCARER : Theme.COLOR_DOOR_LAUGHER;
		}
		if (c instanceof CardCell)         return Theme.COLOR_CARD_CELL;
		if (c instanceof MonsterCell)      return Theme.COLOR_MONSTER_CELL;
		if (c instanceof ConveyorBelt)     return Theme.COLOR_CONVEYOR;
		if (c instanceof ContaminationSock) return Theme.COLOR_SOCK;
		return Theme.COLOR_NORMAL;
	}

	private void buildContent() {
		content = new VBox(1);
		content.setAlignment(Pos.TOP_CENTER);
		content.setPadding(new Insets(2, 2, 2, 2));

		Label idx = new Label(String.valueOf(index));
		idx.getStyleClass().add("cell-index");
		idx.setStyle("-fx-text-fill: #000000; -fx-font-weight: bold;");

		HBox topRow = new HBox(2, idx);
		topRow.setAlignment(Pos.CENTER_LEFT);

		content.getChildren().add(topRow);

		String label = labelFor(cell);
		if (label != null) {
			Label l = new Label(label);
			l.setWrapText(true);
			l.setStyle("-fx-font-size: 9px; -fx-text-fill: #000000; -fx-font-weight: bold;");
			l.setMaxWidth(Theme.CELL_SIZE - 4);
			content.getChildren().add(l);
		}

		String sub = subTextFor(cell);
		if (sub != null) {
			Label l = new Label(sub);
			l.setStyle("-fx-font-size: 9px; -fx-text-fill: #000000;");
			l.setMaxWidth(Theme.CELL_SIZE - 4);
			l.setWrapText(true);
			content.getChildren().add(l);
		}

		// Energy badge for doors
		if (cell instanceof DoorCell) {
			DoorCell d = (DoorCell) cell;
			Label energy = new Label(String.valueOf(d.getEnergy()));
			energy.getStyleClass().add("cell-energy");
			energy.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #000000;");
			content.getChildren().add(energy);
		} else if (cell instanceof ConveyorBelt) {
			ConveyorBelt cb = (ConveyorBelt) cell;
			Label e = new Label("+" + cb.getEffect());
			e.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #000000;");
			content.getChildren().add(e);
		} else if (cell instanceof ContaminationSock) {
			ContaminationSock cs = (ContaminationSock) cell;
			Label e = new Label(String.valueOf(cs.getEffect()));
			e.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #000000;");
			content.getChildren().add(e);
		}

		getChildren().add(content);

		// Exhausted door indicator (X)
		exhaustedMark = new Label("X");
		exhaustedMark.setStyle("-fx-font-size: 28px; -fx-text-fill: rgba(180,80,80,0.7); -fx-font-weight: bold;");
		exhaustedMark.setVisible(false);
		StackPane.setAlignment(exhaustedMark, Pos.CENTER);
		getChildren().add(exhaustedMark);

		// Monster pieces (player & opponent dots)
		HBox piecesBox = new HBox(2);
		piecesBox.setAlignment(Pos.BOTTOM_RIGHT);
		piecesBox.setPadding(new Insets(0, 2, 2, 0));
		String dotTextColor = Theme.COLOR_DOT_TEXT;
		playerDot = new Label("P");
		playerDot.setStyle("-fx-background-color: " + Theme.COLOR_PLAYER + "; -fx-text-fill: " + dotTextColor + ";"
				+ " -fx-font-weight: bold; -fx-padding: 1 5; -fx-background-radius: 8; -fx-font-size: 11px;"
				+ " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 2, 0, 0, 1);");
		playerDot.setVisible(false);
		oppDot = new Label("O");
		oppDot.setStyle("-fx-background-color: " + Theme.COLOR_OPP + "; -fx-text-fill: " + dotTextColor + ";"
				+ " -fx-font-weight: bold; -fx-padding: 1 5; -fx-background-radius: 8; -fx-font-size: 11px;"
				+ " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 2, 0, 0, 1);");
		oppDot.setVisible(false);
		piecesBox.getChildren().addAll(playerDot, oppDot);
		StackPane.setAlignment(piecesBox, Pos.BOTTOM_RIGHT);
		getChildren().add(piecesBox);

		// Tooltip with full info
		Tooltip.install(this, new Tooltip(tooltipText()));
	}

	private String labelFor(Cell c) {
		if (c instanceof CardCell) return "CARD";
		if (c instanceof MonsterCell) {
			Monster m = ((MonsterCell) c).getCellMonster();
			return m != null ? m.getName().split(" ")[0] : "MON";
		}
		if (c instanceof ConveyorBelt) return "BELT";
		if (c instanceof ContaminationSock) return "SOCK";
		if (index == 0) return "START";
		if (index == 99) return "BOO";
		return null;
	}

	private String subTextFor(Cell c) {
		if (c instanceof DoorCell) {
			DoorCell d = (DoorCell) c;
			return (d.getRole() == Role.SCARER ? "Scarer" : "Laugher") + "\nDoor";
		}
		if (c instanceof MonsterCell) {
			Monster m = ((MonsterCell) c).getCellMonster();
			if (m != null) return m.getRole() == Role.SCARER ? "Scarer" : "Laugher";
		}
		return null;
	}

	// (indexColor / textColor helpers removed - all pastel cells use dark text.)

	private String tooltipText() {
		StringBuilder sb = new StringBuilder();
		sb.append("Cell ").append(index).append(" - ").append(cell.getName()).append("\n");
		if (cell instanceof DoorCell) {
			DoorCell d = (DoorCell) cell;
			sb.append("Door: ").append(d.getRole()).append("\nEnergy: ").append(d.getEnergy());
			sb.append("\nActivated: ").append(d.isActivated());
		} else if (cell instanceof MonsterCell) {
			Monster m = ((MonsterCell) cell).getCellMonster();
			if (m != null) {
				sb.append("Monster: ").append(m.getName()).append(" (").append(m.getRole()).append(")");
				sb.append("\nType: ").append(m.getClass().getSimpleName());
				sb.append("\nEnergy: ").append(m.getEnergy());
			}
		} else if (cell instanceof ConveyorBelt) {
			sb.append("Conveyor Belt: +").append(((ConveyorBelt) cell).getEffect()).append(" cells");
		} else if (cell instanceof ContaminationSock) {
			sb.append("Contamination Sock: ").append(((ContaminationSock) cell).getEffect()).append(" cells, -100 energy");
		} else if (cell instanceof CardCell) {
			sb.append("Card Cell - draw a random card");
		}
		return sb.toString();
	}

	public void refresh(Monster player, Monster opponent) {
		// Update activated marker for doors
		if (cell instanceof DoorCell) {
			DoorCell d = (DoorCell) cell;
			exhaustedMark.setVisible(d.isActivated());
			applyBaseStyle();
		}
		// Update monster pieces. CRITICAL: also toggle 'managed' so that an
		// invisible dot does not still take up space in the HBox layout and
		// shift the visible dot off the bottom-right corner.
		boolean hasPlayer = player != null && player.getPosition() == index;
		boolean hasOpp = opponent != null && opponent.getPosition() == index;
		playerDot.setVisible(hasPlayer);
		playerDot.setManaged(hasPlayer);
		oppDot.setVisible(hasOpp);
		oppDot.setManaged(hasOpp);
	}

	/** Temporarily hide / show the in-cell P or O marker (used during animation). */
	public void setPieceVisible(boolean isPlayer, boolean visible) {
		Label dot = isPlayer ? playerDot : oppDot;
		dot.setVisible(visible);
		dot.setManaged(visible);
	}

	public void flash(Color color) {
		String prev = getStyle();
		setStyle("-fx-background-color: #34a203; -fx-border-color: #000000; -fx-border-width: 1;");
		javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(javafx.util.Duration.millis(450));
		pt.setOnFinished(e -> setStyle(prev));
		pt.play();
	}

	private String toHex(Color c) {
		return String.format("#%02X%02X%02X", (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
	}

}
