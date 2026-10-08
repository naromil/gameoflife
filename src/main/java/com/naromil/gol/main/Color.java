package com.naromil.gol.main;

public record Color(int r, int g, int b) {
  public Color {
    if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
      throw new IllegalArgumentException("Color values must be between 0 and 255");
    }
  }
}
