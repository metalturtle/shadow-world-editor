import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JTextField;

import org.json.simple.JSONArray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class MiscObject extends EditorObject {

	String type,params;
	Rectangle color;
	MiscObject(int local_id, Rectangle bound,String type, String params,Rectangle color) {
		super(local_id, bound);
		this.type = type;
		this.params = params;
		this.color = color;
	}
	
}

public class MiscEditor extends Editor {

	JTextField jt_type, jt_params;
	Random rand;
	BitmapFont font;
//	FreeTypeFontGenerator generator;
//	FreeTypeFontGenerator.FreeTypeFontParameter parameter;
	public MiscEditor(int editor_id, EditorMain EditMain) {
		super(EditMain, editor_id, "misc");
		rand = new Random();
//		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts\\cute_letters\\Cute Letters.ttf"));
//		parameter  = new FreeTypeFontGenerator.FreeTypeFontParameter();
//		font = generator.generateFont(parameter);
		font = new BitmapFont();
	}
	
	Rectangle create_color() {
		int comp = rand.nextInt(3);
		rand.setSeed(System.currentTimeMillis());
		float fcol[] = new float[] {.5f,.5f,.5f,.5f};
		fcol[comp] +=0.5f;
		return new Rectangle(fcol[0],fcol[1],fcol[2],fcol[3]);
	}

	@Override
	EditorObject read_object(Object[] rectarr) {
		return new MiscObject(0, new Rectangle(((Number)rectarr[0]).floatValue()*div_factor,
		((Number)rectarr[1]).floatValue()*div_factor,
		((Number)rectarr[2]).floatValue()*div_factor,
		((Number)rectarr[3]).floatValue()*div_factor),
				(String)rectarr[4],
				(String)rectarr[5],
				create_color()
		);
	}
	
	@Override
	JSONArray write_object(EditorObject editObj) {
		JSONArray jsonArr = new JSONArray();
		MiscObject miscObj = (MiscObject)editObj;
		
		jsonArr.add(miscObj.bound.x/div_factor);
		jsonArr.add(miscObj.bound.y/div_factor);
		jsonArr.add(miscObj.bound.w/div_factor);
		jsonArr.add(miscObj.bound.h/div_factor);
		jsonArr.add(miscObj.type);
		jsonArr.add(miscObj.params);
		return jsonArr;
	}

	@Override
	void selected_editor_object_action(EditorObject editObj) {
		// TODO Auto-generated method stub
		MiscObject miscObj = (MiscObject)editObj;
		jt_type.setText(miscObj.type);
		jt_params.setText(miscObj.params);
		jt_type.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selected_editor_object != null) {
					MiscObject misc_obj = (MiscObject)selected_editor_object;
					misc_obj.type = jt_type.getText();
				}
			}
			
		});
		
		jt_params.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selected_editor_object != null) {
					MiscObject misc_obj = (MiscObject)selected_editor_object;
					misc_obj.params = jt_params.getText();
				}
			}
			
		});
	}

	@Override
	void set_editor_panel() {
		jt_params = new JTextField("");
		jt_type = new JTextField("");
		editorPanel.add(jt_type);
		editorPanel.add(jt_params);
	}

	@Override
	void right_click_action(int x, int y) {
	}
	
	@Override
	void reset_selected_editor_object() {
		jt_type.setText("");
		jt_params.setText("");
	}

	@Override
	EditorObject[] add_editor_object(int beginx, int beginy, int endx, int endy) {
		int x_width = 0;
		if (mouse_x2 < mouse_x) {
			x_width = endx-beginx +cell_size;
		} else {
			x_width = endx-beginx;
		}
		return new MiscObject[] {new MiscObject(0,new Rectangle(beginx, beginy ,x_width,endy-beginy + cell_size),jt_type.getText(), jt_params.getText(),create_color())};
	}

	@Override
	void render_editor_object(SpriteBatch spriteBatch, EditorObject edit_obj) {
		MiscObject miscObj = (MiscObject)edit_obj;
		Rectangle bound = miscObj.bound;
		Rectangle color = miscObj.color;
		spriteBatch.setColor(color.x,color.y,color.w,color.h);
		spriteBatch.draw(red,bound.x(),bound.y(),bound.w(),bound.h());
		font.getData().setScale(EditMain.GCamera.get_zoom());
		font.draw(spriteBatch,miscObj.type,bound.center_x() , bound.center_y());
	}

	@Override
	void render_placeholder(SpriteBatch spriteBatch, int beginx, int beginy, int endx, int endy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void render_before_entity(SpriteBatch spriteBatch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void render_after_entity(SpriteBatch spriteBatch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void before_render(SpriteBatch spriteBatch) {
		// TODO Auto-generated method stub
		
	}
	
	public Set get_handler_list() {
		HashSet <String> handlerUsed = new HashSet<String>();

		for(EditorObject editObj: editorObjects) {
			MiscObject miscObj = (MiscObject)editObj;
			if(miscObj.type.toLowerCase().contains("handler")) {
				handlerUsed.add(miscObj.type);
			}
		}
		return handlerUsed;
	}

	
	public Set get_renderer_list() {
		HashSet <String> rendererUsed = new HashSet<String>();

		for(EditorObject editObj: editorObjects) {
			MiscObject miscObj = (MiscObject)editObj;
			if(miscObj.type.toLowerCase().contains("renderer")) {
				rendererUsed.add(miscObj.type);
			}
		}
		return rendererUsed;
	}
}
