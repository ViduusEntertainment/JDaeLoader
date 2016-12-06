/**
 * Created on Dec 1, 2016 by Ethan Toney
 */
package com.viduus.util.models.geometries;

import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.models.loader.DaeParseException;

/**
 * 
 *
 * @author Ethan Toney
 */
public class Verticies {

	public final String id;
	public final HashMap<String,String> sources;
	
	/**
	 * @param child_node
	 * @throws DaeParseException 
	 */
	public Verticies(Node curr_node) throws DaeParseException {
		if( !curr_node.getNodeName().equals("vertices") )
			throw new DaeParseException("Verticies constructor must be called on an <vertices> node.");
		
		sources = new HashMap<>();
		NodeList source_data = curr_node.getChildNodes();
		NamedNodeMap source_attributes = curr_node.getAttributes();
		id = source_attributes.getNamedItem("id").getTextContent();
		
		for( int i=0 ; i<source_data.getLength() ; i++ ){
			Node child_node = source_data.item(i);

			if( child_node.getNodeName().equals("input") ){
				NamedNodeMap child_attrs = child_node.getAttributes();
				String semantic = child_attrs.getNamedItem("semantic").getTextContent();
				String source = child_attrs.getNamedItem("source").getTextContent();
				sources.put(semantic, source);
			}
		}
	}
	
	public String getId(){
		return id;
	}

}
