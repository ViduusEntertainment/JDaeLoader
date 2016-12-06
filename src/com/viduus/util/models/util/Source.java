/**
 * Created on Dec 1, 2016 by Ethan Toney
 */
package com.viduus.util.models.util;

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
public class Source {

	private final String id;
	private final DataArray<?> data;

	/**
	 * @param curr_source
	 * @throws DaeParseException 
	 */
	public Source(final Node curr_source) throws DaeParseException {
		if( !curr_source.getNodeName().equals("source") )
			throw new DaeParseException("Source constructor must take a <source> tag.");
		
		NamedNodeMap source_attributes = curr_source.getAttributes();
		NodeList source_data = curr_source.getChildNodes();
		
		id = source_attributes.getNamedItem("id").getTextContent();
		
		// --- Read in the DataArray ------------------------------------------
		// Used to index the nodes
		HashMap<String, Node> elements = new HashMap<>();
		
		// Properties that need to be set for DataArray
		int stride = 0;
		int count = 0;
		String data_type = "";
		
		// Map Nodes to HashMap
		for( int i=0 ; i<source_data.getLength() ; i++ ){
			Node curr_element = source_data.item(i);
			String element_name = curr_element.getNodeName();
			elements.put(element_name, curr_element);
		}
		
		// Check to make sure that all required Nodes exist
		if( !elements.containsKey("technique_common") || (!elements.containsKey("float_array") && !elements.containsKey("Name_array")) ){
			throw new DaeParseException("Could not parse input data into a DataArray");
		}
		
		// Get the stride and count from the technique_common Node
		Node technique_common = elements.get("technique_common");
		NodeList technique_children = technique_common.getChildNodes();
		for( int i=0 ; i<technique_children.getLength() ; i++ ){
			Node curr_node = technique_children.item(i);
			if( curr_node.getNodeName().equals("accessor") ){
				// Get attributes
				NamedNodeMap node_attrs = curr_node.getAttributes();
				stride = Integer.parseInt(node_attrs.getNamedItem("stride").getTextContent());
				count = Integer.parseInt(node_attrs.getNamedItem("count").getTextContent());
			}
		}
		
		// Check to make sure that both stride and count were found
		if( stride == -1 || count == -1  ){
			throw new DaeParseException("Did not find the sride or count field in technique_common");
		}
		
		// Read in the data into the float array
		if( elements.containsKey("float_array") ){
			if( stride == 16 ){ // FIXME replace with actual float4x4 check
				data = new Mat4Array( elements.get("float_array"), count, stride );
			}else{
				data = new FloatArray( elements.get("float_array"), count, stride );
			}
		}else if( elements.containsKey("Name_array") ){
			data = new NameArray( elements.get("Name_array"), count, stride );
		}else{
			throw new DaeParseException("Did not find a supported data array in the source tag.");
		}
	}

	/**
	 * TODO
	 * @return
	 */
	public String getId() {
		return id;
	}

}
