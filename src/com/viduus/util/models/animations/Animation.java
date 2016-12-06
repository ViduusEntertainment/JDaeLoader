/**
 * Created on Dec 1, 2016 by Ethan Toney
 */
package com.viduus.util.models.animations;

import java.util.HashMap;

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
public class Animation {

	public final String id;
	public final HashMap<String, Source> sources = new HashMap<>();
	public final HashMap<String, String> sampler = new HashMap<>();
	public String target_url;

	/**
	 * @param curr_animation
	 * @throws DaeParseException 
	 */
	public Animation(Node source_node) throws DaeParseException {
		if( !source_node.getNodeName().equals("animation") )
			throw new DaeParseException("Animation constructor must take a <animation> tag.");

		// Get properties of this effect
		NamedNodeMap effect_attributes = source_node.getAttributes();
		id = effect_attributes.getNamedItem("id").getTextContent();
		
		NodeList children = source_node.getChildNodes();
		for( int i=0 ; i<children.getLength() ; i++ ){
			Node child = children.item(i);
			String child_name = child.getNodeName();
			
			if( child_name.equals("source") ){
				Source source = new Source(child);
				sources.put(source.getId(), source);
				
			}else if( child_name.equals("sampler") ){
				NodeList sampler_elements = child.getChildNodes();
				for( int j=0 ; j<sampler_elements.getLength() ; j++ ){
					Node samplet_element = sampler_elements.item(j);
					if( samplet_element.getNodeName().equals("input") ){
						NamedNodeMap sampler_attributes = samplet_element.getAttributes();
						String semantic = sampler_attributes.getNamedItem("semantic").getTextContent();
						String source = sampler_attributes.getNamedItem("source").getTextContent().substring(1);
						sampler.put(semantic, source);
					}
				}
				
			}else if( child_name.equals("channel") ){
				NamedNodeMap channel_attributes = child.getAttributes();
				target_url = channel_attributes.getNamedItem("target").getTextContent();
				
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

	/**
	 * 
	 */
	public void printData() {
		OutputHandler.println("Animation[id:'"+id+"', target:'"+target_url+", #sources:"+sources.size()+", #samplers:"+sampler.size()+"']");
	}
	
}
