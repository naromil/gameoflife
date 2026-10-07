package com.naromil.gol.main;

import com.naromil.gol.display.Color;
import com.naromil.gol.elements.Tile;

public class Grid {
    private static final int MAX_HEIGHT = 100;
    private static final int MAX_WIDTH = 100;

    /* ---- Grid Properties ---- */
    private int height;
    private int width;
    private final Tile[][] tiles;

    public Grid() {
        this.height = MAX_HEIGHT;
        this.width = MAX_WIDTH;
        this.tiles = new Tile[MAX_HEIGHT][MAX_WIDTH];
        // Initialize the grid with some initial state
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                tiles[i][j] = null;
            }
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    protected void clear() {
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                tiles[i][j] = null;
            }
        }
    }

    public void setSize(int gridWidth, int gridHeight) {
        this.width = gridWidth;
        this.height = gridHeight;
    }

    public Color[][] toColorGrid() {
        Color[][] colorGrid = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (tiles[i][j] != null) {
                    colorGrid[i][j] = tiles[i][j].getColor();
                } else {
                    colorGrid[i][j] = new Color(255, 255, 255); // Example: All tiles are white
                }
            }
        }
        return colorGrid;
    }

    /* ---- Logic ---- */
    public void nextState() {
    }
}
