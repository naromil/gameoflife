package com.naromil.gol.main;

import com.naromil.gol.elements.Tile;
import com.naromil.gol.elements.TileFactory;

public class Grid {
    private static final int MAX_HEIGHT = 100;
    private static final int MAX_WIDTH = 100;

    /* ---- Grid Properties ---- */
    private int height;
    private int width;
    private Tile[][] tiles;

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

    public Tile[][] getTiles() {
        return tiles;
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
        if (gridWidth < 1 || gridWidth > MAX_WIDTH || gridHeight < 1 || gridHeight > MAX_HEIGHT) {
            throw new IllegalArgumentException("Grid size must be between 1 and " + MAX_WIDTH + " for width and 1 and " + MAX_HEIGHT + " for height.");
        }
        for (int i = this.width; i < gridWidth; i++) {
            for (int j = 0; j < this.height; j++) {
                tiles[j][i] = null;
            }
        }
        for (int i = 0; i < this.width; i++) {
            for (int j = this.height; j < gridHeight; j++) {
                tiles[j][i] = null;
            }
        }
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

    /* ---- Main Logic ---- */
    public void nextState() {
        // Check if the board is empty and initialize it with random tiles if it is
        if(emptyBoard(tiles)) {
            initializeRandomBoard();
            return;
        }
        // If not empty, act with what is left on the board
        Tile[][] newTiles = new Tile[MAX_HEIGHT][MAX_WIDTH];
        // Assume all survived
        for (int i = 0; i < height; i++) {
            System.arraycopy(tiles[i], 0, newTiles[i], 0, width);
        }
        // Each tile act according to their own rules
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (tiles[i][j] != null) {
                    tiles[i][j].act(tiles, newTiles, i, j, width, height);
                }
            }
        }

        tiles = newTiles;
    }

    private void initializeRandomBoard() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tiles[i][j] = TileFactory.createRandomTile();
            }
        }
    }

    private boolean emptyBoard(Tile[][] tiles2) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (tiles2[i][j] != null) {
                    return false;
                }
            }
        }
        return true;
    }
}
