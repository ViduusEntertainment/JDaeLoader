/**
 * Created on Dec 12, 2016 by Ethan Toney
 */
package com.viduus.util.models;

import java.util.ArrayList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.math.Mat4;
import com.viduus.util.models.visual_scene.SceneNode;

/**
 * 
 *
 * @author ethan
 */
public class Bone {

	public final String id;
	public final Mat4 transformation;
	public final ArrayList<Bone> children = new ArrayList<>();
	
	public Bone( SceneNode node ){
		id = node.getIdentifier();
		transformation = node.transform;
	}

	/**
	 * 
	 */
	public void printData() {
		String children_bone_list = "";
		for( Bone child : children )
			children_bone_list += ", " + child.id;
		if( children_bone_list.length() > 0 )
			children_bone_list = children_bone_list.substring(2);
		children_bone_list = "[ "+children_bone_list+" ]";
		OutputHandler.println("Bone[id:"+id+", children:"+children_bone_list+", transformation:"+transformation+"]");
	}
	
}
