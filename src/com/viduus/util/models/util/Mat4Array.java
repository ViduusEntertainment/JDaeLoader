/**
 * Created on Dec 5, 2016 by Ethan Toney
 */
package com.viduus.util.models.util;

import org.w3c.dom.Node;

import com.viduus.util.math.Mat4;

/**
 *
 *
 * @author Ethan Toney
 */
public class Mat4Array extends DataArray<Mat4> {

	/**
	 * @param count
	 * @param stride
	 */
	public Mat4Array( Node mat4_array, int count, int stride ) {
		super(count, stride);

		data = new Mat4[count];

		// Load in the data
		String[] numbers = mat4_array.getTextContent().split(" ");
		float[] nums = new float[16];
		for( int i=0, j=0 ; i<count*stride ; i++ ){
			nums[i % 16] = Float.parseFloat(numbers[i]);
			if( i != 0 && i%16 == 0 )
				data[j++] = new Mat4(nums);
		}
	}

}
