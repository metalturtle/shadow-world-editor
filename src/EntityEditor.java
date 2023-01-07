
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


class EntityObject extends EditorObject {

	int type;
	String params;
	EntityObject(int local_id, Rectangle bound, int type,String params) {
		super(local_id, bound);
		this.type = type;
		this.params = params;
	}
}

class EntityType {
	String name,file_key;
	int file_type;
	Rectangle bound;
	EntityType(String name, int file_type, String file_key)  {
		this.name = name;
		this.file_key = file_key;
		this.file_type = file_type;
		bound = new Rectangle(0,0,80,80);
	}
	
	EntityType(String name, int file_type, String file_key,Rectangle bound)  {
		this.name = name;
		this.file_key = file_key;
		this.file_type = file_type;
		this.bound = bound;
	}
}

class EntityEditor extends Editor
{
	TextureRegion player;
	Vector playervec;
	EntityType entity_types[];
	String entity_names[];
	Texture entity_textures[];
	
	JComboBox<String> jc_entity_list;
	JTextField jt_entity_params;
	EntityEditor(int handler_id, EditorMain EditMain, MapEditor mapeditor,SpriteBatch spriteBatch,ShapeRenderer shaperender)
	{
		super(EditMain, handler_id, "entity");
		playervec = new Vector(0,0);
//	    String[] entity_names = { "ACTOR_HIDE","ACTOR_SEEK","PICKUP_HEALTH","PICKUP_LIGHT","PICKUP_GUN"};
//	    int[] entity_file_type= {1,1,0,0,0}
//	    String[] entity_file_key= {"actor_stealth_standing"};
		entity_types = new EntityType[] {
				new EntityType("ACTOR_HIDE",1,"actor_stealth_standing"),
				new EntityType("ACTOR_SEEK",1,"actor_torso_walk_machgun"),
				new EntityType("PICKUP_HEALTH",0,"pickup_health"),
				new EntityType("PICKUP_LIGHT",0,"pickup_health"),
				new EntityType("PICKUP_GUN",0,"pickup_gun"),
		};
		entity_names = new String[entity_types.length];
		for(int i = 0; i <entity_types.length; i++) {
			entity_names[i] = entity_types[i].name;
		}
		entity_textures = new Texture[entity_names.length];
		jc_entity_list = new JComboBox<String>(entity_names);
	    jt_entity_params = new JTextField(32);
	}
	
	@Override
	void init () {
		super.init();
//		Texture playertex = new Texture(Gdx.files.internal("levels\\playeranims\\default.png"));
//		player = new TextureRegion(playertex);
//		String keys[] = EditMain.ConfigManage.get_keys("sprite", "");
		HashMap<Object,Object> sprite_keys = EditMain.ConfigManage.get_object("sprite","sprite");
		HashMap<Object,Object> animated_sprite_keys = EditMain.ConfigManage.get_object("sprite","animated_sprite");
		for(int i = 0;i < entity_types.length;i++) {
			EntityType ent_type = entity_types[i];
			if(ent_type.file_type == 0) {
				String folder = (String)sprite_keys.get("folder");
				entity_textures[i] = new Texture(Gdx.files.internal(folder+"//"+(String)sprite_keys.get(ent_type.file_key)));
			}
			if(ent_type.file_type == 1) {
				String folder = (String)animated_sprite_keys.get("folder");
				HashMap <String,Object> animated_sprite_data = (HashMap)animated_sprite_keys.get(ent_type.file_key);
				String filename = (String)animated_sprite_data.get("file");
				entity_textures[i] = new Texture(Gdx.files.internal(folder+"//"+filename));
			}
		}
	}
	
//	JPanel texturepanel;
//	JTextField jt_texsize_x,jt_texsize_y,jt_texpos_x,jt_texpos_y;
//	JButton jb_enter;
//	DocumentListener doclistener;

	@Override
	EditorObject[] add_editor_object(int beginx, int beginy, int endx, int endy) {
		int type = jc_entity_list.getSelectedIndex();
		String params = jt_entity_params.getText();
		EntityType ent_type = entity_types[type];
		Rectangle bound = ent_type.bound;
		return new EditorObject[] {new EntityObject(0,new Rectangle(beginx,beginy,bound.w,bound.h),type,params)};
	}

	@Override
	void render_editor_object(SpriteBatch spriteBatch, EditorObject edit_obj) {
		EntityObject ent_obj = (EntityObject)edit_obj;
		EntityType ent_type = entity_types[ent_obj.type];
		spriteBatch.draw(entity_textures[ent_obj.type],ent_obj.bound.x,ent_obj.bound.y,ent_type.bound.w,ent_type.bound.h);
	}

	@Override
	void render_placeholder(SpriteBatch spriteBatch, int beginx, int beginy, int endx, int endy) {
		int type = jc_entity_list.getSelectedIndex();
		EntityType ent_type = entity_types[type];
		Rectangle bound = ent_type.bound;
		spriteBatch.draw(entity_textures[type],beginx,beginy,ent_type.bound.w,ent_type.bound.h);
	}

	@Override
	void render_before_entity(SpriteBatch spriteBatch) {
	}

	@Override
	void render_after_entity(SpriteBatch spriteBatch) {
		
	}
	
	@Override
	void right_click_action(int x, int y) {
	}

	@Override
	void before_render(SpriteBatch spriteBatch) {
	}

	@Override
	void set_editor_panel() {
		editorPanel.add(jc_entity_list);
		editorPanel.add(jt_entity_params);
	}

	@Override
	void selected_editor_object_action(EditorObject editObj) {
		EntityObject ent_obj = (EntityObject)editObj;
		jc_entity_list.setSelectedIndex(ent_obj.type);
		jt_entity_params.setText(ent_obj.params);
	}
	
	@Override
	void reset_selected_editor_object() {
		jt_entity_params.setText("");
	}

	@Override
	EditorObject read_object(Object[] rectarr) {
		return new EntityObject(0,new Rectangle( ((Number)rectarr[2]).floatValue()*div_factor,
				((Number)rectarr[3]).floatValue()*div_factor,
				((Number)rectarr[4]).floatValue()*div_factor,
				((Number)rectarr[5]).floatValue()*div_factor),
				((Number)rectarr[0]).intValue(),
				((String)rectarr[1]));
	}

	@Override
	JSONArray write_object(EditorObject editObj) {
		EntityObject entObj = (EntityObject)editObj;
		JSONArray jsonArr = new JSONArray();
		Rectangle rect = editObj.bound;
		jsonArr.add(entObj.type);
		jsonArr.add(entObj.params);
		jsonArr.add(rect.x/div_factor);
		jsonArr.add(rect.y/div_factor);
		jsonArr.add(rect.w/div_factor);
		jsonArr.add(rect.h/div_factor);
		return jsonArr;
	}
}
