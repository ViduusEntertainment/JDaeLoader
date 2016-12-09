/**
 * Created on Dec 5, 2016 by Ethan Toney
 */
package com.viduus.util.models.controller;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.loader.DaeParseException;
import com.viduus.util.models.util.Source;

/**
 * 
 *
 * @author Ethan Toney
 */
public class Controller {
	
	public final float[][] joint_buffers;
	public final String id;
	public final String name;
	public Skin skin = null;
	
	/**
	 * @param curr_animation
	 * @throws DaeParseException 
	 */
	public Controller(Node source_node) throws DaeParseException {
		if( !source_node.getNodeName().equals("controller") )
			throw new DaeParseException("Controller constructor must take a <controller> tag.");

		// Get properties of this effect
		NamedNodeMap effect_attributes = source_node.getAttributes();
		id = effect_attributes.getNamedItem("id").getTextContent();
		name = effect_attributes.getNamedItem("name").getTextContent();
		
		NodeList children = source_node.getChildNodes();
		for( int i=0 ; i<children.getLength() ; i++ ){
			Node child = children.item(i);
			String child_name = child.getNodeName();
			
			if( child_name.equals("skin") ){
				if( skin != null )
					throw new DaeParseException("Multiple skin tags are not implemented.");
				skin = new Skin(child);
				
			}else if( !child_name.equals("#text") ){
				throw new DaeParseException(child_name+" animation tag is not implemented.");
			}
		}
		
		joint_buffers = new float[ skin.vertex_weights.size() ][];
	}

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	/**
	 * 
	 */
	public void printData() {
		OutputHandler.println("Controller[id:"+id+", name:"+name+"]");
		OutputHandler.addTab();
		skin.printData();
		OutputHandler.removeTab();
	}

	/**
	 * 
	 */
	public void generateJointBuffer() {
		for( int i=0 ; i<skin.vertex_weights.size() ; i++ ){
			VertexWeights this_weight = skin.vertex_weights.get(i);
			
			// get source data
			Source joint_data = skin.sources.get(this_weight.sources.get("JOINT").substring(1));
			Source weight_data = skin.sources.get(this_weight.sources.get("WEIGHT").substring(1));
			
			// allocate joint buffer
			joint_buffers[i] = new float[2 * this_weight.vcount.length * this_weight.max_vcount];
			int j_index = 0;
			int v_index = 0;
			
			for( int j=0 ; j<this_weight.vcount.length ; j++ ){
				byte vcount = this_weight.vcount[j];
				
				for( int k=0 ; k<this_weight.max_vcount ; k++ ){
					// add padding
					if( k >= vcount ){
						joint_buffers[i][j_index++] = 0;
						joint_buffers[i][j_index++] = 0;
						
					// add actual values
					}else{
						short joint_index = this_weight.v_indexes[v_index++];
						short weight_index = this_weight.v_indexes[v_index++];
						joint_buffers[i][j_index++] = skin.joint_bones.get((String) joint_data.array.data[joint_index]).index;
						joint_buffers[i][j_index++] = (float) weight_data.array.data[weight_index];
					}
				}
			}
		}
	}

}
