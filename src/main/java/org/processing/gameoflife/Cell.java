package org.processing.gameoflife;

/**
 * Created by kevinmcabee on 9/14/17.
 */
public class Cell implements Cloneable {
    boolean prevState = false;
    boolean state = false;

    public Cell(boolean state) {
        this.state = state;
    }

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
