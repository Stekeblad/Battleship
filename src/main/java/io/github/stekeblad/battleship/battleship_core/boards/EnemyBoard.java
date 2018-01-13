package io.github.stekeblad.battleship.battleship_core.boards;

import java.util.Arrays;

/**
 * Represents the enemy's board and has no initial knowledge of ship locations. This knowledge can only be gain by marking shots
 * using fireAt()
 */
public class EnemyBoard {
    private int SIZE_X;
    private int SIZE_Y;
    private BoardCellState[][] board;

    /**
     * @return the board with all known marks
     */
    public BoardCellState[][] getBoard() {
        return board;
    }

    /**
     * Creates a enemy board with all cells having unknown content
     * @param xSize board x size
     * @param ySize board y size
     * @throws Exception if xSize or ySize is less than 1
     */
    public EnemyBoard(int xSize, int ySize) throws Exception {
        if(xSize < 1 || ySize < 1) {
            throw new Exception("Invalid board size, size is zero or negative");
        }
        SIZE_X = xSize;
        SIZE_Y = ySize;
        board = new BoardCellState[xSize][ySize];
        for(int x = 0; x < xSize; x++) {
            Arrays.fill(board[x], BoardCellState.UNKNOWN);
        }
    }

    /**
     * Mark a spot on on the board with the result of a attack, if cellContent is BoardCellState.SUNKEN a scan for the
     * other parts of the ship is done and they are remarked from HIT to SUNKEN
     * @param x x coord that was targeted
     * @param y y coord that was targeted
     * @param cellContent what was found on the given coords
     */
    public void fireAt(int x, int y, BoardCellState cellContent) {
        board[x][y] = cellContent;
        if(cellContent == BoardCellState.SUNKEN) {
            markShipAsSunken(x, y);
        }
    }

    /**
     * Marks a ship as sunken, but because ships are unknown the content of the coords next to the given must be scanned
     * @param x x coord of the cell that reported SUNKEN
     * @param y y coord of the cell that reported SUNKEN
     */
    private void markShipAsSunken(int x, int y) {
        // Scan towards negative x to see if the ship is horizontal and has damaged parts to update
        int X = x - 1;
        while (X > -1) { // do not go of the board
            if (board[X][y] == BoardCellState.HIT) {
                board[X][y] = BoardCellState.SUNKEN;
                X--;
            } else {
                break; // No (more) ship parts in this direction
            }
        }
        // Scan for ship part in positive x direction
        X = x + 1;
        while (X < SIZE_X) { // do not go of the board
            if (board[X][y] == BoardCellState.HIT) {
                board[X][y] = BoardCellState.SUNKEN;
                X++;
            } else {
                break;
            }
        }
        // Scan for ship part in negative y direction
        int Y = y - 1;
        while (Y > -1) { // do not go of the board
            if (board[x][Y] == BoardCellState.HIT) {
                board[x][Y] = BoardCellState.SUNKEN;
                Y--;
            } else {
                break;
            }
        }
        // Scan for ship part in positive y direction
        Y = y + 1;
        while (Y < SIZE_Y) { // do not go of the board
            if (board[x][Y] == BoardCellState.HIT) {
                board[x][Y] = BoardCellState.SUNKEN;
                Y++;
            } else {
                break;
            }
        }
    }
}
