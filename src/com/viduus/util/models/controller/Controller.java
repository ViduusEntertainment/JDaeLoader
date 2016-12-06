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
	
	private final String id;
	private final String name;
	private Skin skin = null;
	
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

}
