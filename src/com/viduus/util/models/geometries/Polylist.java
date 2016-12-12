package com.viduus.util.models.geometries;

import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.loader.DaeParseException;
import com.viduus.util.models.util.FloatArray;
import com.viduus.util.models.util.LoaderFunctions;

/**
 * This class loads and holds all of the face data from collada file.
 * @author Ethan Toney
 *
 */
public class Polylist {
	
	private final Mesh mesh;
	
	/**
	 * Number of vertices in a face. This should be constant
	 */
	public final byte[] vcount;
	
	/**
	 *  Array of indexes that point to the vertex array
	 */
	public final short[] v_indexes;
	
	/**
	 *  Source id for vertex
	 */
	public final HashMap<String, String> sources = new HashMap<>();
	
	/**
	 * This polylist's GPU (Graphics Processing Unit) buffer
	 */
	public float[] gpu_buffer = null;
	
	/**
	 * This polylist's IBO (Index Buffer Object) buffer
	 */
	public short[] ibo_buffer = null;
	
	/**
	 * The number of elements per vertex.  Will be 0 until "getGPUBuffer" is called
	 */
	public int elements_per_vertex = 0;

	public String material_symbol;

	/**
	 * Constructs and loads the Polyface data with a given polylist xml tag Node
	 * 
	 * @param source_data - (NodeList) XML polylist Node's children
	 * @param count - (int) Value of 'count' field on the XML polylist Node
	 * @throws DaeParseException 
	 * @see Node
	 */
	public Polylist(Node source_node, Mesh mesh) throws DaeParseException {
		if( !source_node.getNodeName().equals("polylist") )
			throw new DaeParseException("Polylist constructor must be run on a <polylist> tag.");
		
		this.mesh = mesh;
		
		NodeList source_data = source_node.getChildNodes();
		NamedNodeMap source_attributes = source_node.getAttributes();
		
		int count = Integer.parseInt(source_attributes.getNamedItem("count").getTextContent());
		material_symbol = LoaderFunctions.getAttributeFromMap(source_attributes, "material");

		HashMap<String, Node> elements = new HashMap<>();
		
		vcount = new byte[count];
		
		// Index them into elements and parse input fields
		for( int i=0 ; i<source_data.getLength() ; i++ ){
			Node curr_node = source_data.item(i);
			String node_name = curr_node.getNodeName();
			
			// Get properties of this source
			NamedNodeMap node_attrs = curr_node.getAttributes();
			
			// Check to see if it is a input field
			if( node_name.equals("input") ){
				String semantic = node_attrs.getNamedItem("semantic").getTextContent();
				String source = node_attrs.getNamedItem("source").getTextContent();
				sources.put(semantic, source);
			
			// Happens for vcount and p
			}else{
				elements.put(node_name, curr_node);
			}
		}
		
		// Load in vertex count array
		Node vcount_node = elements.get("vcount");
		String[] nums = vcount_node.getTextContent().split(" ");
		int vert_count = 0;
		for( int i=0 ; i<count ; i++ ){
			vcount[i] = Byte.parseByte(nums[i]);
			vert_count += vcount[i];
		}
		
		// Load in vertex index array
		int numb_inputs = sources.size();
		v_indexes = new short[vert_count*numb_inputs];
		Node index_node = elements.get("p");
		String[] index_nums = index_node.getTextContent().split(" ");
		for( int i=0 ; i<vert_count*numb_inputs ; i++ ){
			v_indexes[i] = Short.parseShort(index_nums[i]);
		}
	}

	/**
	 * FIXME - Should be removed before release
	 * <b>ONLY FOR TESTING PURPOSES</b>
	 */
	public void printData() {
		OutputHandler.println("Polyface:[material_symbol:"+material_symbol+", count:'"+v_indexes.length+"', vertex_source:'"+sources.get("VERTEX")+"', normal_source:'"+sources.get("NORMAL")+"', texture_source:'"+sources.get("TEXCOORD")+"']");
	}
	
	public short[] getIBOBuffer() {
		if(ibo_buffer != null)
			return ibo_buffer;
		
		ibo_buffer = new short[ this.vcount.length * 3 ];
		for( short i = 0 ; i < ibo_buffer.length ; i++ )
			ibo_buffer[i] = i;
		
		return ibo_buffer;
	}

	public float[] getGPUBuffer() {
		if(gpu_buffer == null)
			throw new RuntimeException("Must generate GPU buffer before getting the GPU buffer.");
		
		return gpu_buffer;
	}

	public void generateGpuBuffer(int max_bones_per_vertex, float[] joint_buffer) {
		int gpu_index = 0, index = 0;

	    FloatArray vertexes = this.sources.containsKey("VERTEX") ? (FloatArray) mesh.sources.get(mesh.verticies.get(this.sources.get("VERTEX").substring(1)).sources.get("POSITION").substring(1)).array : null;
	    FloatArray normals = this.sources.containsKey("NORMAL") ? (FloatArray) mesh.sources.get(this.sources.get("NORMAL").substring(1)).array : null;
	    FloatArray textures = this.sources.containsKey("TEXCOORD") ? (FloatArray) mesh.sources.get(this.sources.get("TEXCOORD").substring(1)).array : null;
	
	    elements_per_vertex = (vertexes != null ? vertexes.stride : 0) + 
					    	   (normals != null ? normals.stride : 0) + 
					    	   (textures != null ? textures.stride : 0) + 
					    	   2 * max_bones_per_vertex;
	    
		gpu_buffer = new float[ this.vcount.length * 3 * elements_per_vertex ];
	    
		for( byte verts : this.vcount ){
			
			short[] vert_i = new short[verts];
			short[] norm_i = new short[verts];
			short[] text_i = new short[verts];
			for( int i = 0 ; i < verts ; i++ ){
				if(vertexes != null)
					vert_i[i] = this.v_indexes[index++];
				if(normals != null)
					norm_i[i] = this.v_indexes[index++];
				if(textures != null)
					text_i[i] = this.v_indexes[index++];
			}
			
			for( int i = 0; i < verts; i++ ){
				int offset = -1;
				
				if(vertexes != null) {
					for( int j = 0 ; j < vertexes.stride; j++ )
						gpu_buffer[ gpu_index * elements_per_vertex + (++offset)] = vertexes.data[vert_i[i] * vertexes.stride + j];
				}
				
				if(normals != null) {
					for( int j = 0 ; j < normals.stride; j++ )
						gpu_buffer[ gpu_index * elements_per_vertex + (++offset)] = normals.data[norm_i[i] * normals.stride + j];
				}
				
				if(textures != null) {
					for( int j = 0 ; j < textures.stride; j++ )
						gpu_buffer[ gpu_index * elements_per_vertex + (++offset)] = textures.data[norm_i[i] * textures.stride + j];
				}
				
				if(joint_buffer != null) {
					for( int j = 0 ; j < max_bones_per_vertex*2 ; j++ )
						gpu_buffer[ gpu_index * elements_per_vertex + (++offset)] = joint_buffer[ 2 * max_bones_per_vertex * vert_i[i] + j ];
				}
				
				gpu_index++;
			}
		}
	}
}
