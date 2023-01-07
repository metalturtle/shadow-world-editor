import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.simple.JSONArray;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;


class Ambient extends EditorObject {

	Rectangle color;
	boolean player_visible;
	Ambient(int local_id, Rectangle bound, Rectangle color, boolean player_visible) {
		super(local_id, bound);
		this.color = color;
		this.player_visible = player_visible;
	}
	
	void set_player_visible(boolean flag) {this.player_visible = flag;}
}

public class AmbientEditor extends Editor {

	TextureHandler TexEditor;
	JTextField jt_red, jt_blue, jt_green, jt_alpha;
	JCheckBox jc_player_visible;
	public AmbientEditor(int editor_id, EditorMain EditMain) {
		super(EditMain, editor_id, "ambient");
		TexEditor = EditMain.texeditor;
	}

	@Override
	EditorObject read_object(Object[] rectarr) {
		System.out.println("reading: "+((Number)rectarr[8]).intValue()+" "+(((Number)rectarr[8]).intValue()>0));
		return new Ambient(0, new Rectangle(((Number)rectarr[0]).floatValue()*div_factor,
		((Number)rectarr[1]).floatValue()*div_factor,
		((Number)rectarr[2]).floatValue()*div_factor,
		((Number)rectarr[3]).floatValue()*div_factor),
		new Rectangle(((Number)rectarr[4]).floatValue(),
				((Number)rectarr[5]).floatValue(),
				((Number)rectarr[6]).floatValue(),
				((Number)rectarr[7]).floatValue()),
		((Number)rectarr[8]).intValue()>0
		);
	}

	@Override
	JSONArray write_object(EditorObject editObj) {
		JSONArray jsonArr = new JSONArray();
		Ambient ambient = (Ambient)editObj;
		
		jsonArr.add(ambient.bound.x/div_factor);
		jsonArr.add(ambient.bound.y/div_factor);
		jsonArr.add(ambient.bound.w/div_factor);
		jsonArr.add(ambient.bound.h/div_factor);
		jsonArr.add(ambient.color.x);
		jsonArr.add(ambient.color.y);
		jsonArr.add(ambient.color.w);
		jsonArr.add(ambient.color.h);
		System.out.println("writing: "+ambient.player_visible+" "+(ambient.player_visible ? 1 : 0));
		jsonArr.add(ambient.player_visible ? 1 : 0);
		return jsonArr;
	}

	@Override
	void selected_editor_object_action(EditorObject editObj) {
		Ambient ambient = (Ambient)editObj;
		jt_red.setText(""+ambient.color.x);
		jt_blue.setText(""+ambient.color.y);
		jt_green.setText(""+ambient.color.w);
		jt_alpha.setText(""+ambient.color.h);
		jc_player_visible.setSelected(ambient.player_visible);
	}

	@Override
	void set_editor_panel() {
		JPanel jp_red = new JPanel();
		JPanel jp_blue = new JPanel();
		JPanel jp_green = new JPanel();
		JPanel jp_alpha = new JPanel();
		JPanel jp_visible = new JPanel();
		jt_red = new JTextField(5);
		jt_blue = new JTextField(5);
		jt_green = new JTextField(5);
		jt_alpha = new JTextField(5);
		jc_player_visible = new JCheckBox();
		
		jp_red.add(new JLabel("red: "));
		jp_red.add(jt_red);
		jp_blue.add(new JLabel("blue: "));
		jp_blue.add(jt_blue);
		jp_green.add(new JLabel("green: "));
		jp_green.add(jt_green);
		jp_alpha.add(new JLabel("alpha: "));
		jp_alpha.add(jt_alpha);
		jp_visible.add(new JLabel("visible"));
		jp_visible.add(jc_player_visible);
		
		jt_red.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selected_editor_object != null) {
					Ambient ambient = (Ambient)selected_editor_object;
					ambient.color.x = Float.parseFloat(jt_red.getText());
				}
			}
		});
		
		jt_blue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selected_editor_object != null) {
					Ambient ambient = (Ambient)selected_editor_object;
					ambient.color.y = Float.parseFloat(jt_blue.getText());
				}
			}
		});
		
		jt_green.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selected_editor_object != null) {
					Ambient ambient = (Ambient)selected_editor_object;
					ambient.color.w = Float.parseFloat(jt_green.getText());
				}
			}
		});
		
		jt_alpha.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selected_editor_object != null) {
					Ambient ambient = (Ambient)selected_editor_object;
					ambient.color.h = Float.parseFloat(jt_alpha.getText());
				}
			}
		});

		jc_player_visible.addItemListener(new ItemListener() {
	         public void itemStateChanged(ItemEvent e) {         
				if(selected_editor_object != null) {
					Ambient ambient = (Ambient)selected_editor_object;
					ambient.player_visible = e.getStateChange()==1;
				}
	         }           
	      });
	     
		editorPanel.add(jp_red);
		editorPanel.add(jp_blue);
		editorPanel.add(jp_green);
		editorPanel.add(jp_alpha);
		editorPanel.add(jp_visible);
	}

	@Override
	void right_click_action(int x, int y) {
		
	}

	@Override
	EditorObject[] add_editor_object(int beginx, int beginy, int endx, int endy) {
		int x_width = 0;
		if (mouse_x2 < mouse_x) {
			x_width = endx-beginx +cell_size;
		} else {
			x_width = endx-beginx;
		}
		return new EditorObject[] {new Ambient(0,new Rectangle(beginx, beginy ,x_width,endy-beginy + cell_size),new Rectangle(1,1,1,1), false)};
	}

	@Override
	void render_editor_object(SpriteBatch spriteBatch, EditorObject edit_obj) {
		Ambient ambient = (Ambient)edit_obj;
		if (edit_obj != selected_editor_object) {
			spriteBatch.setColor(ambient.color.x,ambient.color.y,ambient.color.w,ambient.color.h);
		}
		spriteBatch.draw(TexEditor.get_texture("white"),ambient.bound.x,ambient.bound.y,ambient.bound.w,ambient.bound.h);
	}

	@Override
	void render_placeholder(SpriteBatch spriteBatch, int beginx, int beginy, int endx, int endy) {
//		spriteBatch.setColor(1,1,1,1);
		int x_width = 0;
		if (mouse_x2 < mouse_x) {
			x_width = endx-beginx +cell_size;
		} else {
			x_width = endx-beginx;
		}
		spriteBatch.draw(TexEditor.get_texture("white"),beginx,beginy ,x_width,endy-beginy + cell_size);
	}

	@Override
	void render_before_entity(SpriteBatch spriteBatch) {
//		EditMain.mapeditor.render_editor_objects(spriteBatch);
		
	}

	@Override
	void render_after_entity(SpriteBatch spriteBatch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void before_render(SpriteBatch spriteBatch) {
		// TODO Auto-generated method stub
		
	}

}
