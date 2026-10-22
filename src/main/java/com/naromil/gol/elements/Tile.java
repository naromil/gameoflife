package com.naromil.gol.elements;

import java.util.UUID;

import com.naromil.gol.main.Color;
import com.naromil.gol.main.Grid;
import com.naromil.gol.main.Point;

public abstract class Tile implements Comparable<Tile> {
    private final Color color;
    private final UUID uuid;
    // the tile a complex tile is currently holding, Empty if none
    protected Tile containedTile;

    public Tile(Color color) {
        this.color = color;
        this.uuid = this instanceof Empty ? null : UUID.randomUUID();
        // Empty is null object
        this.containedTile = this instanceof Empty ? this : new Empty();
    }

    // the priority of the specific tile class. The lower the more priority.
    public abstract int getRank();

    @Override 
    public int compareTo(Tile next) {
        return getRank() - next.getRank();
    }

    public Color getColor() {
        return color;
    }

    public UUID getUuid() {
        return uuid;
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

    public static void globalAct(Grid grid, Grid newGrid) {
        return;
    }

    public abstract void act(Grid grid, Grid newGrid, Point position);
}
