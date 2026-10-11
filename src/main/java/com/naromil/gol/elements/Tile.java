package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public abstract class Tile {
    public static final Color DEFAULT_COLOR = null;
    public final static int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    protected final Color color;
    protected Tile containedTile = null;

    public Tile(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public Tile getContainedTile() {
        return containedTile;
    }
    public void setContainedTile(Tile tile) {
        this.containedTile = tile;
    }

    // whether this tile is or contains a tile of the specified type. do not use it to check for Empty tiles.
    public boolean hasType(Class<? extends Tile> tileType) {
        return tileType.isInstance(this) || (containedTile != null && tileType.isInstance(containedTile));
    }

    public Color getStatusColor() {
        return null; // default no color so not rendered at all
    }

    public abstract void act(Grid grid, Grid newGrid, int i, int j, int width, int height);
}
