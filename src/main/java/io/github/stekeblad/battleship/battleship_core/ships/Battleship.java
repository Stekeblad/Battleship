package io.github.stekeblad.battleship.battleship_core.ships;

/**
 * The large 4-cells ship
 */
public class Battleship extends BaseShip {
    public static final int SHIPSIZE = 4;
    public static final ShipType SHIP_TYPE = ShipType.BATTLESHIP;

    public Battleship(int x, int y, ShipOrientation orientation) {
        super(x, y, orientation);
    }

    @Override
    public int getShipSize() {
        return SHIPSIZE;
    }
}
