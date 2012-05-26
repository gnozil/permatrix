package com.github.permatrix;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import javax.imageio.ImageIO;

public class CompactImage {
	int[] dots;

	public CompactImage(int size) {
		dots = new int[size];
	}

	public void addDot(int x, int y) {
		dots[x] = y;
	}

	public void addDot(Dot dot) {
		dots[dot.getX()] = dot.getY();
	}

	public List<Dot> getDots() {
		List<Dot> dl = new ArrayList<Dot>();
		int dim = dots.length;
		for (int x = 0; x < dim; x++) {
			dl.add(new Dot(x, dots[x]));
		}
		return dl;
	}

	public CompactImage rotate() {
		int dim = dots.length;
		CompactImage ni = new CompactImage(dim);
		for (int x = 0; x < dim; x++) {
			int x1 = dim - 1 - dots[x];
			int y1 = x;
			ni.addDot(x1, y1);
		}
		return ni;
	}

	public void rotateSelf() {
		int dim = dots.length;
		int[] shadow = new int[dim];
		for (int x = 0; x < dim; x++) {
			int x1 = dim - 1 - dots[x];
			int y1 = x;
			shadow[x1] = y1;
		}
		System.arraycopy(shadow, 0, dots, 0, dim);
	}

	public boolean equals(CompactImage img) {
		assert (img != null);
		int dim = dots.length;
		if (dim != img.dots.length)
			return false;

		for (int idx = 0; idx < dim; idx++) {
			if (dots[idx] != img.dots[idx])
				return false;
		}
		return true;
	}

	public CompactImage clone() {
		int dim = dots.length;
		CompactImage ni = new CompactImage(dim);
		System.arraycopy(dots, 0, ni.dots, 0, dim);
		return ni;
	}

	// imprint rotated images
	public Collection<Dot> imprint() {
		TreeSet<Dot> ts = new TreeSet<Dot>();
		ts.addAll(getDots());

		CompactImage ri = this;
		for (int loop = 0; loop < 3; loop++) {
			ri = ri.rotate();
			ts.addAll(ri.getDots());
		}
		return ts;
	}

	public void visualize(PrintStream pw) {
		int dim = dots.length;
		char[][] visual = new char[dim][dim];
		for (int x = 0; x < dim; x++) {
			Arrays.fill(visual[x], '-');
		}
		for (int x = 0; x < dim; x++) {
			visual[x][dots[x]] = '*';
		}
		for (int x = 0; x < dim; x++) {
			for (int y = 0; y < dim; y++) {
				pw.print(visual[x][y] + " ");
			}
			pw.println();
		}
	}
	
	public void visualize() {
		int dim = dots.length;
		int width = dim * 17 + 1; 
		BufferedImage img = new BufferedImage(width,width,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = img.createGraphics();
		visualize(0, 0, 17, g2d);
		try {
			ImageIO.write(img, "jpeg", new FileOutputStream(new File("image" + dim + "x" + dim + "_" + hashCode() + ".jpg")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		g2d.dispose();
	}

	public void visualize(int posX, int posY, int gridSize, Graphics2D g2d) {
		int dim = dots.length;
		int width = dim * gridSize + 1;
		g2d.drawRect(posX, posY, width - 1, width - 1);
		for(int step = dim - 1; step >= 0; step--) {
			g2d.drawLine(posX, posY + step * gridSize, posX + width - 1, posY + step * gridSize);
			g2d.drawLine(posX + step * gridSize, posY, posX + step * gridSize, posY + width - 1);
		}
		for (int x = 0; x < dim; x++) {
			g2d.fillRect(posX + x * gridSize + 1, posY + dots[x] * gridSize + 1, gridSize - 1, gridSize - 1);
		}
	}
}
