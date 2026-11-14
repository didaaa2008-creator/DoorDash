package game.gui;

import javafx.scene.paint.Color;

/**
 * Clean, professional palette — neutral grays and blues with clear cell distinctions.
 */
public final class Theme {
	private Theme() {}

	// Scene dimensions used by Main.java to set the window min/preferred size.
	public static final double WINDOW_WIDTH  = 1720;
	public static final double WINDOW_HEIGHT = 965;
	// Board cell size (px) used by CellView (preferred/min/max dimensions) and BoardView (grid math).
	public static final double CELL_SIZE     = 75;

	// -------------------------------------------------------------------------
	// Cell fill colors — applied in CellView.applyBaseStyle() and shown in the GameScene legend
	// -------------------------------------------------------------------------
	public static final String COLOR_NORMAL         = "#ECEFF1";   // default board cell background
	public static final String COLOR_DOOR_SCARER    = "#90A4AE";   // active Scarer door cell background
	public static final String COLOR_DOOR_LAUGHER   = "#B0BEC5";   // active Laugher door cell background
	public static final String COLOR_DOOR_EXHAUSTED = "#CFD8DC";   // activated (used-up) door cell background
	public static final String COLOR_CARD_CELL      = "#EF9A9A";   // card-draw cell background
	public static final String COLOR_MONSTER_CELL   = "#80DEEA";   // monster-spawn cell background
	public static final String COLOR_CONVEYOR       = "#A5D6A7";   // conveyor belt cell background
	public static final String COLOR_SOCK           = "#FFCC80";   // contamination sock cell background

	// -------------------------------------------------------------------------
	// Player / opponent position tokens — used in BoardView (floating token) and CellView (in-cell dot)
	// -------------------------------------------------------------------------
	public static final String COLOR_PLAYER = "#1565C0";   // "P" token and in-cell player dot background
	public static final String COLOR_OPP    = "#B71C1C";   // "O" token and in-cell opponent dot background

	// -------------------------------------------------------------------------
	// Text colors
	// -------------------------------------------------------------------------
	// Primary label text in CellView (index, energy, name), MonsterPanel value labels,
	// StartScene instructions, CardDrawnDialog body, ErrorDialog body, GameOverScene results.
	public static final String COLOR_TEXT_DARK   = "#FFFFFF";
	// Secondary / hint text in MonsterPanel row labels ("Original Role:", "Energy:", etc.) and status line.
	public static final String COLOR_TEXT_MUTED  = "#000000";

	// -------------------------------------------------------------------------
	// Accent colors
	// -------------------------------------------------------------------------
	// Highlight color used for: energy progress bar, CardPileView title/mark, CardDrawnDialog header/button,
	// GameOverScene trophy/title/buttons, StartScene "VS" label, and cell flash during movement animation.
	public static final String COLOR_ACCENT_GOLD = "#000000";
	// MonsterPanel active-state border color and confused-role text color in MonsterPanel.refresh().
	public static final String COLOR_ACCENT_ROSE = "#000000";
	// Defined but not currently used anywhere.
	public static final String COLOR_ACCENT_BLUE = "#0288D1";

	// -------------------------------------------------------------------------
	// Background / panel colors
	// -------------------------------------------------------------------------
	// Semi-transparent panel background used by MonsterPanel (base + active states), GameScene dice box,
	// GameOverScene energy box and main card, and the .panel / .monster-panel CSS classes.
	public static final String COLOR_BG_PANEL        = "rgba(255,255,255,0.12)";
	// Defined but not currently used anywhere.
	public static final String COLOR_BG_PANEL_DARK   = "rgba(0,0,0,0.18)";
	// Scene root background (.bg-dark CSS class) and GameOverScene root fill.
	public static final String COLOR_BG_GRADIENT     = "#37474F";
	// Modal dialog background used by ErrorDialog and CardDrawnDialog.
	public static final String COLOR_DIALOG_BG       = "#37474F";
	// MonsterPanel background when it is the active player's panel.
	public static final String COLOR_ACTIVE_PANEL_BG = "rgba(255,255,255,0.08)";
	// Game log TextArea background (.log-area CSS class).
	public static final String COLOR_LOG_BG          = "#263238";
	// BoardView grid pane background behind all cells.
	public static final String COLOR_GRID_BG         = "rgba(0,0,0,0.20)";
	// Light text color used for: .panel-title, .info-label, .info-value, .turn-label, .dice-label,
	// .big-label CSS classes, BoardView floating tokens, CellView in-cell dot text, and the log TextArea text.
	public static final String COLOR_DOT_TEXT        = "#ECEFF1";
	// All panel and cell borders throughout the game (MonsterPanel, CellView, dialogs, GameScene dice box, etc.).
	public static final String COLOR_BORDER          = "#000000";

	// -------------------------------------------------------------------------
	// MonsterPanel header text colors ("Player" / "Opponent" title at the top of each panel)
	// -------------------------------------------------------------------------
	public static final String COLOR_PLAYER_HEADER = "#000000";   // player panel header label
	public static final String COLOR_OPP_HEADER    = "#000000";   // opponent panel header label

	// -------------------------------------------------------------------------
	// Energy delta flash colors — used in MonsterPanel.flashEnergyDelta() and CardDrawnDialog luck label
	// -------------------------------------------------------------------------
	public static final String COLOR_DELTA_POS = "#66BB6A";   // positive energy gain (floating "+N" label)
	public static final String COLOR_DELTA_NEG = "#EF5350";   // negative energy loss (floating "-N" label) and ErrorDialog icon/header

	// -------------------------------------------------------------------------
	// Status badge backgrounds — used in MonsterPanel.badge() to tint each status chip
	// -------------------------------------------------------------------------
	public static final String COLOR_FREEZE_BG   = "#B3E5FC";   // "FROZEN" badge and GameScene freeze banner
	public static final String COLOR_CONFUSED_BG = "#F48FB1";   // "CONFUSED" badge
	public static final String COLOR_SHIELD_BG   = "#B2EBF2";   // "SHIELD" badge
	public static final String COLOR_MOMENTUM_BG = "#FFE0B2";   // "MOMENTUM" badge (Dasher)
	public static final String COLOR_FOCUS_BG    = "#80DEEA";   // "FOCUS" badge (MultiTasker)
	// Text color inside all status badges and the GameScene freeze banner.
	public static final String COLOR_BADGE_TEXT  = "#212121";

	// -------------------------------------------------------------------------
	// Card pile visual stack — used in CardPileView.makeCardBack() to build the layered deck
	// -------------------------------------------------------------------------
	// RGB of the topmost (most recently added) card back.
	public static final String COLOR_CARD_BACK_TOP  = "239,154,154";
	// Per-layer RGB darkening step so deeper cards appear slightly darker.
	public static final String COLOR_CARD_BACK_STEP = "5,3,3";

	// GameOverScene "Quit" button background.
	public static final String COLOR_QUIT_BUTTON   = "#546E7A";

	public static final String INLINE_CSS = buildInlineCss();

	private static String buildInlineCss() {
		return
			".root { -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 13px; }" +
			".bg-dark { -fx-background-color: " + COLOR_BG_GRADIENT + "; }" +

			".panel { -fx-background-color: " + COLOR_BG_PANEL + "; -fx-padding: 12; -fx-background-radius: 10;"
			+ " -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 10; -fx-border-width: 2; }" +
			".panel-title { -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_DOT_TEXT + "; }" +

			".title-banner { -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #ECEFF1; }" +
			".subtitle { -fx-font-size: 16px; -fx-font-style: italic; -fx-text-fill: #B0BEC5; }" +

			".monster-panel { -fx-background-color: " + COLOR_BG_PANEL + "; -fx-padding: 10; -fx-background-radius: 10;"
			+ " -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 10; -fx-border-width: 2; }" +
			".monster-panel-active { -fx-border-color: " + COLOR_BORDER + "; -fx-border-width: 2; }" +
			".info-label { -fx-text-fill: " + COLOR_DOT_TEXT + "; -fx-font-size: 12px; }" +
			".info-value { -fx-text-fill: " + COLOR_DOT_TEXT + "; -fx-font-weight: bold; }" +

			".action-button { -fx-background-color: #EF9A9A; -fx-text-fill: #212121; -fx-font-weight: bold;"
			+ " -fx-padding: 8 18; -fx-background-radius: 8; }" +
			".action-button:hover { -fx-background-color: #E57373; }" +
			".action-button:disabled { -fx-opacity: 0.45; }" +
			".side-button { -fx-min-width: 180; -fx-min-height: 70; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10; }" +
			".side-button-scarer { -fx-background-color: #1565C0; -fx-text-fill: white; }" +
			".side-button-scarer:hover { -fx-background-color: #1976D2; }" +
			".side-button-laugher { -fx-background-color: #8b0000; -fx-text-fill: white; }" +
			".side-button-laugher:hover { -fx-background-color: #B71C1C; }" +

			".log-area { -fx-control-inner-background: " + COLOR_LOG_BG + ";"
			+ " -fx-text-fill: #ECEFF1;"
			+ " -fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; }" +
			".log-area .content { -fx-background-color: " + COLOR_LOG_BG + "; }" +

			".turn-label { -fx-font-size: 14px; -fx-text-fill: " + COLOR_DOT_TEXT + "; -fx-font-weight: bold; }" +
			".dice-label { -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_DOT_TEXT + "; }" +
			".big-label  { -fx-font-size: 22px; -fx-text-fill: " + COLOR_DOT_TEXT + "; -fx-font-weight: bold; }" +

			".cell-base { -fx-border-color: " + COLOR_BORDER + "; -fx-border-width: 2; }" +
			".cell-index { -fx-font-size: 9px; -fx-text-fill: #000000; }" +
			".cell-energy { -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #000000; }" +
			".cell-name { -fx-font-size: 9px; -fx-text-fill: #000000; }";
	}

	public static Color hex(String css) {
		return Color.web(css);
	}
}