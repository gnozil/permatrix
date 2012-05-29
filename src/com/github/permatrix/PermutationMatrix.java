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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

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
    HashMap<Long, CompactImage> images;

    /**
     * if above images are unique or not
     */
    private boolean deducted;

    /**
     * Count of Sole Image - keep same after rotations, Twin Image - have 1
     * sibling after rotation, and Quad Image - have 3 siblings after rotation
     */
    int nSole, nTwin, nQuad;

    public PermutationMatrix(int sz) {
        size = sz;
        images = new HashMap<Long, CompactImage>();
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
                now.incIsomorphicCount();
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
     *            : true - only keep unique images : false - keep all images
     */
    public void generate(boolean unique, boolean verbose) {
        deducted = unique;
        DictOrderPermutator pc = new DictOrderPermutator(size);
        images.clear();
        int progress = 0;
        long cnt = 0;
        while (pc.hasNext()) {
            int[] indices = pc.next();
            CompactImage newImg = new CompactImage(indices);
            if (!unique) {
                images.put(newImg.imageKey(), newImg);
            } else {
                addUniqueImage(newImg);
            }
            if (verbose) {
                cnt++;
                progress++;
                if (progress == 1000) {
                    System.out.println(cnt + " -> " + images.size());
                    progress = 0;
                }
            }
        }
        if (unique) {
            // count for Sole, Twin and Quad image
            for (CompactImage img : images.values()) {
                switch (img.isomorphicCount()) {
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
                    System.err.println("Unexpected reference count: " + img.isomorphicCount() + ", Hash = " + img.imageKey());
                }
            }
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
        int sole = 0;
        int disp = 0;

        Graphics2D g2d = null;

        int w = 2;
        int h = images.size();
        int imgWidth = (size * GRID_SIZE + 1) * w + (GRID_SIZE - 1) * (w - 1) + MARGIN_SIZE * 2;
        int imgHeight = (size * GRID_SIZE + 1) * h + (GRID_SIZE - 1) * (h - 1) + MARGIN_SIZE * 2;

        // draw the canvas on image object
        BufferedImage bimg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_BYTE_BINARY);
        g2d = bimg.createGraphics();

        g2d.drawRect(0, 0, imgWidth - 1, imgHeight - 1);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 10));
        g2d.drawString(size + "x" + size + ": " + h, 20, 15);

        int y = 0;
        for (CompactImage img : images.values()) {
            Collection<Dot> idots = img.imprint();
            int coordY = MARGIN_SIZE + (size + 1) * GRID_SIZE * y;
            img.visualize(MARGIN_SIZE, coordY, GRID_SIZE, g2d);
            visualize(idots, size, MARGIN_SIZE + (size + 1) * GRID_SIZE, coordY, GRID_SIZE, g2d);
            if (idots.size() == size) {
                g2d.drawString("Sole", MARGIN_SIZE, coordY - 2);
                sole++;
            } else if (idots.size() == size * 4) {
                g2d.drawString("Dispersed", MARGIN_SIZE, coordY - 2);
                disp++;
            }
            y++;
        }
        try {
            ImageIO.write(bimg, "png", new FileOutputStream(new File("imprint-" + size + "x" + size + ".png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pw.println("Sole:" + sole + ", Dispersed:" + disp);
    }

    public void visualize(PrintStream pw) {
        for (CompactImage img : images.values()) {
            img.visualize(pw);
            pw.println("---------------------");
        }
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
            g2d.fillRect(posX + dot.x * gridSize + 2, posY + dot.y * gridSize + 2, gridSize - 3, gridSize - 3);
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
            ImageIO.write(img, "png", new FileOutputStream(new File("image-" + size + "x" + size + ".png")));
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

    public void summary() {
        System.out.println("Number of " + (deducted ? "unique" : "all") + " images for " + size + "x" + size + " matrix: "
                + count());
        if (deducted) {
            System.out.println("Sole image: " + nSole);
            System.out.println("Twin image: " + nTwin);
            System.out.println("Quad image: " + nQuad);
        }
    }

    /**
     * This class is for handling command line options of matrix size
     * 
     * @author gnozil@gmail.com
     */
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

    /**
     * the main function
     * 
     * @param args
     *            command line options
     */
    public static void main(String[] args) {
        List<Boundary> matrixes = new ArrayList<Boundary>();

        boolean deducated = true;
        boolean graph = false;
        boolean ascii = true;
        boolean imp = false;
        boolean summary = true;
        boolean verbose = false;

        int idx = 0;
        while (idx < args.length) {
            String arg = args[idx++];
            if (arg.equals("-d")) {
                deducated = true;
            } else if (arg.equals("-f")) {
                deducated = false;
            } else if (arg.equals("-g")) {
                graph = true;
            } else if (arg.equals("-a")) {
                ascii = true;
            } else if (arg.equals("-i")) {
                imp = true;
            } else if (arg.equals("-s")) {
                summary = true;
            } else if (arg.equals("-v")) {
                verbose = true;
            } else if (arg.equals("-n")) {
                ascii = false;
                graph = false;
            } else {
                try {
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
                } catch (NumberFormatException e) {
                    System.err.println("Argument error! Need matrix size");
                }
            }
        }

        if (matrixes.size() == 0) {
            usage();
            System.exit(1);
        }

        long t1 = System.currentTimeMillis();
        for (Boundary boundary : matrixes) {
            for (int m = boundary.getLow(); m <= boundary.getHigh(); m++) {
                PermutationMatrix mx = new PermutationMatrix(m);
                mx.generate(deducated, verbose);

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
                    mx.summary();
                }
            }
        }
        long t2 = System.currentTimeMillis();
        System.out.println("Elapsed (milli-second): " + (t2 - t1));
    }

    public static void usage() {
        StringBuffer text = new StringBuffer();
        text.append("Usage: java " + PermutationMatrix.class.getName() + " [options] <matrix size>\n");
        text.append("  Matrix size format\n");
        text.append("     6        1 matrixes: 6x6\n");
        text.append("     2,3,6    3 matrixes: 2x2,3x3,6x6\n");
        text.append("     4-7      4 matrixes: 4x4,5x5,6x6,7x7\n");
        text.append("     2,4-7,9  6 matrixes: 2x2,4x4,5x5,6x6,7x7,9x9\n");
        text.append("  Options\n");
        text.append("    -d        deducated image\n");
        text.append("    -f        all images without deducation\n");
        text.append("    -g        visualize images in graphic and output a PNG file\n");
        text.append("    -a        visualize images in ascii and output on screen\n");
        text.append("    -n        turn off any visual output\n");
        text.append("    -i        imprint images\n");
        text.append("    -s        output summary information\n");
        text.append("    -v        show matrix generation progress\n");
        System.out.println(text.toString());
    }
}
