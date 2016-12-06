package com.viduus.util.models;

import java.util.HashMap;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.geometries.Mesh;

/**
 * Model class used to represent 3D models and their animations. This class is the output from the DaeLoader class.
 * 
 * @author Ethan Toney
 * @see com.viduus.util.models.loader.DaeLoader
 */
public class Model{

	private final String name, dae_version;
	private final HashMap<String, Mesh> mesh;
	
	/**
	 * Creates a new Model.
	 * @param model_name - The identifier for this model
	 * @param version - Collada version
	 * @param object_meshes - The mesh of vertices, normals, and texture points
	 */
	public Model(String model_name, String version, HashMap<String, Mesh> object_meshes) {
		name = model_name;
		dae_version = version;
		mesh = object_meshes;
	}

	/**
	 * FIXME - Should be removed before release
	 * <b>ONLY FOR TESTING PURPOSES</b>
	 */
	public void printData() {
		OutputHandler.println("Model data:[name:'"+name+"', version:'"+dae_version+"', mesh_pointer:'"+mesh+"']");
		OutputHandler.addTab();
		for( Mesh m : mesh.values() ){
			m.printData();
		}
		OutputHandler.removeTab();
	}

}
