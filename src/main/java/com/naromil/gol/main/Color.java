package com.naromil.gol.main;

public record Color(int r, int g, int b) {
  public Color {
    if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
      throw new IllegalArgumentException("Color values must be between 0 and 255");
    }
  }

  private static final int DELTA = 10;

  private static int randomInt(int min, int max) {
    return (int) (Math.random() * (max - min + 1)) + min;
  }

  public static Color similarColor(Color c) {
    int newR = randomInt(Math.max(0, c.r() - DELTA), Math.min(255, c.r() + DELTA));
    int newG = randomInt(Math.max(0, c.g() - DELTA), Math.min(255, c.g() + DELTA));
    int newB = randomInt(Math.max(0, c.b() - DELTA), Math.min(255, c.b() + DELTA));
    return new Color(newR, newG, newB);
  }
}
