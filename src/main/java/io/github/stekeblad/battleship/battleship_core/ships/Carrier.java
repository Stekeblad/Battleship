package io.github.stekeblad.battleship.battleship_core.ships;

/**
 * The large 5-cells ship
 */
public class Carrier extends BaseShip {
    public static final int SHIPSIZE = 5;
    public static final ShipType SHIP_TYPE = ShipType.CARRIER;

    public Carrier(int x, int y, ShipOrientation orientation) {
       super(x, y, orientation);
    }

    @Override
    public int getShipSize() {
        return SHIPSIZE;
    }
}
