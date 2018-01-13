package io.github.stekeblad.battleship.fxBattleship;

import io.github.stekeblad.battleship.battleship_core.boards.BoardCellState;

/**
 * A way to translate a BoardCellState to a color
 */
public class CellContentColor {
    /**
     * Takes a BoardCellState and returns the color that this type of cell should be colored in
     * @param state a BoardCellState to look up
     * @return the hex color code for that state with a '#' in the beginning
     */
    public static String getColor(BoardCellState state) {
        switch (state) {

            case UNKNOWN:
                return "#00bfff"; // light blue
            case MISS:
                return "#0000ff"; // blue
            case HIT:
                return "#ffff00"; // yellow
            case SUNKEN:
                return "#ff0000"; //red
            case OWN_SHIP:
                return "#00ff00"; // green
            case PLACEMENT_HINT:
                return "#000000"; // black
            default:
                return "#ffffff"; // white
        }
    }
}
