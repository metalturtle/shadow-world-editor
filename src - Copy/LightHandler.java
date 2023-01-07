
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;









import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;


class Light extends Vector
{
	int lightSize;
	float RED,BLUE,GREEN;
	public Light(float x, float y,int lightSize) {
		super(x, y);
		this.lightSize = lightSize;
	}
	void setColor(float R,float G,float B)
	{
		RED = R;
		BLUE = B;
		GREEN = G;
	}
}

class LightHandler {
	WorldReader worldreader;
	ShaderProgram shadowMapShader,shadowRenderShader,bumpmapShader,combineShader;
	public static final float DEFAULT_LIGHT_Z = 0.075f;
	public static final float AMBIENT_INTENSITY = 1f;
	public static final float LIGHT_INTENSITY = 2f;
	public static final Vector3 LIGHT_POS = new Vector3(0f,0f,0.01f);
	public static final Vector3 LIGHT_COLOR = new Vector3(1f, 1f, 1f);
	public static final Vector3 AMBIENT_COLOR = new Vector3(0.4f,0.4f,0.4f);
	public static final Vector3 FALLOFF = new Vector3(.4f, 3f, 20f);
    FrameBuffer occludersFBO,shadowMapFBO,shadarrFBO[],lightFBO,bumpmapFBO,basemapFBO;
    Texture lightMapTexture,bumpMapTexture,baseMapTexture,rock,red;
    TextureRegion occluders,shadowMap1D,rocktex,lighttexreg;
	int SCREEN_X=600,SCREEN_Y=600;
	int SHADER_RES_X=600,SHADER_RES_Y=600;
	SpriteBatch spriteBatch;
	OrthographicCamera lightcamera;
	ArrayList <Light> lights = new ArrayList <Light>();
	MapEditor mapeditor;
	TextureHandler texhandler;
	JFrame frame;
	JTextField jt_lightsize;
	JButton jb_lightsize,jb_color;
	int lightSize = 128;
	int activeLight = 0;
	ShapeRenderer shaperender;
	LightHandler(MapEditor mapeditor,TextureHandler texhandler,SpriteBatch spriteBatch,ShapeRenderer shaperender)
	{
		this.mapeditor = mapeditor;
		this.spriteBatch = spriteBatch;
		this.texhandler = texhandler;
		this.shaperender = shaperender;
		this.lightcamera = new OrthographicCamera();
	    String VERTSHADER = Gdx.files.internal("shaders\\pass.vert").readString();
	    shadowMapShader = new ShaderProgram(
	    		VERTSHADER,
	            Gdx.files.internal("shaders\\shadowmap1D.glsl").readString()
		);

		shadowRenderShader = new ShaderProgram(
				VERTSHADER,
	            Gdx.files.internal("shaders\\shadowRender.frag").readString()
		);			

		bumpmapShader = new ShaderProgram(
				VERTSHADER,
	            Gdx.files.internal("shaders\\bumplight.glsl").readString()
		);				
			
		combineShader = new ShaderProgram(
				VERTSHADER,
				Gdx.files.internal("shaders/fragment.glsl").readString()
		);
		 combineShader.begin();
		 combineShader.setUniformi("u_normals", 1); 
		 combineShader.setUniformf("LightColor", LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, LIGHT_INTENSITY);
//		 combineShader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);
//		 combineShader.setUniformf("Falloff", FALLOFF);
		 combineShader.end();
		 occludersFBO = new FrameBuffer(Format.RGBA8888, 512, 512, false);
		 occluders = new TextureRegion(occludersFBO.getColorBufferTexture());
		 occluders.flip(false, true);
		 
		 shadarrFBO = new FrameBuffer[16];
		 for (int p=0;p<16;p++)
		 {
		 	shadarrFBO[p] = new FrameBuffer(Format.RGBA8888,512,1,false);
		 }
		 
		 System.out.println("light size: "+Gdx.graphics.getWidth()+" "+Gdx.graphics.getHeight());
		 lightFBO = new FrameBuffer(Format.RGBA8888,Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),false);
		 lightMapTexture = lightFBO.getColorBufferTexture();
		 
		rock = new Texture(Gdx.files.internal("levels/concrete.jpg"));
		red = new Texture(Gdx.files.internal("levels/red.png"));
		rock.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		addControlPanel();
	}
	
	int[] convertXY(int x,int y)
	{
		int coord[] =new int[2];
		int upscale = (mapeditor.worldgrid.cell_size/10);
		x = upscale*x-((upscale*x)%mapeditor.worldgrid.cell_size);
		y = upscale*y-((upscale*y)%mapeditor.worldgrid.cell_size);
		coord[0] = x;
		coord[1] = y;
		return coord;
	}
	
	void add_light(int x,int y)
	{
		int v[] = convertXY(x,y);
		x = v[0];y=v[1];
		Light l = new Light(x,y,lightSize);
		l.setColor(LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z);
		lights.add(l);
	}
	
	void set(int x,int y){
//		int v[] = convertXY(x,y);
//		x = v[0];y=v[1];
		if(lights.size() > 0)
		{
			Light l = lights.get(activeLight);
			//l.setColor(LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z);
			l.setXY(x, y);
		}
	}
	
	void touchUp(int x, int y)
	{
		int v[] = convertXY(x,y);
		x = v[0];y=v[1];
		set(x,y);
	}
	
	void rightClick(int x,int y)
	{
		int v[] = convertXY(x,y);
		x = v[0];y=v[1];
		for(int i =0;i<lights.size();i++)
		{
			Light light = lights.get(i);
			if((x>light.getX()-light.lightSize/2 && x<light.getX()+light.lightSize/2)&&(y>light.getY()-light.lightSize/2 && y<light.getY()+light.lightSize/2))
			{
				activeLight = i;
				return;
			}
		}
	}
	
	void savetofile(JSONObject jo)
	{
		JSONArray lightarrjson = new JSONArray();
		for(int i=0;i<lights.size();i++)
		{
			Light light = lights.get(i);
			JSONArray lightjson = new JSONArray();
			lightjson.add(light.getX());
			lightjson.add(light.getY());
			lightjson.add(light.lightSize);
			lightjson.add(light.RED);
			lightjson.add(light.BLUE);
			lightjson.add(light.GREEN);
			lightarrjson.add(lightjson);
		}
		JSONObject lightmodule = new JSONObject();
		lightmodule.put("lights",lightarrjson);
		lightmodule.put("size",lights.size());
		jo.put("lightmodule",lightmodule);
	}
	
	void readfromfile(JSONObject jo)
	{
		JSONObject lightmodule = (JSONObject)jo.get("lightmodule");
		if (lightmodule == null)
			return;
		int size = ((Number)lightmodule.get("size")).intValue();
		JSONArray lightarrjson = (JSONArray)lightmodule.get("lights");
		for(int i=0;i<size;i++)
		{
			JSONArray lightjson = (JSONArray)lightarrjson.get(i);
			Light l = new Light(((Number)lightjson.get(0)).floatValue(),((Number)lightjson.get(1)).floatValue(),((Number)lightjson.get(2)).intValue());
			l.RED = ((Number)lightjson.get(3)).floatValue();
			l.BLUE = ((Number)lightjson.get(4)).floatValue();
			l.GREEN = ((Number)lightjson.get(5)).floatValue();
			lights.add(l);
		}
	}
	
	void rendlight(OrthographicCamera camera,float CAMERA_WIDTH,float CAMERA_HEIGHT)
	{
		for(int i =0;i<lights.size();i++)
		{
			Light visp = lights.get(i);
			occludersFBO.begin();
			Gdx.gl.glClearColor(0f,0f,0f,0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			lightcamera.setToOrtho(false, visp.lightSize, visp.lightSize);
			lightcamera.translate(visp.getX() - visp.lightSize/2f, visp.getY() - visp.lightSize/2f);
			lightcamera.update();
			spriteBatch.setProjectionMatrix(lightcamera.combined);
			spriteBatch.setShader(null);
			spriteBatch.begin();
			for(Rectangle wall : mapeditor.worldreader.walls)
			{
				spriteBatch.draw(red,wall.x,wall.y,wall.w,wall.h);	
			}				
			spriteBatch.end();
			occludersFBO.end();

			shadarrFBO[i].begin();
			Gdx.gl.glClearColor(0f,0f,0f,0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			spriteBatch.setShader(shadowMapShader);
			spriteBatch.begin();
			shadowMapShader.setUniformf("resolution", visp.lightSize, visp.lightSize);
			lightcamera.setToOrtho(false, visp.lightSize, shadarrFBO[i].getHeight());
			spriteBatch.setProjectionMatrix(lightcamera.combined);
			spriteBatch.draw(occluders.getTexture(), 0, 0, visp.lightSize, shadarrFBO[i].getHeight());
			spriteBatch.end();
			shadarrFBO[i].end();
		}
		
		lightFBO.begin();
		lightcamera.setToOrtho(true,CAMERA_WIDTH,CAMERA_HEIGHT);
		//camera.translate(gameobject.gamehandler.camera.x,gameobject.gamehandler.camera.y);
		lightcamera.update();
		spriteBatch.setProjectionMatrix(lightcamera.combined);
		Gdx.gl.glClearColor(0f,0f,0f,1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);		
		spriteBatch.setShader(shadowRenderShader);
		spriteBatch.begin();
		//spriteBatch.enableBlending();
		//spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shadowRenderShader.setUniformf("softShadows", true ? 1f : 0f);
		shadowRenderShader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//float finalSize = lightSize;
		Light visp;
		for(int i=0;i<lights.size();i++)
		{
			
			visp = lights.get(i);
			
			spriteBatch.setColor(new Color(visp.RED,visp.GREEN,visp.BLUE,1));
			spriteBatch.draw(shadarrFBO[i].getColorBufferTexture(), visp.getX()-visp.lightSize/2f, visp.getY()-visp.lightSize/2f, visp.lightSize, visp.lightSize);
		}
		spriteBatch.setShader(null);
		spriteBatch.end();
		lightFBO.end();
	}
	
	void render(OrthographicCamera camera,float CAMERA_WIDTH,float CAMERA_HEIGHT)
	{
		rendlight(camera,CAMERA_WIDTH,CAMERA_HEIGHT);
//		camera.setToOrtho(true);
//		camera.update();
		spriteBatch.begin();
		
		spriteBatch.setProjectionMatrix(camera.combined);
		//spriteBatch.disableBlending();
		//spriteBatch.disableBlending();
		//spriteBatch.setBlendFunction(GL20.GL_DST_COLOR,GL20.GL_ONE_MINUS_SRC_ALPHA);
		spriteBatch.setColor(Color.WHITE);
		for(int i=0;i<texhandler.textures.size();i++)
		{
			TextureObject r = texhandler.textures.get(i);
			spriteBatch.draw(rock, r.x,r.y,r.w,r.h, r.u1, r.v1, r.u2,r.v2);
		}		
		int upscale = mapeditor.worldgrid.cell_size/10;
		System.out.println(CAMERA_WIDTH+" "+CAMERA_HEIGHT+" "+Gdx.graphics.getWidth()+" "+Gdx.graphics.getHeight());
		spriteBatch.draw(lightMapTexture,0,0,CAMERA_WIDTH,CAMERA_HEIGHT);
		//spriteBatch.draw(lightMapTexture,0,0);
		for(int i=0;i<mapeditor.worldreader.walls.size();i++)
		{
			Rectangle r = mapeditor.worldreader.walls.get(i);
			spriteBatch.draw(red,r.x,r.y,r.w,r.h);
		}
		//spriteBatch.draw(occluders,0,0);
		spriteBatch.end();
		
		if(lights.size()>0)
		{
			Light light = lights.get(activeLight);
			shaperender.setColor(Color.BLUE);
			shaperender.begin(ShapeRenderer.ShapeType.Line);
			shaperender.box(light.getX()-light.lightSize/2, light.getY()-light.lightSize/2, 0, light.lightSize, light.lightSize, 0);
			shaperender.end();
		}
	}
	
	void resize(int x, int y)
	{
		 lightFBO = new FrameBuffer(Format.RGBA8888,x,y,false);
		 lightMapTexture = lightFBO.getColorBufferTexture();
	}
	
	JPanel lightpanel;
	void addControlPanel()
	{	
		frame = new JFrame();
		jt_lightsize = new JTextField(7);
		jt_lightsize.setText(lightSize+"");
		jb_lightsize = new JButton("Enter");
		jb_lightsize.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				lightSize = Integer.parseInt(jt_lightsize.getText());
				if (lights.size()>0)
					lights.get(activeLight).lightSize = lightSize;
			}
		});
		
		jb_color = new JButton("Choose");
		jb_color.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed (ActionEvent e)
			{
				
				
				java.awt.Color c = JColorChooser.showDialog(null,"Choose",java.awt.Color.CYAN);
				LIGHT_COLOR.x = c.getRed();
				LIGHT_COLOR.y = c.getBlue();
				LIGHT_COLOR.z = c.getGreen();
				if (lights.size()>0)
				{
					Light light = lights.get(activeLight);
					light.RED = c.getRed();
					light.BLUE = c.getBlue();
					light.GREEN = c.getGreen();
				}
			}
		});
		lightpanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		lightpanel.add(jt_lightsize,c);
		c.gridx = 1;
		lightpanel.add(jb_lightsize,c);
		c.gridx = 1;
		c.gridy = 2;
		lightpanel.add(jb_color,c);
		lightpanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK));
	}
}
