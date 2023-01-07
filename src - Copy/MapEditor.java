
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

class Rectangle {
	float x,y,w,h;
	Rectangle (float x,float y,float w,float h){
		float x1,y1,w1,h1;
		x1 = Math.min(x, x+w);
		y1 = Math.min(y, y+h);
		w1 = Math.abs(w);
		h1 = Math.abs(h);
		this.x=x1;
		this.y=y1;
		this.w=w1;
		this.h=h1;
	}
	void set (float x,float y,float w,float h){
		float x1,y1,w1,h1;
		x1 = Math.min(x, x+w);
		y1 = Math.min(y, y+h);
		w1 = Math.abs(w);
		h1 = Math.abs(h);
		this.x=x1;
		this.y=y1;
		this.w=w1;
		this.h=h1;
	}
}


class WorldGrid
{
	boolean grid[][];
	int grid_size;
	int cell_size;
	WorldGrid(int grid_size,int cell_size)
	{
		this.grid_size = grid_size;
		this.cell_size = cell_size;
		grid = new boolean[grid_size][grid_size];
	}
	
	void set_block(int x,int y,boolean val)
	{
		x =x /20;y = y/20;
		if((x >= grid_size && y >=grid_size)|| (x < 0 && y < 0))
		{
			return;
		}
		grid[y][x] = true;
	}
//	boolean get(int x,int y)
//	{
//		if((x >= grid_size && y >=grid_size)|| (x < 0 && y < 0))
//		{
//			return false;
//		}
//		return grid[y][x];
//	}
}

class MapEditor
{
	int mouseclick,drawwalls;
	int mouse_x,mouse_y,mouse_x2,mouse_y2;
	int SCREEN_X,SCREEN_Y;
	int CAMERA_WIDTH,CAMERA_HEIGHT;
	WorldGrid worldgrid;
	ShapeRenderer shaperender;
	WorldReader worldreader;
	MapEditor(WorldGrid worldgrid,ShapeRenderer shaperender,int CAMERA_WIDTH,int CAMERA_HEIGHT)
	{
		this.worldgrid = worldgrid;
		this.shaperender = shaperender;
		mouseclick = 0;
		worldreader = new WorldReader(worldgrid);
		//this.worldgrid.create_block(0,0);
		this.CAMERA_WIDTH = CAMERA_WIDTH;
		this.CAMERA_HEIGHT = CAMERA_HEIGHT;
	}
	
	void set_screen(int x,int y)
	{
		SCREEN_X = x;
		SCREEN_Y = y;
	}
	
	int[] convertXY(int x,int y)
	{
		int coord[] =new int[2];
		int upscale = (worldgrid.cell_size/10);
		y = (Gdx.graphics.getHeight()-y);
		x = upscale*x-((upscale*x)%worldgrid.cell_size);
		y = upscale*y-((upscale*y)%worldgrid.cell_size);
		coord[0] = x;
		coord[1] = y;
		return coord;
	}
	void hover(int x,int y)
	{
		int coord[] = convertXY(x,y);
		mouse_x = coord[0];mouse_y = coord[1];
	}
	void touchDown(int x,int y)
	{
		int v[] = convertXY(x,y);
		x = v[0];y=v[1];
		mouseclick = 1;
		mouse_x = x;
		mouse_y = y;
		mouse_x2 = x;
		mouse_y2 = y;
	}
	
	void touchDragged(int x,int y)
	{
		int coord[] = convertXY(x,y);
		mouse_x2 = coord[0];mouse_y2 = coord[1];
	}
	void touchUp(int x,int y)
	{
		if (mouseclick == 0)
			return;
		mouseclick = 0;
		int coord[] = convertXY(x,y);
		x = coord[0];y=coord[1];
		if (x == mouse_x && y == mouse_y)
		{
			worldgrid.set_block(x,y,true);
			return;
		}
		int beginx,endx,beginy,endy;
		if(mouse_x < x){beginx = mouse_x;endx = x;}
		else{beginx = x;endx = mouse_x;}
		if(mouse_y < y){beginy = mouse_y;endy = y;}
		else{beginy = y;endy = mouse_y;}
		for (int i=beginx;i<=endx;i++)
			worldgrid.set_block(i, beginy,true);
		for(int i=beginx;i<=endx;i++)
			worldgrid.set_block(i,endy,true);
		for(int i=beginy;i<=endy;i++)
			worldgrid.set_block(beginx,i,true);
		for(int i=beginy;i<endy;i++)
			worldgrid.set_block(endx,i,true);	
	}
	
	void rightClick(int x,int y)
	{
		int coord[] = convertXY(x,y);
		x = coord[0];y=coord[1];
		System.out.println(x+" "+y);
		worldgrid.set_block(x, y, false);
	}
	
	void create_world()
	{
		worldreader.create_walls();
		drawwalls = 1;
	}
	
	void savetofile(JSONObject jo)
	{
		JSONObject jsonworldata = new JSONObject();
		JSONArray jsonwalls = new JSONArray();
		for(int i =0;i<worldreader.walls.size();i++)
		{
			
			JSONArray jsonrect = new JSONArray();
			Rectangle rect = worldreader.walls.get(i);
			jsonrect.add(rect.x);
			jsonrect.add(rect.y);
			jsonrect.add(rect.w);
			jsonrect.add(rect.h);
			jsonwalls.add( jsonrect);
		}
		jsonworldata.put("size",worldreader.walls.size());
		jsonworldata.put("walls",jsonwalls);
		JSONArray jsongrid = new JSONArray();
		//System.out.println("worldgrid"+worldgrid.grid);
		for(int i=0;i<worldgrid.grid_size;i++)
		{
			//jsongrid.add(worldgrid.grid[i]);
			JSONArray jsonrow = new JSONArray();
			for(int j=0;j<worldgrid.grid_size;j++)
			{
				jsonrow.add(worldgrid.grid[j][i]);
			}
			jsongrid.add(jsonrow);
		}
		jsonworldata.put("grid",jsongrid);
		jsonworldata.put("gridsize",worldgrid.grid_size);
		jsonworldata.put("cellsize",worldgrid.cell_size);
		jo.put("world", jsonworldata);
		
	}
	
	void readfromfile(JSONObject jo)
	{
		JSONObject worldjson = (JSONObject)jo.get("world");
		if (worldjson == null)
			return;
		long wallsize = (Long)worldjson.get("size");
		JSONArray walls = (JSONArray)worldjson.get("walls");
		System.out.println(worldreader.walls);
		
		for(int i=0;i<wallsize;i++)
		{
			JSONArray rectjson = (JSONArray)walls.get(i);
			Object rectarr[] = rectjson.toArray();
			worldreader.walls.add(new Rectangle(((Number)rectarr[0]).floatValue(),((Number)rectarr[1]).floatValue(),((Number)rectarr[2]).floatValue(),((Number)rectarr[3]).floatValue()));
		}
		
		JSONArray jgrid = (JSONArray)worldjson.get("grid");
		int gridsize = ((Number)worldjson.get("gridsize")).intValue();
		for(int i=0;i<gridsize;i++)
		{
			JSONArray jgridrow = (JSONArray)jgrid.get(i);
			for(int j=0;j<gridsize;j++)
			{
				boolean val = ((Boolean)jgridrow.get(j));
				
				worldgrid.set_block(i, j, val);
			}
		}
	}
	
	void draw_grid(OrthographicCamera camera)
	{
		
		shaperender.setProjectionMatrix(camera.combined);
		shaperender.setColor(Color.BLACK);
		shaperender.begin(ShapeRenderer.ShapeType.Line);
		for(int i=0;i<Math.min(worldgrid.grid_size*worldgrid.cell_size,CAMERA_WIDTH);i+=worldgrid.cell_size)
			shaperender.line(i,0, i,worldgrid.grid_size*worldgrid.cell_size);
		for(int i=0;i<Math.min(worldgrid.grid_size*worldgrid.cell_size,CAMERA_WIDTH);i+=worldgrid.cell_size)
			shaperender.line(0,i,worldgrid.grid_size*worldgrid.grid_size,i);
		shaperender.end();
		
		if (mouseclick == 1)
		{
			shaperender.begin(ShapeRenderer.ShapeType.Filled);
			int beginx,endx,beginy,endy;
			if(mouse_x < mouse_x2){beginx = mouse_x;endx = mouse_x2;}
			else{beginx = mouse_x2;endx = mouse_x;}
			if(mouse_y < mouse_y2){beginy = mouse_y;endy = mouse_y2;}
			else{beginy = mouse_y2;endy = mouse_y;}
			for (int i=beginx;i<=endx;i+=worldgrid.cell_size)
				shaperender.box(i,beginy, 0, worldgrid.cell_size, worldgrid.cell_size, 0);
			
			for(int i=beginx;i<=endx;i+=worldgrid.cell_size)
				shaperender.box(i, endy, 0, worldgrid.cell_size, worldgrid.cell_size, 0);
			
			for(int i=beginy;i<=endy;i+=worldgrid.cell_size)
				shaperender.box(beginx, i, 0, worldgrid.cell_size, worldgrid.cell_size, 0);
			
			for(int i=beginy;i<endy;i+=worldgrid.cell_size)
				shaperender.box(endx, i, 0, worldgrid.cell_size, worldgrid.cell_size, 0);
			shaperender.end();
		}
		shaperender.setColor(Color.BLACK);
		shaperender.begin(ShapeRenderer.ShapeType.Filled);
		for(int i=0;i<worldgrid.grid_size;i++)
		{
			for(int j=0;j<worldgrid.grid_size;j++)
			{
				if(worldgrid.grid[j][i]==true)
					shaperender.box(i*worldgrid.cell_size,j*worldgrid.cell_size,0,worldgrid.cell_size,worldgrid.cell_size,0);
			}
		}
		shaperender.end();
		
		shaperender.setColor(Color.RED);
		shaperender.begin(ShapeRenderer.ShapeType.Filled);
		shaperender.box(mouse_x, mouse_y, 0, worldgrid.cell_size, worldgrid.cell_size, 0);
		shaperender.end();
		

		
		shaperender.setColor(Color.WHITE);
	}
}

class WorldReader {
	ArrayList <Rectangle> walls = new ArrayList <Rectangle>();
	int rectid[][];
	boolean vert[][];
	WorldGrid wgrid;
	WorldReader(WorldGrid wgrid)
	{
		this.wgrid = wgrid;
		walls.add(new Rectangle(0,0,0,0));
		//rectid = new short[wgrid.grid_size][wgrid.grid_size];
		vert = new boolean[wgrid.grid_size][wgrid.grid_size];
	}
	
	void create_walls()
	{
		//float upscale=4;
		walls.add(new Rectangle(0,0,0,0));
		rectid = new int[wgrid.grid_size][wgrid.grid_size];
		vert = new boolean[wgrid.grid_size][wgrid.grid_size];
		for(int i=1;i<wgrid.grid_size-1;i++)
		{
			for(int j = 1;j<wgrid.grid_size-1;j++)
			{
				
			//	if(wgrid.get(j,i)==true)
				if(wgrid.grid[i][j])
				{
					//walls.add(new Rectangle(j*10,i*10,10,10));
					if(rectid[j][i-1]>0 && vert[j][i-1]==true)
					{
						Rectangle rect = walls.get(rectid[j][i-1]);
						rect.h+=wgrid.cell_size;
						walls.set(rectid[j][i-1],rect);
						rectid[j][i]=rectid[j][i-1];
						vert[j][i]=true;
					}
					else if(rectid[j-1][i]>0  && vert[j-1][i]==false)
					{
						//System.out.println("hor expand");
						Rectangle rect = walls.get(rectid[j-1][i]);
						rect.w+=wgrid.cell_size;
						walls.set(rectid[j-1][i],rect);
						rectid[j][i]=rectid[j-1][i];
						vert[j][i]=false;
						//vert[j-1][i]=false;
					}
					else
					{
						rectid[j][i] = walls.size();
						walls.add(new Rectangle(j*wgrid.cell_size,i*wgrid.cell_size,wgrid.cell_size,wgrid.cell_size));
						//if(wgrid.get(j,i-1)==true)
						if(wgrid.grid[i-1][j])
							vert[j][i]=true;
					}
				}
			}
			//System.out.println("wall no:"+walls.size());
		}
	}
	
}
