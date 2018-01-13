package io.github.stekeblad.battleship.battleship_core.ships;

/**
 * The small 2-cells ship
 */
public class Destroyer extends BaseShip{
    public static final int SHIPSIZE = 2;
    public static final ShipType SHIP_TYPE = ShipType.DESTROYER;

    public Destroyer(int x, int y, ShipOrientation orientation) {
        super(x, y, orientation);
    }

    @Override
    public int getShipSize() {
        return SHIPSIZE;
    }
}
