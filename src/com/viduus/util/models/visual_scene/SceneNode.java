package com.viduus.util.models.visual_scene;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.math.Mat4;
import com.viduus.util.models.loader.DaeParseException;
import com.viduus.util.models.util.LoaderFunctions;

/**
 * A scene node is the transformation that is necessary to get a normalized
 * mesh to be relative to the scene or object.
 * 
 * @author Ethan Toney
 */
public class SceneNode {
	
	public final String name, id;
	public final Mat4 transform;
	public final List<Instance> instances = new ArrayList<>();
	public final ArrayList<SceneNode> sub_nodes = new ArrayList<>();
	
	/**
	 * Parses a new SceneNode from the given xml node.
	 * 
	 * @param curr_node - (Node) The scene node.
	 * @throws DaeParseException 
	 */
	public SceneNode(Node curr_node) throws DaeParseException {
		if( !curr_node.getNodeName().equals("node") )
			throw new DaeParseException("SceneNode contructor must take a <node> tag.");
		
		NamedNodeMap attributes = curr_node.getAttributes();
		name = attributes.getNamedItem("name").getTextContent();
		id = attributes.getNamedItem("id").getTextContent();

		HashMap<String, HashMap<String, Node>> child_nodes = LoaderFunctions.loadAllChildNodes(curr_node);
		
		transform = new Mat4();
		loadTransforms(child_nodes);
		
		loadInstances(child_nodes);
		
		// Read recursive armature information
		if( child_nodes.containsKey("node") ){
			for( Node node : child_nodes.get("node").values() )
				sub_nodes.add(new SceneNode(node));
		}
		
		if( child_nodes.containsKey("asset") ){
			// TODO Implement <asset> tag
		}
		
		if( child_nodes.containsKey("extra") ){
			// TODO Implement <extra> tag
		}
	}
	
	/**
	 * @param child_nodes
	 * @throws DaeParseException 
	 */
	private void loadInstances(HashMap<String, HashMap<String, Node>> child_nodes) throws DaeParseException {
		if( child_nodes.containsKey("instance_camera") ){
			// TODO Implement <instance_camera> tag.
		}

		if( child_nodes.containsKey("instance_controller") ){
			for( Node instance_controller : child_nodes.get("instance_controller").values() )
				instances.add(new InstanceController(instance_controller));
		}

		if( child_nodes.containsKey("instance_geometry") ){
			for( Node instance_geometry : child_nodes.get("instance_geometry").values() )
				instances.add(new InstanceGeometry(instance_geometry));
		}

		if( child_nodes.containsKey("instance_light") ){
			// TODO Implement <instance_light> tag.
		}

		if( child_nodes.containsKey("instance_node") ){
			// TODO Implement <instance_node> tag.
		}
	}

	/**
	 * @param child_nodes
	 */
	private void loadTransforms(HashMap<String, HashMap<String, Node>> child_nodes) {
		if( child_nodes.containsKey("matrix") ){
			Node matrix_node = child_nodes.get("matrix").get("transform");
			String[] s_numbs = matrix_node.getTextContent().split(" ");
			float[] f_numbs = new float[16];
			for( int i=0 ; i<16 ; i++ )
				f_numbs[i] = Float.parseFloat(s_numbs[i]);
			transform.multiply(new Mat4(f_numbs));
		}
		
		if( child_nodes.containsKey("translate") ){
			String[] s_loc_nums = child_nodes.get("translate").get("location").getTextContent().split(" ");
			float[] f_loc_nums = new float[3];
			for( int i=0 ; i<3 ; i++ )
				f_loc_nums[i] = Float.parseFloat(s_loc_nums[i]);
			transform.translate(f_loc_nums);
		}
		
		if( child_nodes.containsKey("scale") ){
			String[] s_scale_nums = child_nodes.get("scale").get("scale").getTextContent().split(" ");
			float[] f_scale_nums = new float[3];
			for( int i=0 ; i<3 ; i++ )
				f_scale_nums[i] = Float.parseFloat(s_scale_nums[i]);
			transform.scale(f_scale_nums);
		}
		
		if( child_nodes.containsKey("skew") ){
			// TODO Implement <skew> tag parsing
		}
		
		if( child_nodes.containsKey("lookat") ){
			// TODO Implement <lookat> tag parsing
		}
		
		if( child_nodes.containsKey("rotate") ){
			HashMap<String, Node> rotations = child_nodes.get("rotate");
			
			if( rotations.containsKey("rotationX") ){
				String[] s_rot_x_nums = rotations.get("rotationX").getTextContent().split(" ");
				float[] f_rot_x_nums = new float[4];
				for( int i=0 ; i<4 ; i++ )
					f_rot_x_nums[i] = Float.parseFloat(s_rot_x_nums[i]);
				transform.rotate(f_rot_x_nums[0], f_rot_x_nums[1], f_rot_x_nums[2], (float) Math.toRadians(f_rot_x_nums[3]));
			}
			
			if( rotations.containsKey("rotationY") ){
				String[] s_rot_y_nums = rotations.get("rotationY").getTextContent().split(" ");
				float[] f_rot_y_nums = new float[4];
				for( int i=0 ; i<4 ; i++ )
					f_rot_y_nums[i] = Float.parseFloat(s_rot_y_nums[i]);
				transform.rotate(f_rot_y_nums[0], f_rot_y_nums[1], f_rot_y_nums[2], (float) Math.toRadians(f_rot_y_nums[3]));
			}
			
			if( rotations.containsKey("rotationZ") ){
				String[] s_rot_z_nums = rotations.get("rotationZ").getTextContent().split(" ");
				float[] f_rot_z_nums = new float[4];
				for( int i=0 ; i<4 ; i++ )
					f_rot_z_nums[i] = Float.parseFloat(s_rot_z_nums[i]);
				transform.rotate(f_rot_z_nums[0], f_rot_z_nums[1], f_rot_z_nums[2], (float) Math.toRadians(f_rot_z_nums[3]));
			}
		}
	}

	/**
	 * @return The name of this scene node.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * @return The matrix for this scene node.
	 */
	public Mat4 getMatrix(){
		return transform;
	}

	/**
	 * 
	 */
	public void printData() {
		OutputHandler.println("SceneNode[name:"+name+", id:"+id+", transform:"+transform+", num_instances:"+instances.size()+"]");
		OutputHandler.addTab();
		for( SceneNode node : sub_nodes )
			node.printData();
		for( Instance instance : instances )
			instance.printData();
		OutputHandler.removeTab();
	}

	/**
	 * @return
	 */
	public String getIdentifier() {
		return ( id == null ) ? name : id ;
	}

}
