
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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

class TextureObject extends EditorObject
{
	int texid;
	float u1,v1,u2,v2,angle;
	float texx,texy,texw,texh;
	EditorObject collideObject;
	int priority=100;
	static int MAX_PRIORITY=100;
	boolean lightblock;
	
	TextureObject (int texid, Rectangle bound,float u1,float v1,float u2,float v2,float angle) {
		super(0, new Rectangle(bound));
		this.texid = texid;
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
		this.angle = angle;
		this.priority = MAX_PRIORITY++;
	}
	
	TextureObject(int texid,float x, float y, float w, float h,float u1,float v1,float u2,float v2,float angle,boolean lightblock) {
		super(0, new Rectangle(x, y, w, h));
		this.texid = texid;
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
		this.angle = angle;
		this.lightblock = lightblock;
		this.priority = MAX_PRIORITY++;
	}
	
	void set_tex_xy(float x,float y,float w,float h) {
		texx = x;
		texy = y;
		texw = w;
		texh = h;
	}
	
	void set_tex_uv(float u,float v,float u2,float v2) {
		this.u1 = u;
		this.v1 = v;
		this.u2 = u2;
		this.v2 = v2;
	}
}

class TextureHandler extends Editor
{
	int SCREEN_X,SCREEN_Y;
//	Texture rock;
//	TextureRegion rockregion;
	
	JPanel texturepanel;
	JTextField jt_texsize_x,jt_texsize_y,jt_texpos_x,jt_texpos_y,jt_priority;
	JLabel jl_texres_x, jl_texres_y;
	JCheckBox jc_collide,jc_rotate, jc_lightblock,jc_showlighttex;
	JComboBox<String> jc_texture_list;
	JButton jb_enter, jb_open_dialog;
	DocumentListener doclistener;
	int activeTexture = 0;
	ArrayList <TextureRegion> textures;
	HashMap <String, Integer> textureNameMap;
	HashMap <String,String> textureFileNameMap;
	HashMap <String,Long> textureLastModified;
	String texture_names[],tex_file_names[];
	String texture_key_name;
	String folder_name, file_to_add;
	long modify_time;
	boolean SHOW_LIGHT_BLOCK_TEX;
	
	TextureHandler(int editor_id, EditorMain EditMain)
	{
		super(EditMain, editor_id,"texture");
//		rock = new Texture(Gdx.files.internal("levels\\concrete.jpg"));
//		rock.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
//		rockregion = new TextureRegion(rock);
		textures = new ArrayList <TextureRegion>();
		textureNameMap = new HashMap<String, Integer>();
		textureLastModified = new HashMap<String,Long>();
		textureFileNameMap = new HashMap <String,String>();
	    String[] choices = { "CHOICE 1"};
	    jc_texture_list = new JComboBox<String>(choices);
	    texture_key_name = "texture";
	    
	}
	
	@Override
	void init() {
		super.init();
		read_all_texture_files(EditMain.ResManage);
	}
	
	public TextureRegion get_texture (String name) {
		return textures.get( textureNameMap.get(name));
	}
	
	void add_texture (String file_name, String tex_key) {
		Texture texture = new Texture(Gdx.files.internal(folder_name+"/"+file_name));
		File file = new File(folder_name+"/"+file_name);
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		textures.add(new TextureRegion(texture));
		textureNameMap.put(tex_key, textures.size()-1);
		textureFileNameMap.put(tex_key,file_name);
		textureLastModified.put(file_name, file.lastModified());
	}
	
	void read_textures(ResourceManager ResManage, String key_name) {
		String all_textures[][] = ResManage.get_all_resource_dir(key_name);
		String folder = ResManage.get_resource_folder(key_name);
		Arrays.sort(all_textures,new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				String file_str1[] = (String[])o1;
				String file_str2[] = (String[])o2;
				return file_str1[0].compareTo(file_str2[0]);
			}
		});
		folder_name = folder;
		this.texture_names = new String[all_textures.length];
		this.tex_file_names = new String[all_textures.length];
		for (int i = 0; i < all_textures.length; i++) {
			add_texture(all_textures[i][1], all_textures[i][0]);
			this.texture_names[i] = all_textures[i][0];
			this.tex_file_names[i] = all_textures[i][1];
		}
		jc_texture_list.setModel(new JComboBox<String>(this.texture_names).getModel());
	}
	
	void read_all_texture_files(ResourceManager ResManage) {
		read_textures(ResManage,"texture");
//		read_textures(ResManage,"basic_texture");
	}
	
	void set_screen(int x,int y) {
		SCREEN_X = x;
		SCREEN_Y = y;
	}
	
	void convert_xy_to_uv(TextureObject tex) {
		Rectangle bound = tex.bound;
//		tex.u1 = tex.texx == 0 ? 0 : bound.w/(tex.texx*10);
//		tex.v1 = tex.texy == 0 ? 0 : bound.h/(tex.texy*10);
		tex.u1 = tex.texx == 0 ? 0 : (tex.texx*10)/bound.w;
		tex.v1 = tex.texy == 0 ? 0 : (tex.texy*10)/bound.h;
		tex.u2 = (tex.texw) == 0 ? 0 : (tex.u1+bound.w()/((tex.texw)*10));
		tex.v2 = (tex.texh) == 0 ? 0 : (tex.v1+bound.h()/((tex.texh)*10));
		System.out.println("tex uv: "+tex.u1+" "+tex.v1+" "+tex.u2+" "+tex.v2+" "+tex.texx+" "+tex.texy+" "+tex.texw+" "+tex.texh);
	}
	
	void convert_uv_to_xy(TextureObject tex) {
		Rectangle bound = tex.bound;
		tex.texx = tex.u1 == 0 ? 0 : ((bound.w*tex.u1)/10);
		tex.texy = tex.v1 == 0 ? 0 : ((bound.h*tex.v1)/10);
		tex.texw = (tex.u2-tex.u1)==0?0:(bound.w()/((tex.u2-tex.u1)*10));
		tex.texh = (tex.v2-tex.v1)==0?0:(bound.h()/((tex.v2-tex.v1)*10));
	}

	@Override
	EditorObject[] add_editor_object (int beginx, int beginy, int endx, int endy) {
		int x_width = 0;
		if (mouse_x2 < mouse_x) {
			x_width = endx-beginx +cell_size;
		} else {
			x_width = endx-beginx;
		}
		return new TextureObject[] {new TextureObject(jc_texture_list.getSelectedIndex(), beginx, beginy, x_width, endy-beginy + cell_size, 0,0,x_width/10,(endy-beginy + cell_size)/10,0,false)};
	}
	
	@Override
	void render_editor_object(SpriteBatch spriteBatch, EditorObject edit_obj) {
		TextureObject texObj = (TextureObject) edit_obj;
		Rectangle bound = texObj.bound;
		TextureRegion texreg = textures.get(texObj.texid);

//		texreg.setU(texObj.u1);
//		texreg.setV( texObj.v1);
//		texreg.setU2(texObj.u2);
//		texreg.setV2(texObj.v2);
//		texreg.setRegion(bound.x(),bound.y(),bound.w(),bound.h());
		Texture tex = texreg.getTexture();
		float f= (float)Math.cos(Math.toRadians(texObj.angle))*0+1;
		float w = f*bound.w() + (1-f)*bound.h();
		float h = f*bound.h() + (1-f)*bound.w();
		if(SHOW_LIGHT_BLOCK_TEX && texObj.lightblock) {
			spriteBatch.setColor(0f,0f,0f,1f);
		}
//		spriteBatch.draw(texreg,bound.x(),bound.y(),bound.w()/2,bound.h()/2,w, h, 1f, 1f,(int)texObj.angle);.
		spriteBatch.draw(texreg.getTexture(),bound.x(),bound.y(),w,h,texObj.u1,texObj.v1,texObj.u2,texObj.v2);
		spriteBatch.setColor(1f,1f,1f,1f);
//		spriteBatch.draw(texreg, bound.x()-texreg.getRegionWidth()/2, bound.y()-texreg.getRegionHeight()/2,0,0,texreg.getRegionWidth(),texreg.getRegionHeight(),1,1,texObj.angle,true);
//		texreg.
//		spriteBatch.draw(texreg,bound.x(),bound.y(),bound.w(),bound.h());
	}

	@Override
	void render_placeholder(SpriteBatch spriteBatch, int beginx, int beginy, int endx, int endy) {
		int x_width = 0;
		if (mouse_x2 < mouse_x) {
			x_width = endx-beginx +cell_size;
		} else {
			x_width = endx-beginx;
		}
		spriteBatch.draw(textures.get(jc_texture_list.getSelectedIndex()),beginx,beginy ,x_width,endy-beginy + cell_size);
	}

	@Override
	void render_before_entity(SpriteBatch spriteBatch) {

	}

	@Override
	void render_after_entity(SpriteBatch spriteBatch) {
		// TODO Auto-generated method stub
//		EditMain.mapeditor.render_editor_objects(spriteBatch);
	}
	
	@Override
	void right_click_action(int x, int y) {
		
	}

	@Override
	void before_render(SpriteBatch spriteBatch) {
		// TODO Auto-generated method stub
		if(file_to_add != null) {
			add_texture (file_to_add, file_to_add.substring(0,file_to_add.indexOf('.')));
  		  	EditMain.ResManage.add_resource(texture_key_name, file_to_add.substring(0,file_to_add.indexOf('.')), file_to_add);
			file_to_add = null;
		}
		if(modify_time < System.currentTimeMillis()) {
			File folder = new File(folder_name);
			Set <String> file_keys = textureFileNameMap.keySet();
			for (String file_key: file_keys) {
				String filename = textureFileNameMap.get(file_key);
				File file = new File(folder_name+"//"+filename);
				long lastModified = textureLastModified.get(filename);
				if(lastModified < file.lastModified()) {
					int id = textureNameMap.get(file_key);
					Texture texture = new Texture(Gdx.files.internal(folder_name+"/"+filename));
					texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
					textures.set(id, new TextureRegion(texture));
				}
			}
			modify_time = System.currentTimeMillis() + 5000;
		}
	}

	@Override
	void set_editor_panel() {
		jt_texsize_x = new JTextField(6);
		jt_texsize_y = new JTextField(6);
		jt_texpos_x = new JTextField(6);
		jt_texpos_y = new JTextField(6);
		jl_texres_x = new JLabel("");
		jl_texres_y = new JLabel("");
		jc_collide = new JCheckBox();
		jc_collide.setEnabled(false);
		jt_priority = new JTextField(6);
		jc_rotate = new JCheckBox();
		jc_lightblock = new JCheckBox();
		jc_showlighttex = new JCheckBox();
		JLabel jl_texpos_x = new JLabel("u1");
		JLabel jl_texpos_y = new JLabel("v1");
		JLabel jl_texsize_x = new JLabel("u2");
		JLabel jl_texsize_y = new JLabel("v2");
		JPanel jp_texpos_x = new JPanel();
		JPanel jp_texpos_y = new JPanel();
		JPanel jp_texsize_x = new JPanel();
		JPanel jp_texsize_y = new JPanel();
		JPanel jp_texres_x = new JPanel();
		JPanel jp_texres_y = new JPanel();
		JPanel jp_collide = new JPanel();
		JPanel jp_priority = new JPanel();
		JPanel jp_angle = new JPanel();
		JPanel jp_lightblock = new JPanel();
		JPopupMenu popupMenu = new JPopupMenu();
		jb_open_dialog = new JButton("add file");

		jp_texres_x.add(new JLabel("width"));
		jp_texres_x.add(jl_texres_x);
		jp_texres_y.add(new JLabel("height"));
		jp_texres_y.add(jl_texres_y);
		jp_texpos_x.add(jl_texpos_x);
		jp_texpos_x.add(jt_texpos_x);
		jp_texpos_y.add(jl_texpos_y);
		jp_texpos_y.add(jt_texpos_y);
		jp_texsize_x.add(jl_texsize_x);
		jp_texsize_x.add(jt_texsize_x);
		jp_texsize_y.add(jl_texsize_y);
		jp_texsize_y.add(jt_texsize_y);
		jp_collide.add(new JLabel("collide"));
		jp_collide.add(jc_collide);
		jp_priority.add(new JLabel("order"));
		jp_priority.add(jt_priority);
		jp_angle.add(new JLabel("rotate 90"));
		jp_angle.add(jc_rotate);
		jp_lightblock.add(new JLabel("light block"));
		jp_lightblock.add(jc_lightblock);
		jp_lightblock.add(new JLabel("show lightblock tex"));
		jp_lightblock.add(jc_showlighttex);
		editorPanel.add(jp_texres_x);
		editorPanel.add(jp_texres_y);
		editorPanel.add(jp_texpos_x);
		editorPanel.add(jp_texpos_y);
		editorPanel.add(jp_texsize_x);
		editorPanel.add(jp_texsize_y);
		editorPanel.add(jp_angle);
		editorPanel.add(jp_lightblock);
		editorPanel.add(jp_collide);
		editorPanel.add(jp_priority);
		editorPanel.add(popupMenu);
	    editorPanel.add(jc_texture_list);
		editorPanel.add(jb_open_dialog);
		
		
        jt_texpos_x.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextureObject texObj = (TextureObject)selected_editor_object;
				if(texObj != null) {
					texObj.texx = Float.parseFloat( jt_texpos_x.getText());
					convert_xy_to_uv(texObj);
				}
			} });
        
        jt_texpos_y.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextureObject texObj = (TextureObject)selected_editor_object;
				if(texObj != null) {
					texObj.texy = Float.parseFloat( jt_texpos_y.getText());
					convert_xy_to_uv(texObj);
				}
//				if(texObj != null && texObj.collideObject != null) texObj.collideObject.bound.set(texObj.bound);
			} });
        jt_texsize_x.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextureObject texObj = (TextureObject)selected_editor_object;
				if(texObj != null) {
					texObj.texw = Float.parseFloat( jt_texsize_x.getText());
					convert_xy_to_uv(texObj);
				}
			} });
        jt_texsize_y.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextureObject texObj = (TextureObject)selected_editor_object;
				if(texObj != null) {
					texObj.texh = Float.parseFloat( jt_texsize_y.getText());
					convert_xy_to_uv(texObj);
				}
			} });
        jc_rotate.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				TextureObject texObj = (TextureObject)selected_editor_object;
				if(texObj != null) {
					Rectangle bound = texObj.bound;
					if(e.getStateChange() == 1) {
						texObj.angle = 90;
						if(texObj.collideObject != null)
							texObj.collideObject.bound.set(bound.x()+(bound.w()-bound.h())*0.5f,bound.y()-(bound.w()-bound.h())*0.5f,bound.h(),bound.w());	
					}
					else {
						texObj.angle = 0;
						if(texObj.collideObject != null)
							texObj.collideObject.bound.set(bound);
					}
				}

			} });
        jc_texture_list.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		TextureObject texObj = (TextureObject)selected_editor_object;
        		if(texObj != null)	texObj.texid = jc_texture_list.getSelectedIndex();
        	}
        });
        jb_open_dialog.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		JFileChooser fileChooser = new JFileChooser();
        		fileChooser.setCurrentDirectory(new File(EditMain.ResManage.get_resource_folder(texture_key_name)));
        		if (fileChooser.showOpenDialog(editorPanel) == JFileChooser.APPROVE_OPTION) {
        		  File file = fileChooser.getSelectedFile();
        		  String file_name = file.getName();
        		  file_to_add = file_name;
//        		  this.texture_names/
        		  String new_texture_names[] = new String[texture_names.length+1];
        		  for(int i = 0; i < texture_names.length;i++) {
        			  new_texture_names[i] = texture_names[i];
        		  }
        		  new_texture_names[texture_names.length] = file_name.substring(0,file_name.indexOf('.'));
        		  texture_names = new_texture_names;
        		  jc_texture_list.setModel(new JComboBox<String>(texture_names).getModel());
        		  System.out.println("added to combo box");
        		  // load from file
        		}
        	}
        });
        
		jc_collide.addItemListener(new ItemListener() {
	         public void itemStateChanged(ItemEvent e) {         
				if(selected_editor_object != null) {
					TextureObject texObj = (TextureObject)selected_editor_object;
					if(e.getStateChange()==1) {
						if(texObj.collideObject != null) {
							EditMain.collisioneditor.removeList.add(texObj.collideObject);
						}
						CollisionObject colObj = new CollisionObject(0,texObj.bound);
						colObj.textureObject = texObj;
						texObj.collideObject = colObj;
						EditMain.collisioneditor.addingList.add(colObj);
					} else {
						if(texObj.collideObject != null) {
							EditMain.collisioneditor.removeList.add(texObj.collideObject);
							texObj.collideObject=null;
						}
						
					}
				}
				
	         }           
	      });
		
		jc_lightblock.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(selected_editor_object != null) {
					TextureObject texObj = (TextureObject)selected_editor_object;
					texObj.lightblock=e.getStateChange()==1;
				}
			}
		});
		
		jc_showlighttex.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				SHOW_LIGHT_BLOCK_TEX = e.getStateChange()==1;
			}
			
		});
		
		jt_priority.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selected_editor_object != null) {
					TextureObject texObj = (TextureObject)selected_editor_object;
					texObj.priority = Integer.parseInt(jt_priority.getText());
					sort_textures();
				}
			}
			
		});
	}
	
	@Override
	void selected_editor_object_action(EditorObject editObj) {
		TextureObject texObj = (TextureObject) editObj;
		jl_texres_x.setText(textures.get(texObj.texid).getTexture().getWidth()+"");
		jl_texres_y.setText(textures.get(texObj.texid).getTexture().getHeight()+"");
		jt_texpos_x.setText(texObj.texx+"");
		jt_texpos_y.setText(texObj.texy+"");
		jt_texsize_x.setText(texObj.texw+"");
		jt_texsize_y.setText(texObj.texh+"");
		jc_rotate.setEnabled(true);
		jc_rotate.setSelected(texObj.angle>0);
		jc_texture_list.setSelectedIndex(texObj.texid);
		jc_collide.setEnabled(true);
		jc_lightblock.setEnabled(true);
		jc_lightblock.setSelected(texObj.lightblock);
		jt_priority.setText(texObj.priority+"");
		if(texObj.collideObject != null) {
			jc_collide.setSelected(true);
		}
	}
	
	@Override
	void reset_selected_editor_object() {
		super.reset_selected_editor_object();
		jl_texres_x.setText("");
		jl_texres_y.setText("");
		jt_selected_id.setText("");
		jt_texpos_x.setText("");
		jt_texpos_y.setText("");
		jt_texsize_x.setText("");
		jt_texsize_y.setText("");
		jt_priority.setText("");
		jc_lightblock.setEnabled(false);
		jc_lightblock.setSelected(false);
		jc_rotate.setSelected(false);
		jc_rotate.setEnabled(false);
		jc_collide.setSelected(false);
		jc_collide.setEnabled(false);
	}

	@Override
	EditorObject read_object(Object[] rectarr) {
		TextureObject texobj = new TextureObject(textureNameMap.get(rectarr[0]),
		((Number)rectarr[1]).floatValue()*div_factor,
		((Number)rectarr[2]).floatValue()*div_factor,
		((Number)rectarr[3]).floatValue()*div_factor,
		((Number)rectarr[4]).floatValue()*div_factor,
		((Number)rectarr[5]).floatValue(),
		((Number)rectarr[6]).floatValue(),
		((Number)rectarr[7]).floatValue(),
		((Number)rectarr[8]).floatValue(),
		((Number)rectarr[9]).floatValue(),
//		false
		((Boolean)rectarr[10])
		);
		convert_uv_to_xy(texobj);
////		int texid, Rectangle bound,float u1,float v1,float u2,float v2,float angle
//		TextureObject copyTex = new TextureObject(0,new Rectangle(),texobj.texx,texobj.texy,texobj.texw,texobj.texh,0);
//		copyTex.set_tex_xy(texobj.u1, texobj.v1, texobj.u2, texobj.v2);
//		texobj.set_tex_uv(copyTex.u1,copyTex.v1,copyTex.u2,copyTex.v2);
//		texobj.set_tex_xy(copyTex.texx, copyTex.texy, copyTex.texw,copyTex.texh);
		
		System.out.println("texobj: "+texobj.u1+" "+texobj.v1+" "+texobj.u2+" "+texobj.v2+" "+texobj.texx+" "+texobj.texy+" "+texobj.texw+" "+texobj.texh);
		return texobj;
	}

	@Override
	JSONArray write_object(EditorObject editObj) {
		JSONArray jsonArr = new JSONArray();
		TextureObject tex = (TextureObject)editObj;
		for (int i = 0; i < texture_names.length; i++) {
			System.out.println("texnames: "+texture_names[i]+" "+i+" "+tex.texid);
		}
		jsonArr.add(texture_names[tex.texid]);
		jsonArr.add(tex.bound.x/div_factor);
		jsonArr.add(tex.bound.y/div_factor);
		jsonArr.add(tex.bound.w/div_factor);
		jsonArr.add(tex.bound.h/div_factor);
		jsonArr.add(tex.u1);
		jsonArr.add(tex.v1);
		jsonArr.add(tex.u2);
		jsonArr.add(tex.v2);
		jsonArr.add(tex.angle);
		jsonArr.add(tex.lightblock);
		return jsonArr;
	}
	
	@Override
	public void delete() {
		if(selected_editor_object != null) {
			TextureObject texObj = (TextureObject)selected_editor_object;
			if(texObj.collideObject != null) {
				EditMain.collisioneditor.removeList.add(texObj.collideObject);
			}
		}
		super.delete();
	}
	
	@Override
	public void after_modified_selected() {
		TextureObject texObj = (TextureObject)selected_editor_object;
		if(texObj.collideObject != null)texObj.collideObject.bound.set(texObj.bound);
	}
	
	public Set get_used_texture_list() {
		HashSet <String> textureUsed = new HashSet<String>();
		for(String texnames: tex_file_names) {
			textureUsed.add(texnames);
		}
		
//		for(EditorObject editObj: editorObjects) {
//			TextureObject texObj = (TextureObject)editObj;
//			textureUsed.add(texture_names[texObj.texid]);
//		}
//		return textureUsed.toArray();
//		JSONArray jsonArr = new JSONArray();
//		for(String tex_name: textureUsed) {
//			jsonArr.add(tex_name);
//		}
		return textureUsed;
	}
	
	public void sort_textures() {
		Collections.sort(editorObjects,new Comparator<EditorObject>() {

			@Override
			public int compare(EditorObject o1, EditorObject o2) {
				TextureObject texObj1 = (TextureObject)o1;
				TextureObject texObj2 = (TextureObject)o2;
				
				return texObj1.priority - texObj2.priority;
			}
			
		});
	}
}
