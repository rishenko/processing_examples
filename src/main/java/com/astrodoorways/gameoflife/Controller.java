package com.astrodoorways.gameoflife;

import processing.core.PApplet;

/**
 * Variation of the Game of Life example found at processing.org. Original implementation was
 * written by Joan Soler-Adillon.
 *
 * Keyboard Input:
 * <p>
 *     <ul>r/R: reset the game to a randomized board</ul>
 *     <ul>c/C: clear the board</ul>
 *     <ul>spacebar: pause the game</ul>
 * </p>
 *
 * Mouse Input:
 * Left Mouse Button: toggle dead/alive state of a cell while game is paused
 *
 * Cell States:
 * <p>
 *     <ul>black: cell state - last round: DEAD, this round: DEAD</ul>
 *     <ul>green: cell state - last round: ALIVE, this round: ALIVE</ul>
 *     <ul>blue: cell state - last round: DEAD, this round: ALIVE</ul>
 *     <ul>red: cell state - last round: ALIVE, this round: DEAD</ul>
 * </p>
 */
public class Controller extends PApplet {
    // Size of cells
    private int cellSize = 5;

    // How likely for a cell to be alive at start (in percentage)
    private float probabilityOfAliveAtStart = 10;

    // Variables for timer
    private static final int INTERVAL = 75;
    private int lastRecordedTime = 0;

    // Colors for active/inactive cells
    private final int COLOR_LIVING = color(0, 200, 0);
    private final int COLOR_DEAD = color(0);
    private final int COLOR_LIVING_NEW = color(0, 0, 200);
    private final int COLOR_DEAD_NEW = color(200, 0, 0);

    // Array of cells
    private Cell[][] cells;
    // Buffer to record the state of the cells and use this while changing the others in the iterations
    private Cell[][] cellsBuffer;

    private int maxCols = 0;
    private int maxRows = 0;

    // Pause
    private boolean pause = false;

    public static void main(String args[]) {
        PApplet.main("com.astrodoorways.gameoflife.Controller");
    }

    public void settings() {
        size (1024, 768);
        noSmooth();
    }

    public void setup() {
        // Instantiate arrays
        maxCols = width/cellSize;
        maxRows = height/cellSize;

        cells = new Cell[maxCols][maxRows];
        cellsBuffer = new Cell[maxCols][maxRows];

        restart();

        background(0);
        stroke(48);
    }

    /***********
     * DRAWING *
     ***********/

    public void draw() {
        drawGrid();

        // Iterate if timer ticks
        if ((millis() - lastRecordedTime) > INTERVAL && !pause) {
            iteration();
            lastRecordedTime = millis();
        }

        processMouseEvent();
    }

    private void drawGrid() {
        //Draw grid
        for (int x=0; x<maxCols; x++) {
            for (int y=0; y<maxRows; y++) {
                fill(getCellColor(cells[x][y]));
                rect (x*cellSize, y*cellSize, cellSize, cellSize);
            }
        }
    }

    public int getCellColor(Cell cell) {
        int color = 0;
        if (!cell.isAlive() && !cell.wasAlive()) {
            color = COLOR_DEAD;
        }
        else if (!cell.isAlive() && cell.wasAlive()) {
            color = COLOR_DEAD_NEW;
        }
        else if (cell.isAlive() && !cell.wasAlive()) {
            color = COLOR_LIVING_NEW;
        }
        else if (cell.isAlive() && cell.wasAlive()) {
            color = COLOR_LIVING;
        }
        return color;
    }

    /*************
     * ITERATION *
     *************/

    public void iteration() {
        updateCellBuffer();

        for (int x=0; x<maxCols; x++) {
            for (int y=0; y<maxRows; y++) {
                processCellState(x, y, getLiveNeighbourCount(x, y));
            }
        }
    }

    private void processCellState(int x, int y, int numLiveNeighbors) {
        if (cellsBuffer[x][y].isAlive()) {
            boolean state = !(numLiveNeighbors < 2 || numLiveNeighbors > 3);
            cells[x][y].setAlive(state);
        }
        else {
            cells[x][y].setAlive(numLiveNeighbors == 3);
        } // End of if
    }

    private void updateCellBuffer() {
        for (int x=0; x<maxCols; x++) {
            for (int y=0; y<maxRows; y++) {
                cellsBuffer[x][y] = cells[x][y].dup();
            }
        }
    }

    private int getLiveNeighbourCount(int x, int y) {
        int neighbours = 0;
        int xMax = x+1;
        int yMax = y+1;
        for (int nx=x-1; nx<=xMax; nx++) {
            for (int ny=y-1; ny<=yMax; ny++) {
                if (((nx>=0) && (nx<maxCols)) && ((ny>=0) && (ny<maxRows))) {
                    if (!((nx==x) && (ny==y)) && cellsBuffer[nx][ny].isAlive()) neighbours++;
                }
            }
        }
        return neighbours;
    }



    /********************
     * USER INTERACTION *
     ********************/

    private void processMouseEvent() {
        if (pause && mousePressed) {
            int mx = mapMouseToX();
            int my = mapMouseToY();

            if (cellsBuffer[mx][my].isAlive()) {
                cells[mx][my].setAlive(false);
                fill(COLOR_DEAD);
            } else {
                cells[mx][my].setAlive(true);
                fill(COLOR_LIVING);
            }
        } else if (pause) {
            for (int x = 0; x < maxCols; x++) {
                for (int y = 0; y < maxRows; y++) {
                    cellsBuffer[x][y] = cells[x][y].dup();
                }
            }
        }
    }

    private int mapMouseToX() {
        int xCellOver = (int) map(mouseX, 0, width, 0, maxCols);
        return constrain(xCellOver, 0, maxCols - 1);
    }

    private int mapMouseToY() {
        int yCellOver = (int) map(mouseY, 0, height, 0, maxRows);
        return constrain(yCellOver, 0, maxRows - 1);
    }

    public void keyPressed() {
        switch(key) {
            case 'r':case 'R':
                restart();
                break;
            case ' ':
                togglePause();
                break;
            case 'c':case 'C':
                clearAll();
                break;
            default:
                break;
        }
    }

    public void clearAll() {
        for (int x=0; x<maxCols; x++) {
            for (int y=0; y<maxRows; y++) {
                cells[x][y].setAlive(false); // Save all to zero
            }
        }
    }

    public void togglePause() { pause = !pause; }

    public void restart() {
        for (int x=0; x<maxCols; x++) {
            for (int y=0; y<maxRows; y++) {
                boolean state = random (100) <= probabilityOfAliveAtStart;
                cells[x][y] = new Cell(state, state);
                cellsBuffer[x][y] = new Cell(state, state);
            }
        }
    }
}
