/**
 * Created on Dec 5, 2016 by Ethan Toney
 */
package com.viduus.util.models.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.math.Mat4;
import com.viduus.util.models.geometries.Polylist;
import com.viduus.util.models.loader.DaeParseException;
import com.viduus.util.models.util.Source;

/**
 * 
 *
 * @author Ethan Toney
 */
public class Skin {

	public final String source_id;
	public final HashMap<String, Source> sources = new HashMap<>();
	public final HashMap<String, String> joints = new HashMap<>();
	public final List<VertexWeights> vertex_weights = new ArrayList<>();
	public final HashMap<String, Joint> joint_bones = new HashMap<>();
	public Mat4 bind_shape_matrix;
	
	/**
	 * @param child
	 * @throws DaeParseException 
	 */
	public Skin(Node source_node) throws DaeParseException {
		if( !source_node.getNodeName().equals("skin") )
			throw new DaeParseException("Animation constructor must take a <animation> tag.");

		// Get properties of this effect
		NamedNodeMap effect_attributes = source_node.getAttributes();
		source_id = effect_attributes.getNamedItem("source").getTextContent().substring(1);
		
		NodeList children = source_node.getChildNodes();
		for( int i=0 ; i<children.getLength() ; i++ ){
			Node child = children.item(i);
			String child_name = child.getNodeName();
			
			if( child_name.equals("source") ){
				Source source = new Source(child);
				sources.put(source.getId(), source);
				
			}else if( child_name.equals("bind_shape_matrix") ){
				float[] mat = new float[16];
				String[] numbers = child.getTextContent().split(" ");
				for( int j=0 ; j<numbers.length ; j++ )
					mat[j] = Float.parseFloat(numbers[j]);
				bind_shape_matrix = new Mat4(mat);
				
			}else if( child_name.equals("joints") ){
				NodeList sampler_elements = child.getChildNodes();
				for( int j=0 ; j<sampler_elements.getLength() ; j++ ){
					Node samplet_element = sampler_elements.item(j);
					if( samplet_element.getNodeName().equals("input") ){
						NamedNodeMap sampler_attributes = samplet_element.getAttributes();
						String semantic = sampler_attributes.getNamedItem("semantic").getTextContent();
						String source = sampler_attributes.getNamedItem("source").getTextContent().substring(1);
						joints.put(semantic, source);
					}
				}
				
			}else if( child_name.equals("vertex_weights") ){
				vertex_weights.add(new VertexWeights(child));
				
			}else if( !child_name.equals("#text") ){
				throw new DaeParseException(child_name+" animation tag is not implemented.");
			}
		}
		
		// Check for unsupported stuff
		// TODO Support multiple <vertex_weights>
		if( vertex_weights.size() > 1 )
			throw new DaeParseException("Multiple <vertex_weights> tags are not supported.");
		
		// Create joint bones
		Source joint_names = sources.get(joints.get("JOINT"));
		Source joint_matricies = sources.get(joints.get("INV_BIND_MATRIX"));
		for( int i=0 ; i<joint_names.array.count ; i++ ){
			Joint n_joint = new Joint( (String)joint_names.array.data[i], i, (Mat4)joint_matricies.array.data[i] );
			joint_bones.put(n_joint.name, n_joint);
		}
	}

	/**
	 * 
	 */
	public void printData() {
		OutputHandler.println("Skin[source_id:'"+source_id+"', ]");
		OutputHandler.addTab();
		for( VertexWeights vert_weight : vertex_weights )
			vert_weight.printData();
		for( Joint joint : joint_bones.values() )
			joint.printData();
		for( Source source : sources.values() )
			source.printData();
		for( String key : joints.keySet() ){
			OutputHandler.println("JointAttribute:[name:'"+key+"', data:'"+joints.get(key).toString()+"']");
		}
		OutputHandler.println("BindShapeMatrix @ "+bind_shape_matrix);
		OutputHandler.removeTab();
	}

}
