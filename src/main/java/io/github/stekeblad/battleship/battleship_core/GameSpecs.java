package io.github.stekeblad.battleship.battleship_core;

/**
 * Intended as a collection of game data useful then setting up the game.
 * Contains board x size, y size and the number of the different ship types
 */
public class GameSpecs {
    private final int boardSizeX;
    private final int boardSizeY;
    private final int numBattleships;
    private final int numCarriers;
    private final int numCruisers;
    private final int numDestroyers;
    private final int numSubmarines;

    public int getBoardSizeX() {
        return boardSizeX;
    }

    public int getBoardSizeY() {
        return boardSizeY;
    }

    public int getNumBattleships() {
        return numBattleships;
    }

    public int getNumCarriers() {
        return numCarriers;
    }

    public int getNumCruisers() {
        return numCruisers;
    }

    public int getNumDestroyers() {
        return numDestroyers;
    }

    public int getNumSubmarines() {
        return numSubmarines;
    }

    public GameSpecs(int boardSizeX, int boardSizeY, int numBattleships, int numCarriers, int numCruisers, int numDestroyers, int numSubmarines) {
        this.boardSizeX = boardSizeX;
        this.boardSizeY = boardSizeY;
        this.numBattleships = numBattleships;
        this.numCarriers = numCarriers;
        this.numCruisers = numCruisers;
        this.numDestroyers = numDestroyers;
        this.numSubmarines = numSubmarines;
    }
}
