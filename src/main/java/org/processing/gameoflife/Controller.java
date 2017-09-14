package org.processing.gameoflife;

import processing.core.PApplet;

/**
 * Created by kevinmcabee on 9/14/17.
 */
public class Controller extends PApplet {
    // Size of cells
    int cellSize = 5;

    // How likely for a cell to be alive at start (in percentage)
    float probabilityOfAliveAtStart = 15;

    // Variables for timer
    int interval = 100;
    int lastRecordedTime = 0;

    // Colors for active/inactive cells
    int alive = color(0, 200, 0);
    int dead = color(0);

    // Array of cells
    Cell[][] cells;
    // Buffer to record the state of the cells and use this while changing the others in the iterations
    Cell[][] cellsBuffer;

    int maxCols = 0;
    int maxRows = 0;

    // Pause
    boolean pause = false;

    public static void main(String args[]) {
        PApplet.main("org.processing.gameoflife.Controller");
    }

    public void settings() {
        size (100, 100);
        noSmooth();
    }

    public void setup() {
        // Instantiate arrays
        maxCols = width/cellSize;
        maxRows = height/cellSize;

        cells = new Cell[maxCols][maxRows];
        cellsBuffer = new Cell[maxCols][maxRows];

        // Initialization of cells
        for (int x=0; x<maxCols; x++) {
            for (int y=0; y<maxRows; y++) {
                float state = random (100);
                state = state > probabilityOfAliveAtStart ? 0 : 1;
                cells[x][y] = new Cell(state); // Save state of each cell
            }
        }

        background(0); // Fill in black in case cells don't cover all the windows
        // This stroke will draw the background grid
        stroke(48);
    }

    public void draw() {

        //Draw grid
        for (int x=0; x<maxCols; x++) {
            for (int y=0; y<maxRows; y++) {
                if (cells[x][y].getState()==1) {
                    fill(alive); // If alive
                }
                else {
                    fill(dead); // If dead
                }
                rect (x*cellSize, y*cellSize, cellSize, cellSize);
            }
        }
        // Iterate if timer ticks
        if (millis()-lastRecordedTime>interval) {
            if (!pause) {
                iteration();
                lastRecordedTime = millis();
            }
        }

        // Create  new cells manually on pause
        if (pause && mousePressed) {
            // Map and avoid out of bound errors
            int xCellOver = (int) map(mouseX, 0, width, 0, maxCols);
            xCellOver = constrain(xCellOver, 0, maxCols-1);
            int yCellOver = (int) map(mouseY, 0, height, 0, maxRows);
            yCellOver = constrain(yCellOver, 0, maxRows-1);

            // Check against cells in buffer
            if (cellsBuffer[xCellOver][yCellOver].getState()==1) { // Cell is alive
                cells[xCellOver][yCellOver].setState(0); // Kill
                fill(dead); // Fill with kill color
            }
            else { // Cell is dead
                cells[xCellOver][yCellOver].setState(1); // Make alive
                fill(alive); // Fill alive color
            }
        }
        else if (pause && !mousePressed) { // And then save to buffer once mouse goes up
            // Save cells to buffer (so we opeate with one array keeping the other intact)
            for (int x=0; x<maxCols; x++) {
                for (int y=0; y<maxRows; y++) {
                    cellsBuffer[x][y] = cells[x][y].dup();
                }
            }
        }
    }



    public void iteration() { // When the clock ticks
        // Save cells to buffer (so we opeate with one array keeping the other intact)
        for (int x=0; x<maxCols; x++) {
            for (int y=0; y<maxRows; y++) {
                cellsBuffer[x][y] = cells[x][y].dup();
            }
        }

        // Visit each cell:
        for (int x=0; x<maxCols; x++) {
            for (int y=0; y<maxRows; y++) {
                // And visit all the neighbours of each cell
                int neighbours = 0; // We'll count the neighbours
                for (int xx=x-1; xx<=x+1;xx++) {
                    for (int yy=y-1; yy<=y+1;yy++) {
                        if (((xx>=0)&&(xx<maxCols))&&((yy>=0)&&(yy<maxRows))) { // Make sure you are not out of bounds
                            if (!((xx==x)&&(yy==y))) { // Make sure to to check against self
                                if (cellsBuffer[xx][yy].getState()==1){
                                    neighbours++; // Check alive neighbours and count them
                                }
                            } // End of if
                        } // End of if
                    } // End of yy loop
                } //End of xx loop
                // We've checked the neigbours: apply rules!
                if (cellsBuffer[x][y].getState()==1) { // The cell is alive: kill it if necessary
                    if (neighbours < 2 || neighbours > 3) {
                        cells[x][y].setState(0); // Die unless it has 2 or 3 neighbours
                    }
                }
                else { // The cell is dead: make it live if necessary
                    if (neighbours == 3 ) {
                        cells[x][y].setState(1); // Only if it has 3 neighbours
                    }
                } // End of if
            } // End of y loop
        } // End of x loop
    } // End of function

    public void keyPressed() {
        if (key=='r' || key == 'R') {
            // Restart: reinitialization of cells
            for (int x=0; x<maxCols; x++) {
                for (int y=0; y<maxRows; y++) {
                    float state = random (100);
                    if (state > probabilityOfAliveAtStart) {
                        state = 0;
                    }
                    else {
                        state = 1;
                    }
                    cells[x][y].setState(state); // Save state of each cell
                }
            }
        }
        if (key==' ') { // On/off of pause
            pause = !pause;
        }
        if (key=='c' || key == 'C') { // Clear all
            for (int x=0; x<maxCols; x++) {
                for (int y=0; y<maxRows; y++) {
                    cells[x][y].setState(0); // Save all to zero
                }
            }
        }
    }
}
