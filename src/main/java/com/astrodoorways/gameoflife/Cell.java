package com.astrodoorways.gameoflife;

/**
 * An individual cell used for implementations of Conway's Game of Life.
 */
public class Cell implements Cloneable {
    boolean prevState = false;
    boolean state = false;

    public Cell(boolean state, boolean prevState) {
        this.state = state;
        this.prevState = prevState;
    }

    public boolean isAlive() {
        return state;
    }

    public void setAlive(boolean state) {
        this.prevState = this.state;
        this.state = state;
    }

    public boolean wasAlive() {
        return prevState;
    }

    /**
     * @return a duplicate Cell object
     */
    public Cell dup() { return new Cell(state, prevState); }
}
