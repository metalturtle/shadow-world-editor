
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


class EntityEditor
{
	int mouseclick,drawwalls;
	int mouse_x,mouse_y,mouse_x2,mouse_y2;
	int SCREEN_X,SCREEN_Y;
	SpriteBatch spriteBatch;
	Texture rock,red,playertex;
	TextureRegion rockregion,player;
	ShapeRenderer shaperender;
	MapEditor mapeditor;
	int activeTexture = 0;
	Vector playervec;
	EntityEditor(MapEditor mapeditor,SpriteBatch spriteBatch,ShapeRenderer shaperender)
	{
		this.mapeditor = mapeditor;
		this.spriteBatch = spriteBatch;
		this.shaperender = shaperender;
		mouseclick = 0;
		rock = new Texture(Gdx.files.internal("levels\\concrete.jpg"));
		red = new Texture(Gdx.files.internal("levels\\red.png"));
		//this.worldgrid.create_block(0,0);
		playertex = new Texture(Gdx.files.internal("levels\\playeranims\\default.png"));
		player = new TextureRegion(playertex);
		playervec = new Vector(0,0);
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
	}
	
	void rightClick(int x,int y)
	{
		int coord[] = mapeditor.convertXY(x,y);
		playervec.setXY(coord[0], coord[1]);
	}
	
	void savetofile(JSONObject jo)
	{
		JSONArray ja = new JSONArray();
		ja.add(playervec.getX());
		ja.add(playervec.getY());
		jo.put("entities",ja);
	}
	void readfromfile(JSONObject jo)
	{
		JSONArray ja = (JSONArray)jo.get("entities");
		if (ja == null)
			return;
		playervec.setXY(((Number)ja.get(0)).floatValue(), ((Number)ja.get(1)).floatValue());
	}
	
	void render(OrthographicCamera camera)
	{
		//camera.setToOrtho(true);
		//camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		shaperender.setProjectionMatrix(camera.combined);
		shaperender.setColor(Color.BLACK);
		shaperender.begin(ShapeRenderer.ShapeType.Line);
		for(int i=0;i<mapeditor.worldgrid.grid_size*mapeditor.worldgrid.cell_size;i+=mapeditor.worldgrid.cell_size)
			shaperender.line(i,0, i,mapeditor.worldgrid.grid_size*mapeditor.worldgrid.cell_size);
		for(int i=0;i<mapeditor.worldgrid.grid_size*mapeditor.worldgrid.cell_size;i+=mapeditor.worldgrid.cell_size)
			shaperender.line(0,i,mapeditor.worldgrid.grid_size*mapeditor.worldgrid.grid_size,i);
		shaperender.end();
		spriteBatch.begin();
		for(int i=0;i<mapeditor.worldreader.walls.size();i++)
		{
			Rectangle r = mapeditor.worldreader.walls.get(i);
			spriteBatch.draw(red,r.x,r.y,r.w,r.h);
		}
		//spriteBatch.draw(player,playervec.getX(),playervec.getY());
		//spriteBatch.draw(player, playervec.getX()-player.getWidth()/2,playervec.getY()-player.getHeight()/2,(int)(player.getWidth()/2),(int)(player.getHeight()/2),(int) player.getWidth(), (int)player.getHeight(), 2f, 2f, 0);
		spriteBatch.draw(player, playervec.getX()-player.getRegionWidth()/2,playervec.getY()-player.getRegionHeight()/2,player.getRegionWidth()/2,player.getRegionHeight()/2, player.getRegionWidth(), player.getRegionHeight(), 2f, 2f, 0);
		spriteBatch.end();
		
	}
	
	JPanel texturepanel;
	JTextField jt_texsize_x,jt_texsize_y,jt_texpos_x,jt_texpos_y;
	JButton jb_enter;
	DocumentListener doclistener;
	
	void controlPanel()
	{
		
	}
}
