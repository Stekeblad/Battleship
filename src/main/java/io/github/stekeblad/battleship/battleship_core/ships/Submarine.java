package io.github.stekeblad.battleship.battleship_core.ships;

/**
 * The medium size 3-cells ship
 */
public class Submarine extends BaseShip {
    public static final int SHIPSIZE = 3;
    public static final ShipType SHIP_TYPE = ShipType.SUBMARINE;

    public Submarine(int x, int y, ShipOrientation orientation) {
      super(x, y, orientation);
    }

    @Override
    public int getShipSize() {
        return SHIPSIZE;
    }
}
