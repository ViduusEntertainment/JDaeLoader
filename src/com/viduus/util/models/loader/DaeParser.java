package com.viduus.util.models.loader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.Bone;
import com.viduus.util.models.ModelData;
import com.viduus.util.models.animations.Animation;
import com.viduus.util.models.controller.Controller;
import com.viduus.util.models.effects.Effect;
import com.viduus.util.models.geometries.Mesh;
import com.viduus.util.models.materials.Material;
import com.viduus.util.models.visual_scene.Instance;
import com.viduus.util.models.visual_scene.InstanceController;
import com.viduus.util.models.visual_scene.InstanceGeometry;
import com.viduus.util.models.visual_scene.SceneNode;

/**
 * This class implements parsing the collada dae file format. 
 * 
 * @author Ethan Toney
 */
public class DaeParser {
	
	/*
	 * Variables used that are loaded verbatim form DAE file
	 */
	private final String model_name, version;
	private HashMap<String, Mesh> object_meshes;
	private HashMap<String, SceneNode> object_visual_scenes;
	private HashMap<String, Effect> object_effects;
	private HashMap<String, Material> object_materials;
	private HashMap<String, Animation> object_animations;
	private HashMap<String, Controller> object_controllers;

	/*
	 * Variables that are created using the loaded data.
	 */
	private HashMap<String, Bone> model_bones;
	private HashMap<String, Mesh> model_meshes = new HashMap<>();
	
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
				object_visual_scenes = loadVisualScene( curr_section );
				
			}else if( section_name.equals("scene") ){
				// TODO Not implemented
				
				
			// If section is not recognized
			}else if( !section_name.equals("#text") ){
				OutputHandler.println("\tCould not parse section "+section_name+" in model "+model_name);
			}
			
		}
		
//		printLoadingInfo();

		applyEffects();
		applyVisualScene();
		
		printProcessedInfo();
	}

	/**
	 * 
	 */
	private void applyEffects() {
		for( Material material : object_materials.values() ){
			material.setEffect( object_effects.get(material.effect_url) );
		}
	}

	/**
	 * 
	 */
	private void printProcessedInfo() {
		if(model_bones != null) {
			for( Bone bone : model_bones.values() )
				bone.printData();
		}
		
		if(model_meshes != null) {
			for( Mesh mesh : model_meshes.values() )
				mesh.printData();
		}
	}

	/**
	 * 
	 */
	private void printLoadingInfo() {
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
		
		if(object_visual_scenes != null){
			for( SceneNode node : object_visual_scenes.values() )
				node.printData();
		}
	}

	/**
	 * 
	 */
	private void applyVisualScene() {
		// Create and attaches bones, uses DFS
		for( SceneNode node : object_visual_scenes.values() ){
			// Check that it has sub nodes
			if( node.sub_nodes.size() > 0 ){
				model_bones = new HashMap<>();
				List<SceneNode> nodes = new LinkedList<>();
				// Do DFS: to create bones
				nodes.addAll(node.sub_nodes);
				while( !nodes.isEmpty() ){
					SceneNode curr_node = nodes.remove(0);
					String id = curr_node.getIdentifier();
					model_bones.put(id, new Bone(curr_node));
					for( SceneNode sub_node : curr_node.sub_nodes )
						nodes.add(sub_node);
				}
				// Do DFS: to attach bones
				nodes.addAll(node.sub_nodes);
				while( !nodes.isEmpty() ){
					SceneNode curr_node = nodes.remove(0);
					String id = curr_node.getIdentifier();
					Bone curr_bone = model_bones.get(id);
					for( SceneNode sub_node : curr_node.sub_nodes ){
						String tid = sub_node.getIdentifier();
						curr_bone.children.add( model_bones.get(tid) );
						nodes.add(sub_node);
					}
				}
			}
		}
		
		// Instantiate everything
		for( SceneNode node : object_visual_scenes.values() ){
			// Check that it has instantiated things
			if( node.instances.size() > 0 ){
				for( Instance instance : node.instances ){
					// Instantiate a geometry
					if( instance instanceof InstanceGeometry ){
						InstanceGeometry instance_geometry = (InstanceGeometry) instance;
						Mesh instantiated_mesh = object_meshes.get(instance.reference_url);
						instantiated_mesh.setJointBuffer();
						
						// Attach materials to mesh
						for( String symbol : instance_geometry.bind_material.instance_materials.keySet() ){
							String target = instance_geometry.bind_material.instance_materials.get(symbol);
							instantiated_mesh.addMaterial(symbol, object_materials.get(target));
						}

						model_meshes.put(instantiated_mesh.id, instantiated_mesh);
						
					// Instantiate a controller
					}else if( instance instanceof InstanceController ){
						InstanceController instance_controller = (InstanceController) instance;
						Controller instantiated_controller = object_controllers.get(instance.reference_url);
						instantiated_controller.generateJointBuffer();
						
						// Get target mesh
						Mesh instantiated_mesh = object_meshes.get(instantiated_controller.skin.source_id);
						instantiated_mesh.setJointBuffer(instantiated_controller.max_joints_per_vert, instantiated_controller.joint_buffers);
						
						// TODO Apply bind pose matrix to mesh
						
						// Attach materials to mesh
						for( String symbol : instance_controller.bind_material.instance_materials.keySet() ){
							String target = instance_controller.bind_material.instance_materials.get(symbol);
							instantiated_mesh.addMaterial(symbol, object_materials.get(target));
						}
						
						model_meshes.put(instantiated_mesh.id, instantiated_mesh);
					}
				}
			}
		}
		
		// Apply bone transformations
		
		
//		scene_meshes = new HashMap<>();
//		for( String transform_key : object_visual_scenes.keySet() ){
//			SceneNode scene_tranform = object_visual_scenes.get(transform_key);
//			// Make sure that it has been loaded
//			if( object_meshes.containsKey( scene_tranform.reference_url ) ){
//				Mesh affected_model = object_meshes.get( scene_tranform.reference_url );
//				Mesh transformed_mesh = affected_model.setObjectMatrix( scene_tranform );
//				scene_meshes.put(scene_tranform.getName(), affected_model);
//				System.out.println("data "+scene_tranform.getName());
//			}
//		}
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
	 * @throws DaeParseException 
	 */
	private HashMap<String, SceneNode> loadVisualScene(Node section) throws DaeParseException {
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
