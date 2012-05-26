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
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

/**
 * 
 * @author gnozil@gmail.com
 *
 */
public class PermutationMatrix {
	private static final int GRID_SIZE = 17;
	private static final int MARGIN_SIZE = 20;
	private int size;
	List<CompactImage> images;

	public PermutationMatrix(int sz) {
		size = sz;
		images = new ArrayList<CompactImage>();
	}

	public List<CompactImage> getImages() {
		return images;
	}

	public boolean addUniqueImage(CompactImage img) {
		boolean found = false;
		CompactImage ri = img.clone();
		for (int loop = 0; loop < 3; loop++) {
			ri.rotateSelf();
			for (CompactImage now : images) {
				if (ri.equals(now)) {
					found = true;
					break;
				}
			}
			if (found)
				break;
		}
		if (!found) {
			images.add(img);
		}
		return !found;
	}

	public void generate(boolean unique) {
		DictOrderPermutator pc = new DictOrderPermutator(size);
		images.clear();
		while (pc.hasNext()) {
			int[] indices = pc.next();
			CompactImage newImg = new CompactImage(size);
			int x = 0;
			for (int y : indices) {
				newImg.addDot(x++, y);
			}
			if (!unique) {
				images.add(newImg);
			} else {
				addUniqueImage(newImg);
			}
		}
	}

	public void visualize(PrintStream pw) {
		for (CompactImage img : images) {
			img.visualize(pw);
			pw.println("---------------------");
		}
	}

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
		for (CompactImage img : images) {
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

	public void visualize() {
		int cnt = images.size();

		// calculate canvas size
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
		for (int idx = 0; idx < cnt; idx++) {
			x = idx % w;
			if (x == 0) {
				y++;
			}
			CompactImage cimg = images.get(idx);
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
				if (graph) {
					mx.visualize();
				}
				if (ascii) {
					mx.visualize(System.out);
				}
				if (imp) {
					mx.imprint(System.out);
				}
				if (summary) {
					List<CompactImage> imgs = mx.getImages();
					System.out.println("get images of " + m + "! = " + imgs.size());
				}
			}
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Used (milli-second): " + (t2 - t1));
	}
}
