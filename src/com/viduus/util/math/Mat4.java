package com.viduus.util.math;

import java.util.Arrays;
import java.util.Vector;

/**
 * Simple implementation of a 4x4 float matrix. This class is not concurrent.
 * 
 * @author Ethan Toney
 */
public class Mat4 {

	private static final float[] IDENTITY_VALS = {
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1
	};
	private static final int ZERO_MATRIX = 0;
	private static final int IDENTITY_MATRIX = 1;
	private static final int ARRAY_SIZE = 16;
	
	public static Mat4 add( Mat4 a, Mat4 b ){
		Mat4 new_mat = new Mat4();
		
		// first row
		new_mat.values[0]  = a.values[0] +  b.values[0];
		new_mat.values[1]  = a.values[1] +  b.values[1];
		new_mat.values[2]  = a.values[2] +  b.values[2];
		new_mat.values[3]  = a.values[3] +  b.values[3];

		// first row
		new_mat.values[4]  = a.values[4] +  b.values[4];
		new_mat.values[5]  = a.values[5] +  b.values[5];
		new_mat.values[6]  = a.values[6] +  b.values[6];
		new_mat.values[7]  = a.values[7] +  b.values[7];

		// first row
		new_mat.values[8]  = a.values[8] +  b.values[8];
		new_mat.values[9]  = a.values[9] +  b.values[9];
		new_mat.values[10] = a.values[10] + b.values[10];
		new_mat.values[11] = a.values[11] + b.values[11];

		// first row
		new_mat.values[12] = a.values[12] + b.values[12];
		new_mat.values[13] = a.values[13] + b.values[13];
		new_mat.values[14] = a.values[14] + b.values[14];
		new_mat.values[15] = a.values[15] + b.values[15];
		
		return new_mat;
	}
	
	/**
	 * This function calculates a view frustrum given the different specified
	 * bounds.
	 * 
	 * @param left - The left plane.
	 * @param right - The right plane.
	 * @param bottom - The bottom plane.
	 * @param top - The Top plane.
	 * @param znear - The near plane.
	 * @param zfar - The far plane.
	 * @return
	 */
	public static Mat4 createFrustrum( float left, float right, float bottom, float top, float znear, float zfar ){
		float temp, temp2, temp3, temp4;
	    temp = (float) (2.0 * znear);
	    temp2 = right - left;
	    temp3 = top - bottom;
	    temp4 = zfar - znear;
	    
	    float matrix[] = {
	    		temp / temp2, 0, (right + left) / temp2, 0,
	    		0, temp / temp3, (top + bottom) / temp3, 0,
	    		0, 0, (-zfar - znear) / temp4, (-temp * zfar) / temp4,
	    		0, 0, -1, 0
	    };

	    return new Mat4( matrix );
	}
	
	public static Mat4 createLookAtMatrix( float ex, float ey, float ez, float cx, float cy, float cz, float ux, float uy, float uz ){
		return createLookAtMatrix( new Vec3(ex, ey, ez), new Vec3(cx, cy, cz), new Vec3(ux, uy, uz) );
	}
	
	public static Mat4 createLookAtMatrix( Vec3 eye, Vec3 center, Vec3 up ){
		Vec3 f = Vec3.normalize( Vec3.subtract(center, eye) );
		Vec3 u = up.normalize();
		Vec3 s = Vec3.normalize( Vec3.crossProduct(f, u) );
		u = Vec3.crossProduct(s, f);
		
		Mat4 result = new Mat4( new float[] {
			s.x, s.y, s.z, -Vec3.dotProduct(s, eye),
			u.x, u.y, u.z, -Vec3.dotProduct(u, eye),
			-f.x, -f.y, -f.z, Vec3.dotProduct(f, eye),
			0, 0, 0, 1
		});
		
		return result;
	}
	
	/**
	 * Creates an orthogonal projection matrix
	 * @param l - Left plane.
	 * @param r - Right plane.
	 * @param b - Bottom plane.
	 * @param t - Top plane.
	 * @param n - Near plane.
	 * @param f - Far plane.
	 * @return
	 */
	public static Mat4 createOrthoMatrix( float l, float r, float b, float t, float n, float f ) {
		float matrix[] = {
	    		2/(r-l), 0, 0, -(r+l)/(r-l),
	    		0, 2/(t-b), 0, -(t+b)/(t-b),
	    		0, 0, -2/(f-n), -(f+n)/(f-n),
	    		0, 0, 0, 1
	    };
		
		return new Mat4( matrix );
	}
	
	/**
	 * Multiplies two matricies a and b. Result is from the following order of
	 * operations.</br>
	 * [a]*[b]
	 * 
	 * @param a - (Mat4) First matrix in multiplication
	 * @param b - (Mat4) Second matrix in multiplication
	 * @return result of [a]*[b]
	 */
	public static Mat4 multiply( Mat4 a, Mat4 b ){
		Mat4 new_mat = new Mat4();
		
		// first row
		new_mat.values[0] = a.values[0]*b.values[0] + a.values[1]*b.values[4] + a.values[2]*b.values[8]  + a.values[3]*b.values[12];
		new_mat.values[1] = a.values[0]*b.values[1] + a.values[1]*b.values[5] + a.values[2]*b.values[9]  + a.values[3]*b.values[13];
		new_mat.values[2] = a.values[0]*b.values[2] + a.values[1]*b.values[6] + a.values[2]*b.values[10] + a.values[3]*b.values[14];
		new_mat.values[3] = a.values[0]*b.values[3] + a.values[1]*b.values[7] + a.values[2]*b.values[11] + a.values[3]*b.values[15];
		
		// second row
		new_mat.values[4] = a.values[4]*b.values[0] + a.values[5]*b.values[4] + a.values[6]*b.values[8]  + a.values[7]*b.values[12];
		new_mat.values[5] = a.values[4]*b.values[1] + a.values[5]*b.values[5] + a.values[6]*b.values[9]  + a.values[7]*b.values[13];
		new_mat.values[6] = a.values[4]*b.values[2] + a.values[5]*b.values[6] + a.values[6]*b.values[10] + a.values[7]*b.values[14];
		new_mat.values[7] = a.values[4]*b.values[3] + a.values[5]*b.values[7] + a.values[6]*b.values[11] + a.values[7]*b.values[15];
		
		// thrid row
		new_mat.values[8] = a.values[8]*b.values[0] + a.values[9]*b.values[4] + a.values[10]*b.values[8]  + a.values[11]*b.values[12];
		new_mat.values[9] = a.values[8]*b.values[1] + a.values[9]*b.values[5] + a.values[10]*b.values[9]  + a.values[11]*b.values[13];
		new_mat.values[10] = a.values[8]*b.values[2] + a.values[9]*b.values[6] + a.values[10]*b.values[10] + a.values[11]*b.values[14];
		new_mat.values[11] = a.values[8]*b.values[3] + a.values[9]*b.values[7] + a.values[10]*b.values[11] + a.values[11]*b.values[15];
		
		// fourth row
		new_mat.values[12] = a.values[12]*b.values[0] + a.values[13]*b.values[4] + a.values[14]*b.values[8]  + a.values[15]*b.values[12];
		new_mat.values[13] = a.values[12]*b.values[1] + a.values[13]*b.values[5] + a.values[14]*b.values[9]  + a.values[15]*b.values[13];
		new_mat.values[14] = a.values[12]*b.values[2] + a.values[13]*b.values[6] + a.values[14]*b.values[10] + a.values[15]*b.values[14];
		new_mat.values[15] = a.values[12]*b.values[3] + a.values[13]*b.values[7] + a.values[14]*b.values[11] + a.values[15]*b.values[15];
		
		return new_mat;
	}
	
	// Multiply a vector by a matrix
	public static Vec3 multiply(Mat4 a, Vec3 b){		
		float x = (a.values[0] * b.x + a.values[1] * b.y + a.values[2] * b.z + a.values[3] * 1);
		float y = (a.values[4] * b.x + a.values[5] * b.y + a.values[6] * b.z + a.values[7] * 1);
		float z = (a.values[8] * b.x + a.values[9] * b.y + a.values[10] * b.z + a.values[11] * 1);
		float w = (a.values[12] * b.x + a.values[13] * b.y + a.values[14] * b.z + a.values[15] * 1);
		
//		if( w != 1 ){
//			x /= w;
//			y /= w;
//			z /= w;
//			w /= w;
//		}
		
		return new Vec3(x,y,z);
	}

	float[] values = new float[16];

	/**
	 * Creates a new instance of Mat4. Default values are the identity matrix.
	 * </br></br>
	 * 1, 0, 0, 0,</br>
	 * 0, 1, 0, 0,</br>
	 * 0, 0, 1, 0,</br>
	 * 0, 0, 0, 1
	 */
	public Mat4(){
		values = Arrays.copyOf(IDENTITY_VALS, ARRAY_SIZE);
	}
	
	/**
	 * Creates a Mat4 with the given initial values. Also, for order of
	 * operations the rotate happens before the translation.
	 * 
	 * @param x - (float) Origin x value.
	 * @param y - (float) Origin y value.
	 * @param z - (float) Origin z value.
	 * @param rx - (float) Rotation along the x axis
	 * @param ry - (float) Rotation along the y axis
	 * @param rz - (float) Rotation along the z axis
	 */
	public Mat4(float x, float y, float z, float rx, float ry, float rz) {
		this();
		rotate( rx, ry, rz );
		System.out.println("rotate: "+this);
		translate( x, y, z );
		System.out.println("translate: "+this);
	}
	
	/**
	 * Copies values of float[] into the values of this matrix. References not
	 * kept through this process.
	 * @param matrix - (float[16]) The values for the desired matrix.
	 */
	public Mat4(float[] matrix) {
		values = Arrays.copyOf(matrix, ARRAY_SIZE);
	}
	
	public Mat4( int matrix_type ){
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
	 * Copy constructor for the Mat4 class. Takes another matrix, matrix, and
	 * copies it's values.
	 * @param matrix - (Mat4) The matrix you wish to copy.
	 */
	public Mat4( Mat4 matrix ){
		values = Arrays.copyOf(matrix.values, ARRAY_SIZE);
	}
	
	/**
	 * Adds the two matrices with the following order of operations.</br>
	 * [this]+[b]
	 * @param b - (Mat4) The matrix to be added.
	 */
	public synchronized void add( Mat4 b ){
		// first row
		values[0]  += b.values[0];
		values[1]  += b.values[1];
		values[2]  += b.values[2];
		values[3]  += b.values[3];

		// first row
		values[4]  += b.values[4];
		values[5]  += b.values[5];
		values[6]  += b.values[6];
		values[7]  += b.values[7];

		// first row
		values[8]  += b.values[8];
		values[9]  += b.values[9];
		values[10] += b.values[10];
		values[11] += b.values[11];

		// first row
		values[12] += b.values[12];
		values[13] += b.values[13];
		values[14] += b.values[14];
		values[15] += b.values[15];
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
		Mat4 other = (Mat4) obj;
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

	/**
	 * Multiplies this matrix by another matrix, b. Order of operation is as
	 * follows.</br>[this]*[b]
	 * 
	 * @param b - (Mat4) Matrix to multiply this one by.
	 */
	public synchronized void multiply( Mat4 b ){
		
		Mat4 temp = new Mat4();
		
		// first row
		temp.values[0] = values[0]*b.values[0] + values[1]*b.values[4] + values[2]*b.values[8]  + values[3]*b.values[12];
		temp.values[1] = values[0]*b.values[1] + values[1]*b.values[5] + values[2]*b.values[9]  + values[3]*b.values[13];
		temp.values[2] = values[0]*b.values[2] + values[1]*b.values[6] + values[2]*b.values[10] + values[3]*b.values[14];
		temp.values[3] = values[0]*b.values[3] + values[1]*b.values[7] + values[2]*b.values[11] + values[3]*b.values[15];
		
		// second row
		temp.values[4] = values[4]*b.values[0] + values[5]*b.values[4] + values[6]*b.values[8]  + values[7]*b.values[12];
		temp.values[5] = values[4]*b.values[1] + values[5]*b.values[5] + values[6]*b.values[9]  + values[7]*b.values[13];
		temp.values[6] = values[4]*b.values[2] + values[5]*b.values[6] + values[6]*b.values[10] + values[7]*b.values[14];
		temp.values[7] = values[4]*b.values[3] + values[5]*b.values[7] + values[6]*b.values[11] + values[7]*b.values[15];
		
		// thrid row
		temp.values[8] = values[8]*b.values[0] + values[9]*b.values[4] + values[10]*b.values[8]  + values[11]*b.values[12];
		temp.values[9] = values[8]*b.values[1] + values[9]*b.values[5] + values[10]*b.values[9]  + values[11]*b.values[13];
		temp.values[10] = values[8]*b.values[2] + values[9]*b.values[6] + values[10]*b.values[10] + values[11]*b.values[14];
		temp.values[11] = values[8]*b.values[3] + values[9]*b.values[7] + values[10]*b.values[11] + values[11]*b.values[15];
		
		// fourth row
		temp.values[12] = values[12]*b.values[0] + values[13]*b.values[4] + values[14]*b.values[8]  + values[15]*b.values[12];
		temp.values[13] = values[12]*b.values[1] + values[13]*b.values[5] + values[14]*b.values[9]  + values[15]*b.values[13];
		temp.values[14] = values[12]*b.values[2] + values[13]*b.values[6] + values[14]*b.values[10] + values[15]*b.values[14];
		temp.values[15] = values[12]*b.values[3] + values[13]*b.values[7] + values[14]*b.values[11] + values[15]*b.values[15];
		
		values = temp.values;
	}
	
	/**
	 * Multiplies this matrix by another matrix, b. Order of operation is as
	 * follows.</br>[b]*[this]
	 * 
	 * @param b - (Mat4) Matrix to multiply this one by.
	 */
	public synchronized void multiplyLeft( Mat4 b ){
		
		Mat4 temp = new Mat4();
		
		// first row
		temp.values[0] = b.values[0]*values[0] + b.values[1]*values[4] + b.values[2]*values[8]  + b.values[3]*values[12];
		temp.values[1] = b.values[0]*values[1] + b.values[1]*values[5] + b.values[2]*values[9]  + b.values[3]*values[13];
		temp.values[2] = b.values[0]*values[2] + b.values[1]*values[6] + b.values[2]*values[10] + b.values[3]*values[14];
		temp.values[3] = b.values[0]*values[3] + b.values[1]*values[7] + b.values[2]*values[11] + b.values[3]*values[15];
		
		// second row
		temp.values[4] = b.values[4]*values[0] + b.values[5]*values[4] + b.values[6]*values[8]  + b.values[7]*values[12];
		temp.values[5] = b.values[4]*values[1] + b.values[5]*values[5] + b.values[6]*values[9]  + b.values[7]*values[13];
		temp.values[6] = b.values[4]*values[2] + b.values[5]*values[6] + b.values[6]*values[10] + b.values[7]*values[14];
		temp.values[7] = b.values[4]*values[3] + b.values[5]*values[7] + b.values[6]*values[11] + b.values[7]*values[15];
		
		// thrid row
		temp.values[8] = b.values[8]*values[0] + b.values[9]*values[4] + b.values[10]*values[8]  + b.values[11]*values[12];
		temp.values[9] = b.values[8]*values[1] + b.values[9]*values[5] + b.values[10]*values[9]  + b.values[11]*values[13];
		temp.values[10] = b.values[8]*values[2] + b.values[9]*values[6] + b.values[10]*values[10] + b.values[11]*values[14];
		temp.values[11] = b.values[8]*values[3] + b.values[9]*values[7] + b.values[10]*values[11] + b.values[11]*values[15];
		
		// fourth row
		temp.values[12] = b.values[12]*values[0] + b.values[13]*values[4] + b.values[14]*values[8]  + b.values[15]*values[12];
		temp.values[13] = b.values[12]*values[1] + b.values[13]*values[5] + b.values[14]*values[9]  + b.values[15]*values[13];
		temp.values[14] = b.values[12]*values[2] + b.values[13]*values[6] + b.values[14]*values[10] + b.values[15]*values[14];
		temp.values[15] = b.values[12]*values[3] + b.values[13]*values[7] + b.values[14]*values[11] + b.values[15]*values[15];
		
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
	
	/**
	 * Rotate this matrix about each axis by some given angle.
	 * @param rx - (float) Rotation about the x axis, in radians.
	 * @param ry - (float) Rotation about the y axis, in radians.
	 * @param rz - (float) Rotation about the z axis, in radians.
	 */
	public void rotate( float rx, float ry, float rz ){
		/*
		 * a - alpha - x axis
		 * b - beta - y axis
		 * y - gamma - z axis
		 */
		
		float cos_rx = (float) Math.cos(rx);
		float cos_ry = (float) Math.cos(ry);
		float cos_rz = (float) Math.cos(rz);
		
		float sin_rx = (float) Math.sin(rx);
		float sin_ry = (float) Math.sin(ry);
		float sin_rz = (float) Math.sin(rz);
		
		Mat4 rot_matrix = new Mat4();

		// first row
		rot_matrix.values[0]  = cos_ry*cos_rz;
		rot_matrix.values[1]  = cos_rz*sin_rx*sin_ry - cos_rx*sin_rz;
		rot_matrix.values[2]  = cos_rx*cos_rz*sin_ry - sin_rx*sin_rz;
		rot_matrix.values[3]  = 0;

		// second row
		rot_matrix.values[4]  = cos_ry*sin_rz;
		rot_matrix.values[5]  = cos_rx*cos_rz + sin_rx*sin_ry*sin_rz;
		rot_matrix.values[6]  = -cos_rz*sin_rx + cos_rx*sin_ry*sin_rz;
		rot_matrix.values[7]  = 0;

		// third row
		rot_matrix.values[8]  = -sin_ry;
		rot_matrix.values[9]  = cos_ry*sin_rx;
		rot_matrix.values[10] = cos_rx*cos_ry;
		rot_matrix.values[11] = 0;

		// fourth row
		rot_matrix.values[12] = 0;
		rot_matrix.values[13] = 0;
		rot_matrix.values[14] = 0;
		rot_matrix.values[15] = 1;
		
		// apply changes
		multiply( rot_matrix );
	}

	/**
	 * Rotate this matrix about the given vector by a given angle.
	 * @param dx - (float) X component of the rotation vector
	 * @param dy - (float) Y component of the rotation vector
	 * @param dz - (float) Z component of the rotation vector
	 * @param angle - (float) Angle of the rotation, in radians.
	 * @see #rotate(Vector, float)
	 */
	public void rotate( float dx, float dy, float dz, float angle ){
		rotate( new Vec3( dx, dy, dz ), angle );
	}

	/**
	 * Shorthand for {@link #rotate(float, float, float)}
	 * @param rotation - (float[]) The array containing the rotations about
	 * each axis. <b>Must be exactly 3 elements long.</b>
	 */
	public void rotate(float[] rotation) {
		if( rotation.length == 3 ){
			rotate( rotation[0], rotation[1], rotation[2] );
		}else{
			throw new IllegalArgumentException( "The array should have eactly three elements." );
		}
	}

	/**
	 * Rotate this matrix about the given vector by a given angle.
	 * @param rotation_vector - (Vector) The vector this matrix will be rotated by
	 * @param angle - (float) Angle to rotate by
	 */
	public void rotate( Vec3 rotation_vector, float angle ){
		Vec3 norm = rotation_vector.normalize();
		
		Mat4 rot_matrix = new Mat4();
		
		float usq = norm.x*norm.x;
		float vsq = norm.y*norm.y;
		float wsq = norm.z*norm.z;
		
		float uv = norm.x*norm.y;
		float vw = norm.y*norm.z;
		float uw = norm.x*norm.z;
		
		float cos = (float) Math.cos( angle );
		float sin = (float) Math.sin( angle );
		
		// first row
//		rot_matrix.values[0] = usq + (1 - usq) * cos;
		rot_matrix.values[0] = cos + usq * (1 - cos);
		rot_matrix.values[1] = uv * (1 - cos) - norm.z*sin;
		rot_matrix.values[2] = uw * (1 - cos) + norm.y*sin;
		rot_matrix.values[3] = 0;
		
		// second row
		rot_matrix.values[4] = uv * (1 - cos) + norm.z*sin;
//		rot_matrix.values[5] = vsq + (1 - vsq) * cos;
		rot_matrix.values[5] = cos + vsq * (1 - cos);
		rot_matrix.values[6] = vw * (1 - cos) - norm.x*sin;
		rot_matrix.values[7] = 0;
		
		// third row
		rot_matrix.values[8] = uw * (1 - cos) - norm.y*sin;
		rot_matrix.values[9] = vw * (1 - cos) + norm.x*sin;
//		rot_matrix.values[10] = wsq + (1 - wsq) * cos;
		rot_matrix.values[10] = cos + wsq * (1 - cos);
		rot_matrix.values[11] = 0;
		
		// fourth row
		rot_matrix.values[12] = 0;
		rot_matrix.values[13] = 0;
		rot_matrix.values[14] = 0;
		rot_matrix.values[15] = 1;
		
		// apply transform
		multiply( rot_matrix );
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Mat4 [values=" + Arrays.toString(values) + "]";
	}
	
	public void translate(float dx, float dy, float dz) {
		Mat4 trans_matrix = new Mat4( Mat4.IDENTITY_MATRIX );

		// setup translate matrix
		trans_matrix.values[3] = dx;
		trans_matrix.values[7] = dy;
		trans_matrix.values[11] = dz;
		trans_matrix.values[15] = 1;
		
		// apply transform
		multiply( trans_matrix );
	}
	
	/**
	 * Shorthand for {@link #translate(float, float, float)}
	 * @param rotation - (float[]) The array containing the displacements.
	 * <b>Must be exactly 3 elements long.</b>
	 */
	public void translate(float[] location) {
		if( location.length == 3 ){
			translate( location[0], location[1], location[2] );
		}else{
			throw new IllegalArgumentException( "The array should have eactly three elements." );
		}
	}
	
	/**
	 * Shorthand for {@link #translate(float, float, float)}
	 * @param disp - (Vector) The displacement.
	 */
	public void translate( Vec3 disp ){
		translate( disp.x, disp.y, disp.z );
	}
	
	/**
	 * TODO
	 * @param i
	 * @param j
	 * @param k
	 */
	public void scale(int tx, int ty, int tz) {
		Mat4 trans_matrix = new Mat4( Mat4.IDENTITY_MATRIX );

		// setup translate matrix
		trans_matrix.values[0] = tx;
		trans_matrix.values[5] = ty;
		trans_matrix.values[10] = tz;
		trans_matrix.values[15] = 1;
		
		// apply transform
		multiply( trans_matrix );
	}
	
}
