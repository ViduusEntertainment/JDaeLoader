/**
 * Created on Dec 1, 2016 by Ethan Toney
 */
package com.viduus.util.models.effects;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.geometries.Mesh;
import com.viduus.util.models.loader.DaeParseException;
import com.viduus.util.models.util.Color;

/**
 * 
 *
 * @author Ethan Toney
 */
public class Effect {

	private final String id;
	private LightingModel lighting_model;

	/**
	 * @param curr_effect
	 * @throws DaeParseException 
	 */
	public Effect(Node source_node) throws DaeParseException {
		// Get properties of this effect
		NamedNodeMap effect_attributes = source_node.getAttributes();
		id = effect_attributes.getNamedItem("id").getTextContent();

		NodeList source_data = source_node.getChildNodes();
		for( int i=0 ; i<source_data.getLength() ; i++ ){
			Node profile_node = source_data.item(i);
			NodeList profile_data = profile_node.getChildNodes();
			String profile_name = profile_node.getNodeName();
			
			// Make sure that the profile is supported
			if( profile_name.equals("profile_COMMON") ){
				for( int j=0 ; j<profile_data.getLength() ; j++ ){
					Node technique_node = profile_data.item(j);
					NodeList technique_data = technique_node.getChildNodes();
					NamedNodeMap technique_attributes = technique_node.getAttributes();
					String technique_node_name = technique_node.getNodeName();
					
					if( technique_node_name.equals("technique") ){
						String technique_name = technique_attributes.getNamedItem("sid").getTextContent();
						
						// Check that the technique is supported
						if( technique_name.equals("common") ){
							for( int k=0 ; k<technique_data.getLength() ; k++ ){
								Node type_node = technique_data.item(k);
								String type_node_name = type_node.getNodeName();
								
								// Check that the type of technique is supported
								if( type_node_name == "phong" ){
									lighting_model = new PhongModel( type_node );
									
								}else if( !type_node_name.equals("#text") ){
									throw new DaeParseException("Effect technique:'"+technique_name+"' type:'"+type_node_name+"' is not supported!");
								}
							}
						}else if( !technique_name.equals("#text") ){
							throw new DaeParseException("Effect technique '"+technique_name+"' is not supported!");
						}
					}else if( !technique_node_name.equals("#text") ){
						throw new DaeParseException("Effect profile:'"+profile_name+"' tag:'"+profile_name+"' is not supported!");
					}
				}
			}else if( !profile_name.equals("#text") ){
				throw new DaeParseException("Unsupported effect profile '"+profile_name+"'!");
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
	 * FIXME - Should be removed before release
	 * <b>ONLY FOR TESTING PURPOSES</b>
	 */
	public void printData() {
		OutputHandler.println("Effect[id:'"+id+"']");
		OutputHandler.addTab();
		lighting_model.printData();
		OutputHandler.removeTab();
	}

}
