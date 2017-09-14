package org.processing.gameoflife;

/**
 * Created by kevinmcabee on 9/14/17.
 */
public class Cell {
    int state = 0;

    public Cell(int state) {
        this.state = state;
    }

    public Cell(float state) {
        this.state = (int) state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setState(float state) {
        this.state = (int) state;
    }

    public Cell dup() {
        return new Cell(state);
    }

}
