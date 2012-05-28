package com.github.permatrix;

public class DictOrderPermutator {
	private int[] a;
	private long numLeft;
	private long total;

	// The number of permutations of n! can be very large, 
	// even when n is as small as 20 --
	// 20! = 2,432,902,008,176,640,000 and
	// 21! is too big to fit into a Java long,
	public DictOrderPermutator(int n) {
		if (n < 1 || n > 20) {
			throw new IllegalArgumentException("Min 1 and Max 20");
		}
		init(n);
	}

	// Are there more permutations?
	public boolean hasNext() {
		return (numLeft != 0);
	}

	// Generate next permutation (algorithm from Rosen p. 284)
	// TODO: caller should not change the returning array
	public int[] next() {
		if (numLeft == total) {
			numLeft--;
			return a;
		}

		int temp;

		// Find largest index j with a[j] < a[j+1]
		int j = a.length - 2;
		while (a[j] > a[j + 1]) {
			j--;
		}

		// Find index k such that a[k] is smallest integer
		// greater than a[j] to the right of a[j]
		int k = a.length - 1;
		while (a[j] > a[k]) {
			k--;
		}

		// Interchange a[j] and a[k]
		temp = a[k];
		a[k] = a[j];
		a[j] = temp;

		// Put tail end of permutation after jth position in increasing order
		int r = a.length - 1;
		int s = j + 1;

		while (r > s) {
			temp = a[s];
			a[s] = a[r];
			a[r] = temp;
			r--;
			s++;
		}

		numLeft--;
		return a;
	}

	// Reset
	private void init(int n) {
		a = new int[n];
		total = getFactorial(n);
		for (int i = 0; i < a.length; i++) {
			a[i] = i;
		}
		numLeft = total;
	}

	// Compute factorial
	private static long getFactorial(int n) {
		long fact = 1;
		for (int i = n; i > 1; i--) {
			fact *= i;
		}
		return fact;
	}
	
	/**
	 * A test method of the permutation generator
	 * @param args
	 */
	public static void main(String[] args) {
		DictOrderPermutator pc = new DictOrderPermutator(4);
		while (pc.hasNext()) {
			int[] dots = pc.next();
			long hash = 0;
			long poly = 1;
			for (int x = 0; x < dots.length; x++) {
				System.out.print(dots[x]);
				hash += dots[x] * poly;
				poly *= 11;

			}
			System.out.println(" => " + hash);
		}
	}
}
