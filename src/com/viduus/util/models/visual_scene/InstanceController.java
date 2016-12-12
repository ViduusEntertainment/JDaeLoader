/**
 * Created on Dec 9, 2016 by Ethan Toney
 */
package com.viduus.util.models.visual_scene;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.loader.DaeParseException;

/**
 * 
 *
 * @author ethan
 */
public class InstanceController extends Instance {

	public String skeleton_id;
	public BindMaterial bind_material;
	
	public InstanceController(Node instance_controller) throws DaeParseException{
		if( !instance_controller.getNodeName().equals("instance_controller") )
			throw new DaeParseException("InstanceController must take a <instance_controller> tag.");
		
		NamedNodeMap attributes = instance_controller.getAttributes(); 
		reference_url = attributes.getNamedItem("url").getTextContent().substring(1);
		
		NodeList children = instance_controller.getChildNodes();
		for( int i=0 ; i<children.getLength() ; i++ ){
			Node child = children.item(i);
			String child_name = child.getNodeName();
			
			if( child_name.equals("skeleton") ){
				skeleton_id = child.getTextContent().substring(1);
				
			}else if( child_name.equals("bind_material") ){
				bind_material = new BindMaterial(child);
				
			}else if( child_name.equals("extra") ){
				// TODO Implement <extra>
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.viduus.util.models.visual_scene.Instance#printData()
	 */
	@Override
	public void printData() {
		OutputHandler.println("InstanceController[ref_url:"+reference_url+", skeleton_id:"+skeleton_id+"]");
		OutputHandler.addTab();
		if( bind_material != null )
			bind_material.printData();
		OutputHandler.removeTab();
	}
	
}
