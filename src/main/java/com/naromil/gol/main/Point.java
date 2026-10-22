package com.naromil.gol.main;

public record Point(int x, int y) {
    public Point plus(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }

    public Point plus(Point other) {
        return plus(other.x, other.y);
    }

    public Point minus(Point other) {
        return new Point(x - other.x, y - other.y);
    }

	public int distanceTo(Point point) {
        // chebyshev distance
        return (int) Math.max(point.x() - x, point.y() - y);
	}
}
