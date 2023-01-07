import org.json.simple.JSONArray;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class CollisionObject extends EditorObject
{
	EditorObject textureObject;
	CollisionObject(int local_id, Rectangle bound) {
		super(local_id, bound);
		// TODO Auto-generated constructor stub
	}
	
}

public class CollisionEditor extends Editor {

	public CollisionEditor(int editor_id, EditorMain EditMain) {
		super(EditMain, editor_id, "collision");
		// TODO Auto-generated constructor stub
	}

	@Override
	EditorObject read_object(Object[] rectarr) {
		return new CollisionObject(0,new Rectangle( ((Number)rectarr[0]).floatValue()*div_factor,((Number)rectarr[1]).floatValue()*div_factor,((Number)rectarr[2]).floatValue()*div_factor,((Number)rectarr[3]).floatValue()*div_factor));
	}

	@Override
	JSONArray write_object(EditorObject editObj) {
		JSONArray jsonArr = new JSONArray();
		Rectangle rect = editObj.bound;
		jsonArr.add(rect.x/div_factor);
		jsonArr.add(rect.y/div_factor);
		jsonArr.add(rect.w/div_factor);
		jsonArr.add(rect.h/div_factor);
		return jsonArr;
	}

	@Override
	void selected_editor_object_action(EditorObject editObj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void set_editor_panel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void right_click_action(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	EditorObject[] add_editor_object(int beginx, int beginy, int endx, int endy) {
		int x_width = 0;
		if (mouse_x2 < mouse_x) {
			x_width = endx-beginx +cell_size;
		} else {
			x_width = endx-beginx;
		}
		return new EditorObject[] {new CollisionObject(0, new Rectangle(beginx, beginy, x_width, endy-beginy + cell_size))};
	}

	
	public void add_collision_object(int beginx, int beginy, int endx, int endy) {
		EditorObject colObj = add_editor_object(beginx,beginy,endx,endy)[0];
		addingList.add(colObj);
	}
	
	@Override
	void render_editor_object(SpriteBatch spriteBatch, EditorObject edit_obj) {
		spriteBatch.setColor(Color.BROWN);
		Rectangle bound = edit_obj.bound;
		spriteBatch.draw(red,bound.x,bound.y,bound.w,bound.h);
//		spriteBatch.draw(red, bound.x(), bound.y(), bound.w(), bound.h(), 0, 0, 1,1);
		spriteBatch.setColor(Color.WHITE);
	}

	@Override
	void render_placeholder(SpriteBatch spriteBatch, int beginx, int beginy, int endx, int endy) {
		int x_width = 0;
		if (mouse_x2 < mouse_x) {
			x_width = endx-beginx +cell_size;
		} else {
			x_width = endx-beginx;
		}
		spriteBatch.draw(red,beginx,beginy ,x_width,endy-beginy + cell_size);
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
	}

	@Override
	public void in_focus() {
		fit_to_texture();
	}
	boolean done = false;
	
	public void fit_to_texture() {
		for(EditorObject editObj : editorObjects) {
			CollisionObject colObj = (CollisionObject)editObj;
			if(colObj.textureObject != null) {
				colObj.bound.set(colObj.textureObject.bound);
			}
		}
	}
	
}
