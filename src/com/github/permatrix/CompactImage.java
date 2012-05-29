package com.github.permatrix;

import java.awt.Graphics2D;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class CompactImage {
	public static enum ImageType {
		SOLE, TWIN, QUAD, DISPERSED, UNKNOWN
	}

	/**
	 * compacted dots set of this image
	 */
	private int[] dots;

	/**
	 * image key, should be unique and depends on dots
	 */
	private long key;

	/**
	 * the count of isomorphic images, only values are 1, 2, or 4
	 */
	private int nIsomorphic;

	/**
	 * image type that matches nIsomorphic
	 */
	private ImageType type;

	private CompactImage parent;

	/**
	 * construct an image object only by matrix size
	 * 
	 * @param size
	 *            the matrix size
	 */
	public CompactImage(int size) {
		dots = new int[size];
		nIsomorphic = 1;
		key = -1;
	}

	/**
	 * construct an image object by dots values
	 * 
	 * @param src
	 *            the source dots
	 */
	public CompactImage(final int[] src) {
		this(src.length);
		System.arraycopy(src, 0, dots, 0, src.length);
	}

	/**
	 * return the isomorphic count
	 * 
	 * @return the isomorphic count
	 */
	public int isomorphicCount() {
		return nIsomorphic;
	}

	/**
	 * increase the isomorphic count
	 */
	public void incIsomorphicCount() {
		nIsomorphic++;
	}

	/**
	 * return image type
	 * 
	 * @return image type enum
	 */
	public ImageType getType() {
		return type;
	}

	/**
	 * update image type according to the isomorphic count
	 * 
	 * @return updated image type
	 */
	public ImageType refreshType() {
		switch (nIsomorphic) {
		case 1:
			type = ImageType.SOLE;
			break;
		case 2:
			type = ImageType.TWIN;
			break;
		case 4:
			type = ImageType.QUAD;
			break;
		default:
			type = ImageType.UNKNOWN;
		}
		return type;
	}

	/**
	 * add a new dot in image
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	public void addDot(int x, int y) {
		assert (x < dots.length);
		assert (y < dots.length);
		dots[x] = y;
	}

	/**
	 * add a new Dot object in image
	 * 
	 * @param dot
	 *            the Dot object
	 */
	public void addDot(Dot dot) {
		dots[dot.getX()] = dot.getY();
	}

	/**
	 * convert the compact representation to Dot set
	 * 
	 * @return a List of of Dot object
	 */
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
	 * 
	 * @return the new rotated image
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
	 * We choose 17 as prime number for polynomial function because large prime
	 * number will lead the result is too big to fit into Java long. That is to
	 * say, we only support up to 16x16 matrix.
	 */
	private static final int P_PRIME = 17;

	/**
	 * calculate an unique key for each image by using polynomial function.
	 * 
	 * @return the image key in long.
	 */
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

	/**
	 * Compare two Image by compare each dot, slowest method
	 * 
	 * @param img
	 * @return true - when two images are equal; false - when two images are not
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

	/**
	 * clone a new image base on this one
	 */
	public CompactImage clone() {
		int dim = dots.length;
		CompactImage ni = new CompactImage(dim);
		System.arraycopy(dots, 0, ni.dots, 0, dim);
		return ni;
	}

	/**
	 * imprint 3 rotated images with original and put all dots in a collection.
	 * 
	 * @return the imprinted dot set for this image
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
	 * 
	 * @param pw
	 *            the PrintStream for accepting output
	 */
	public void visualize(PrintStream pw) {
		int dim = dots.length;
		char[][] visual = new char[dim][dim];
		for (int x = 0; x < dim; x++) {
			Arrays.fill(visual[x], '-');
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
	 * draw the graphic representation of the image in a graphic device
	 * 
	 * @param posX
	 *            the start x position on canvas for drawing
	 * @param posY
	 *            the start y position on canvas for drawing
	 * @param gridSize
	 *            the grid size
	 * @param g2d
	 *            the underline graphics device
	 */
	public void visualize(int posX, int posY, int gridSize, Graphics2D g2d) {
		int dim = dots.length;
		int width = dim * gridSize + 1;
		g2d.drawRect(posX, posY, width - 1, width - 1);
		for (int x = 0; x < dim; x++) {
			int xtg = x * gridSize;
			g2d.drawLine(posX, posY + xtg, posX + width - 1, posY + xtg);
			g2d.drawLine(posX + xtg, posY, posX + xtg, posY + width - 1);
			g2d.fillRect(posX + xtg + 2, posY + dots[x] * gridSize + 2, gridSize - 3, gridSize - 3);
		}
	}

	public CompactImage getParent() {
		return parent;
	}

	public void setParent(CompactImage ci) {
		parent = ci;
	}
}
