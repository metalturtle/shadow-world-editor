
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

class TextureObject extends Rectangle
{
	int texid;
	float u1,v1,u2,v2;
	TextureObject(int texid,float x, float y, float w, float h,float u1,float v1,float u2,float v2) {
		super(x, y, w, h);
		this.texid = 0;
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
	}
}
class TextureHandler
{
	int mouseclick,drawwalls;
	int mouse_x,mouse_y,mouse_x2,mouse_y2;
	int SCREEN_X,SCREEN_Y;
	SpriteBatch spriteBatch;
	ArrayList <TextureObject> textures = new ArrayList <TextureObject> ();
	Texture rock,red;
	TextureRegion rockregion;
	ShapeRenderer shaperender;
	MapEditor mapeditor;
//	OrthographicCamera camera;
	
	JPanel texturepanel;
	JTextField jt_texsize_x,jt_texsize_y,jt_texpos_x,jt_texpos_y;
	JButton jb_enter;
	DocumentListener doclistener;
	int activeTexture = 0;
	TextureHandler(MapEditor mapeditor,SpriteBatch spriteBatch,ShapeRenderer shaperender)
	{
		this.mapeditor = mapeditor;
		this.spriteBatch = spriteBatch;
		this.shaperender = shaperender;
//		this.camera = camera;
		mouseclick = 0;
		rock = new Texture(Gdx.files.internal("levels\\concrete.jpg"));
		red = new Texture(Gdx.files.internal("levels\\red.png"));
		//this.worldgrid.create_block(0,0);
		rock.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		rockregion = new TextureRegion(rock);
		controlPanel();
	}
	
	void set_screen(int x,int y)
	{
		SCREEN_X = x;
		SCREEN_Y = y;
	}
	
//	int[] convertXY(int x,int y)
//	{
//		int coord[] =new int[2];
//		x = x-(x%mapeditor.worldgrid.cell_size);
//		y = y-(y%mapeditor.worldgrid.cell_size);
//		coord[0] = x;
//		coord[1] = y;
//		return coord;
//	}
	void hover(int x,int y)
	{
		int coord[] = mapeditor.convertXY(x,y);
		mouse_x = coord[0];mouse_y = coord[1];
	}
	void touchDown(int x,int y)
	{
		int v[] = mapeditor.convertXY(x,y);
		x = v[0];y=v[1];
		mouseclick = 1;
		mouse_x = x;
		mouse_y = y;
		mouse_x2 = x;
		mouse_y2 = y;
	}
	
	void touchDragged(int x,int y)
	{
		int coord[] = mapeditor.convertXY(x,y);
		mouse_x2 = coord[0];mouse_y2 = coord[1];
	}
	void touchUp(int x,int y)
	{
		if (mouseclick == 0)
			return;
		mouseclick = 0;
		int coord[] = mapeditor.convertXY(x,y);
		x = coord[0];y=coord[1];
		if (x == mouse_x && y == mouse_y)
		{
			//mapeditor.worldgrid.set_block(x,y,1);
			return;
		}
		int beginx,endx,beginy,endy;
		if(mouse_x < x){beginx = mouse_x;endx = x;}
		else{beginx = x;endx = mouse_x;}
		if(mouse_y < y){beginy = mouse_y;endy = y;}
		else{beginy = y;endy = mouse_y;}
		textures.add(new TextureObject(0,beginx,beginy,endx-beginx,endy-beginy,0,0,1,1));
		activeTexture = textures.size()-1;
	}
	
	void rightClick(int x,int y)
	{
		for(int i =0;i<textures.size();i++)
		{
			TextureObject tex = textures.get(i);
			if((x>tex.x && x<tex.x+tex.w)&&(y>tex.y && y<tex.y+tex.h))
			{
				activeTexture = i;
				return;
			}
		}
	}
	
	void savetofile(JSONObject jo)
	{
		JSONObject texmodulejson = new JSONObject();
		JSONArray texarrjson = new JSONArray();
		for(int i=0;i<textures.size();i++)
		{
			TextureObject tex = textures.get(i);
			JSONArray texjson = new JSONArray();
			texjson.add(tex.texid);
			texjson.add(tex.x);
			texjson.add(tex.y);
			texjson.add(tex.w);
			texjson.add(tex.h);
			texjson.add(tex.u1);
			texjson.add(tex.v1);
			texjson.add(tex.u2);
			texjson.add(tex.v2);
			texarrjson.add(texjson);
		}
		texmodulejson.put("size",textures.size());
		texmodulejson.put("textures",texarrjson);
		jo.put("texturemodule",texmodulejson);
	}
	
	void readfromfile(JSONObject jo)
	{
		JSONObject texmodulejson = (JSONObject)jo.get("texturemodule");
		if (texmodulejson == null)
			return;
		int size = ((Number)texmodulejson.get("size")).intValue();
		JSONArray texarrjson = (JSONArray)texmodulejson.get("textures");
		for(int i=0;i<size;i++)
		{
			JSONArray texjson = (JSONArray)texarrjson.get(i);
			textures.add(new TextureObject(((Number)texjson.get(0)).intValue(),
					((Number)texjson.get(1)).floatValue(),
					((Number)texjson.get(2)).floatValue(),
					((Number)texjson.get(3)).floatValue(),
					((Number)texjson.get(4)).floatValue(),
					((Number)texjson.get(5)).floatValue(),
					((Number)texjson.get(6)).floatValue(),
					((Number)texjson.get(7)).floatValue(),
					((Number)texjson.get(8)).floatValue()  ));
		}
	}
	
	void render(OrthographicCamera camera)
	{
//		camera.setToOrtho(true);
//		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		shaperender.setProjectionMatrix(camera.combined);
		shaperender.setColor(Color.BLACK);
		shaperender.begin(ShapeRenderer.ShapeType.Line);
		for(int i=0;i<mapeditor.worldgrid.grid_size*mapeditor.worldgrid.cell_size;i+=mapeditor.worldgrid.cell_size)
			shaperender.line(i,0, i,mapeditor.worldgrid.grid_size*mapeditor.worldgrid.cell_size);
		for(int i=0;i<mapeditor.worldgrid.grid_size*mapeditor.worldgrid.cell_size;i+=mapeditor.worldgrid.cell_size)
			shaperender.line(0,i,mapeditor.worldgrid.grid_size*mapeditor.worldgrid.grid_size,i);
		shaperender.end();
		
		if (mouseclick == 1)
		{
			spriteBatch.begin();
			int beginx,endx,beginy,endy;
			if(mouse_x < mouse_x2){beginx = mouse_x;endx = mouse_x2;}
			else{beginx = mouse_x2;endx = mouse_x;}
			if(mouse_y < mouse_y2){beginy = mouse_y;endy = mouse_y2;}
			else{beginy = mouse_y2;endy = mouse_y;}
			spriteBatch.draw(rock,beginx,beginy,endx-beginx,endy-beginy);
			spriteBatch.end();
		}

		spriteBatch.begin();
		for(int i=0;i<textures.size();i++)
		{
			TextureObject r = textures.get(i);
			spriteBatch.draw(rock, r.x,r.y,r.w,r.h, r.u1, r.v1, r.u2,r.v2);
		}
		spriteBatch.end();
		
		spriteBatch.begin();
		for(int i=0;i<mapeditor.worldreader.walls.size();i++)
		{
			Rectangle r = mapeditor.worldreader.walls.get(i);
			spriteBatch.draw(red,r.x,r.y,r.w,r.h);
			

		}
		spriteBatch.end();
		
		spriteBatch.setColor(Color.RED);
		spriteBatch.begin();
		spriteBatch.draw(rock,mouse_x, mouse_y,mapeditor.worldgrid.cell_size,mapeditor.worldgrid.cell_size);
		spriteBatch.end();
		
		if(textures.size()>0)
		{
			shaperender.setColor(Color.BLUE);
			shaperender.begin(ShapeRenderer.ShapeType.Line);
			TextureObject activetex = textures.get(activeTexture);
			shaperender.box(activetex.x, activetex.y, 0, activetex.w, activetex.h, 0);
			shaperender.end();
		}
		spriteBatch.setColor(Color.WHITE);
	}
	

	
	void controlPanel()
	{
		texturepanel = new JPanel();
		texturepanel.setLayout(new GridBagLayout());
		jt_texsize_x = new JTextField(8);
		jt_texsize_y = new JTextField(8);
		jt_texpos_x = new JTextField(8);
		jt_texpos_y = new JTextField(8);
		jb_enter = new JButton("Enter");
		jt_texsize_x.setText(1+"");
		jt_texsize_y.setText(1+"");
		jt_texpos_x.setText(0+"");
		jt_texpos_y.setText(0+"");

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor=GridBagConstraints.WEST;
		texturepanel.add(jt_texsize_x,c);
		c.gridy = 1;
		texturepanel.add(jt_texsize_y,c);
		c.gridy = 2;
		texturepanel.add(jt_texpos_x,c);
		c.gridy = 3;
		texturepanel.add(jt_texpos_y,c);
		c.gridy = 4;
		texturepanel.add(jb_enter,c);
		//texturepanel.addComponentListener(doclistener);
		texturepanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK));
		jb_enter.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//rockregion.setRegion(0,0,rock.getWidth()*Double.parseDouble(jt_texsize_x.getText()),rock.getHeight()*Double.parseDouble(jt_texsize_y.getText()));
				if(textures.size()>0){
					TextureObject tex = textures.get(activeTexture);
					float sx,sy,sw,sh;
					sx =  Float.parseFloat(jt_texpos_x.getText());
					sy =  Float.parseFloat(jt_texpos_y.getText());
					sw = Float.parseFloat(jt_texsize_x.getText());
					sh= Float.parseFloat(jt_texsize_x.getText());
					float shiftx = sx/sw,shifty = sy/sh;
					tex.u1 = tex.x/sw+shiftx;
					tex.v1 = tex.y/sh+shifty;
					tex.u2 = (tex.x+tex.w)/sw+shiftx;
					tex.v2 = (tex.y+tex.h)/sh+shifty;
					System.out.println(shiftx+" "+shifty+" "+tex.u2+" "+tex.v2);
				}
			}
		
		});
		
//		jt_texsize_x.getDocument().addDocumentListener(doclistener);
//		jt_texsize_y.getDocument().addDocumentListener(doclistener);
//		jt_texpos_x.getDocument().addDocumentListener(doclistener);
//		jt_texpos_y.getDocument().addDocumentListener(doclistener);
	}
}
