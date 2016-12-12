/**
 * Created on Dec 9, 2016 by Ethan Toney
 */
package com.viduus.util.models.util;

import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 *
 * @author ethan
 */
public class LoaderFunctions {

	public static HashMap<String, HashMap<String, Node>> loadAllChildNodes(Node parent){
		HashMap<String, HashMap<String, Node>> child_nodes = new HashMap<>();
		
		NodeList children = parent.getChildNodes();
		for(int i=0 ; i<children.getLength() ; i++){
			Node child = children.item(i);
			String name = child.getNodeName();
			if( name.equals("#text") )
				continue;
			
			NamedNodeMap attributes = child.getAttributes();
			
			// get element id
			Node temp = null;
			String id = null;
			if( id == null && (temp = attributes.getNamedItem("id")) != null )
				id = temp.getTextContent();
			if( id == null && (temp = attributes.getNamedItem("sid")) != null )
				id = temp.getTextContent();
			
			// check that array list exists
			if( !child_nodes.containsKey(name) )
				child_nodes.put(name, new HashMap<>(1));

			// add element
			if( id != null ){
				child_nodes.get(name).put(id, child);
			}else{
				child_nodes.get(name).put(name+"#"+child_nodes.get(name).size(), child);
			}
				
		}
		
		return child_nodes;
	}
	
	public static String getAttributeFromMap( NamedNodeMap attributes, String identifier ){
		Node attribute_node = attributes.getNamedItem(identifier);
		return ( attribute_node == null ) ? null : attribute_node.getTextContent();
	}
	
}
