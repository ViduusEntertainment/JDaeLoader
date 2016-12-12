/**
 * Created on Dec 11, 2016 by Ethan Toney
 */
package com.viduus.util.models.visual_scene;

import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.loader.DaeParseException;
import com.viduus.util.models.util.LoaderFunctions;

/**
 * 
 *
 * @author ethan
 */
public class BindMaterial {

	public HashMap<String, String> instance_materials = new HashMap<>();
	
	public BindMaterial(Node bind_material) throws DaeParseException{
		if( !bind_material.getNodeName().equals("bind_material") )
			throw new DaeParseException("BindMaterial must take a <bind_material> tag.");
		
		NodeList children = bind_material.getChildNodes();
		
		for( int i=0 ; i<children.getLength() ; i++ ){
			Node child = children.item(i);
			String child_name = child.getNodeName();
			
			if( child_name.equals("technique_common") ){
				processTechniqueCommon(child);
				
			}else if( child_name.equals("technique") ){
				// TODO Implement <technique>
				
			}else if( child_name.equals("param") ){
				// TODO Implement <param>
				
			}else if( child_name.equals("extra") ){
				// TODO Implement <extra>
				
			}
		}
	}
	
	private void processTechniqueCommon(Node technique_common) throws DaeParseException{
		if( technique_common.getNodeName().equals("bind_material") )
			throw new DaeParseException("BindMaterial::processTechniqueCommon() must take a <technique_common> tag.");
		
		NodeList children = technique_common.getChildNodes();
		
		for( int i=0 ; i<children.getLength() ; i++ ){
			Node child = children.item(i);
			NamedNodeMap child_attributes = child.getAttributes();
			
			if( child.getNodeName().equals("instance_material") ){
				String symbol = LoaderFunctions.getAttributeFromMap(child_attributes, "symbol");
				String target = LoaderFunctions.getAttributeFromMap(child_attributes, "target").substring(1);
				if( symbol != null && target != null )
					instance_materials.put(symbol, target);
			}
			
		}
	}

	/**
	 * 
	 */
	public void printData() {
		for( String symbol : instance_materials.keySet() )
			OutputHandler.println("InstanceMaterials[symbol:"+symbol+", target:"+instance_materials.get(symbol)+"]");
	}
	
}
