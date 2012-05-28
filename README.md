Permutation Matrix
==================

To explore mathematics characteristics of permutation matrix.

Overview
--------

This project is to solve a mathematical question which has attracted me for
years. To be simple, the question can be described by below steps:

### Step 1
On a square N x N grid, select exact N cells that satisfy condition: only one
cell selected in same row and column. How many solutions will be?

The answer is very simple: N!. It is actually a full permutation of N numbers
and each solution is a Permutation Matrix. We call above selection
as "Permutation Selection".

Then we will go to step 2 of this question.

### Step 2
Each permutation selection forms an 2-D image. While we found some images will
equal to others after 1 to 3 times 90 degree rotation, and some images keep
unique even after rotations. (please see image files in results directory)
If we discard all images which are just another image's rotation, then we'll
get a set of images which no one could be derived from others by rotation in
the same set, and we call such image Unique Image. The 2nd question is: How many
unique images will be for a N x N grid? 

The answer is unknown to me for an arbitrary N.

But it is possible to use brute force approach to find solutions. It is also the
reason why I created this project. With an application, I got part of solutions
below:

    Matrix size     All images    Unique images
            2x2     2             1
            3x3     6             2
            4x4     24            9
            5x5     120           33
            6x6     720           192
            7x7     5040          1272
            8x8     40320         10182
            9x9     362880        90822
            10x10   3628800       908160
            11x11   39916800      9980160

My computer is not so powerful for getting answers for larger grids up to today.
During the experiments, I got other interesting findings that allow me go
further.

### Step 3
For an unique image, we could rotate it up to 3 times and imprint 3 rotated
images into the original one. As a result, in an imprinted image, there will
be N to 4N selected cells. For N cells case, we call the original image is a
Sole Image. For 4N cells case, we call the original image is a Dispersed Image.
Then we got the 3rd question: how many Sole Image and Dispersed Image for a
N x N grid by permutation selection?

The answer is unknown too. We would find a Sole Image only exists for N x N
grid, when N = 4K or N = 4K+1, where K is an integer.

In addition, we can define another two kinds of image:

* Twin Image: we'll get exact 1 other image with 3 rotations on a Twin Image.
* Quad Image: we'll get exact 3 other images with 3 rotations on a Quad Image.

And we can also get a simple formula among number of Sole, Twin, and Quad image:

    N! = Sole + 2 * Twin + 4 * Quad

and

    Unique Image number = Sole + Twin + Quad

### Step 4
Above "Permutation Selection" is in 2-D space, how about in 3-D space? where the
rotation is allowed in X-Y, X-Z and Y-Z planes.

The questions for 3-D and higher spaces are totally open now, and I planned to
cover them in this project in the future.

### Step 5
Is there any real world use of permutation selection, and are there other
characteristics of it?

At least, I found imprinted 2-D images are very beautiful - they are fully
symmetric. We may print such images on clothes, arrange floor bricks following
their patterns, or do any other things with them in the way that we can imagine.

The Application
---------------
I wrote the Java application to find answers for question steps 1-3 up to today,
and only for small N.

I am not good at either mathematics or programming, so the application codes
may look immature. It includes only several files that can generate permutation
selections, find unique images in brute force way, and render out the results
by Java2D API. Up to now, the application's performance is not good enough. In
my Lenovo ThinkPad T420 laptop, it can only process up to a 11x11 grid without
graphics output.

The application has below function:
* Generate original Permutation Selections and find unique images for a specified grid.
  Now the grid size must be below 20.
* Render Permutation Selections in ASCII format and 1-pixel PNG image
* Imprint rotated images and render the final image in PNG
* Accept several command line options for different behaviors
* Divide the unique images to types of Sole, Twin and Quad

The application requires Java 1.6. It was written and tested in Ubuntu Linux
11.10 x86-64 system with Oracle JDK 1.6.0\_32 64-bit edition.

The 'results' folder include several generated PNG files. One 'image\*.png' file
include all unique images for a grid. One 'imprint\*.png' file include all
unique image and their imprinted images. One 'all\*.png' file include all
original permutation selections for a grid.

__Note:__ Your image viewer would eat up your computer's memory for displaying
some images, although these PNG files are small in size, their image canvas is big.

TODO List
---------
* Get final mathematical formula for number of unique, sole, twin, quad and
dispersed image.
* Check uniqueness of imprinted images
* Split large PNG image to multiple small one by modifying render approach
* Add a ANT build script file
* Add a simple GUI application launcher
* Optimize performance for CPU and memory utilization

Other Documents
---------------
I am also writing Wiki pages about the question on [Github Wiki] (https://github.com/gnozil/permatrix/wiki/Discuss-of-Permutation-Matrix)

References
----------
* [Euclidean Space] (http://www.euclideanspace.com/maths/algebra/matrix/orthogonal/rotation/index.htm)
* [Wikipedia Rotation Matrix] (http://en.wikipedia.org/wiki/Rotation_matrix)
