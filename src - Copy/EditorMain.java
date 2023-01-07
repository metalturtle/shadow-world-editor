
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JFrame;









import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;




class HelloWorld implements ApplicationListener,InputProcessor  {

	
    static {
        GdxNativesLoader.load();
    }
    int MODE;
    int TARGET_FPS,SCREEN_X,SCREEN_Y,CAMERA_WIDTH,CAMERA_HEIGHT;
    float CAMERA_X,CAMERA_Y;
    int GRID_SIZE;
    float ZOOM,CELL_SIZE;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;
    ShapeRenderer shaperender;
    WorldGrid worldgrid;
    MapEditor mapeditor;
    LightHandler lighteditor;
    TextureHandler texeditor;
    JFrame frame;
    EntityEditor enteditor;
    JSONParser parser;
    void GameInit(boolean server) 
    {	
    	TARGET_FPS = 30;
    	SCREEN_X = 1024;
    	SCREEN_Y = 768;
    	CAMERA_WIDTH=SCREEN_X*2;
    	CAMERA_HEIGHT=SCREEN_Y*2;
   	 	LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
	    cfg.title = "graphicsdemo";
	    cfg.foregroundFPS = TARGET_FPS;
	    cfg.width = SCREEN_X ;
	    cfg.height = SCREEN_Y;
	    new LwjglApplication(this, cfg);
    }
    
	@Override
	public void create() {
		Gdx.graphics.setVSync(true);	
		Gdx.input.setInputProcessor(this);
		
		spriteBatch = new SpriteBatch();
		camera = new OrthographicCamera();
		shaperender = new ShapeRenderer();
		camera.setToOrtho(false,CAMERA_WIDTH,CAMERA_HEIGHT);
		worldgrid = new WorldGrid(2000,20);
		mapeditor = new MapEditor(worldgrid,shaperender,CAMERA_WIDTH,CAMERA_HEIGHT);
		texeditor = new TextureHandler(mapeditor,spriteBatch,shaperender);
		lighteditor = new LightHandler(mapeditor,texeditor,spriteBatch,shaperender);
		enteditor = new EntityEditor(mapeditor,spriteBatch,shaperender);
		frame = new JFrame();
		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;c.gridy=0;
		c.anchor = GridBagConstraints.NORTH;
		frame.add(lighteditor.lightpanel,c);
		c.gridy=1;
		frame.add(texeditor.texturepanel,c);
		frame.setBounds(200,200,200,800);
		frame.setVisible(true);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		CAMERA_X = 0;
		CAMERA_Y = 0;
		ZOOM = 1;
		GRID_SIZE = 1000;
		CELL_SIZE = 10;
		MODE = 0;

		readfromfile();
		
	}
	
	void savetofile()
	{
		JSONObject jo = new JSONObject(); 
		mapeditor.savetofile(jo);
		lighteditor.savetofile(jo);
		texeditor.savetofile(jo);
		enteditor.savetofile(jo);
//		jo.put("lights",lighteditor.lights);
//		jo.put("textures",texeditor.textures);
//		jo.put("entities",enteditor.playervec);
        PrintWriter pw;
		try {
			pw = new PrintWriter("C:\\Users\\Madan Rao\\Desktop\\eclipse-workspace\\WorldEditor\\level.json");
	        pw.write(jo.toJSONString()); 
	        pw.flush(); 
	        pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
 	
	}

	void readfromfile()
	{
		try {
			Object obj = new JSONParser().parse(new FileReader("level.json"));
			 JSONObject jo = (JSONObject) obj; 
			 mapeditor.readfromfile(jo);
			 lighteditor.readfromfile(jo);
			 texeditor.readfromfile(jo);
			 enteditor.readfromfile(jo);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 void rendergraphics() {

		Gdx.gl.glClearColor(1,1,1,1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.setToOrtho(false,CAMERA_WIDTH,CAMERA_HEIGHT);
		camera.translate(CAMERA_X,CAMERA_Y);
		camera.update();
		if (MODE == 0)
		{
			mapeditor.draw_grid(camera);
		}
		if(MODE == 1)
		{
			lighteditor.render(camera,CAMERA_WIDTH,CAMERA_HEIGHT);
		}
		if(MODE == 2)
		{
			texeditor.render(camera);
		}
		if(MODE == 3)
		{
			enteditor.render(camera);
		}

	}
	
	long count = 0;
	@Override
	public void render() {
		try{

		rendergraphics();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public void dispose() { 
		spriteBatch.dispose();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resize(int x, int y) {
	    camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
	    SCREEN_X =x;
		SCREEN_Y=y;
		if(MODE == 2){
			lighteditor.resize( x, y);
		}
	}

	@Override
	public void resume() {
	}

	@Override
	public boolean keyDown(int e) {
		//System.out.println("key press:"+(char)e+" "+Input.Keys.W);

		return true;
	}

	@Override
	public boolean keyTyped(char e) {
		// TODO Auto-generated method stub
		//keys[e] = false;
		
		//gameobject.gamehandler.setinput_keyboard(keys);
		return false;
	}

	@Override
	public boolean keyUp(int e) {
		if(MODE == 0){
			if(e == Input.Keys.M)
			{
				mapeditor.create_world();
			}
		}
		if(MODE == 1)
		{
			if(e==Input.Keys.P)
			{
				lighteditor.add_light(Gdx.input.getX(),Gdx.input.getY());
			}
		}
		
		
		if(e == Input.Keys.NUM_1)
		{
			MODE = 0;
		}
		if(e == Input.Keys.NUM_2)
		{
			MODE = 1;
		}
		if(e == Input.Keys.NUM_3)
		{
			MODE = 2;
		}
		if(e == Input.Keys.NUM_4)
		{
			MODE= 3;
		}
		if(e== Input.Keys.S )
		{
			savetofile();
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		if (MODE == 0)
		{
		mapeditor.hover(x, y);
		}
		return true;
	}

	@Override
	public boolean scrolled(int arg0) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		if (MODE == 0)
		{
		if(button == Input.Buttons.LEFT)
			mapeditor.touchDown(x,y);
		if(button == Input.Buttons.RIGHT)
			mapeditor.rightClick(x, y);
		}
		if(MODE == 1)
		{
			if(button == Input.Buttons.RIGHT)
				lighteditor.rightClick(x, y);
		}
		if (MODE == 2)
		{
		if(button == Input.Buttons.LEFT)
			texeditor.touchDown(x,y);
		if(button == Input.Buttons.RIGHT)
			texeditor.rightClick(x, y);
		}
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int arg2) {
		if (MODE == 0)
		{
		mapeditor.touchDragged(x,y);
		}
		if (MODE == 2)
		{
		texeditor.touchDragged(x,y);
		}		
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if(MODE == 0)
		{
		mapeditor.touchUp(x,y);
		}
		if(MODE == 1)
		{
			if(button == Input.Buttons.LEFT)
				lighteditor.touchUp(x,y);
		}
		if(MODE == 2)
		{
		texeditor.touchUp(x,y);
		}
		if(MODE == 3)
		{
			if(button == Input.Buttons.RIGHT)
			enteditor.rightClick(x, y);
		}
		return true;
	}
}

public class EditorMain
{
	public static void main(String args[])
	{
//		new HelloWorld().GameInit(true);
		new HelloWorld().GameInit(false);
	}
}
