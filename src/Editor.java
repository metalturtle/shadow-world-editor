import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Editor {

	static int mouse_x, mouse_y, mouse_x2, mouse_y2, real_mouse_x, real_mouse_y;
	int cell_size;
//	boolean left_mouse_down;
	boolean left_mouse, right_mouse;
	LinkedList <EditorObject> editorObjects, addingList, removeList;
	int EDITOR_ID, RENDER_PRIORITY;
	EditorMain EditMain;
	Texture red;
	JPanel editorPanel;
	JTextField jt_selected_id, jt_bound_x, jt_bound_y, jt_bound_w,jt_bound_h;
	JButton jb_delete,jb_prev,jb_next;
	EditorObject selected_editor_object;
	int selected_editor_index;
	String editorName;
	static float div_factor = 10;
	boolean visible = false;
	int modify_grid=-1;
	
	public Editor(EditorMain EditorMain, int editor_id, String editorName) {
		this.EditMain = EditorMain;
		this.EDITOR_ID = editor_id;
		this.editorName = editorName;
		editorObjects = new LinkedList <EditorObject>();
		addingList = new LinkedList <EditorObject>();
		removeList = new LinkedList <EditorObject>();
		this.cell_size = (int)EditMain.get_cell_size();

		editorPanel = new JPanel();
		editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));
		jt_selected_id = new JTextField(2);
		jb_delete = new JButton("Delete");
		jb_prev = new JButton("previous");
		jb_next = new JButton("next");
		jt_bound_x = new JTextField(6);
		jt_bound_y = new JTextField(6);
		jt_bound_w = new JTextField(6);
		jt_bound_h = new JTextField(6);
		JPanel jp_bound_x = new JPanel();
		JPanel jp_bound_y = new JPanel();
		JPanel jp_bound_w = new JPanel();
		JPanel jp_bound_h = new JPanel();
		jt_selected_id.disable();
//		editorPanel.add(new JLabel("id: "));
//		editorPanel.add(jt_selected_id);
		editorPanel.add(jb_delete);
		editorPanel.add(jb_prev);
		editorPanel.add(jb_next);
		jp_bound_x.add(new JLabel("x: "));
		jp_bound_x.add(jt_bound_x);
		
		jp_bound_y.add(new JLabel("y: "));
		jp_bound_y.add(jt_bound_y);
		
		jp_bound_w.add(new JLabel("w: "));
		jp_bound_w.add(jt_bound_w);
		
		jp_bound_h.add(new JLabel("h: "));
		jp_bound_h.add(jt_bound_h);
		
		editorPanel.add(jp_bound_x);
		editorPanel.add(jp_bound_y);
		editorPanel.add(jp_bound_w);
		editorPanel.add(jp_bound_h);
		
		jt_bound_x.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selected_editor_object != null) {
					selected_editor_object.bound.x = Float.parseFloat(jt_bound_x.getText())*cell_size;
					after_modified_selected();
				}
			}
			
		});
		
		jt_bound_y.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selected_editor_object != null) {
					selected_editor_object.bound.y = Float.parseFloat(jt_bound_y.getText())*cell_size;
					after_modified_selected();
				}
			}
			
		});
		
		jt_bound_w.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selected_editor_object != null) {
					selected_editor_object.bound.w = Float.parseFloat(jt_bound_w.getText())*cell_size;
					after_modified_selected();
				}
			}
			
		});
		
		jt_bound_h.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selected_editor_object != null) {
					selected_editor_object.bound.h = Float.parseFloat(jt_bound_h.getText())*cell_size;
					after_modified_selected();
				}
			}	
		});
		
		jb_delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				delete();
			}
			
		});
		
		jb_prev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int esize = editorObjects.size();
				if(esize > 0) {
					if(selected_editor_object == null ) {
						selected_editor_object = editorObjects.get(0);
					} else {
						int id = editorObjects.indexOf(selected_editor_object);
						selected_editor_object = editorObjects.get((id+esize-1)%esize);
					}
					Rectangle bound = selected_editor_object.bound;
//					EditMain.CAMERA_X = bound.x()+bound.w()/2-(EditMain.CAMERA_WIDTH*EditMain.ZOOM)/2;
//					EditMain.CAMERA_Y = bound.y()+bound.h()/2-(EditMain.CAMERA_HEIGHT*EditMain.ZOOM)/2;
					EditMain.GCamera.set_center(bound.x()+bound.w()/2,bound.y()+bound.h()/2);
				}

			}
		});
		
		jb_next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int esize = editorObjects.size();
				if(esize > 0) {
					if(selected_editor_object == null ) {
						selected_editor_object = editorObjects.get(esize-1);
					} else {
						int id = editorObjects.indexOf(selected_editor_object);
						selected_editor_object=editorObjects.get((id+esize-1)%esize);
					}
					Rectangle bound = selected_editor_object.bound;
					EditMain.GCamera.set_center(bound.x()+bound.w()/2,bound.y()+bound.h()/2);
				}

			}
		});
	}
	
	void init() {
		red = new Texture(Gdx.files.internal("levels\\red.png"));
//		green = new Texture(Gdx.files.internal("levels\\green.png"));
	}
	
	void set_render_priority(int PRIORITY) {
		this.RENDER_PRIORITY = PRIORITY;
	}
	
	@SuppressWarnings("unchecked")
	JSONObject save_to_file()
	{
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for(int i =0;i<editorObjects.size();i++)
		{
			
//			JSONArray jsonRect = new JSONArray();
//			Rectangle rect = editorObjects.get(i).bound;
//			jsonRect.add(rect.x);
//			jsonRect.add(rect.y);
//			jsonRect.add(rect.w);
//			jsonRect.add(rect.h);
			jsonArray.add(write_object(editorObjects.get(i)));
		}
		jsonObject.put("size",editorObjects.size());
		jsonObject.put("object",jsonArray);
//		jo.put(name, jsonworldata);
		return jsonObject;
	}
	
	void read_from_file(JSONObject jo)
	{
		JSONObject jsonObject = (JSONObject)jo.get(editorName);
		if (jsonObject == null)
			return;
		long wallsize = (Long)jsonObject.get("size");
		JSONArray walls = (JSONArray)jsonObject.get("object");
		
		for(int i=0;i<wallsize;i++)
		{
			JSONArray rectjson = (JSONArray)walls.get(i);
			Object rectarr[] = rectjson.toArray();
//			addingList.add(new Wall(0, new Rectangle( ((Number)rectarr[0]).floatValue(),((Number)rectarr[1]).floatValue(),((Number)rectarr[2]).floatValue(),((Number)rectarr[3]).floatValue()), true));
			EditorObject editObj = read_object(rectarr);
			if (editObj != null) {
				editorObjects.add(editObj);
			}
		}
	}
	
	abstract EditorObject read_object(Object rectarr[]) ;
	abstract JSONArray write_object(EditorObject editObj);
//	{
//		new EditorObject(0,new Rectangle( ((Number)rectarr[0]).floatValue(),((Number)rectarr[1]).floatValue(),((Number)rectarr[2]).floatValue(),((Number)rectarr[3]).floatValue()));
//	}
	
	void set_editor_id(int editor_id) {
		this.EDITOR_ID = editor_id;
	}
	
	int[] convertXY(int x,int y)
	{
		int coord[] =new int[2];
		int upscale = 1;
		x = upscale*x-((upscale*x)%cell_size) ;
		y = upscale*y-((upscale*y)%cell_size);
		coord[0] = x;
		coord[1] = y;
		return coord;
	}
	
	void hover()
	{
		int x = (int)InputState.mouse.getX(), y = (int)InputState.mouse.getY();
		int coord[] = convertXY(x,y);
		mouse_x = coord[0];mouse_y = coord[1];
		real_mouse_x = x;
		real_mouse_y = (Gdx.graphics.getHeight()-y);
	}
	
	void touch_down()
	{
		int x = (int)InputState.mouse.getX(), y = (int)InputState.mouse.getY();
		real_mouse_x = x;
		real_mouse_y = (Gdx.graphics.getHeight()-y);
		int v[] = convertXY(x,y);
		x = v[0];y=v[1];
//		left_mouse_down = true;
		mouse_x = x;
		mouse_y = y;
		mouse_x2 = x;
		mouse_y2 = y;
		
		if (selected_editor_object != null) {
			if(InputState.left_click == true) {
				Rectangle bound = selected_editor_object.bound;
				int select_point_size = 40;
				int hsize=select_point_size/2;
				Rectangle mod[] = get_edit_points(bound);
				Vector mouse_vec = new Vector(x,y);
				for(int i =0;i<mod.length;i++) {
					if(Rectangle.check_point_intersection(mod[i], mouse_vec)) {
						modify_grid = i;
						break;
					}
				}
			}
		}
//		left_mouse = true;
	}
	
	void touch_dragged()
	{
		int x = (int)InputState.mouse.getX(), y = (int)InputState.mouse.getY();
		real_mouse_x = x;
		real_mouse_y = (Gdx.graphics.getHeight()-y);
		int coord[] = convertXY(x,y);
		mouse_x2 = coord[0];mouse_y2 = coord[1];
		
		if(selected_editor_object != null && modify_grid != -1) {
			Rectangle bound = selected_editor_object.bound;
			if(modify_grid == 2) {
				float new_w = Math.max(bound.w+(bound.x - mouse_x2),0);
				if(new_w != 0){
					bound.set(mouse_x2,bound.y,new_w,bound.h);	
				}
			}
			if(modify_grid == 3) {
				if((mouse_x2 - bound.x)> 0)
					bound.set(bound.x,bound.y,mouse_x2-bound.x,bound.h);
			}
			if(modify_grid == 0) {
				float new_h =Math.max(bound.h+(bound.y-mouse_y2), 0) ;
				if(new_h != 0) {
					bound.set(bound.x,mouse_y2,bound.w,new_h);	
				}
			}
			if(modify_grid == 1) {
				if((mouse_y2 - bound.y) > 0)
					bound.set(bound.x,bound.y,bound.w,mouse_y2-bound.y);
			}
			if(modify_grid == 4) {
				bound.x(mouse_x2-bound.w()/2);
				bound.y(mouse_y2-bound.h()/2);
			}
			selected_editobj_action_panel(selected_editor_object);
			after_modified_selected();
		}
	}
	
	void touch_up()
	{
		int x = (int)InputState.mouse.getX(), y = (int)InputState.mouse.getY();
		real_mouse_x = x;
		real_mouse_y = (Gdx.graphics.getHeight()-y);
		if(left_mouse == true && InputState.left_click == false) {
			int coord[] = convertXY(x,y);
			x = coord[0];y=coord[1];
			if (x == mouse_x && y == mouse_y) {
				return;
			}
			int beginx,endx,beginy,endy;
			if(mouse_x < mouse_x2){beginx = mouse_x;endx = mouse_x2 + cell_size;}
			else{beginx = mouse_x2;endx = mouse_x;}
			if(mouse_y < mouse_y2){beginy = mouse_y;endy = mouse_y2;}
			else{beginy = mouse_y2 ;endy = mouse_y;}
			
			if (selected_editor_object == null) {
				EditorObject editObjs[] = add_editor_object(beginx, beginy, endx, endy);
				if (editObjs != null) {
					for (EditorObject editObj: editObjs) {
						if (editObj == null)
							continue;
						addingList.add(editObj);
					}	
				}
			} else {
				modify_grid = -1;
			}
		}
	}
	
	void right_click() {
		
		if(selected_editor_object != null) {
			selected_editor_object = null;
			reset_selected_editor_object();
		} else {
			EditorObject editObj = find_selected_editor_object();
			selected_editor_object = editObj;
			if (editObj != null) {
				selected_editobj_action_panel(editObj);
			} else {
				right_click_action((int)InputState.mouse.getX(),(int)InputState.mouse.getY());
				reset_selected_editor_object();
			}
		}
	}
	
	void add_editor_object (EditorObject editObj) {
		addingList.add(editObj);
	}
	
	boolean add_from_list() {
		boolean added=false;
		for(EditorObject editObj: addingList) {
			editorObjects.add(editObj);
			if(added == false)
				added = true;
		}
		while (addingList.size() > 0) {
			addingList.remove();
		}
		return added;
	}
	
	boolean remove_from_list () {
		boolean remove=false;
		for (EditorObject editObj : removeList) {
			editorObjects.remove(editObj);
			if(remove == false) {
				remove = true;
			}
		}
		while (removeList.size() > 0) {
			removeList.remove();
		}
		return remove;
	}
	
	EditorObject find_selected_editor_object() {
		EditorObject selectedEditObj = null;
		for(int i =0;i<editorObjects.size();i++)
		{
			EditorObject editObj = editorObjects.get(i);
			if((mouse_x + 0.5>=editObj.bound.x() && mouse_x + 0.5<=editObj.bound.x() + editObj.bound.w())&&(mouse_y + 0.5>=editObj.bound.y() && mouse_y + 0.5<=editObj.bound.y()+editObj.bound.h()))
			{
				selectedEditObj = editObj;
				selected_editor_index = i;
			}
		}
		return selectedEditObj;
	}
	
	
	
	void selected_editobj_action_panel(EditorObject editObj) {
		jt_bound_x.setText(editObj.bound.x/cell_size+"");
		jt_bound_y.setText(editObj.bound.y/cell_size+"");
		jt_bound_w.setText(editObj.bound.w/cell_size+"");
		jt_bound_h.setText(editObj.bound.h/cell_size+"");
		selected_editor_object_action(editObj);
	}
	
	void reset_selected_editor_object() {
		jt_selected_id.setText("");
		jt_bound_x.setText("");
		jt_bound_y.setText("");
		jt_bound_w.setText("");
		jt_bound_h.setText("");
	}
	
	void handle_mouse(String mouse_state) {
		if(mouse_state.equals("hover")) {
			hover();
		}
		if(mouse_state.equals("touch_down")) {
			touch_down();
		}
		if(mouse_state.equals("touch_dragged")) {
			touch_dragged();
		}
		if(mouse_state.equals("touch_up")) {
			touch_up();
		}
		left_mouse = InputState.left_click;
		right_mouse = InputState.right_click;
	}
	
	abstract void selected_editor_object_action(EditorObject editObj);
	abstract void set_editor_panel();
	abstract void right_click_action(int x, int y);
	abstract EditorObject[] add_editor_object(int beginx, int beginy, int endx, int endy);
	abstract void render_editor_object(SpriteBatch spriteBatch, EditorObject edit_obj);
	abstract void render_placeholder(SpriteBatch spriteBatch, int beginx, int beginy, int endx, int endy);
	abstract void render_before_entity(SpriteBatch spriteBatch);
	abstract void render_after_entity(SpriteBatch spriteBatch);
	abstract void before_render(SpriteBatch spriteBatch);
	
	public void render_editor_objects(SpriteBatch spriteBatch ) {
		
		for (EditorObject edit_obj : editorObjects) {
			if (edit_obj == selected_editor_object) {
				spriteBatch.setColor(1,0,0,1);
				render_selected_object(spriteBatch);
				spriteBatch.setColor(1,1,1,1);
			}
			else
				render_editor_object(spriteBatch, edit_obj);
		}
	}
	
	public void render_outline() {
		EditMain.shaperender.setColor(1,0,0,1);
		EditMain.shaperender.begin(ShapeRenderer.ShapeType.Filled);
		float thickness=3;
		if(EditMain.MODE == this.EDITOR_ID) {
			for (EditorObject edit_obj : editorObjects) {
					Rectangle bound = edit_obj.bound;
					EditMain.shaperender.rect(bound.x, bound.y-thickness, bound.w,thickness);
					EditMain.shaperender.rect(bound.x, bound.y, thickness,bound.h);
					EditMain.shaperender.rect(bound.x, bound.y+bound.h-thickness, bound.w,thickness);
					EditMain.shaperender.rect(bound.x+bound.w, bound.y, thickness,bound.h);
			}
		}
		EditMain.shaperender.end();
		EditMain.shaperender.setColor(1,1,1,1);
	}
	
	public void render(SpriteBatch spriteBatch) {
		int beginx,endx,beginy,endy;
		if(mouse_x < mouse_x2){beginx = mouse_x;endx = mouse_x2 + cell_size;}
		else{beginx = mouse_x2;endx = mouse_x;}
		if(mouse_y < mouse_y2){beginy = mouse_y;endy = mouse_y2;}
		else{beginy = mouse_y2 ;endy = mouse_y;}
		
//		modify_selected_object();
		before_render(spriteBatch);
		spriteBatch.begin();
		render_before_entity(spriteBatch);
		spriteBatch.setColor(Color.WHITE);
		render_editor_objects(spriteBatch);
		render_after_entity(spriteBatch);
		if (InputState.left_click) {
			spriteBatch.setColor(Color.RED);
			render_placeholder(spriteBatch, beginx, beginy, endx, endy);
		}
		render_cursor(spriteBatch);
		spriteBatch.end();
		render_outline();
//		add_from_list();
//		remove_from_list();
	}
	
	void render_cursor (SpriteBatch spriteBatch) {
		spriteBatch.setColor(Color.RED);
		spriteBatch.draw(red,mouse_x, mouse_y,cell_size,cell_size);
	}
	
	void render_selected_object(SpriteBatch spriteBatch) {
		if (selected_editor_object != null) {
			spriteBatch.setColor(new Color(0,255,0,100));
			render_editor_object(spriteBatch, selected_editor_object);
			Rectangle bound = selected_editor_object.bound;
			spriteBatch.setColor(Color.GREEN);
//			int psize = 20;
//			int hlsize= psize/2;
//			spriteBatch.draw(red,bound.x+bound.w/2,bound.y-hlsize, psize,psize);
//			spriteBatch.draw(red,bound.x+bound.w/2,bound.y+bound.h, psize,psize);
//			spriteBatch.draw(red,bound.x-hlsize,bound.y+bound.h/2, psize,psize);
//			spriteBatch.draw(red,bound.x+bound.w,bound.y+bound.h/2, psize,psize);
			
//			spriteBatch.draw(red,bound.x+bound.w/2-hlsize,bound.y+bound.h/2-hlsize, psize,psize);

			Rectangle mod[] = get_edit_points(bound);
			for(int i = 0; i < mod.length; i++) {
				Rectangle editp = mod[i];
				spriteBatch.draw(red,editp.x,editp.y,editp.w,editp.h);
			}
			
			spriteBatch.setColor(Color.WHITE);
		}
	}
	
	Rectangle [] get_edit_points(Rectangle bound) {
		int select_point_size = 20;
		int hsize=select_point_size;
		Rectangle mod[] = new Rectangle[] {new Rectangle(bound.x+bound.w/2,bound.y-hsize,select_point_size,select_point_size),
				new Rectangle(bound.x+bound.w/2,bound.y+bound.h,select_point_size,select_point_size),
				new Rectangle(bound.x-hsize,bound.y+bound.h/2,select_point_size,select_point_size),
				new Rectangle(bound.x+bound.w,bound.y+bound.h/2,select_point_size,select_point_size),
				new Rectangle(bound.x+bound.w/2,bound.y+bound.h/2,select_point_size,select_point_size)};
		return mod;
	}
	
	public void delete() {
		if(selected_editor_object != null) {
			removeList.add(selected_editor_object);
			selected_editor_object = null;
		}
	}
	public void in_focus() {}
	public void after_modified_selected() {}
	
//	void render_grid (ShapeRenderer shaperender) {
//		shaperender.setProjectionMatrix(camera.combined);
//		shaperender.setColor(Color.BLACK);
//		shaperender.begin(ShapeRenderer.ShapeType.Line);
//		for(int i=0;i<mapeditor.grid_size*mapeditor.cell_size;i+=mapeditor.cell_size)
//			shaperender.line(i,0, i,mapeditor.grid_size*mapeditor.cell_size);
//		for(int i=0;i<mapeditor.grid_size*mapeditor.cell_size;i+=mapeditor.cell_size)
//			shaperender.line(0,i,mapeditor.grid_size*mapeditor.grid_size,i);
//		shaperender.end();
//	}
}
