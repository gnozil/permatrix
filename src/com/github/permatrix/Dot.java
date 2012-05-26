package com.github.permatrix;


public class Dot implements Comparable<Dot> {
	int x;
	int y;

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Dot(int xi, int yi) {
		x = xi;
		y = yi;
	}

	public boolean equals(Dot dot) {
		assert (dot != null);
		return (dot.x == x) && (dot.y == y);
	}

	public String toString() {
		return "(" + x + " " + y + ")";
	}

	@Override
	public int compareTo(Dot dot) {
		int diff = x - dot.x;
		if (diff == 0) {
			diff = y - dot.y;
		}
		return diff;
	}

	public Dot clone() {
		return new Dot(x, y);
	}
}