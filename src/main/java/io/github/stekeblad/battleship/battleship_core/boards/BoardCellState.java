package io.github.stekeblad.battleship.battleship_core.boards;

/**
 * Names the different things that can be in a cell
 */
public enum BoardCellState {
    UNKNOWN ,
    MISS    ,
    HIT     ,
    SUNKEN  ,
    OWN_SHIP,
    PLACEMENT_HINT
}
