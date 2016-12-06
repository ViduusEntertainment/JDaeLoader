package com.viduus.util.math;

import java.util.Arrays;

/**
 * Simple implementation of a 4x4 float matrix. This class is not concurrent.
 * 
 * @author Ethan Toney
 */
public class Mat3 {

	private static final float[] IDENTITY_VALS = {
			1, 0, 0,
			0, 1, 0,
			0, 0, 1,
	};
	private static final int ZERO_MATRIX = 0;
	private static final int IDENTITY_MATRIX = 1;
	private static final int ARRAY_SIZE = 9;
	
	public static Mat3 add( Mat3 a, Mat3 b ){
		Mat3 new_mat = new Mat3();
		
		// first row
		new_mat.values[0]  = a.values[0] +  b.values[0];
		new_mat.values[1]  = a.values[1] +  b.values[1];
		new_mat.values[2]  = a.values[2] +  b.values[2];

		// second row
		new_mat.values[3]  = a.values[3] +  b.values[3];
		new_mat.values[4]  = a.values[4] +  b.values[4];
		new_mat.values[5]  = a.values[5] +  b.values[5];

		// third row
		new_mat.values[6]  = a.values[6] +  b.values[6];
		new_mat.values[7]  = a.values[7] +  b.values[7];
		new_mat.values[8]  = a.values[8] +  b.values[8];
		
		return new_mat;
	}
	
	/**
	 * Multiplies two matricies a and b. Result is from the following order of
	 * operations.</br>
	 * [a]*[b]
	 * 
	 * @param a - (Mat3) First matrix in multiplication
	 * @param b - (Mat3) Second matrix in multiplication
	 * @return result of [a]*[b]
	 */
	public static Mat3 multiply( Mat3 a, Mat3 b ){
		Mat3 new_mat = new Mat3();
		
		// first row
		new_mat.values[0] = a.values[0]*b.values[0] + a.values[1]*b.values[3] + a.values[2]*b.values[6];
		new_mat.values[1] = a.values[0]*b.values[1] + a.values[1]*b.values[4] + a.values[2]*b.values[7];
		new_mat.values[2] = a.values[0]*b.values[2] + a.values[1]*b.values[5] + a.values[2]*b.values[8];
		
		// second row
		new_mat.values[3] = a.values[3]*b.values[0] + a.values[4]*b.values[3] + a.values[5]*b.values[6];
		new_mat.values[4] = a.values[3]*b.values[1] + a.values[4]*b.values[4] + a.values[5]*b.values[7];
		new_mat.values[5] = a.values[3]*b.values[2] + a.values[4]*b.values[5] + a.values[5]*b.values[8];
		
		// thrid row
		new_mat.values[6] = a.values[6]*b.values[0] + a.values[7]*b.values[3] + a.values[8]*b.values[6];
		new_mat.values[7] = a.values[6]*b.values[1] + a.values[7]*b.values[4] + a.values[8]*b.values[7];
		new_mat.values[8] = a.values[6]*b.values[2] + a.values[7]*b.values[5] + a.values[8]*b.values[8];
		
		return new_mat;
	}
	
	// Multiply a vector by a matrix
	public static Vec3 multiply(Mat3 a, Vec3 b){		
		float x = (a.values[0] * b.x + a.values[1] * b.y + a.values[2] * b.z);
		float y = (a.values[3] * b.x + a.values[4] * b.y + a.values[5] * b.z);
		float z = (a.values[6] * b.x + a.values[7] * b.y + a.values[8] * b.z);
		
		return new Vec3(x,y,z);
	}
	
	float[] values = new float[ARRAY_SIZE];
	
	/**
	 * Creates a new instance of Mat3. Default values are the identity matrix.
	 * </br></br>
	 * 1, 0, 0,</br>
	 * 0, 1, 0,</br>
	 * 0, 0, 1
	 */
	public Mat3(){
		values = Arrays.copyOf(IDENTITY_VALS, ARRAY_SIZE);
	}
	
	/**
	 * Copies values of float[] into the values of this matrix. References not
	 * kept through this process.
	 * @param matrix - (float[16]) The values for the desired matrix.
	 */
	public Mat3(float[] matrix) {
		values = Arrays.copyOf(matrix, ARRAY_SIZE);
	}

	public Mat3( int matrix_type ){
		switch( matrix_type ){
		
		case( ZERO_MATRIX ):
			Arrays.fill(values, 0);
			break;
			
		case( IDENTITY_MATRIX ):
			values = Arrays.copyOf(IDENTITY_VALS, ARRAY_SIZE);
			break;
		}
	}

	/**
	 * Copy constructor for the Mat3 class. Takes another matrix, matrix, and
	 * copies it's values.
	 * @param matrix - (Mat3) The matrix you wish to copy.
	 */
	public Mat3( Mat3 matrix ){
		values = Arrays.copyOf(matrix.values, ARRAY_SIZE);
	}
	
	public Mat3( Mat4 matrix ){
		for( int c=0 ; c<3 ; c++ ){
			for( int r=0 ; r<3 ; r++ ){
				values[ 3*r+c ] = matrix.values[ 4*r+c ];
			}
		}
	}
	
	/**
	 * Adds the two matrices with the following order of operations.</br>
	 * [this]+[b]
	 * @param b - (Mat3) The matrix to be added.
	 */
	public synchronized void add( Mat3 b ){
		// first row
		values[0]  += b.values[0];
		values[1]  += b.values[1];
		values[2]  += b.values[2];

		// second row
		values[3]  += b.values[3];
		values[4]  += b.values[4];
		values[5]  += b.values[5];

		// third row
		values[6]  += b.values[6];
		values[7]  += b.values[7];
		values[8]  += b.values[8];
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mat3 other = (Mat3) obj;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}
	
	/**
	 * Returns the array backing this matrix in row-major order.
	 * @return The backing array to this matrix.
	 */
	public float[] getBackingArray() {
		return values;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
		return result;
	}
	
	public Mat3 inverse(){
		return GaussJordanElimination.invert(this);
	}
	
	/**
	 * Multiplies this matrix by another matrix, b. Order of operation is as
	 * follows.</br>[this]*[b]
	 * 
	 * @param b - (Mat3) Matrix to multiply this one by.
	 */
	public synchronized void multiply( Mat3 b ){
		Mat3 temp = new Mat3();
		
		// first row
		temp.values[0] = values[0]*b.values[0] + values[1]*b.values[3] + values[2]*b.values[6];
		temp.values[1] = values[0]*b.values[1] + values[1]*b.values[4] + values[2]*b.values[7];
		temp.values[2] = values[0]*b.values[2] + values[1]*b.values[5] + values[2]*b.values[8];
		
		// second row
		temp.values[3] = values[3]*b.values[0] + values[4]*b.values[3] + values[5]*b.values[6];
		temp.values[4] = values[3]*b.values[1] + values[4]*b.values[4] + values[5]*b.values[7];
		temp.values[5] = values[3]*b.values[2] + values[4]*b.values[5] + values[5]*b.values[8];
		
		// thrid row
		temp.values[6] = values[6]*b.values[0] + values[7]*b.values[3] + values[8]*b.values[6];
		temp.values[7] = values[6]*b.values[1] + values[7]*b.values[4] + values[8]*b.values[7];
		temp.values[8] = values[6]*b.values[2] + values[7]*b.values[5] + values[8]*b.values[8];
		
		values = temp.values;
	}
	
	/**
	 * Multiplies this matrix by another matrix, b. Order of operation is as
	 * follows.</br>[b]*[this]
	 * 
	 * @param b - (Mat3) Matrix to multiply this one by.
	 */
	public synchronized void multiplyLeft( Mat3 b ){
		Mat3 temp = new Mat3();
		
		// first row
		temp.values[0] = b.values[0]*values[0] + b.values[1]*values[3] + b.values[2]*values[6];
		temp.values[1] = b.values[0]*values[1] + b.values[1]*values[4] + b.values[2]*values[7];
		temp.values[2] = b.values[0]*values[2] + b.values[1]*values[5] + b.values[2]*values[8];
		
		// second row
		temp.values[3] = b.values[3]*values[0] + b.values[4]*values[3] + b.values[5]*values[6];
		temp.values[4] = b.values[3]*values[1] + b.values[4]*values[4] + b.values[5]*values[7];
		temp.values[5] = b.values[3]*values[2] + b.values[4]*values[5] + b.values[5]*values[8];
		
		// thrid row
		temp.values[6] = b.values[6]*values[0] + b.values[7]*values[3] + b.values[8]*values[6];
		temp.values[7] = b.values[6]*values[1] + b.values[7]*values[4] + b.values[8]*values[7];
		temp.values[8] = b.values[6]*values[2] + b.values[7]*values[5] + b.values[8]*values[8];
		
		values = temp.values;
	}

	/**
	 * Resets this matrix to it's identity matrix.
	 */
	public synchronized void reset() {
		for( int i=0 ; i<ARRAY_SIZE ; i++ ){
			values[i] = IDENTITY_VALS[i];
		}
	}
	
	/**
	 * Resets this matrix to the given type of matrix.
	 * 
	 * @see #IDENTITY_MATRIX
	 * @see #ZERO_MATRIX
	 */
	public synchronized void reset( int type ) {
		for( int i=0 ; i<ARRAY_SIZE ; i++ ){
			switch(type){
			case( ZERO_MATRIX ):
				values[i] = 0;
				break;
			case( IDENTITY_MATRIX ):
			default:
				values[i] = IDENTITY_VALS[i];
				break;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Mat3 [values=" + Arrays.toString(values) + "]";
	}

	/**
	 * TODO
	 * @return
	 */
	public Mat3 transpose() {
		Mat3 new_mat = new Mat3();

		new_mat.values[0] = this.values[0];
		new_mat.values[1] = this.values[3];
		new_mat.values[2] = this.values[6];

		new_mat.values[3] = this.values[1];
		new_mat.values[4] = this.values[4];
		new_mat.values[5] = this.values[7];

		new_mat.values[6] = this.values[2];
		new_mat.values[7] = this.values[5];
		new_mat.values[8] = this.values[8];
		
		return new_mat;
	}
	
}
