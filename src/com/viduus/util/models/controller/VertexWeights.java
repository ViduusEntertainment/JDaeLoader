package com.viduus.util.models.controller;

import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.loader.DaeParseException;

/**
 * This class loads and holds all of the face data from collada file.
 * @author Ethan Toney
 *
 */
public class VertexWeights {
	
	/**
	 * Number of vertices in a face. This should be constant
	 */
	public final byte[] vcount;
	
	/**
	 *  Array of indexes that point to the vertex array
	 */
	public final short[] v_indexes;
	
	/**
	 *  Source id for vertex
	 */
	public final HashMap<String, String> sources = new HashMap<>();

	/**
	 * Constructs and loads the Polyface data with a given polylist xml tag Node
	 * 
	 * @param source_data - (NodeList) XML polylist Node's children
	 * @param count - (int) Value of 'count' field on the XML polylist Node
	 * @throws DaeParseException 
	 * @see Node
	 */
	public VertexWeights(Node source_node) throws DaeParseException {
		if( !source_node.getNodeName().equals("vertex_weights") )
			throw new DaeParseException("VertexWeights constructor must be run on a <vertex_weights> tag.");
		
		NodeList source_data = source_node.getChildNodes();
		NamedNodeMap source_attributes = source_node.getAttributes();
		
		int count = Integer.parseInt(source_attributes.getNamedItem("count").getTextContent());

		HashMap<String, Node> elements = new HashMap<>();
		
		vcount = new byte[count];
		
		// Index them into elements and parse input fields
		for( int i=0 ; i<source_data.getLength() ; i++ ){
			Node curr_node = source_data.item(i);
			String node_name = curr_node.getNodeName();
			
			// Get properties of this source
			NamedNodeMap node_attrs = curr_node.getAttributes();
			
			// Check to see if it is a input field
			if( node_name.equals("input") ){
				String semantic = node_attrs.getNamedItem("semantic").getTextContent();
				String source = node_attrs.getNamedItem("source").getTextContent();
				sources.put(semantic, source);
			
			// Happens for vcount and v
			}else{
				elements.put(node_name, curr_node);
			}
		}
		
		// Load in vertex count array
		Node vcount_node = elements.get("vcount");
		String[] nums = vcount_node.getTextContent().split(" ");
		int vert_count = 0;
		for( int i=0 ; i<count ; i++ ){
			vcount[i] = Byte.parseByte(nums[i]);
			vert_count += vcount[i];
		}
		
		// Load in vertex index array
		int numb_inputs = sources.size();
		v_indexes = new short[vert_count*numb_inputs];
		Node index_node = elements.get("v");
		String[] index_nums = index_node.getTextContent().split(" ");
		for( int i=0 ; i<vert_count*numb_inputs ; i++ ){
			v_indexes[i] = Short.parseShort(index_nums[i]);
		}
	}

	/**
	 * FIXME - Should be removed before release
	 * <b>ONLY FOR TESTING PURPOSES</b>
	 */
	public void printData() {
		OutputHandler.println("VertexWeights:[count:'"+v_indexes.length+"', joint_source:'"+sources.get("JOINT")+"', weight_source:'"+sources.get("WEIGHT")+"']");
	}

}
