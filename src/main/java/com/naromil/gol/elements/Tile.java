package com.naromil.gol.elements;

import com.naromil.gol.main.Color;

public abstract class Tile {

  public static final Color DEFAULT_COLOR = null;

  protected final Color color;

  public Tile(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }

  public abstract void act(Tile[][] tiles, Tile[][] newTiles, int i, int j, int width, int height);
}
