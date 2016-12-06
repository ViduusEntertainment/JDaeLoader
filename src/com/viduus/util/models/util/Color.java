/**
 * Created on Dec 1, 2016 by Ethan Toney
 */
package com.viduus.util.models.util;

import org.w3c.dom.Node;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.loader.DaeParseException;

/**
 *
 *
 * @author Ethan Toney
 */
public class Color {

	public final float r, g, b, a;

	/**
	 * @param child_node
	 * @throws DaeParseException
	 */
	public Color(Node child_node) throws DaeParseException {
		if( !child_node.getNodeName().equals("color") )
			throw new DaeParseException("Color constructor must be passed a <color> tag.");

		String[] numbers = child_node.getFirstChild().getTextContent().split(" ");

		r = Float.parseFloat(numbers[0]);
		g = Float.parseFloat(numbers[1]);
		b = Float.parseFloat(numbers[2]);
		a = Float.parseFloat(numbers[3]);
	}

	@Override
	public String toString(){
		return "Color[r:"+r+", g:"+g+", b:"+b+", a:"+a+"] @ " + super.toString();
	}

}
