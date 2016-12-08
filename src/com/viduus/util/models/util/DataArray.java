/**
 * Created on Dec 5, 2016 by Ethan Toney
 */
package com.viduus.util.models.util;

/**
 * This class holds an array of floats that were loaded from a dae file. This class
 * also handles sending it's data to the GPU.
 * 
 * @author Ethan Toney
 */
public abstract class DataArray <T> {

	/**
	 * Number of numbers representing a point
	 */
	public final int stride;
	
	/**
	 * Number of points
	 */
	public final int count;

	/**
	 * Backing array for this data array.
	 */
	public T[] data;

	/**
	 * @param count
	 * @param stride
	 */
	public DataArray(int count, int stride) {
		this.count = count;
		this.stride = stride;
	}
	
}
