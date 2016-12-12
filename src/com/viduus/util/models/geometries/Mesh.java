package com.viduus.util.models.geometries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.math.Mat3;
import com.viduus.util.math.Mat4;
import com.viduus.util.models.loader.DaeParseException;
import com.viduus.util.models.materials.Material;
import com.viduus.util.models.util.FloatArray;
import com.viduus.util.models.util.Source;

/**
 * A object mesh is a the set of points that make up the body of the model.
 * These can change over time depending on the object.
 * 
 * @author Ethan Toney
 */
public class Mesh{

	// HashMap of ids to FloatArrays
	public final HashMap<String, Source> sources = new HashMap<>();
	public final HashMap<String, Verticies> verticies = new HashMap<>();
	public final List<Polylist> polylists = new ArrayList<>();
	public final String id, name;
	
	public final HashMap<String, Material> materials = new HashMap<>();
	
	/**
	 * Creates a new mesh off of a mesh xml tag.
	 * @param geometry_name 
	 * @param geometry_id 
	 * 
	 * @param curr_mesh - The mesh tag
	 * @throws DaeParseException
	 */
	public Mesh(String geometry_id, String geometry_name, Node curr_mesh) throws DaeParseException{
		if( !curr_mesh.getNodeName().equals("mesh") )
			throw new DaeParseException("Mesh constructor must take a <mesh> tag.");
		
		id = geometry_id;
		name = geometry_name;
		
		// Get children data sources
		NodeList data_sources = curr_mesh.getChildNodes();
		
		for( int src_id=0 ; src_id<data_sources.getLength() ; src_id++ ){
			// Get the current source
			Node curr_source = data_sources.item(src_id);
			String source_name = curr_source.getNodeName();
			
			if( !source_name.equals("#text") ){
				
				// Load in FloatArrays
				if( source_name.equals("source") ){
					Source source_node = new Source(curr_source);
					sources.put(source_node.getId(), source_node);
					
				// Load in vertex pointer
				}else if( source_name.equals("vertices") ){
					Verticies verticies_node = new Verticies(curr_source);
					verticies.put(verticies_node.getId(), verticies_node);
					
				// Load in all of the faces
				}else if( source_name.equals("polylist") ){
					polylists.add(new Polylist(curr_source, this));
				}
			}
		}
	}
	
	public String getName() {
		return this.name;
	}

	/**
	 * FIXME - Should be removed before release
	 * <b>ONLY FOR TESTING PURPOSES</b>
	 */
	public void printData() {
		OutputHandler.println("Mesh[id:'"+id+"', name:'"+name+"']");
		OutputHandler.addTab();
		for( String key : verticies.keySet() ){
			OutputHandler.println("Verticies:[name:'"+key+"', data:'"+verticies.get(key).sources.get("POSITION")+"']");
		}
		for( String key : sources.keySet() ){
			OutputHandler.println("Source:[name:'"+key+"', data:'"+sources.get(key).toString()+"']");
		}
		for( Polylist polylist : polylists ){
			polylist.printData();
		}
		for( Material material : materials.values() ){
			material.printData();
		}
		OutputHandler.removeTab();
	}

	/**
	 * @param joint_buffers
	 */
	public void setJointBuffer(int[] max_joints_per_vert, float[][] joint_buffers) {
		// generate polylist's vbos.
		for( Polylist polylist : polylists ){
			polylist.generateGpuBuffer(max_joints_per_vert[0], joint_buffers[0]);
		}
	}

	/**
	 * @param joint_buffers
	 */
	public void setJointBuffer() {
		// generate polylist's vbos.
		for( Polylist polylist : polylists ){
			polylist.generateGpuBuffer(0, null);
		}
	}

	/**
	 * @param symbol
	 * @param material
	 */
	public void addMaterial(String symbol, Material material) {
		materials.put(symbol, material);
	}

	/**
	 * Apply the meshes transformation matrix so that it is relative to the
	 * object.
	 * 
	 * @param scene_transform - (SceneNode) The transformation to get this mesh
	 * in the correct location relative to the object.
	 * @return The transformed mesh.
	 */
//	public Mesh setObjectMatrix(SceneNode scene_transform) {
//		object_matrix = scene_transform;
//		
//		Mesh result = new Mesh(this);
//		
//		FloatArray vertexes = result.data_sets.get(vertex_source.substring(1));
//		FloatArray normals = result.data_sets.get(faces.normal_source.substring(1));
//		
//		Mat4 model_matrix = new Mat4( object_matrix.getMatrix().array() );
//		Mat3 normal_matrix = new Mat3( model_matrix ).inverse().transpose();
//		
//		vertexes.multMat4f( object_matrix.getMatrix() );
//		normals.multMat3( normal_matrix );
//		
//		return result;
//	}

}
