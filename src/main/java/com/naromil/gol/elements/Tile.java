package com.naromil.gol.elements;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;

public abstract class Tile {
    private final Color color;
    // the tile a complex tile is currently holding, Empty if none
    protected Tile containedTile;

    public Tile(Color color) {
        this.color = color;
        // Empty is null object
        this.containedTile = this instanceof Empty ? this : new Empty();
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
        return tileType.isInstance(this) || tileType.isInstance(containedTile);
    }

    public Color getStatusColor() {
        return null; // default no color so not rendered at all
    }

    // the list of tile types that can contain another one
    // for outer tile only since this is the definition of complex tile
    public abstract boolean isComplexType();

    // tiles that support swirl and crystallize
    public abstract boolean isStrongType();

    public static void globalAct(Grid grid, Grid newGrid, int width, int height) {
        return;
    }

    public abstract void act(Grid grid, Grid newGrid, int i, int j, int width, int height);
}
