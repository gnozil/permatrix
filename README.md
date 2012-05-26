Permutation Matrix
==================

To explore mathematics characteristics of permutation matrix.

Overview
--------

This project is to resolve a mathematical question which has attracted me for years.
To be simple, the question can be described as below steps:

### Step 1
On a square N x N grid, select exact N cells that match condition: only
one cell in same row and column. How many solutions will be?
        
This answer is very simple: N!. It is actually a full permutation of N
numbers and each solution is a Permutation Matrix. We call above selection
as "Permutation Selection".

Then we will go to step 2 of this question.

### Step 2
Each permutation selection forms an 2-D Image. While a image will be
equal to another one after 1 to 3 times 90 degree rotation. But some images
keep unique even after rotations. How many unique images will be for a
N x N grid?
        
The answer is unknown to me up to today.
        
But it is possible to use brute force approach to find solutions of this
question. It is the reason why I created this project. I have part of
solutions below:
        2x2   1
        3x3   2
        4x4   9
        5x5   33
        6x6   192
        7x7   1272
        8x8   10182
        9x9   90822

My computer is not so powerful that to get answers for larger grids up to today.
During the experiments, I have gone further steps:

### Step 3
For an unique image, we could rotate it up to 3 times and imprint 3 rotated
images to the original one. As a result, in the imprinted image, there will
be N to 4N cells. For N cells case, we call the original image is a
Symmetric Image. For 4N cells case, we call the original image is a
Dispersed Image. Then question 3 is: how many Symmetric Image and Dispersed
Image for a N x N grid by permutation selection?

The answer is unknown too. Partial answers in below:

Typical, Symmetric Image only exists for N x N grid, when N = 4K or N = 4K+1,
where K is an integer.

### Step 4
Above "Permutation Selection" is in 2-D space, how about 3-D space? in 3-D
space, the rotation is allowed by X-Y, X-Z and Y-Z axis.

This question is totally open now, my project codes are only for 2-D.

### Step 5
Is there any real world use of permutation selection and other characteristics
of symmetric or dispersed image?

At least, I found the imprinted images are very beautiful, we may print
the full symmetric image on cloth, arrange floor bricks following the image,
or any other place we can imagine.

The Application
---------------
I wrote the Java application to solve the question (Step 1-4 up to 26 May 2012).
I am not good at either mathematics or programming, so the application codes
may look immature. It includes only several files that do generate permutation
serial, find the unique images in brute force way, and render out the results
by Java2D API. Up to now, the application's performance is not good enough. In
my Lenovo ThinkPad T420 laptop, it can only calculate up to a 9x9 grid without
graphics output.

The application has below function:
* Generate original Permutation Selections and find unique images for a specified grid.
  Now the grid size must below than 20.
* Render Permutation Selections in Ascii format and 1-pixel PNG image
* Imprint rotated images and render the final image in PNG
* Accept several command line options for different behaviors

The application requires Java 1.6. It was written and tested in Ubuntu Linux 11.10 AMD64
version with Oracle JDK 1.6.0_32 64-bit.

The 'results' folder include several generated PNG files. One 'image*.png' file include
all unique images for a grid. One 'imprint*.png' file include all unique image and their
imprinted images.


TODO List
---------
* Get final mathematical formula for number of unique, symmetric and dispersed image.
* Change PNG render approach for large image by multiple files
* Add a ANT build script file
* Add a simple GUI application launcher
* Optimize performance in CPU and memory
* Check the uniqueness of imprinted images
    