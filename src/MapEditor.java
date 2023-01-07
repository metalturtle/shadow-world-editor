
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


class Wall extends EditorObject {
	boolean wall;
	Wall(int local_id, Rectangle bound, boolean wall) {
		super(local_id, bound);
		this.wall = wall;
	}
}
 
class MapEditor extends Editor
{
	JComboBox<String> jc_draw_type;
	MapEditor(int editor_id, EditorMain EditMain)
	{
		super(EditMain, editor_id, "map");
	}
	
	@SuppressWarnings("unchecked")
	void savetofile(JSONObject jo)
	{
		JSONObject jsonworldata = new JSONObject();
		JSONArray jsonwalls = new JSONArray();
		for(int i =0;i<editorObjects.size();i++)
		{
			
			JSONArray jsonrect = new JSONArray();
			Rectangle rect = editorObjects.get(i).bound;
			jsonrect.add(rect.x);
			jsonrect.add(rect.y);
			jsonrect.add(rect.w);
			jsonrect.add(rect.h);
			jsonwalls.add( jsonrect);
		}
		jsonworldata.put("size",editorObjects.size());
		jsonworldata.put("walls",jsonwalls);
		jo.put("world", jsonworldata);
	}
	
	void readfromfile(JSONObject jo)
	{
		JSONObject worldjson = (JSONObject)jo.get("world");
		if (worldjson == null)
			return;
		long wallsize = (Long)worldjson.get("size");
		JSONArray walls = (JSONArray)worldjson.get("walls");
		
		for(int i=0;i<wallsize;i++)
		{
			JSONArray rectjson = (JSONArray)walls.get(i);
			Object rectarr[] = rectjson.toArray();
			addingList.add(new Wall(0, new Rectangle( ((Number)rectarr[0]).floatValue(),((Number)rectarr[1]).floatValue(),((Number)rectarr[2]).floatValue(),((Number)rectarr[3]).floatValue()), true));
		}
	}
	
	void cut_wall2(Wall rect1) {
		Wall rect2 = null;
		int index = -1;
		for (int i = 0; i < editorObjects.size(); i++) {
			Wall temp_wall = (Wall)editorObjects.get(i);
			Rectangle a = rect1.bound, b = temp_wall.bound;
			if (Rectangle.check_rect_intersection(a, b)) {
				rect2 = temp_wall;
				index = i;
				break;
			}
		}
		if(rect2 == null) {
		//	return new Wall[] {rect1};
		}
		
		Rectangle x_val[] = new Rectangle[4];
		float y_val[] = new float[4];
		if (rect1.bound.x < rect2.bound.x) {
			x_val[0] = rect1.bound;
			x_val[1] = rect2.bound;
		} else {
			x_val[0] = rect2.bound;
			x_val[1] = rect1.bound;
		}
		if (rect1.bound.x + rect1.bound.w < rect2.bound.x + rect2.bound.w) {
			x_val[2] = rect1.bound;
			x_val[3] = rect2.bound;
		} else {
			x_val[2] = rect2.bound;
			x_val[3] = rect1.bound;
		}
		if (rect1.bound.y < rect2.bound.y) {
			y_val[0] = rect1.bound.y;
			y_val[1] = rect2.bound.y;
		} else {
			y_val[0] = rect2.bound.y;
			y_val[1] = rect1.bound.y;
		}
		if(rect1.bound.y+rect1.bound.h < rect2.bound.y + rect2.bound.h) {
			y_val[2] = rect1.bound.y+rect1.bound.h;
			y_val[3] = rect2.bound.y + rect2.bound.h;
		} else {
			y_val[2] = rect2.bound.y + rect2.bound.h;
			y_val[3] = rect1.bound.y+rect1.bound.h;
		}
		// x cut
		Rectangle new_rect[] = new Rectangle[5];
		new_rect[0] = new Rectangle(x_val[0].x, x_val[0].y,x_val[1].x - x_val[0].x, x_val[0].y);
		new_rect[1] = new Rectangle(x_val[1].x, x_val[1].y,x_val[2].x - x_val[1].x, x_val[1].y);
		new_rect[2] = new Rectangle(x_val[2].x, x_val[2].y,x_val[3].x - x_val[2].x, x_val[2].y);

//		new_rect[3] = new Rectangle(y_val[0].x, y_val[0].x)
		
	}

	Wall[] cut_wall(Wall rect1) {
		Wall rect2 = null;
		int index = -1;
		for (int i = 0; i < editorObjects.size(); i++) {
			Wall temp_wall = (Wall)editorObjects.get(i);
			Rectangle a = rect1.bound, b = temp_wall.bound;
			if (Rectangle.check_rect_intersection(a, b) && !removeList.contains(temp_wall)) {
				rect2 = temp_wall;
				index = i;
				break;
			}
//			boolean xcheck = ((a.x < b.x && a.x+a.w > b.x) || (a.x > b.x && a.x+ a.w < b.x+b.w) || (a.x < b.x+b.w && a.x+a.w > b.x+b.w));
////			boolean xcheck = ( (a.x > b.x && a.x+ a.w < b.x+b.w));
//			boolean ycheck = ((a.y < b.y && a.y+a.h > b.y) || (a.y > b.y && a.y+ a.h < b.y+b.h) || (a.y < b.y+b.h && a.y+a.h > b.y+b.h));
//			if (xcheck && a.y == b.y) {
//				rect2 = temp_wall;
//				index = i;
//				break;
//			}
//			if (ycheck && a.x == b.x) {
//				rect2 = temp_wall;
//				index = i;
//				break;
//			}
		}
		if(rect2 == null) {
			return new Wall[] {rect1};
		}

		float x_val[] = new float[4];
		float y_val[] = new float[4];
		if (rect1.bound.x < rect2.bound.x) {
			x_val[0] = rect1.bound.x;
			x_val[1] = rect2.bound.x;
		} else {
			x_val[0] = rect2.bound.x;
			x_val[1] = rect1.bound.x;
		}
		if (rect1.bound.x + rect1.bound.w < rect2.bound.x + rect2.bound.w) {
			x_val[2] = rect1.bound.x+rect1.bound.w;
			x_val[3] = rect2.bound.x + rect2.bound.w;
		} else {
			x_val[2] = rect2.bound.x + rect2.bound.w;
			x_val[3] = rect1.bound.x + rect1.bound.w;
		}
		
		if (rect1.bound.y < rect2.bound.y) {
			y_val[0] = rect1.bound.y;
			y_val[1] = rect2.bound.y;
		} else {
			y_val[0] = rect2.bound.y;
			y_val[1] = rect1.bound.y;
		}
		if(rect1.bound.y+rect1.bound.h < rect2.bound.y + rect2.bound.h) {
			y_val[2] = rect1.bound.y+rect1.bound.h;
			y_val[3] = rect2.bound.y + rect2.bound.h;
		} else {
			y_val[2] = rect2.bound.y + rect2.bound.h;
			y_val[3] = rect1.bound.y+rect1.bound.h;
		}
		
		Rectangle new_rect1 = null,new_rect2 = null, new_rect3 = null;
		Wall div_walls[] = null;
		if (rect1.bound.w() > rect1.bound.h() && rect2.bound.w() > rect2.bound.h()) {
			new_rect1 = new Rectangle(x_val[0], rect1.bound.y(),x_val[1] - x_val[0], rect1.bound.h());
			new_rect2 = new Rectangle(x_val[1], rect1.bound.y(),x_val[2] - x_val[1], rect1.bound.h());
			new_rect3 = new Rectangle(x_val[2], rect1.bound.y(),x_val[3] - x_val[2], rect1.bound.h());
//			editorObjects.remove(index);
			removeList.add(rect2);
			Wall nwall1 = new Wall(0,new_rect1, true),nwall2 = new Wall(0,new_rect2,true),nwall3 = new Wall(0, new_rect3, true);
			Wall div1[] = cut_wall(nwall1);
			Wall div2[] = cut_wall(nwall2);
			Wall div3[] = cut_wall(nwall3);
			int total_length = div1.length + div2.length + div3.length;
			div_walls = new Wall[total_length];
			int k = 0;
			for (int i = 0; i < div1.length; i++) {
				div_walls[k] = div1[i];
				k+=1;
			}
			for (int i = 0; i < div2.length; i++) {
				div_walls[k] = div2[i];
				k+=1;
			}
			for (int i = 0; i < div3.length; i++) {
				div_walls[k] = div3[i];
				k+=1;
			}
			return div_walls;
		}
		else if (rect1.bound.w() < rect1.bound.h() && rect2.bound.w() < rect2.bound.h()) {
			new_rect1 = new Rectangle(rect1.bound.x(), y_val[0], rect1.bound.w(), y_val[1] - y_val[0]);
			new_rect2 = new Rectangle(rect1.bound.x(), y_val[1], rect1.bound.w(), y_val[2] - y_val[1]);
			new_rect3 = new Rectangle(rect1.bound.x(), y_val[2], rect1.bound.w(), y_val[3] - y_val[2]);
//			editorObjects.remove(index);
			removeList.add(rect2);
			Wall nwall1 = new Wall(0,new_rect1, true),nwall2 = new Wall(0,new_rect2,true),nwall3 = new Wall(0, new_rect3, true);
			Wall div1[] = cut_wall(nwall1);
			Wall div2[] = cut_wall(nwall2);
			Wall div3[] = cut_wall(nwall3);
			int total_length = div1.length + div2.length + div3.length;
			div_walls = new Wall[total_length];
			int k = 0;
			for (int i = 0; i < div1.length; i++) {
				div_walls[k] = div1[i];
				k+=1;
			}
			for (int i = 0; i < div2.length; i++) {
				div_walls[k] = div2[i];
				k+=1;
			}
			for (int i = 0; i < div3.length; i++) {
				div_walls[k] = div3[i];
				k+=1;
			}
			return div_walls;
		} else {
			if (rect1.bound.w() > rect1.bound.h()) {
				new_rect1 = new Rectangle(x_val[0], rect1.bound.y(),x_val[1] - x_val[0], rect1.bound.h());
//				new_rect2 = new Rectangle(x_val[1], rect1.bound.y(),x_val[2] - x_val[1], rect1.bound.h());
				new_rect2 = new Rectangle(x_val[2], rect1.bound.y(),x_val[3] - x_val[2], rect1.bound.h());
			} else {
				new_rect1 = new Rectangle(rect1.bound.x(), y_val[0], rect1.bound.w(), y_val[1] - y_val[0]);
//				new_rect2 = new Rectangle(rect1.bound.x(), y_val[1], rect1.bound.w(), y_val[2] - y_val[1]);
				new_rect2 = new Rectangle(rect1.bound.x(), y_val[2], rect1.bound.w(), y_val[3] - y_val[2]);
			}
			
			Wall nwall1 = new Wall(0,new_rect1, true),nwall2 = new Wall(0,new_rect2,true);
			Wall div1[] = cut_wall(nwall1);
			Wall div2[] = cut_wall(nwall2);
			int total_length = div1.length + div2.length;
			div_walls = new Wall[total_length];
			int k = 0;
			for (int i = 0; i < div1.length; i++) {
				div_walls[k] = div1[i];
				k+=1;
			}
			for (int i = 0; i < div2.length; i++) {
				div_walls[k] = div2[i];
				k+=1;
			}
			return div_walls;
		}
	}
	
	@Override
	EditorObject[] add_editor_object(int beginx, int beginy, int endx, int endy) {
		if (jc_draw_type.getSelectedIndex() == 0) {
			if (Math.abs(mouse_x - mouse_x2) > Math.abs(mouse_y - mouse_y2)) {
				if (mouse_x < mouse_x2) {
					return new EditorObject[] {new Wall(0, new Rectangle(mouse_x, mouse_y, (mouse_x2 - mouse_x), cell_size), true)};
				} else {
					return  new EditorObject[] {new Wall(0, new Rectangle(mouse_x2, mouse_y, (mouse_x - mouse_x2 + cell_size), cell_size), true)};
				}
			} else {
				if (mouse_y < mouse_y2) {
					return  new EditorObject[] {new Wall(0, new Rectangle(mouse_x, mouse_y, cell_size, (mouse_y2 - mouse_y)), true)};
				} else {
					return  new EditorObject[] {new Wall(0, new Rectangle(mouse_x, mouse_y2, cell_size, (mouse_y - mouse_y2 + cell_size)), true)};
				}
			}
		} else {
			Wall walls[] = new Wall[4];
			walls[0] = new Wall(0, new Rectangle(beginx + cell_size , beginy, (endx - beginx - cell_size), cell_size), true);
			walls[1] = new Wall(0, new Rectangle(beginx, beginy + cell_size , cell_size, (endy - beginy - cell_size)), true);
			walls[2] = new Wall(0, new Rectangle(beginx + cell_size , endy, (endx - beginx - cell_size), cell_size), true);
			walls[3] = new Wall(0, new Rectangle(endx, beginy + cell_size, cell_size, (endy - beginy - cell_size )), true);
			return walls;
		}
	}
	
//@Override
//EditorObject[] add_editor_object(int beginx, int beginy, int endx, int endy) {
//	if (jc_draw_type.getSelectedIndex() == 0) {
//		if (Math.abs(mouse_x - mouse_x2) > Math.abs(mouse_y - mouse_y2)) {
//			if (mouse_x < mouse_x2) {
//				return cut_wall(new Wall(0, new Rectangle(mouse_x, mouse_y, (mouse_x2 - mouse_x), cell_size), true));
//			} else {
//				return cut_wall(new Wall(0, new Rectangle(mouse_x2, mouse_y, (mouse_x - mouse_x2 + cell_size), cell_size), true));
//			}
//		} else {
//			if (mouse_y < mouse_y2) {
//				return cut_wall(new Wall(0, new Rectangle(mouse_x, mouse_y, cell_size, (mouse_y2 - mouse_y)), true));
//			} else {
//				return cut_wall(new Wall(0, new Rectangle(mouse_x, mouse_y2, cell_size, (mouse_y - mouse_y2 + cell_size)), true));
//			}
//		}
//	} else {
//		Wall walls[] = new Wall[4];
//		walls[0] = new Wall(0, new Rectangle(beginx + cell_size , beginy, (endx - beginx - cell_size), cell_size), true);
//		walls[1] = new Wall(0, new Rectangle(beginx, beginy + cell_size , cell_size, (endy - beginy - cell_size)), true);
//		walls[2] = new Wall(0, new Rectangle(beginx + cell_size , endy, (endx - beginx - cell_size), cell_size), true);
//		walls[3] = new Wall(0, new Rectangle(endx, beginy + cell_size, cell_size, (endy - beginy - cell_size )), true);
//		if (editorObjects.size() == 0) {
//			return walls;
//		} else {
//				Wall div_walls1[] = cut_wall(walls[0]);
//				Wall div_walls2[] = cut_wall(walls[1]);
//				Wall div_walls3[] = cut_wall(walls[2]);
//				Wall div_walls4[] = cut_wall(walls[3]);
//				int total_length = div_walls1.length + div_walls2.length + div_walls3.length + div_walls4.length; 
//				Wall div_walls[] = new Wall[total_length] ;
//				int k = 0;
//				for (int i = 0; i < div_walls1.length; i++) {
//					div_walls[k] = div_walls1[i];
//					k+=1;
//				}
//				for (int i = 0; i < div_walls2.length; i++) {
//					div_walls[k] = div_walls2[i];
//					k+=1;
//				}
//				for (int i = 0; i < div_walls3.length; i++) {
//					div_walls[k] = div_walls3[i];
//					k+=1;
//				}
//				for (int i = 0; i < div_walls4.length; i++) {
//					div_walls[k] = div_walls4[i];
//					k+=1;
//				}
//				return div_walls;
//		}
//	}
//}

@Override
void render_editor_object(SpriteBatch spriteBatch, EditorObject edit_obj) {
	Wall wall = (Wall) edit_obj;
	if (wall.wall == true) {
		if(wall.bound.w() > wall.bound.h()) {
			spriteBatch.draw(EditMain.texeditor.get_texture("wall2").getTexture(), edit_obj.bound.x, edit_obj.bound.y, edit_obj.bound.w, edit_obj.bound.h, 0, 0,wall.bound.w()/20,1);
		} else {
			spriteBatch.draw(EditMain.texeditor.get_texture("wall1").getTexture(), edit_obj.bound.x, edit_obj.bound.y, edit_obj.bound.w, edit_obj.bound.h, 0, 0,1,wall.bound.h()/20);
		}
	}
	else
		spriteBatch.draw(EditMain.texeditor.get_texture("edge"),edit_obj.bound.x,edit_obj.bound.y,edit_obj.bound.w,edit_obj.bound.h);
}

@Override
void render_placeholder(SpriteBatch spriteBatch, int beginx, int beginy, int endx, int endy) {
//	spriteBatch.draw(red,beginx, beginy, (endx - beginx), cell_size);
//	spriteBatch.draw(red,beginx, beginy, cell_size, (endy - beginy));
//	spriteBatch.draw(red,beginx, endy, (endx - beginx), cell_size);
//	spriteBatch.draw(red,endx, beginy, cell_size, (endy - beginy + cell_size));
	
	if (jc_draw_type.getSelectedIndex() == 0) {
		if (Math.abs(mouse_x - mouse_x2) > Math.abs(mouse_y - mouse_y2)) {
			if (mouse_x < mouse_x2) {
				spriteBatch.draw(red,mouse_x, mouse_y, (mouse_x2 - mouse_x), cell_size);
			} else {
				spriteBatch.draw(red,mouse_x2, mouse_y, (mouse_x - mouse_x2), cell_size);
			}
		} else {
			if (mouse_y < mouse_y2) {
				spriteBatch.draw(red,mouse_x, mouse_y, cell_size, (mouse_y2 - mouse_y));
			} else {
				spriteBatch.draw(red,mouse_x, mouse_y2, cell_size, (mouse_y - mouse_y2));
			}
		}
	} else {
		spriteBatch.draw(red,beginx, beginy, (endx - beginx), cell_size);
		spriteBatch.draw(red,beginx, beginy, cell_size, (endy - beginy));
		spriteBatch.draw(red,beginx, endy, (endx - beginx), cell_size);
		spriteBatch.draw(red,endx, beginy, cell_size, (endy - beginy + cell_size));
	}

	
//	if (endx - beginx > endy - beginy) {
//		spriteBatch.draw(red,mouse_x, mouse_y, (endx - beginx), cell_size);
//	} else {
//		spriteBatch.draw(red,mouse_x, mouse_y, cell_size, (endy - beginy));
//	}
}

@Override
void render_before_entity(SpriteBatch spriteBatch) {
//	spriteBatch.setColor(Color.RED);
}

@Override
void render_after_entity(SpriteBatch spriteBatch) {
	// TODO Auto-generated method stub
	
}
@Override
void right_click_action(int x, int y) {
	
}


@Override
void before_render(SpriteBatch spriteBatch) {
	// TODO Auto-generated method stub
	
}


@Override
void set_editor_panel() {
	// TODO Auto-generated method stub
	String draw_types [] = new String[] {"line", "rectangle"};
	jc_draw_type = new JComboBox<String>(draw_types);
	
	editorPanel.add(jc_draw_type);
}

@Override
void selected_editor_object_action(EditorObject editObj) {
	// TODO Auto-generated method stub
	
}

@Override
void reset_selected_editor_object() {
	super.reset_selected_editor_object();
}

@Override
EditorObject read_object(Object[] rectarr) {
	return new Wall(0,new Rectangle( ((Number)rectarr[0]).floatValue()*div_factor,((Number)rectarr[1]).floatValue()*div_factor,((Number)rectarr[2]).floatValue()*div_factor,((Number)rectarr[3]).floatValue()*div_factor), true);
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
}
