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
import com.viduus.util.models.SceneNode;
import com.viduus.util.models.loader.DaeParseException;
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
	private final HashMap<String, Source> sources = new HashMap<>();
	private final HashMap<String, Verticies> verticies = new HashMap<>();
	private final List<Polylist> polylists = new ArrayList<>();
	private final String id, name;
	
	private SceneNode object_matrix;
	public float[] gpu_buffer = null;
	public final int ELEMENTS_PER_VERTEX = 8;
	public short[] ibo_buffer = null;
	
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
					polylists.add(new Polylist(curr_source));
				}
			}
		}
		
		// Combine the normals and vertices
		generateGpuBuffer();
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
		OutputHandler.removeTab();
	}
	
	private void generateGpuBuffer(){
		// Combine the normals and vertices
		int gpu_index = 0;
		
	    int gpu_buffer_count = 0, ibo_buffer_counter = 0;
	    for(int i = 0; i < polylists.size(); i++) {
	    	Polylist faces = polylists.get(i);
	    	gpu_buffer_count += faces.vcount.length*3*ELEMENTS_PER_VERTEX;
	    	ibo_buffer_counter += faces.vcount.length*3;
	    }
	    
		gpu_buffer = new float[ gpu_buffer_count ];
		ibo_buffer = new short[ ibo_buffer_counter ];
		for( short i=0 ; i<ibo_buffer.length ; i++ )
			ibo_buffer[i] = i;
		
		for(int k = 0; k < polylists.size(); k++) {
			
			int index = 0;
		    Polylist faces = polylists.get(k);
		    FloatArray vertexes = (FloatArray) sources.get(verticies.get(faces.sources.get("VERTEX").substring(1)).sources.get("POSITION").substring(1)).data;
		    FloatArray normals = (FloatArray) sources.get(faces.sources.get("NORMAL").substring(1)).data;
		
			for( byte verts : faces.vcount ){
				
				short[] vert_i = new short[verts];
				short[] norm_i = new short[verts];
				short[] text_i = new short[verts];
				for( int i=0 ; i<verts ; i++ ){
					vert_i[i] = faces.v_indexes[index++];
					norm_i[i] = faces.v_indexes[index++];
					text_i[i] = faces.v_indexes[index++];
				}
				
				float[][] points = new float[verts][3];
				float[][] norms = new float[verts][3];
					
				for( int i=0 ; i<verts ; i++ ){
					for( int j=0 ; j<vertexes.stride ; j++ ){
						points[i][j] = vertexes.data[vert_i[i]*vertexes.stride+j];
						norms[i][j] = normals.data[norm_i[i]*normals.stride+j];
					}
				}
				
				for( int i=0 ; i<verts ; i++ ){
					gpu_buffer[ gpu_index*ELEMENTS_PER_VERTEX ] = points[i][0]; // x
					gpu_buffer[ gpu_index*ELEMENTS_PER_VERTEX + 1 ] = points[i][1]; // y
					gpu_buffer[ gpu_index*ELEMENTS_PER_VERTEX + 2 ] = points[i][2]; // z
					gpu_buffer[ gpu_index*ELEMENTS_PER_VERTEX + 3 ] = norms[i][0]; // nx
					gpu_buffer[ gpu_index*ELEMENTS_PER_VERTEX + 4 ] = norms[i][1]; // ny
					gpu_buffer[ gpu_index*ELEMENTS_PER_VERTEX + 5 ] = norms[i][2]; // nz
					gpu_buffer[ gpu_index*ELEMENTS_PER_VERTEX + 6 ] = 0.0f; // t
					gpu_buffer[ gpu_index*ELEMENTS_PER_VERTEX + 7 ] = 0.0f; // s
					gpu_index++;
				}
			}
		}
	}

//	/**
//	 * Apply the meshes transformation matrix so that it is relative to the
//	 * object.
//	 * 
//	 * @param scene_transform - (SceneNode) The transformation to get this mesh
//	 * in the correct location relative to the object.
//	 * @return The transformed mesh.
//	 */
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
