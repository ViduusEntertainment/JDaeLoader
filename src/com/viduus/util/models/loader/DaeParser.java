package com.viduus.util.models.loader;

import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.ModelData;
import com.viduus.util.models.SceneNode;
import com.viduus.util.models.animations.Animation;
import com.viduus.util.models.controller.Controller;
import com.viduus.util.models.effects.Effect;
import com.viduus.util.models.geometries.Mesh;
import com.viduus.util.models.materials.Material;

/**
 * This class implements parsing the collada dae file format. 
 * 
 * @author Ethan Toney
 */
public class DaeParser {
	
	private final String model_name, version;
	private HashMap<String, Mesh> object_meshes;
	private HashMap<String, SceneNode> scene_transforms;
	private HashMap<String, Mesh> scene_meshes;
	private HashMap<String, Effect> object_effects;
	private HashMap<String, Material> object_materials;
	private HashMap<String, Animation> object_animations;
	private HashMap<String, Controller> object_controllers;

	/**
	 * Creates a new dae parser.
	 * 
	 * @param model_name - (String) Name of the model.
	 * @param version - (String) Dae version.
	 */
	public DaeParser( String model_name, String version ) {
		this.model_name = model_name;
		this.version = version;
	}

	/**
	 * Begin parsing a dae file.
	 * 
	 * @param curr_node - (Node) The root node of the dae file.
	 * @throws DaeParseException
	 */
	public void parse( Node curr_node ) throws DaeParseException {
		NodeList sections = curr_node.getChildNodes();
		
		/*
		 * Read in all the data
		 */
		for( int sec_id=0 ; sec_id<sections.getLength() ; sec_id++ ){
			// Get the current section
			Node curr_section = sections.item(sec_id);
			String section_name = curr_section.getNodeName();
			
			if( section_name.equals("asset") ){
				// TODO Not implemented
				
			}else if( section_name.equals("library_cameras") ){
				// TODO Not implemented
				
			}else if( section_name.equals("library_lights") ){
				// TODO Not implemented
				
			}else if( section_name.equals("library_images") ){
				// TODO Not implemented
				
			/*
			 * Holds all of the effects used in this object
			 */
			}else if( section_name.equals("library_effects") ){
				object_effects = loadEffects( curr_section );
			
			/*
			 * Holds all of the materials used in this object
			 */
			}else if( section_name.equals("library_materials") ){
				object_materials = loadMaterials( curr_section );
				
			/*
			 * Holds all of the mesh data
			 */
			}else if( section_name.equals("library_geometries") ){
				object_meshes = loadGeometries( curr_section );
				
			/*
			 * Holds all of the animation data
			 */
			}else if( section_name.equals("library_animations") ){
				object_animations = loadAnimations( curr_section );
				
			/*
			 * Holds all of the controller data
			 */
			}else if( section_name.equals("library_controllers") ){
				object_controllers = loadControllers( curr_section );
				
			/*
			 * Holds all of the default transformation matrices for the model
			 */
			}else if( section_name.equals("library_visual_scenes") ){
				scene_transforms = loadVisualScene( curr_section );
				
			}else if( section_name.equals("scene") ){
				// TODO Not implemented
				
				
			// If section is not recognized
			}else if( !section_name.equals("#text") ){
				OutputHandler.println("\tCould not parse section "+section_name+" in model "+model_name);
			}
			
		}
		
		if(object_effects != null) {
			for( Effect effect : object_effects.values() )
				effect.printData();
			}
		
		if(object_materials != null) {
			for( Material material : object_materials.values() )
				material.printData();
		}
		
		if(object_meshes != null) {
			for( Mesh mesh : object_meshes.values() )
				mesh.printData();
		}
		
		if(object_animations != null) {
			for( Animation animation : object_animations.values() )
				animation.printData();
		}
		
		if(object_controllers != null) {
			for( Controller controller : object_controllers.values() )
				controller.printData();
		}
		
		/*
		 * Apply transformations and attach everything to each other
		 */
//		scene_meshes = new HashMap<>();
//		for( String transform_key : scene_transforms.keySet() ){
//			SceneNode scene_tranform = scene_transforms.get(transform_key);
//			// Make sure that it has been loaded
//			if( object_meshes.containsKey( scene_tranform.reference_url ) ){
//				Mesh affected_model = object_meshes.get( scene_tranform.reference_url );
//				Mesh transformed_mesh = affected_model.setObjectMatrix( scene_tranform );
//				scene_meshes.put(scene_tranform.getName(), affected_model);
//				System.out.println("data "+scene_tranform.getName());
//			}
//		}
		
//		System.exit(0);
	}

	/**
	 * @param curr_section
	 * @return
	 * @throws DaeParseException 
	 */
	private HashMap<String, Controller> loadControllers(Node section) throws DaeParseException {
		HashMap<String, Controller> result = new HashMap<>();
		
		NodeList controllers = section.getChildNodes();
		for( int i=0 ; i<controllers.getLength() ; i++ ){
			// Get the current material
			Node curr_controller = controllers.item(i);
			String curr_name = curr_controller.getNodeName();
			
			if( curr_name.equals("controller") ){
				Controller controller = new Controller(curr_controller);
				result.put(controller.getId(), controller);
			}
		}
		
		return result;
	}

	/**
	 * @param curr_section
	 * @return
	 * @throws DaeParseException 
	 */
	private HashMap<String, Animation> loadAnimations(Node section) throws DaeParseException {
		HashMap<String, Animation> result = new HashMap<>();
		
		NodeList animations = section.getChildNodes();
		for( int i=0 ; i<animations.getLength() ; i++ ){
			// Get the current material
			Node curr_animation = animations.item(i);
			String curr_name = curr_animation.getNodeName();
			
			if( curr_name.equals("animation") ){
				Animation animation = new Animation(curr_animation);
				result.put(animation.getId(), animation);
			}
		}
		
		return result;
	}

	/**
	 * @param curr_section
	 * @return
	 * @throws DaeParseException 
	 */
	private HashMap<String, Material> loadMaterials(Node section) throws DaeParseException {
		HashMap<String, Material> result = new HashMap<>();
		
		NodeList materials = section.getChildNodes();
		for( int i=0 ; i<materials.getLength() ; i++ ){
			// Get the current material
			Node curr_material = materials.item(i);
			String curr_name = curr_material.getNodeName();
			
			if( curr_name.equals("material") ){
				Material material = new Material(curr_material);
				result.put(material.getId(), material);
			}
		}
		
		return result;
	}

	/**
	 * @param curr_section
	 * @return
	 * @throws DaeParseException 
	 */
	private HashMap<String, Effect> loadEffects(Node section) throws DaeParseException {
		HashMap<String, Effect> result = new HashMap<>();

		NodeList effects = section.getChildNodes();
		for( int i=0 ; i<effects.getLength() ; i++ ){
			// Get the current effect
			Node curr_effect = effects.item(i);
			String curr_name = curr_effect.getNodeName();
			
			if( curr_name.equals("effect") ){
				Effect effect = new Effect(curr_effect);
				result.put(effect.getId(), effect);
			}
		}
		
		return result;
	}

	/**
	 * Processes the 'library_visual_scenes' section of a dae file.
	 * 
	 * @param curr_section - (Node) The 'library_visual_scenes' node in the xml file.
	 */
	private HashMap<String, SceneNode> loadVisualScene(Node section) {
		HashMap<String, SceneNode> result = new HashMap<>();
		
		NodeList scenes = section.getChildNodes();
		for( int scene_id=0 ; scene_id<scenes.getLength() ; scene_id++ ){
			// Get the scene name
			Node curr_scene = scenes.item(scene_id);
			String scene_name = curr_scene.getNodeName();
			
			if( scene_name.equals("visual_scene") ){
				// Get the current scene's nodes
				NodeList nodes = curr_scene.getChildNodes();
				for( int node_id=0 ; node_id<nodes.getLength() ; node_id++ ){
					// Get node name
					Node curr_node = nodes.item(node_id);
					String node_name = curr_node.getNodeName();
					
					if( node_name.equals("node") ){
						// Get properties of the current Node
						NamedNodeMap node_attributes = curr_node.getAttributes();
						String type = node_attributes.getNamedItem("type").getTextContent();
						String id = node_attributes.getNamedItem("id").getTextContent();
						
						if( type.equals("NODE") ){
							SceneNode new_node = new SceneNode( curr_node );
							result.put(id, new_node);
						}
					}
				}
			}
			
		}
		
		return result;
	}

	/**
	 * Processes the 'library_geometries' section of a dae file.
	 * 
	 * @param section - (Node) The 'library_geometries' node in the xml file.
	 * @throws DaeParseException
	 */
	private HashMap<String, Mesh> loadGeometries(Node section) throws DaeParseException {
		HashMap<String, Mesh> result = new HashMap<>();
		
		NodeList geometries = section.getChildNodes();
		for( int geom_id=0 ; geom_id<geometries.getLength() ; geom_id++ ){
			// Get the current geometry
			Node curr_geometry = geometries.item(geom_id);
			String curr_name = curr_geometry.getNodeName();
			
			if( curr_name.equals("geometry") ){
				// Get properties of this geometry
				NamedNodeMap geom_attributes = curr_geometry.getAttributes();
				String geometry_id = geom_attributes.getNamedItem("id").getTextContent();
				String geometry_name = geom_attributes.getNamedItem("name").getTextContent();
				
				// Get children meshes
				NodeList meshes = curr_geometry.getChildNodes();
				for( int mesh_id=0 ; mesh_id<meshes.getLength() ; mesh_id++ ){
					// Get the current mesh
					Node curr_mesh = meshes.item(mesh_id);
					String mesh_name = curr_mesh.getNodeName();
					
					if( mesh_name.equals("mesh") ){
						Mesh new_mesh = new Mesh(geometry_id, geometry_name, curr_mesh);
						result.put(geometry_id, new_mesh);
					}
				}
			}
		}
		return result;
	}

	/**
	 * @return The processed model from this parser.
	 */
	public ModelData getModelData() {
		ModelData new_model = new ModelData( model_name, version, object_meshes );
		return new_model;
	}

}
