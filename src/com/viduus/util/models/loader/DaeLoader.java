package com.viduus.util.models.loader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.ModelData;

/**
 * This class is responsible for loading all of the Collada DAE files and for managing the memory
 * they are taking up.
 * @author Ethan Toney
 *
 */
public class DaeLoader {
	
	// Versions of Collada that this loader supports
	private static final String IMPLEMENTED_VERSIONS = "1.4.1";
	// HashMap of file paths to booleans where the boolean represents if the Model is actively in RAM
	private static final HashMap<String, ModelData> model_files = new HashMap<>();
	private DocumentBuilder db;
	
	/**
	 * Constructs a new DAE Collada file loader. Currently only has support for 1.4.1
	 * @throws ParserConfigurationException
	 */
	public DaeLoader() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		db = dbf.newDocumentBuilder(); 
	}
	
	/**
	 * Adds a 3D object file to be tracked but not loaded or processed. This should always
	 * be done when a 3D file is not being used so that RAM is used sparingly.
	 * @param in_file
	 * @see #closeModel(String)
	 */
	public void addModel( File in_file ){
		String identifier = in_file.getPath();
		if( !model_files.containsKey(identifier) ){
			OutputHandler.println("Added a new model file: "+identifier);
			model_files.put(identifier, null);
		}else{
			OutputHandler.println("Model file already loaded: "+identifier);
		}
	}
	
	/**
	 * Releases the memory being used by the given model.
	 * @param file_path
	 * @see #addModel(File)
	 * @see #loadModel(String)
	 */
	public void closeModel( String file_path ){
		if( model_files.containsKey(file_path) && model_files.get(file_path) != null ){
			model_files.put(file_path, null);
		}
	}
	
	/**
	 * Returns the Model for a given model file path.
	 * @param file_path
	 * @return Returns the model, returns <b>null</b> if the model is not currently loaded
	 * in memory.
	 */
	public ModelData getModel( String file_path ){
		if( model_files.containsKey(file_path) && model_files.get(file_path) != null ){
			return model_files.get(file_path);
		}
		return null;
	}
	
	/**
	 * Loads a perviously added file to RAM and returns the Model for this file.
	 * @param file_path - path to the file relative to the project location
	 * @return Will return the loaded Model, if an error occurs then will return <b>null</b>.
	 * @throws SAXException - Happens when a XML error happens
	 * @throws IOException - Could either happened because the file does not exist
	 * or the Collada file was corrupted
	 * @see #addModel(File)
	 */
	public ModelData loadModel( String file_path ) throws SAXException, IOException{
		OutputHandler out = new OutputHandler();
		out.startTimedPrintln("Loading model "+file_path+" into memory...");
		
		// Check to make sure that it has already been added
		if( !model_files.containsKey(file_path) ){
			Document doc = db.parse( new File(file_path) );
			
			NodeList list = doc.getChildNodes();
			for( int i=0 ; i<list.getLength() ; i++ ){
				Node curr_node = list.item(i);
				// If inside of the COLLADA tag
				if( curr_node.getNodeName().equals("COLLADA") ){
					// Attributes in the tag for COLLADA
					NamedNodeMap doc_attributes = curr_node.getAttributes();
					String collada_version = doc_attributes.getNamedItem("version").getTextContent();
					
					// Make sure that it is a valid version
					if( IMPLEMENTED_VERSIONS.contains(collada_version) ){
						DaeParser parser = new DaeParser(file_path, collada_version);
						parser.parse( curr_node );
						ModelData new_model = parser.getModelData();
						model_files.put(file_path, new_model);
						
						// FIXME - temporary and only used for testing
						new_model.printData();
						
						out.endTimedPrintln("Finihsed loading model "+file_path+" into memory");
						return model_files.get(file_path);
						
					// Not a valid version
					}else{
						OutputHandler.println("Could not load model "+file_path+" because it is either corrupted or is not an acceptable version.");
					}
				}else{
					OutputHandler.println("No COLLADA tag found.");
				}
			}
		}else{
			return model_files.get(file_path);
		}
		return null;
	}

}
