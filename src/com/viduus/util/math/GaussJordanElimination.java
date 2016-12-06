package com.viduus.util.math;

/**
 * Adapted from http://introcs.cs.princeton.edu/java/95linear/GaussJordanElimination.java.html
 * 
 * @author Ethan Toney
 */
public class GaussJordanElimination {
    private static final double EPSILON = 1e-8;

	private static final float[] MAT3_SOL = { 1, 1, 1 };

    private final int N;      // N-by-N system
    private float[][] a;     // N-by-N+1 augmented matrix

    // Gauss-Jordan elimination with partial pivoting
    public GaussJordanElimination( float[] mat, int N, float[] sol ) {
    	this.N = N;
    	
        // build augmented matrix
        a = new float[N][N+N+1];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                a[i][j] = mat[ i*N+j ];

        // only need if you want to find certificate of infeasibility (or compute inverse)
        for (int i = 0; i < N; i++)
            a[i][N+i] = 1.0f;

        for (int i = 0; i < N; i++) a[i][N+N] = sol[i];

        solve();

        assert check( mat, sol );
    }

    private void solve() {

        // Gauss-Jordan elimination
        for (int p = 0; p < N; p++) {
            // show();

            // find pivot row using partial pivoting
            int max = p;
            for (int i = p+1; i < N; i++) {
                if (Math.abs(a[i][p]) > Math.abs(a[max][p])) {
                    max = i;
                }
            }

            // exchange row p with row max
            swap(p, max);

            // singular or nearly singular
            if (Math.abs(a[p][p]) <= EPSILON) {
                continue;
                // throw new RuntimeException("Matrix is singular or nearly singular");
            }

            // pivot
            pivot(p, p);
        }
//        show();
    }

    // swap row1 and row2
    private void swap(int row1, int row2) {
        float[] temp = a[row1];
        a[row1] = a[row2];
        a[row2] = temp;
    }


    // pivot on entry (p, q) using Gauss-Jordan elimination
    private void pivot(int p, int q) {

        // everything but row p and column q
        for (int i = 0; i < N; i++) {
            double alpha = a[i][q] / a[p][q];
            for (int j = 0; j <= N+N; j++) {
                if (i != p && j != q) a[i][j] -= alpha * a[p][j];
            }
        }

        // zero out column q
        for (int i = 0; i < N; i++)
            if (i != p) a[i][q] = 0.0f;

        // scale row p (ok to go from q+1 to N, but do this for consistency with simplex pivot)
        for (int j = 0; j <= N+N; j++)
            if (j != q) a[p][j] /= a[p][q];
        a[p][q] = 1.0f;
    }

    // extract solution to Ax = b
    public double[] primal() {
        double[] x = new double[N];
        for (int i = 0; i < N; i++) {
            if (Math.abs(a[i][i]) > EPSILON)
                x[i] = a[i][N+N] / a[i][i];
            else if (Math.abs(a[i][N+N]) > EPSILON)
                return null;
        }
        return x;
    }

    // extract solution to yA = 0, yb != 0
    public double[] dual() {
        double[] y = new double[N];
        for (int i = 0; i < N; i++) {
            if ( (Math.abs(a[i][i]) <= EPSILON) && (Math.abs(a[i][N+N]) > EPSILON) ) {
                for (int j = 0; j < N; j++)
                    y[j] = a[i][N+j];
                return y;
            }
        }
        return null;
    }

    // does the system have a solution?
    public boolean isFeasible() {
        return primal() != null;
    }

    // print the tableaux
    private void show() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.printf("%8.3f ", a[i][j]);
            }
            System.out.printf("| ");
            for (int j = N; j < N+N; j++) {
            	System.out.printf("%8.3f ", a[i][j]);
            }
            System.out.printf("| %8.3f\n", a[i][N+N]);
        }
        System.out.println();
    }


    // check that Ax = b or yA = 0, yb != 0
    private boolean check(float[] mat, float[] sol) {

        // check that Ax = b
        if (isFeasible()) {
            double[] x = primal();
            for (int i = 0; i < N; i++) {
                double sum = 0.0;
                for (int j = 0; j < N; j++) {
                     sum += mat[ N*i+j ] * x[j];
                }
                if (Math.abs(sum - sol[i]) > EPSILON) {
                	System.out.println("not feasible");
                	System.out.printf("b[%d] = %8.3f, sum = %8.3f\n", i, sol[i], sum);
                   return false;
                }
            }
            return true;
        }

        // or that yA = 0, yb != 0
        else {
            double[] y = dual();
            for (int j = 0; j < N; j++) {
                double sum = 0.0;
                for (int i = 0; i < N; i++) {
                     sum += mat[ N*i+j ] * y[i];
                }
                if (Math.abs(sum) > EPSILON) {
                	System.out.println("invalid certificate of infeasibility");
                    System.out.printf("sum = %8.3f\n", sum);
                    return false;
                }
            }
            double sum = 0.0;
            for (int i = 0; i < N; i++) {
                sum += y[i] * sol[i];
            }
            if (Math.abs(sum) < EPSILON) {
            	System.out.println("invalid certificate of infeasibility");
            	System.out.printf("yb  = %8.3f\n", sum);
                return false;
            }
            return true;
        }
    }
    
    
    public static Mat3 invert( Mat3 matrix ){
    	GaussJordanElimination gaussian = new GaussJordanElimination( matrix.values, 3, MAT3_SOL );
	    if (gaussian.isFeasible()) {
	    	float[] identity = gaussian.getIdentity();
	    	return new Mat3(identity);
	    	
	    } else {
	    	System.out.println("Certificate of infeasibility");
	        double[] y = gaussian.dual();
	        for (int j = 0; j < y.length; j++) {
	        	System.out.printf("%10.6f\n", y[j]);
	        }
	    }
	    System.out.println();
	    return null;
    }


    private float[] getIdentity() {
    	float[] result = new float[ N*N ];
    	
    	for( int c=N ; c<2*N ; c++ ){
    		for( int r=0 ; r<N ; r++ ){
    			result[ c%N+r*N ] = a[r][c];
    		}
    	}
    	
    	return result;
	}

	// 3-by-3 nonsingular system
    public static void test1() {
        float[] A = {
            0, 1,  1,
            2, 4, -2,
            0, 3, 15
        };
        System.out.println(invert( new Mat3(A) ));
    }
    
    public static void main( String[] args ){
    	test1();
    }

}