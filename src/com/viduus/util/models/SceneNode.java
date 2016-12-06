package com.viduus.util.models;

import java.nio.FloatBuffer;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A scene node is the transformation that is necessary to get a normalized
 * mesh to be relative to the scene or object.
 * 
 * @author Ethan Toney
 */
public class SceneNode {

	enum NodeType {
		NONE,
		CAMERA,
		LIGHT,
		GEOMETRY
	}
	
	enum MatrixType {
		NONE,
		TRANFORM
	}
	
	/**
	 * The type of the node.
	 * @see NodeType
	 */
	public final NodeType type;
	
	/**
	 * The xml url to this element.
	 */
	public final String reference_url;
	
	/**
	 * The type of transformation this scene node represents.
	 * @see MatrixType
	 */
	public final MatrixType matrix_type;
	private String name = "";
	private FloatBuffer matrix_vals;
	
	/**
	 * Parses a new SceneNode from the given xml node.
	 * 
	 * @param curr_node - (Node) The scene node.
	 */
	public SceneNode(Node curr_node) {
		// Values to be used through construction
		NodeType loaded_type = NodeType.NONE;
		String ref_url = "";
		MatrixType mat_type = MatrixType.NONE;
		
		// Get children of this <node> element
		NodeList parts = curr_node.getChildNodes();
		
		// Get attributes from the node
		NamedNodeMap attributes = curr_node.getAttributes();
		name = attributes.getNamedItem("id").getTextContent();
		
		for( int part_id=0 ; part_id<parts.getLength() ; part_id++ ){
			// Get name of node part
			Node part = parts.item(part_id);
			NamedNodeMap part_attributes = part.getAttributes();
			String name = part.getNodeName();
			
			// Load matrix data
			if( name.equals("matrix") ){
				// Get the tranformation type
				String sid = part_attributes.getNamedItem("sid").getTextContent();
				if( sid.equals("transform") )
					mat_type = MatrixType.TRANFORM;
				
				// Read in matrix data
				String[] floats = part.getTextContent().split(" ");
				matrix_vals = FloatBuffer.allocate(floats.length);
				for( int i=0 ; i<floats.length ; i++ )
					matrix_vals.put( Float.parseFloat(floats[i]) );
				
			// Load geometry transformation
			}else if( name.equals("instance_geometry") ){
				loaded_type = NodeType.GEOMETRY;
				ref_url = part_attributes.getNamedItem("url").getTextContent().substring(1);
				
			// Load light transformation
			}else if( name.equals("instance_light") ){
				loaded_type = NodeType.LIGHT;
				ref_url = part_attributes.getNamedItem("url").getTextContent().substring(1);
				
			// Load camera transformation
			}else if( name.equals("instance_camera") ){
				loaded_type = NodeType.CAMERA;
				ref_url = part_attributes.getNamedItem("url").getTextContent().substring(1);
				
			}
		}
		
		matrix_type = mat_type;
		reference_url = ref_url;
		type = loaded_type;
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
	public FloatBuffer getMatrix(){
		return matrix_vals;
	}

}
