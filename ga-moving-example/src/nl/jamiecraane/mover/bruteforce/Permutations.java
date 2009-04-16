package nl.jamiecraane.mover.bruteforce;

public class Permutations {
	public static int[] nextPerm(int[] arr) {
		int[] a = (int[]) arr.clone();
		int n = a.length - 1;
		int j = n - 1;
		// "123, 132, 213, 231, 312, 321"
		while (a[j] > a[j + 1]) {
			if (j == 0) {
				// last permutation - reset
				int[] error = new int[] { 0 };
				return error;
			}
			j--;
		}

		// j is the largest subscript with a[j] < a[j+1]
		int k = n;
		while (a[j] > a[k]) {
			// sanity checking here
			k--;
		}
		// a[k] is the smallest term greater than a[j] to the
		// right of a[j] -- swap a[j] and a[k]
		int tmp = a[j];
		a[j] = a[k];
		a[k] = tmp;
		int r = n;
		int s = j + 1;

		while (r > s) {
			// swap a[r] and a[s]
			tmp = a[r];
			a[r] = a[s];
			a[s] = tmp;
			r--;
			s++;
		}

		return a;
	}
	
	// factorial
	public long fact(long n) {
		long f = 1;
		for (long i = 1; i <= n; i++)
			f *= i;
		return f;
	}

	public static void main(String[] args) {
		Permutations p = new Permutations();

		int[] ar = { 0, 1, 2 };
		long n = p.fact(ar.length);
		long start = System.currentTimeMillis();
		for (long i = 0; i < n; i++) {
			for (int j = 0; j < ar.length; j++)
				System.out.print(ar[j] + ", ");
			System.out.println();
			ar = p.nextPerm(ar);
		}
		long end = System.currentTimeMillis();
		System.out.println("Time = " + end + " -- elapsed = " + "" + (end - start));
	}
}
