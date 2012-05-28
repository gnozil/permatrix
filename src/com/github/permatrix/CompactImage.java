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

public class CompactImage implements Comparable<CompactImage> {
	int[] dots;
	int count;
	private long key;

	public CompactImage(int size) {
		dots = new int[size];
		count = 1;
		key = -1;
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

	/**
	 * create a new image that is the 90 degree rotation of the image   
	 * @return
	 */
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

	/**
	 * rotate the image by 90 degree, and re-use this object
	 */
	public void rotateSelf() {
		int dim = dots.length;
		int[] shadow = new int[dim];
		for (int x = 0; x < dim; x++) {
			int x1 = dim - 1 - dots[x];
			int y1 = x;
			shadow[x1] = y1;
		}
		System.arraycopy(shadow, 0, dots, 0, dim);
		key = -1;
	}

	/**
	 * calculate an unique key for each image by using polynomial. 
	 * @return
	 */
	private static final int P_PRIME = 19;
	public long imageKey() {
		if (key == -1) {
			key = 0;
			long poly = 1;
			for (int x = 0; x < dots.length; x++) {
				key += dots[x] * poly;
				poly *= P_PRIME;
			}
		}
		return key;
	}

	@Override
	public int hashCode() {
		return (int) key;
	}

	@Override
	public boolean equals(Object obj) {
		CompactImage img = (CompactImage) obj;
		return imageKey() == img.imageKey();
	}

	/**
	 * Compare two Image by compare each dot, slowest method
	 * 
	 * @param img
	 * @return True - when two images are equal; False - when two images are not
	 *         same
	 */
	public boolean compareWith(CompactImage img) {
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

	/**
	 * imprint 3 rotated images with original and put all dots in a collection.
	 * @return
	 */
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

	/**
	 * draw the visual representation of the image in Ascii graphics
	 * @param pw
	 */
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

	/**
	 * generate the PNG graphics representation of the image and save to file
	 */
	public void visualize() {
		int dim = dots.length;
		int width = dim * 17 + 1;
		BufferedImage img = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
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

	/**
	 * draw the graphic representation of the image in a graphic device
	 * @param posX
	 * @param posY
	 * @param gridSize
	 * @param g2d
	 */
	public void visualize(int posX, int posY, int gridSize, Graphics2D g2d) {
		int dim = dots.length;
		int width = dim * gridSize + 1;
		g2d.drawRect(posX, posY, width - 1, width - 1);
		for (int step = dim - 1; step >= 0; step--) {
			g2d.drawLine(posX, posY + step * gridSize, posX + width - 1, posY + step * gridSize);
			g2d.drawLine(posX + step * gridSize, posY, posX + step * gridSize, posY + width - 1);
		}
		for (int x = 0; x < dim; x++) {
			g2d.fillRect(posX + x * gridSize + 1, posY + dots[x] * gridSize + 1, gridSize - 1, gridSize - 1);
		}
	}

	@Override
	public int compareTo(CompactImage img) {
		return (int)(key - img.key);
	}
	
}
