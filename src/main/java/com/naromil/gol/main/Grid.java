package com.naromil.gol.main;

import com.naromil.gol.display.Color;
import com.naromil.gol.elements.Tile;

public class Grid {
    private final int height;
    private final int width;
    private final Tile[][] tiles;

    public Grid(int gridHeight, int gridWidth) {
        this.height = gridHeight;
        this.width = gridWidth;
        this.tiles = new Tile[gridHeight][gridWidth];
        // Initialize the grid with some initial state
        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++) {
                tiles[i][j] = null; // TODO: have random types of tiles
            }
        }
    }

    public void update() {
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
}
