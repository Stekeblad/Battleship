package io.github.stekeblad.battleship.battleship_core.boards;

import io.github.stekeblad.battleship.battleship_core.ships.BaseShip;
import io.github.stekeblad.battleship.battleship_core.ships.ShipOrientation;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents the player's board. Ships can be added at creation time or afterwards with addShip().
 * When ships are added the cell around it are marked to indicate that no ship can be placed there. This hints can be
 * seen then calling getBoard() and can be removed by calling removePlacementGuide(). Note that if a new ship is added
 * after the placement guide has been removed the distance checks to other ships does not work and then calling
 * removePlacementGuide the guide cells state will be replaced with unknown independent of what was there before adding the ship.
 * For safety: DO NOT ADD A SHIP AFTER REMOVEPLACEMENTGUIDE() HAS BEEN CALLED AND SHOOTING HAS STARTED
 */
public class PlayerBoard {
    private int SIZE_X;
    private int SIZE_Y;
    private BoardCellState[][] board;
    private ArrayList<BaseShip> ships;
    private int shipsLeft;

    /**
     * @return the board with all known marks
     */
    public BoardCellState[][] getBoard() {
        return board;
    }

    /**
     * @return the number of ships that has not been completely destroyed
     */
    public int getShipsLeft() {
        return shipsLeft;
    }

    /**
     * A define-ships-later constructor, just creates a empty board
     * @param xSize board x size
     * @param ySize board y size
     * @throws Exception if xSize or ySize is less than 1
     */
    public PlayerBoard(int xSize, int ySize) throws Exception {
        if (xSize < 1 || ySize < 1) {
            throw new Exception("Invalid board size, size is zero or negative");
        }
        SIZE_X = xSize;
        SIZE_Y = ySize;
        board = new BoardCellState[xSize][ySize];
        for(int x = 0; x < xSize; x++) {
            Arrays.fill(board[x], BoardCellState.UNKNOWN);
        }
        ships = new ArrayList<>();
    }

    /**
     * Fires a shot at [x, y] and returns a BoardCellState with what was there
     * @param x x coord to target
     * @param y y coord to target
     * @return BoardCellState.HIT if a ship was hit but not destroyed.
     * BoardCellState.SUNKEN if a ship was destroyed.
     * BoardCellState.MISS if the shot did not hit a ship.
     */
    public BoardCellState fireAt(int x, int y) throws IndexOutOfBoundsException {
        if(x < 0 || y < 0 || x >= SIZE_X || y >= SIZE_Y) {
            throw new IndexOutOfBoundsException("Invalid coordinates");
        }
        if(board[x][y] != BoardCellState.UNKNOWN && board[x][y] != BoardCellState.OWN_SHIP) {
            return board[x][y]; // shot fired at this cell before, return the result of that shot
        }
        for (BaseShip ship : ships) { // Check all ships if they was hit
            if(ship.checkHit(x, y)) {
                if(ship.getStillFloating()) { // Ship hit but not destroyed
                    board[x][y] = BoardCellState.HIT;
                    return BoardCellState.HIT;
                } else { // mark all the ship's cells on board as sunken
                    markShipAsSunken(ship);
                    return BoardCellState.SUNKEN;
                }
            }
        }
        board[x][y] = BoardCellState.MISS;
        return BoardCellState.MISS;
    }

    /**
     * Tries to add a ship to the board
     * @param newShip the ship to add
     * @throws IndexOutOfBoundsException if parts of the ship is outside the board
     * @throws Exception if the ship is to close to a existing one
     */
    public void addShip(BaseShip newShip) throws Exception {
        // Check if ship is completely inside the board
        if(newShip.getxCoord() < 0 || newShip.getyCoord() < 0) {
            throw new IndexOutOfBoundsException("A ship is outside the board");
        }
        if(newShip.getOrientation() == ShipOrientation.VERTICAL) {
            if(newShip.getxCoord() >= SIZE_X || newShip.getyCoord() + newShip.getShipSize() > SIZE_Y) {
                throw new IndexOutOfBoundsException("A ship is outside the board");
            }
        } else {
            if(newShip.getxCoord() + newShip.getShipSize() > SIZE_X || newShip.getyCoord() >= SIZE_Y) {
                throw new IndexOutOfBoundsException("A ship is outside the board");
            }
        }
        // check if new ship has valid position based on hints placed on board
        int shipX = newShip.getxCoord();
        int shipY = newShip.getyCoord();
        // check top left part of ship
        if(board[shipX][shipY] != BoardCellState.UNKNOWN) {
            throw new Exception("Invalid ship placement, to close to another ship");
        }
        if (newShip.getOrientation() == ShipOrientation.VERTICAL) {
            for(int i = 1; i < newShip.getShipSize(); i++) {
                if (board[shipX][shipY + i] != BoardCellState.UNKNOWN) {
                    throw new Exception("Invalid ship placement, to close to another ship");
                }
            }
        } else { //newShip is horizontal
            for(int i = 1; i < newShip.getShipSize(); i++) {
                if (board[shipX + i][shipY] != BoardCellState.UNKNOWN) {
                    throw new Exception("Invalid ship placement, to close to another ship");
                }
            }
        }
        // Ship placement is valid, update board hints
        if (newShip.getOrientation() == ShipOrientation.VERTICAL) {
            for (int i = -1; i < newShip.getShipSize() + 1; i++) {
                if(i == -1 && shipY == 0) {
                    continue; // Do not mark cells outside board
                }
                if(i == newShip.getShipSize() && newShip.getShipSize() + shipY == SIZE_Y) {
                    continue; // Do not mark cells outside board
                }
                if(shipX != 0) { board[shipX - 1][shipY + i] = BoardCellState.PLACEMENT_HINT; }
                if(shipX != SIZE_X - 1) { board[shipX + 1][shipY + i] = BoardCellState.PLACEMENT_HINT; }
                if(i == -1 || i == newShip.getShipSize()) {
                    board[shipX][shipY + i] = BoardCellState.PLACEMENT_HINT;
                } else {
                    board[shipX][shipY + i] = BoardCellState.OWN_SHIP;
                }
            }
        } else { // ship is horizontal
            for (int i = -1; i < newShip.getShipSize() + 1; i++) {
                if(i == -1 && shipX == 0) {
                    continue; // Do not mark cells outside board
                }
                if(i == newShip.getShipSize() && newShip.getShipSize() + shipX == SIZE_X) {
                    continue; // Do not mark cells outside board
                }
                if(shipY != 0) { board[shipX + i][shipY - 1] = BoardCellState.PLACEMENT_HINT; }
                if(shipY != SIZE_Y - 1) { board[shipX + i][shipY + 1] = BoardCellState.PLACEMENT_HINT; }
                if(i == -1 || i == newShip.getShipSize()) {
                    board[shipX + i][shipY] = BoardCellState.PLACEMENT_HINT;
                } else {
                    board[shipX + i][shipY] = BoardCellState.OWN_SHIP;
                }
            }
        } // end updating board hints
        this.ships.add(newShip);
        shipsLeft++;
    }

    /**
     * During ship placement phase the board marks the cells around all ships to show where ships can not be placed.
     * This method should be called shortly after the last ship has been placed and before the shooting phase begins.
     */
    public void removePlacementGuide() {
        for(int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                if(board[x][y] == BoardCellState.PLACEMENT_HINT) {
                    board[x][y] = BoardCellState.UNKNOWN;
                }
            }
        }
    }

    /**
     * Updates the marks to show a ship as SUNKEN instead of HIT
     * @param ship the ship to be marked as SUNKEN
     */
    private void markShipAsSunken(BaseShip ship) {
        int x = ship.getxCoord();
        int y = ship.getyCoord();
        if(ship.getOrientation() == ShipOrientation.HORIZONTAL) {
            for(int i = 0; i < ship.getShipSize(); i++) {
                board[x + i][y] = BoardCellState.SUNKEN;
            }
        } else {
            for(int i = 0; i < ship.getShipSize(); i++) {
                board[x][y + i] = BoardCellState.SUNKEN;
            }
        }
        shipsLeft--;
    }
}
