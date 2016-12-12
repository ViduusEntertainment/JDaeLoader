/**
 * Created on Nov 30, 2016 by Ethan Toney
 */
package com.viduus.util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.viduus.util.models.loader.DaeLoader;

/**
 *
 *
 * @author Ethan Toney
 */
public class Tester {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
		DaeLoader loader = new DaeLoader();
		loader.loadModel("./models/Goofy.dae");
//		loader.loadModel("./models/BasketballHoop.dae");
	}

}
