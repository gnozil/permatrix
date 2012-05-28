package com.github.permatrix;

import java.awt.Font;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.imageio.ImageIO;

/**
 * This class is to generate permutation matrix and find the unique ones in
 * brute force approach.
 * 
 * @author gnozil@gmail.com
 * 
 */
public class PermutationMatrix {
	/**
	 * the size of grid in PNG image
	 */
	private static final int GRID_SIZE = 17;

	/**
	 * the image margin
	 */
	private static final int MARGIN_SIZE = 20;

	/**
	 * dimension of the matrix. TODO, to avoid confusion with 3-D cube in future
	 * , here keep to use 'size'
	 */
	private int size;

	/**
	 * all permutation images
	 */
	TreeMap<Long, CompactImage> images;

	/**
	 * Count of Sole Image - keep same after rotations, Twin Image - have 1
	 * sibling after rotation, and Quad Image - have 3 siblings after rotation
	 */
	int nSole, nTwin, nQuad;

	public PermutationMatrix(int sz) {
		size = sz;
		images = new TreeMap<Long, CompactImage>();
	}

	public int count() {
		return images.size();
	}

	/**
	 * add a new image to permutation image list, while only for unique one
	 * 
	 * @param img
	 * @return
	 */
	public boolean addUniqueImage(CompactImage img) {
		boolean found = false;
		CompactImage ri = img.clone();
		for (int loop = 0; loop < 3; loop++) {
			ri.rotateSelf();
			long hc = ri.imageKey();
			if (images.containsKey(hc)) {
				found = true;
				CompactImage now = images.get(hc);
				now.count++;
				break;
			}
		}
		if (!found) {
			images.put(img.imageKey(), img);
		}
		return !found;
	}

	/**
	 * generate all images for this order of matrix
	 * 
	 * @param unique
	 *            : true - only keep unique images
	 *            : false - keep all images
	 */
	public void generate(boolean unique) {
		DictOrderPermutator pc = new DictOrderPermutator(size);
		images.clear();
//		int progress = 0;
//		long cnt = 0;
		while (pc.hasNext()) {
			int[] indices = pc.next();
			CompactImage newImg = new CompactImage(size);
			int x = 0;
			for (int y : indices) {
				newImg.addDot(x++, y);
			}
			if (!unique) {
				images.put(newImg.imageKey(), newImg);
			} else {
				addUniqueImage(newImg);
			}
//			cnt++;
//			progress++;
//			if (progress == 1000) {
//				System.out.println(cnt + ", " + images.size());
//				progress = 0;
//			}
		}
	}

	public void visualize(PrintStream pw) {
		for (CompactImage img : images.values()) {
			img.visualize(pw);
			pw.println("---------------------");
		}
	}

	/**
	 * draw an image and its imprinted image side by side. the final PNG
	 * graphics include all images of this matrix
	 * 
	 * @param pw
	 *            : output some text messages in this PrintStream
	 */
	public void imprint(PrintStream pw) {
		int symm = 0;
		int disp = 0;
		HashSet<Integer> set = new HashSet<Integer>();
		int w = 2;
		int h = images.size();
		int imgWidth = (size * GRID_SIZE + 1) * w + (GRID_SIZE - 1) * (w - 1) + MARGIN_SIZE * 2;
		int imgHeight = (size * GRID_SIZE + 1) * h + (GRID_SIZE - 1) * (h - 1) + MARGIN_SIZE * 2;

		// draw the canvas on image object
		BufferedImage bimg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D g2d = bimg.createGraphics();

		g2d.drawRect(0, 0, imgWidth - 1, imgHeight - 1);
		g2d.setFont(new Font("Dialog", Font.PLAIN, 10));
		g2d.drawString(size + "x" + size + ": " + h, 20, 15);

		int y = 0;
		for (CompactImage img : images.values()) {
			img.visualize(MARGIN_SIZE, MARGIN_SIZE + (size + 1) * GRID_SIZE * y, GRID_SIZE, g2d);
			Collection<Dot> newImg = img.imprint();
			visualize(newImg, size, MARGIN_SIZE + (size + 1) * GRID_SIZE, MARGIN_SIZE + (size + 1) * GRID_SIZE * y, GRID_SIZE,
					g2d);
			set.add(Integer.valueOf(newImg.size()));
			if (newImg.size() == size) {
				g2d.drawString("Symmetric", MARGIN_SIZE, MARGIN_SIZE + (size + 1) * GRID_SIZE * y - 2);
				symm++;
			} else if (newImg.size() == size * 4) {
				g2d.drawString("Dispersed", MARGIN_SIZE, MARGIN_SIZE + (size + 1) * GRID_SIZE * y - 2);
				disp++;
			}
			y++;
		}
		try {
			ImageIO.write(bimg, "png", new FileOutputStream(
					new File("imprint-" + size + "x" + size + "_" + hashCode() + ".png")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.println(set.toString() + " Symmetric:" + symm + ", Dispersed:" + disp);
	}

	/**
	 * count for sole/twin/quad images from the set of all unique images
	 */
	private void countSTQ() {
		for (CompactImage img : images.values()) {
			switch (img.count) {
			case 1:
				nSole++;
				break;
			case 2:
				nTwin++;
				break;
			case 4:
				nQuad++;
				break;
			default:
				// possible exception: when image key is too large to fit into
				// Java long
				System.out.println("Unexpected count: " + img.count + ", Hash = " + img.imageKey());

			}
		}

		System.out.println("Sole: " + nSole + ", Half: " + nTwin + ", Quad: " + nQuad);
		System.out.println("Sole + 2 * Twin + 4 * Quad = " + (nSole + 2 * nTwin + 4 * nQuad));
		System.out.println("Sole + Twin + Quad = " + (nSole + nTwin + nQuad));
	}

	/**
	 * draw a single image by its dot set
	 * 
	 * @param dots
	 *            dots to draw
	 * @param dim
	 *            the order of the matrix
	 * @param posX
	 *            start x coordinate in canvas for drawing
	 * @param posY
	 *            start y coordinate in canvas for drawing
	 * @param gridSize
	 *            grid size
	 * @param g2d
	 *            the Java2D graphics device
	 */
	private void visualize(Collection<Dot> dots, int dim, int posX, int posY, int gridSize, Graphics2D g2d) {
		int width = dim * gridSize + 1;
		g2d.drawRect(posX, posY, width - 1, width - 1);
		for (int step = dim - 1; step >= 0; step--) {
			g2d.drawLine(posX, posY + step * gridSize, posX + width - 1, posY + step * gridSize);
			g2d.drawLine(posX + step * gridSize, posY, posX + step * gridSize, posY + width - 1);
		}
		for (Dot dot : dots) {
			g2d.fillRect(posX + dot.x * gridSize + 1, posY + dot.y * gridSize + 1, gridSize - 1, gridSize - 1);
		}
	}

	/**
	 * draw all images in graphics canvas
	 */
	public void visualize() {
		int cnt = images.size();

		// try to get a square canvas
		int h = (int) Math.sqrt(cnt);
		int w = h;
		if (cnt != h * h) {
			if (cnt <= (h + 1) * h) {
				w = h + 1;
			} else {
				w++;
				h++;
			}
		}

		int imgWidth = (size * GRID_SIZE + 1) * w + (GRID_SIZE - 1) * (w - 1) + MARGIN_SIZE * 2;
		int imgHeight = (size * GRID_SIZE + 1) * h + (GRID_SIZE - 1) * (h - 1) + MARGIN_SIZE * 2;

		// draw the canvas on image object
		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_BYTE_BINARY); // BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = img.createGraphics();

		g2d.drawRect(0, 0, imgWidth - 1, imgHeight - 1);
		g2d.setFont(new Font("Dialog", Font.PLAIN, 10));
		g2d.drawString(size + "x" + size + ": " + cnt, 20, 15);

		// draw each image
		int x = 0;
		int y = -1;
		Iterator<CompactImage> iter = images.values().iterator();
		for (int idx = 0; idx < cnt; idx++) {
			x = idx % w;
			if (x == 0) {
				y++;
			}
			CompactImage cimg = iter.next();
			cimg.visualize(MARGIN_SIZE + (size + 1) * GRID_SIZE * x, MARGIN_SIZE + (size + 1) * GRID_SIZE * y, GRID_SIZE, g2d);
		}

		try {
			ImageIO.write(img, "png", new FileOutputStream(new File("image-" + size + "x" + size + "_" + hashCode() + ".png")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * draw a set of dots in ascii graphics
	 * 
	 * @param dots
	 *            the dots set to be drawed
	 * @param dim
	 *            the dimension of the matrix
	 */
	public static void visualize(Collection<Dot> dots, int dim) {
		char[][] visual = new char[dim][dim];
		for (int x = 0; x < dim; x++) {
			Arrays.fill(visual[x], '-');
		}
		for (Dot dot : dots) {
			visual[dot.x][dot.y] = '*';
		}
		for (int x = 0; x < dim; x++) {
			for (int y = 0; y < dim; y++) {
				System.out.print(visual[x][y] + " ");
			}
			System.out.println();
		}
	}

	static class Boundary {
		int low;
		int high;

		public Boundary(int l, int h) {
			low = l;
			high = h;
		}

		public int getHigh() {
			return high;
		}

		public int getLow() {
			return low;
		}

		public String toString() {
			return "(" + low + "," + high + ")";
		}

	}

	public static void usage() {
		StringBuffer text = new StringBuffer();
		text.append("Usage: java " + PermutationMatrix.class.getName() + " [options] <matrix size boundary>\n");
		text.append("	   6		1 matrixes(default)\n");
		text.append("	   2,3,6	3 matrixes\n");
		text.append("	   4-7		4 matrixes\n");
		text.append("	   2,4-7,8	6 matrixes\n");
		text.append("	-d			deducated image\n");
		text.append("	-f			full image\n");
		text.append("	-g			graphic image in png\n");
		text.append("	-a			ascii image on screen\n");
		text.append("	-i			imprit image\n");
		text.append("	-s			summary information\n");
		System.out.println(text.toString());
	}

	/**
	 * the main function
	 * 
	 * @param args
	 *            command line options
	 */
	public static void main(String[] args) {
		List<Boundary> matrixes = new ArrayList<Boundary>();

		boolean full = false;
		boolean deducated = true;
		boolean graph = true;
		boolean ascii = false;
		boolean imp = false;
		boolean summary = true;

		int idx = 0;
		while (idx < args.length) {
			String arg = args[idx++];
			if (arg.equals("-d")) {
				deducated = true;
				full = false;
			} else if (arg.equals("-f")) {
				full = true;
				deducated = false;
			} else if (arg.equals("-g")) {
				graph = true;
				ascii = false;
			} else if (arg.equals("-a")) {
				ascii = true;
				graph = false;
			} else if (arg.equals("-i")) {
				imp = true;
			} else if (arg.equals("-s")) {
				summary = true;
			} else {
				StringTokenizer st = new StringTokenizer(arg, ",");
				while (st.hasMoreTokens()) {
					String bd = st.nextToken();
					int dash = bd.indexOf("-");
					if (dash != -1) {
						int l = Integer.valueOf(bd.substring(0, dash));
						int h = Integer.valueOf(bd.substring(dash + 1));
						matrixes.add(new Boundary(l, h));
					} else {
						int n = Integer.valueOf(bd);
						matrixes.add(new Boundary(n, n));
					}
				}
			}
		}

		if (matrixes.size() == 0) {
			matrixes.add(new Boundary(6, 6));
		}

		long t1 = System.currentTimeMillis();
		for (Boundary boundary : matrixes) {
			for (int m = boundary.getLow(); m <= boundary.getHigh(); m++) {
				PermutationMatrix mx = new PermutationMatrix(m);
				mx.generate(deducated);
				mx.countSTQ();
				if (graph) {
					mx.visualize();
				}
				if (ascii) {
					// mx.visualize(System.out);
				}
				if (imp) {
					mx.imprint(System.out);
				}
				if (summary) {
					System.out.println("get images of " + m + "! = " + mx.count());
				}
			}
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Used (milli-second): " + (t2 - t1));
	}
}
