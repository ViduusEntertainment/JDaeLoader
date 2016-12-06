package com.viduus.util.models.util;

import java.nio.FloatBuffer;
import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.math.Mat3;
import com.viduus.util.models.loader.DaeParseException;

/**
 * This class holds an array of floats that were loaded from a dae file. This class
 * also handles sending it's data to the GPU.
 * 
 * @author Ethan Toney
 */
public class FloatArray extends DataArray<Float>{
	
	/**
	 * Creates a new FloatArray by reading the given XML and looking for a float_array
	 * tag.
	 * @param source_data - (NodeList) The XML node of the float array.
	 * @throws DaeParseException Thrown if there was a parsing error.
	 */
	public FloatArray( Node float_array, int count, int stride ) throws DaeParseException {
		super( count, stride );
		data = new Float[count*stride];
		
		// Load in the data
		String[] numbers = float_array.getTextContent().split(" ");
		for( int i=0 ; i<count*stride ; i++ )
			data[i] = Float.parseFloat(numbers[i]);
	}
	
	/**
	 * Add the data contained in two FloatArrays and returns a float[] containing both.
	 * 
	 * @param a - (FloatArray) The first part of the new float array.
	 * @param b - (FloatArray) The second part of the new float array.
	 * @return The combined array.
	 */
	public static float[] addFloatArrays( FloatArray a, FloatArray b ){
		float[] result = new float[ a.count + b.count ];
		
		System.out.println( a.count +" "+a.stride+" "+b.count+" "+b.stride );
		
		if( a.count/a.stride != b.count/b.stride ){
			throw new RuntimeException( "FloatArray lengths do not match up for addition" );
		}
		
		int res_stride = a.stride + b.stride;
		
		for( int i=0 ; i<a.count/a.stride ; i++ ){
			int j=0;
			for( ; j<a.stride ; j++ )
				result[ i*res_stride+j ] = a.data[ i*a.stride+j ];
			for( int k=0 ; k<b.stride ; k++ )
				result[ i*res_stride+j+k ] = b.data[ i*b.stride+k ];
		}
		
		return result;
	}

	/**
	 * Transform all of the xyz points in this FloatArray by some
	 * transformation matrix.
	 * 
	 * @param mat - (FloatBuffer) A float buffer of length 16 containing
	 * transformation matrices.
	 */
	public void multMat4f(FloatBuffer mat) {
		float[] matrix = mat.array();
		Float[] transformed_data = new Float[ data.length ];
		
		for( int i=0 ; i<data.length ; i+=3 ){
			transformed_data[i] = matrix[0]*data[i] + matrix[1]*data[i+1] + matrix[2]*data[i+2] + matrix[3];
			transformed_data[i+1] = matrix[4]*data[i] + matrix[5]*data[i+1] + matrix[6]*data[i+2] + matrix[7];
			transformed_data[i+2] = matrix[8]*data[i] + matrix[9]*data[i+1] + matrix[10]*data[i+2] + matrix[11];
		}
		
		data = transformed_data;
	}

	/**
	 * Transform all of the xyz points in this FloatArray by some
	 * transformation matrix.
	 * 
	 * @param normal_matrix - (Mat3) Matrix containing a transformation for
	 * these points.
	 */
	public void multMat3(Mat3 normal_matrix) {
		float[] matrix = normal_matrix.getBackingArray();
		Float[] transformed_data = new Float[ data.length ];
		
		for( int i=0 ; i<data.length ; i+=3 ){
			transformed_data[i] = matrix[0]*data[i] + matrix[1]*data[i+1] + matrix[2]*data[i+2];
			transformed_data[i+1] = matrix[3]*data[i] + matrix[4]*data[i+1] + matrix[5]*data[i+2];
			transformed_data[i+2] = matrix[6]*data[i] + matrix[7]*data[i+1] + matrix[8]*data[i+2];
		}
		
		data = transformed_data;
	}

}
