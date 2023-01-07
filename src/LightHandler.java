
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

class Light extends EditorObject
{
	int lightSize;
	float RED,BLUE,GREEN;
	public Light(int id, float x, float y,int lightSize) {
		super(id, new Rectangle( x, y, 0, 0));
		this.lightSize = lightSize;
	}
	void setColor(float R,float G,float B)
	{
		RED = R;
		BLUE = B;
		GREEN = G;
	}
}

class LightHandler extends Editor {
	ShaderProgram shadowMapShader,shadowRenderShader,bumpmapShader,combineShader;
	public static final float DEFAULT_LIGHT_Z = 0.075f;
	public static final float AMBIENT_INTENSITY = 1f;
	public static final float LIGHT_INTENSITY = 2f;
	public static final Vector3 LIGHT_POS = new Vector3(0f,0f,0.01f);
	public static final Vector3 LIGHT_COLOR = new Vector3(1f, 1f, 1f);
	public static final Vector3 AMBIENT_COLOR = new Vector3(0.4f,0.4f,0.4f);
	public static final Vector3 FALLOFF = new Vector3(.4f, 3f, 20f);
    FrameBuffer occludersFBO,shadowMapFBO,shadarrFBO[],lightFBO,bumpmapFBO,basemapFBO;
    Texture lightMapTexture,bumpMapTexture,baseMapTexture;
    TextureRegion occluders,shadowMap1D,rocktex,lighttexreg;
	OrthographicCamera lightCamera;
	JFrame frame;
	JTextField jt_lightsize;
	JButton jb_lightsize,jb_color;
	
	ShapeRenderer shaperender;
	LightHandler(int editor_id, EditorMain EditMain)
	{
		super(EditMain, editor_id, "light");
//		addControlPanel();
	}
	
	@Override
	void init() {
		super.init();
		this.lightCamera = new OrthographicCamera();
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
	}
	
//	void set(int x,int y){
////		int v[] = convertXY(x,y);
////		x = v[0];y=v[1];
//		if(lights.size() > 0)
//		{
//			Light l = lights.get(activeLight);
//			//l.setColor(LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z);
//			l.setXY(x, y);
//		}
//	}
	
//	void rightClick(int x,int y)
//	{
//		int v[] = convertXY(x,y);
//		x = v[0];y=v[1];
//		for(int i =0;i<lights.size();i++)
//		{
//			Light light = lights.get(i);
//			if((x>light.getX()-light.lightSize/2 && x<light.getX()+light.lightSize/2)&&(y>light.getY()-light.lightSize/2 && y<light.getY()+light.lightSize/2))
//			{
//				activeLight = i;
//				return;
//			}
//		}
//	}
	
//	void savetofile(JSONObject jo)
//	{
//		JSONArray lightarrjson = new JSONArray();
//		for(int i=0;i<lights.size();i++)
//		{
//			Light light = lights.get(i);
//			JSONArray lightjson = new JSONArray();
//			lightjson.add(light.getX());
//			lightjson.add(light.getY());
//			lightjson.add(light.lightSize);
//			lightjson.add(light.RED);
//			lightjson.add(light.BLUE);
//			lightjson.add(light.GREEN);
//			lightarrjson.add(lightjson);
//		}
//		JSONObject lightmodule = new JSONObject();
//		lightmodule.put("lights",lightarrjson);
//		lightmodule.put("size",lights.size());
//		jo.put("lightmodule",lightmodule);
//	}
//	
//	void readfromfile(JSONObject jo)
//	{
//		JSONObject lightmodule = (JSONObject)jo.get("lightmodule");
//		if (lightmodule == null)
//			return;
//		int size = ((Number)lightmodule.get("size")).intValue();
//		JSONArray lightarrjson = (JSONArray)lightmodule.get("lights");
//		for(int i=0;i<size;i++)
//		{
//			JSONArray lightjson = (JSONArray)lightarrjson.get(i);
//			Light l = new Light(((Number)lightjson.get(0)).floatValue(),((Number)lightjson.get(1)).floatValue(),((Number)lightjson.get(2)).intValue());
//			l.RED = ((Number)lightjson.get(3)).floatValue();
//			l.BLUE = ((Number)lightjson.get(4)).floatValue();
//			l.GREEN = ((Number)lightjson.get(5)).floatValue();
//			lights.add(l);
//		}
//	}
	
	void render_light(SpriteBatch spriteBatch)
	{
		SpriteBatch occludeSpriteBatch = new SpriteBatch();
		Rectangle lightrect=new Rectangle(0,0,0,0);
		int lightsceneid=0;
		int vispid[] = new int[16];
		float THRESHOLD = 0.3f;
		lightFBO.begin();
		Gdx.gl.glClearColor(THRESHOLD,THRESHOLD,THRESHOLD,0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		lightFBO.end();
		//int lightid = 0,lightsize = lights.size(),lightlimit = 0,i;
//		while(lightid<lightsize)
		int lightlimit = 16, i = 0;
//		for(EditorObject editObj : editorObjects)
		{
			//lightlimit = Math.min(lightid+8,lights.size());
			lightsceneid = 0;
			for( i = 0; i < editorObjects.size(); i++)
			{
				Light visp = (Light)editorObjects.get(i);
				lightrect.set(visp.bound.x()-visp.lightSize/2,visp.bound.y()-visp.lightSize/2 , visp.lightSize, visp.lightSize);
				
//				if(!Rectangle.check_rect_intersection(lightrect,viewrect))
//				{
//					continue;
//				}
//				
				occludersFBO.begin();
				Gdx.gl.glClearColor(0,0,0,0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				lightCamera.setToOrtho(false, visp.lightSize, visp.lightSize);
				lightCamera.translate(visp.bound.x() - visp.lightSize/2f, visp.bound.y() - visp.lightSize/2f);
				lightCamera.update();
				occludeSpriteBatch.setProjectionMatrix(lightCamera.combined);
				occludeSpriteBatch.setShader(null);
				occludeSpriteBatch.begin();
				for(EditorObject editObj : EditMain.enteditor.editorObjects)
				{
					Rectangle wall_rect = editObj.bound;
					occludeSpriteBatch.draw(red,wall_rect.get_x(),wall_rect.get_y(),wall_rect.get_w(),wall_rect.get_h());	
				}				
				occludeSpriteBatch.end();
				occludersFBO.end();
	
				shadarrFBO[lightsceneid].begin();
				Gdx.gl.glClearColor(0f,0f,0f,0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				occludeSpriteBatch.setShader(shadowMapShader);
				occludeSpriteBatch.begin();
				shadowMapShader.setUniformf("resolution", visp.lightSize, visp.lightSize);
				lightCamera.setToOrtho(false, visp.lightSize, shadarrFBO[lightsceneid].getHeight());
				occludeSpriteBatch.setProjectionMatrix(lightCamera.combined);
				occludeSpriteBatch.draw(occluders.getTexture(), 0, 0, visp.lightSize, shadarrFBO[lightsceneid].getHeight());
				occludeSpriteBatch.end();
				shadarrFBO[lightsceneid].end();
				vispid[lightsceneid] = i;
				lightsceneid++;
			}
			lightFBO.begin();
			lightCamera.setToOrtho(true,EditMain.SCREEN_X,EditMain.SCREEN_Y);
//			lightCamera.translate(viewrect.get_x(),viewrect.get_y());
			lightCamera.update();
			spriteBatch.setProjectionMatrix(lightCamera.combined);
			//Gdx.gl.glClearColor(threshold,threshold,threshold,1f);
			//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);		
			spriteBatch.setShader(shadowRenderShader);
			spriteBatch.begin();
			//spriteBatch.enableBlending();
			//spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shadowRenderShader.setUniformf("softShadows", true ? 1f : 0f);
			shadowRenderShader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			
			//float finalSize = lightSize;
			Light visp;
			for(i=0;i<lightsceneid;i++)
			{
				visp = (Light)editorObjects.get(vispid[i]);
				spriteBatch.setColor(new Color(visp.RED,visp.GREEN,visp.BLUE,1));
				//spriteBatch.setColor(new Color(250,8,202,1));
				spriteBatch.draw(shadarrFBO[i].getColorBufferTexture(), visp.bound.x()-visp.lightSize/2f, visp.bound.y()-visp.lightSize/2f, visp.lightSize, visp.lightSize);
			}
			spriteBatch.setShader(null);
			spriteBatch.end();
			lightFBO.end();
		}
	}

//	
//	
//	void rendlight(SpriteBatch spriteBatch)
//	{
//		for(int i =0;i<editorObjects.size();i++)
//		{
//			spriteBatch.end();
//			Light visp = (Light)editorObjects.get(i);
//			occludersFBO.begin();
//			Gdx.gl.glClearColor(0f,0f,0f,0f);
//			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//			lightcamera.setToOrtho(false, visp.lightSize, visp.lightSize);
//			lightcamera.translate(visp.bound.x() - visp.lightSize/2f, visp.bound.y() - visp.lightSize/2f);
//			lightcamera.update();
//			spriteBatch.setProjectionMatrix(lightcamera.combined);
//			spriteBatch.setShader(null);
//			spriteBatch.begin();
//			for(EditorObject editObj : EditMain.mapeditor.editorObjects)
//			{
//				Rectangle wall = editObj.bound;
//				spriteBatch.draw(red,wall.x,wall.y,wall.w,wall.h);	
//			}				
//			spriteBatch.end();
//			occludersFBO.end();
//
//			shadarrFBO[i].begin();
//			Gdx.gl.glClearColor(0f,0f,0f,0f);
//			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//			spriteBatch.setShader(shadowMapShader);
//			spriteBatch.begin();
//			shadowMapShader.setUniformf("resolution", visp.lightSize, visp.lightSize);
//			lightcamera.setToOrtho(false, visp.lightSize, shadarrFBO[i].getHeight());
//			spriteBatch.setProjectionMatrix(lightcamera.combined);
//			spriteBatch.draw(occluders.getTexture(), 0, 0, visp.lightSize, shadarrFBO[i].getHeight());
//			spriteBatch.end();
//			shadarrFBO[i].end();
//		}
//		
//		lightFBO.begin();
//		lightcamera.setToOrtho(true,EditMain.CAMERA_WIDTH,EditMain.CAMERA_HEIGHT);
//		//camera.translate(gameobject.gamehandler.camera.x,gameobject.gamehandler.camera.y);
//		lightcamera.update();
//		spriteBatch.setProjectionMatrix(lightcamera.combined);
//		Gdx.gl.glClearColor(0f,0f,0f,1f);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);		
//		spriteBatch.setShader(shadowRenderShader);
//		spriteBatch.begin();
//		//spriteBatch.enableBlending();
//		//spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
//		shadowRenderShader.setUniformf("softShadows", true ? 1f : 0f);
//		shadowRenderShader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		
//		//float finalSize = lightSize;
//		Light visp;
//		for(int i=0;i<editorObjects.size();i++)
//		{
//			
//			visp = (Light)editorObjects.get(i);
//			
//			spriteBatch.setColor(new Color(visp.RED,visp.GREEN,visp.BLUE,1));
//			spriteBatch.draw(shadarrFBO[i].getColorBufferTexture(), visp.bound.x()-visp.lightSize/2f, visp.bound.y()-visp.lightSize/2f, visp.lightSize, visp.lightSize);
//		}
//		spriteBatch.setShader(null);
//		spriteBatch.end();
//		lightFBO.end();
//	}
	
//	void resize(int x, int y)
//	{
//		 lightFBO = new FrameBuffer(Format.RGBA8888,x,y,false);
//		 lightMapTexture = lightFBO.getColorBufferTexture();
//	}
	
//	JPanel lightpanel;
//	void addControlPanel()
//	{	
//		frame = new JFrame();
//		jt_lightsize = new JTextField(7);
//		jt_lightsize.setText(lightSize+"");
//		jb_lightsize = new JButton("Enter");
//		jb_lightsize.addActionListener(new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				lightSize = Integer.parseInt(jt_lightsize.getText());
//				if (lights.size()>0)
//					lights.get(activeLight).lightSize = lightSize;
//			}
//		});
//		
//		jb_color = new JButton("Choose");
//		jb_color.addActionListener(new ActionListener(){
//			@Override
//			public void actionPerformed (ActionEvent e)
//			{
//				
//				
//				java.awt.Color c = JColorChooser.showDialog(null,"Choose",java.awt.Color.CYAN);
//				LIGHT_COLOR.x = c.getRed();
//				LIGHT_COLOR.y = c.getBlue();
//				LIGHT_COLOR.z = c.getGreen();
//				if (lights.size()>0)
//				{
//					Light light = lights.get(activeLight);
//					light.RED = c.getRed();
//					light.BLUE = c.getBlue();
//					light.GREEN = c.getGreen();
//				}
//			}
//		});
//		lightpanel = new JPanel(new GridBagLayout());
//		GridBagConstraints c = new GridBagConstraints();
//		c.gridx = 0;
//		c.gridy = 0;
//		c.anchor = GridBagConstraints.WEST;
//		lightpanel.add(jt_lightsize,c);
//		c.gridx = 1;
//		lightpanel.add(jb_lightsize,c);
//		c.gridx = 1;
//		c.gridy = 2;
//		lightpanel.add(jb_color,c);
//		lightpanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK));
//	}

	@Override
	void right_click_action(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	EditorObject[] add_editor_object(int beginx, int beginy, int endx, int endy) {
//		Light l = new Light(0, beginx, beginy,endx - beginx);
//		l.setColor(LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z);
//		return new Light[] {l};
		return new Light[] {};
	}

	@Override
	void render_editor_object(SpriteBatch spriteBatch, EditorObject edit_obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void render_placeholder(SpriteBatch spriteBatch, int beginx, int beginy, int endx, int endy) {
		System.out.println("checking: "+beginx+" "+beginy+" "+endx+" "+endy);
		Rectangle rect = new Rectangle(beginx, beginy, endx - beginx, endy - beginy);
		if (rect.w() > rect.h())
			rect.set_w(rect.h());
		else
			rect.set_h(rect.w());
		spriteBatch.draw(red,rect.x,rect.y,rect.w,rect.h);
	}

	@Override
	void render_before_entity(SpriteBatch spriteBatch) {
		// TODO Auto-generated method stub
		spriteBatch.draw(lightMapTexture,0,0,EditMain.GCamera.w(),EditMain.GCamera.h());
//		EditMain.mapeditor.render_editor_objects(spriteBatch);
	}

	@Override
	void render_after_entity(SpriteBatch spriteBatch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void before_render(SpriteBatch spriteBatch) {
		// TODO Auto-generated method stub
//		render_light(spriteBatch);
	}

	@Override
	void set_editor_panel() {
		
		editorPanel.add(new JLabel("Light"));
		jt_lightsize = new JTextField(7);
		JPanel jp_light_size = new JPanel();
		jp_light_size.add(new JLabel("light size"));
		jp_light_size.add(jt_lightsize);
		editorPanel.add(jp_light_size);
		
		JPanel jp_test = new JPanel();
		jp_test.add(new JLabel("test"));
		jp_test.add(new JTextField(7));
		editorPanel.add(jp_test);
//		editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));
	}
	
	@Override
	void selected_editor_object_action(EditorObject editObj) {
		// TODO Auto-generated method stub
		
	}
	@Override
	void reset_selected_editor_object() {
		
	}

	@Override
	EditorObject read_object(Object[] rectarr) {
		
		return new Light(0,((Number)rectarr[0]).floatValue(), ((Number)rectarr[1]).floatValue(), ((Number)rectarr[2]).intValue());
	}

	@Override
	JSONArray write_object(EditorObject editObj) {
		JSONArray jsonArr = new JSONArray();
		Light light = (Light)editObj;
		jsonArr.add(light.bound.x());
		jsonArr.add(light.bound.y());
		jsonArr.add(light.lightSize);
		jsonArr.add(light.RED);
		jsonArr.add(light.BLUE);
		jsonArr.add(light.GREEN);
		return jsonArr;
	}
}
