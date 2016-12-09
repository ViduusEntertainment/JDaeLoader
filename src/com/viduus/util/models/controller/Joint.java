/**
 * Created on Dec 7, 2016 by Ethan Toney
 */
package com.viduus.util.models.controller;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.math.Mat4;

/**
 * 
 *
 * @author ethan
 */
public class Joint {

	public final int index;
	public final String name;
	
	public final Mat4 bind_pose_matrix;
	
	public Joint( String name, int index, Mat4 bind_pose_matrix ){
		this.name = name;
		this.index = index;
		this.bind_pose_matrix = bind_pose_matrix;
	}

	/**
	 * 
	 */
	public void printData() {
		OutputHandler.println("Joint[name:"+name+", bind_pose_matrix:"+bind_pose_matrix+"]");
	}
	
}
