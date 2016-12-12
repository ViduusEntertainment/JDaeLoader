/**
 * Created on Dec 1, 2016 by Ethan Toney
 */
package com.viduus.util.models.effects;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.loader.DaeParseException;
import com.viduus.util.models.util.Color;

/**
 * 
 *
 * @author Ethan Toney
 */
public class PhongModel extends LightingModel{

	public Color emission, ambient, diffuse, specular, reflective, transparent;
	public float shininess, reflectivity, transparency, index_of_refraction;
	
	/**
	 * @param type_node
	 * @throws DaeParseException 
	 */
	public PhongModel(Node source_node) throws DaeParseException {
		if( !source_node.getNodeName().equals("phong") )
			throw new DaeParseException("PhongModel constructor must be passed a <phong> tag.");
		
		NodeList source_data = source_node.getChildNodes();
		for( int i=0 ; i<source_data.getLength() ; i++ ){
			Node data_type_node = source_data.item(i);
			String data_type = data_type_node.getNodeName();
			NodeList children = data_type_node.getChildNodes();
			Node child_node = null;
			for( int j=0 ; j<children.getLength() ; j++ )
				if( children.item(j).getNodeName() != "#text" )
					child_node = children.item(j);
			
			if( data_type == "emission" ){
				emission = new Color(child_node);
			}else if( data_type == "ambient" ){
				ambient = new Color(child_node);
			}else if( data_type == "diffuse" ){
				diffuse = new Color(child_node);
			}else if( data_type == "specular" ){
				specular = new Color(child_node);
			}else if( data_type == "shininess" ){
				shininess = Float.parseFloat(child_node.getTextContent());
			}else if( data_type == "reflective" ){
				reflective = new Color(child_node);
			}else if( data_type == "reflectivity" ){
				reflectivity = Float.parseFloat(child_node.getTextContent());
			}else if( data_type == "transparent" ){
				transparent = new Color(child_node);
			}else if( data_type == "transparency" ){
				transparency = Float.parseFloat(child_node.getTextContent());
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.viduus.util.models.effects.LightingModel#printData()
	 */
	@Override
	public void printData() {
		OutputHandler.println("PhongModel[emission:"+emission+", ambient:"+ambient+
				", diffuse:"+diffuse+", specular:"+specular+", shininess:"+shininess+
				", reflective:"+reflective+", reflectivity:"+reflectivity+", transparent:"+transparent+
				", transparency:"+transparency+"]");
	}

}
