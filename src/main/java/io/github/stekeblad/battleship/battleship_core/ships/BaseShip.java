package io.github.stekeblad.battleship.battleship_core.ships;

import java.util.Arrays;

/**
 * abstract class defining some features all ships need.
 * All subclasses should also implement the following.
 * <p>SHIPSIZE is the number of cells the ship take up.</p>
 *  public static final int SHIPSIZE =
 * <p>
 *
 * </p>
 * SHIP_TYPE is used to find witch class the ship is of then stored as a ArrayList&lt;BaseShip&gt; for example.
 * Add all new subclasses to the ShipType enum
 * <p>
 *      public static final ShipType SHIP_TYPE =
 * </p>
 */
public abstract class BaseShip {
    // Variables all ships have
    public static final ShipType shiptype = ShipType.NOT_SPECIFIED; // should be overridden
    int xCoord; // the ships top left x coord
    int yCoord; // the ships top left y coord
    ShipOrientation orientation; // horizontal or vertical
    private boolean[] hits; // contains witch parts of the ship has been hit

    // Getters
    public int getxCoord() {
        return xCoord;
    }
    public int getyCoord() {
        return yCoord;
    }
    public ShipOrientation getOrientation() {
        return orientation;
    }

    /**
     * Tests if this ship has been destroyed
     * @return false if the ship is destroyed, true if at least one part of it is not yet hit
     */
    public boolean getStillFloating() { // Should this be final to prevent overriding? You could create cool things with ships if things could be overridden
        for (boolean hit : hits){
            // If ship part is not hit, it is still floating
            if(! hit) {
                return true;
            }
        }
        // No part undamaged, ship sunken
        return false;
    }

    // All ships must implement the following
    public abstract int getShipSize();

    //Implemented methods
    /**
     * Creates a ship at [x, y] with the orientation <code>orientation</code> and size <code>getShipSize()</code>
     */
    BaseShip(int x, int y, ShipOrientation orientation) {
        xCoord = x;
        yCoord = y;
        this.orientation = orientation;
        hits = new boolean[getShipSize()];
        Arrays.fill(hits, false);
    }

    /**
     * Checks if this ship is hit by a shot at [x, y] and if so marks the ship as damaged on that part
     * @param x x-coord
     * @param y y-coord
     * @return true if the ship was hit, false if not.
     */
    public boolean checkHit(int x, int y) {// Should this be final to prevent overriding? You could create cool things with ships if things could be overridden
        // If ship is rotated horizontally and the shot landed on that row (yCoord)
        if(orientation == ShipOrientation.HORIZONTAL && y == yCoord) {
            // Test if it was on one of the xCoord s the ship occupy
            if(x >= xCoord && x < xCoord + getShipSize()) {
                hits[x - xCoord] = true; // flag ship part as damaged
                return true;
            }
            return false;
        }
        // Same as above if but for vertical ships
        if(orientation == ShipOrientation.VERTICAL && x == xCoord) {
            if(y >= yCoord && y < yCoord + getShipSize()) {
                hits[y - yCoord] = true;
                return true;
            }
            return false;
        }
        return false;
    }
}
