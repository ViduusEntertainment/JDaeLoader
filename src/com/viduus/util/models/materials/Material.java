/**
 * Created on Dec 1, 2016 by Ethan Toney
 */
package com.viduus.util.models.materials;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.loader.DaeParseException;

/**
 * 
 *
 * @author Ethan Toney
 */
public class Material {

	private final String id;
	private String effect_url;

	/**
	 * @param curr_material
	 * @throws DaeParseException 
	 */
	public Material(Node source_node) throws DaeParseException {
		if( !source_node.getNodeName().equals("material") )
			throw new DaeParseException("Material constructor must be passed a <material> tag.");
		
		// Get properties of this material
		NamedNodeMap material_attributes = source_node.getAttributes();
		id = material_attributes.getNamedItem("id").getTextContent();
		
		NodeList children = source_node.getChildNodes();
		for( int i=0 ; i<children.getLength() ; i++ ){
			Node child_node = children.item(i);
			String child_name = child_node.getNodeName();
			
			if( child_name.equals("instance_effect") ){
				NamedNodeMap instance_attributes = child_node.getAttributes();
				effect_url = instance_attributes.getNamedItem("url").getTextContent();
				// TODO Implement tags that can be inside of <instance_effect>
				
			}else if( !child_name.equals("#text") ){
				throw new DaeParseException("Material child tag <"+child_name+"> is not supported.");
			}
		}
	}

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 */
	public void printData() {
		OutputHandler.println("Material[id:'"+id+"', effect_url:'"+effect_url+"']");
	}

	
	
}
