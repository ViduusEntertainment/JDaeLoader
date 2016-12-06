/**
 * Created on Dec 5, 2016 by Ethan Toney
 */
package com.viduus.util.models.util;

import org.w3c.dom.Node;

import com.viduus.util.models.loader.DaeParseException;

/**
 *
 *
 * @author Ethan Toney
 */
public class NameArray extends DataArray<String> {

	/**
	 * Creates a new NameArray by reading the given XML and looking for a Name_array
	 * tag.
	 * @param source_data - (NodeList) The XML node of the Name array.
	 * @throws DaeParseException Thrown if there was a parsing error.
	 */
	public NameArray( Node name_array, int count, int stride ) throws DaeParseException {
		super( count, stride );

		// Load in the data
		data = name_array.getTextContent().split(" ");
	}

}
