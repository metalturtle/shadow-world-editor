
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.channels.FileChannel;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.json.simple.JSONArray;
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
import com.badlogic.gdx.utils.viewport.*;



class EditorMain implements ApplicationListener,InputProcessor  {

	
    static {
        GdxNativesLoader.load();
    }
    int MODE;
    int TARGET_FPS,SCREEN_X,SCREEN_Y;
    GameCamera GCamera;
//    float CAMERA_WIDTH,CAMERA_HEIGHT;
//    float CAMERA_X,CAMERA_Y;
    int GRID_SIZE;
    float CELL_SIZE;
    boolean DISPLAY_GRID;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;
    ShapeRenderer shaperender;
    MapEditor mapeditor;
    LightHandler lighteditor;
    TextureHandler texeditor;
//    PropEditor propeditor;
    JFrame frame;
    EntityEditor enteditor;
    AmbientEditor ambienteditor;
//    BumpmapEditor bumpmapeditor;
    CollisionEditor collisioneditor;
    MiscEditor misceditor;
    JSONParser parser;
    ResourceManager ResManage;
    ConfigManager ConfigManage;
    
    ArrayList <Editor> editors;
    ArrayList <Editor> render_list;
    Viewport viewport;
    JPanel editorPanel,masterPanel;
    JCheckBox displayEditorCheckbox[],gridCheckbox;
    JLabel editorModeLabel;
    JButton migrateFilesButton,saveButton,writeResourceButton;
    HashMap <String,Editor> editorMap;
    TextureFrame TexFrame;
    
    
    LwjglApplicationConfiguration cfg;
    void GameInit(boolean server) 
    {	
    	TARGET_FPS = 30;
    	SCREEN_X = 600;
    	SCREEN_Y = 600;
		GRID_SIZE = 1000;
		GCamera = new GameCamera(0,0,SCREEN_X,SCREEN_Y);
		CELL_SIZE = 10;
//    	CAMERA_WIDTH= (SCREEN_X/ZOOM);
//    	CAMERA_HEIGHT= (SCREEN_Y/ZOOM);
   	 	cfg = new LwjglApplicationConfiguration();
	    cfg.title = "graphicsdemo";
	    cfg.foregroundFPS = TARGET_FPS;
	    cfg.width = SCREEN_X ;
	    cfg.height = SCREEN_Y;
	    
//		CAMERA_X = 0;
//		CAMERA_Y = 0;
//		ZOOM = 1;
		MODE = 0;
		editors = new ArrayList <Editor>();
		render_list = new ArrayList<Editor>();
		editorMap = new HashMap<String,Editor>();
		ConfigManage = new ConfigManager();
		ConfigManage.read_files();
		ResManage = new ResourceManager(ConfigManage);
		
//		mapeditor = new MapEditor(1, this);

//	    return new LwjglApplication(this, cfg);
    }
    
	@Override
	public void create() {
		Gdx.graphics.setVSync(true);	
		Gdx.input.setInputProcessor(this);
		
		frame = new JFrame();
//		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;c.gridy=0;
		c.anchor = GridBagConstraints.NORTH;
//		frame.add(lighteditor.lightpanel,c);
		c.gridy=1;
//		TexFrame = new TextureFrame();
//		frame.add(texeditor.texturepanel,c);
		
		spriteBatch = new SpriteBatch();
		camera = new OrthographicCamera();
		shaperender = new ShapeRenderer();
		camera.setToOrtho(false,GCamera.w(),GCamera.h());
		viewport = new FillViewport(100,100, camera);
		
		viewport.apply();
		
		lighteditor = new LightHandler(2, this);
		texeditor = new TextureHandler(3, this);
		enteditor = new EntityEditor(4, this, mapeditor,spriteBatch,shaperender);
		ambienteditor = new AmbientEditor(6,this);
		collisioneditor = new CollisionEditor(8,this);
		misceditor = new MiscEditor(9,this);
		add_editors();
		
		int i=0;
		editorModeLabel = new JLabel();
		JPanel checkboxPanel = new JPanel();
		checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
		editorModeLabel.setText(editors.get(MODE).editorName);
		checkboxPanel.add(editorModeLabel);
		displayEditorCheckbox = new JCheckBox[editors.size()];
		for(Editor editor: editors) {
			displayEditorCheckbox[i] = new JCheckBox(editor.editorName+".("+(editor.EDITOR_ID+1)+")");
			checkboxPanel.add(displayEditorCheckbox[i]);
			displayEditorCheckbox[i].addItemListener(new ItemListener() {
				
			@Override
			public void itemStateChanged(ItemEvent e) {
				JCheckBox check = (JCheckBox)e.getSource();				
				String editorName = check.getLabel();
				editorName = editorName.substring(0,editorName.indexOf("."));
				Editor editor = editorMap.get(editorName);
				editor.visible = e.getStateChange() == 1;
				
			}});
			i+=1;
//			editorPanel.add(displayEditorCheckbox[i]);
			editor.init();
		}
		
		saveButton = new JButton("save");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				savetofile();
			}
			
		});
		migrateFilesButton = new JButton("migrate");
		migrateFilesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				File levelFile = new File("level.json");
//				File outputFile= new File("C:\\Users\\mithu\\Documents\\Projects\\eclipse-workspace\\ShadowHunt\\desktop\\level.json");
				savetofile();
//				String util_files[] = new String[] {"level.json","config\\resource.json","config\\sprite.json","config\\weapons.json"};
				String util_files[] = new String[] {"level.json"};
				try {
					for(int i = 0; i < util_files.length;i++) {
						String util_file = util_files[i];
						System.out.println("util file: "+util_file );
						  FileChannel src
				            = new FileInputStream(util_file)
				                  .getChannel();
				        FileChannel dest
				            = new FileOutputStream(
				                  "C:\\Users\\mithu\\Documents\\Projects\\eclipse-workspace\\ShadowHunt\\desktop\\"+util_file)
				                  .getChannel();
				        dest.transferFrom(src, 0, src.size());
			            src.close();
			            dest.close();
					}
					File level_file = new File("levels");
					listFilesForFolder(level_file);
//				System.out.println("bytes copied"+	Files.copy(levelFile.toPath(),outputFile.toPath(),StandardCopyOption.REPLACE_EXISTING));
//					System.out.println("migrated");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		writeResourceButton = new JButton("create resource");
		writeResourceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ResManage.write_resource_file();
			}
		});
		gridCheckbox = new JCheckBox("grid");
		gridCheckbox.setSelected(true);
		DISPLAY_GRID = true;
		gridCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox check = (JCheckBox)e.getSource();
				DISPLAY_GRID = check.isSelected();
			}
			
		});
		
		checkboxPanel.add(gridCheckbox);
		checkboxPanel.add(saveButton);
		checkboxPanel.add(migrateFilesButton);
		checkboxPanel.add(writeResourceButton);
		editorPanel.add(checkboxPanel);
		
		readfromfile();
		
		for (Editor editor: editors) {
			String editorName = editor.editorName;
			displayEditorCheckbox[editor.EDITOR_ID].setText(editorName+".("+(editor.EDITOR_ID+1)+")-"+editor.editorObjects.size());
		}
		
		Collections.sort(render_list,new Comparator<Editor>() {
			@Override
			public int compare(Editor o1, Editor o2) {
				return o1.RENDER_PRIORITY- o2.RENDER_PRIORITY;
			}
		});
	}

	public void listFilesForFolder(final File folder) throws IOException {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            System.out.println(fileEntry.getName());
				  FileChannel src
		            = new FileInputStream(fileEntry)
		                  .getChannel();
		        FileChannel dest
		            = new FileOutputStream(
		                  "C:\\Users\\mithu\\Documents\\Projects\\eclipse-workspace\\ShadowHunt\\desktop\\levels\\"+fileEntry.getName())
		                  .getChannel();
		        dest.transferFrom(src, 0, src.size());
	            src.close();
	            dest.close();
	        }
	    }
	}

	void set_editor_panel(JPanel editorPanel) {
		this.editorPanel = editorPanel;
	}
	void add_editors() {
//		add_editor(mapeditor,100);
		add_editor(lighteditor,99);
		add_editor(texeditor,10);
		add_editor(enteditor,98);
		add_editor(ambienteditor,101);
		add_editor(collisioneditor,97);
		add_editor(misceditor,96);
	}
	
	void add_editor(Editor editor, int PRIORITY) {
		editor.set_editor_panel();
		JPanel pan = editor.editorPanel;
		editor.set_editor_id(editors.size());
		editor.set_render_priority(PRIORITY);
		editors.add(editor);
		render_list.add(editor);
		if(editor.EDITOR_ID != 0)
			pan.setVisible(false);
		editorPanel.add(pan);
		editorMap.put(editor.editorName,editor);
	}
	
	void savetofile()
	{
		JSONObject jo = new JSONObject();
		ResManage.write_resource_file();
		Set<String> tex_demand_set = new HashSet<String>();
		for (Editor editor: editors) {
			jo.put(editor.editorName, editor.save_to_file());
			if(editor instanceof TextureHandler) {
				TextureHandler tex_handle = (TextureHandler)editor;
				tex_demand_set.addAll(tex_handle.get_used_texture_list());
			}
		}
		JSONArray jsonDemandArr = new JSONArray();
		for(String name: tex_demand_set) {
			jsonDemandArr.add(name);
		}
		jo.put("texture_demand",jsonDemandArr);
		
		jsonDemandArr = new JSONArray();
		Set <String> usedSet = misceditor.get_handler_list();
		for(String handler_name : usedSet) {
			jsonDemandArr.add(handler_name);
		}
		jo.put("handler_demand", jsonDemandArr);
		
		jsonDemandArr = new JSONArray();
		usedSet = misceditor.get_renderer_list();
		for(String rendererName : usedSet) {
			jsonDemandArr.add(rendererName);
		}
		jo.put("renderer_demand", jsonDemandArr);
		
        PrintWriter pw;
		try {
			pw = new PrintWriter("level.json");
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
			for (Editor editor: editors) {
				editor.read_from_file(jo);
			}
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
	
	float get_cell_size() {
		return CELL_SIZE;
	}
	
	void render_grid()
	{
		if(DISPLAY_GRID) {
			shaperender.setColor(Color.BLACK);
			shaperender.begin(ShapeRenderer.ShapeType.Line);
			for(int i=0;i<GRID_SIZE*CELL_SIZE;i+=CELL_SIZE)
				shaperender.line(i,0, i,GRID_SIZE*CELL_SIZE);
			for(int i=0;i<GRID_SIZE*CELL_SIZE;i+=CELL_SIZE)
				shaperender.line(0,i,GRID_SIZE*CELL_SIZE,i);
			shaperender.end();
			
			int line_thickness=2;
			shaperender.begin(ShapeRenderer.ShapeType.Filled);
			shaperender.setColor(Color.PURPLE);
			for(int i=0;i<GRID_SIZE*CELL_SIZE;i+=CELL_SIZE*8)
				shaperender.rect(i-line_thickness,0, line_thickness,GRID_SIZE*CELL_SIZE);
			for(int i=0;i<GRID_SIZE*CELL_SIZE;i+=CELL_SIZE*8)
				shaperender.rect(0,i-line_thickness,GRID_SIZE*CELL_SIZE,line_thickness);
			shaperender.end();
		}
	}
	
	 void rendergraphics() {
		 float movement = CELL_SIZE;
		 if(GCamera.get_zoom()>4) {
			 movement = CELL_SIZE*4;
		 }
		 else if(GCamera.get_zoom()>1) {
			 movement = CELL_SIZE*2;
		 }
			if (left) {
				GCamera.x(Math.max(0, GCamera.x()-movement));
			}
			if (right) {
				GCamera.x(Math.max(0, GCamera.x()+movement));
			}
			if (up) {
				GCamera.y(Math.max(0, GCamera.y()+movement));
			}
			if (down) {
				GCamera.y(Math.max(0, GCamera.y()-movement));
			}
			if(InputState.keyboard['z']) {
//				ZOOM +=0.1f;
				GCamera.set_zoom(GCamera.get_zoom()+0.1f);
			}
			if(InputState.keyboard['x']) {
//				ZOOM -=0.1f;
				GCamera.set_zoom(GCamera.get_zoom()-0.1f);
			}
			if(GCamera.get_zoom() <0.5f)
//				ZOOM = 0.5f;
				GCamera.set_zoom(0.5f);
			if(GCamera.get_zoom() > 6f) {
//				ZOOM = 2f;
				GCamera.set_zoom(6f);
			}
		Gdx.gl.glClearColor(1,1,1,1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.setToOrtho(false,GCamera.w(),GCamera.h());
//		camera.translate(CAMERA_X,CAMERA_Y);
		
//		camera.zoom = ZOOM;
		camera.position.set(GCamera.center_x(),GCamera.center_y(),0);
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		shaperender.setProjectionMatrix(camera.combined);
//		editors.get(MODE).render(spriteBatch);
		for(Editor editor: render_list) {
			if(editor.visible == true) {
				editor.render(spriteBatch);
			}
		}
		for (Editor editor: editors) {
			if(editor.add_from_list() || editor.remove_from_list()) {
				String editorName = editor.editorName;
				displayEditorCheckbox[editor.EDITOR_ID].setText(editorName+".("+(editor.EDITOR_ID+1)+")-"+editor.editorObjects.size());
			}
		}
		
		render_grid();
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
		viewport.update(x,x);
//	    camera.position.set(camera.viewportHeight/2,camera.viewportHeight/2,0);
		viewport.apply();
	    camera.update();
	    SCREEN_X =x;
		SCREEN_Y=x;
//		CAMERA_WIDTH = SCREEN_X*2;
//		CAMERA_HEIGHT = SCREEN_Y*2;
//    	CAMERA_WIDTH=(int) (SCREEN_X/ZOOM);
//    	CAMERA_HEIGHT=(int) (SCREEN_Y/ZOOM)
		GCamera.w(SCREEN_X);
		GCamera.h(SCREEN_Y);
		GCamera.set_zoom(GCamera.get_zoom());
	}

	@Override
	public void resume() {
	}

	boolean left,right, up, down,zoomup,zoomdown;
	@Override
	public boolean keyDown(int e) {
		if (e == Input.Keys.LEFT) {
			left = true;
		}
		if (e == Input.Keys.RIGHT) {
			right = true;
		}
		if (e == Input.Keys.UP) {
			up = true;
		}
		if (e == Input.Keys.DOWN) {
			down = true;
		}
		if (e == Input.Keys.O) {
			zoomup = true;
		}
		if (e == Input.Keys.P) {
			zoomdown = true;
		}
		char val = Input.Keys.toString(e).toLowerCase().charAt(0);
		InputState.keyboard[val] = true;
		return true;
	}

	@Override
	public boolean keyTyped(char e) {
		return true;
	}

	@Override
	public boolean keyUp(int e) {
		if (e == Input.Keys.LEFT) {
			left = false;
		}
		if (e == Input.Keys.RIGHT) {
			right = false;
		}
		if (e == Input.Keys.UP) {
			up = false;
		}
		if (e == Input.Keys.DOWN) {
			down = false;
		}
		
		int prev_mode = MODE;
		char val = Input.Keys.toString(e).toLowerCase().charAt(0);
		InputState.keyboard[val] = false;
		
		if(Character.isDigit(val)) {
			int new_mode = Integer.parseInt(val+"");
			if(new_mode <= editors.size()) {
				MODE = new_mode;
				if(MODE != 0)
					MODE -=1;
			}
			if(prev_mode != MODE) {
//				ZOOM = 1f;
				GCamera.set_zoom(1f);
				editors.get(prev_mode).editorPanel.setVisible(false);
				editors.get(MODE).editorPanel.setVisible(true);
				editorModeLabel.setText(editors.get(MODE).editorName);
				editors.get(MODE).in_focus();
//				editors.get(MODE).visible=true;
			}
		}

		return true;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		Vector3 worldCoordinates = viewport.unproject(new Vector3(x,y,0));
		InputState.mouse.setXY(worldCoordinates.x, worldCoordinates.y);
		editors.get(MODE).handle_mouse("hover");
//		editors.get(MODE).hover((int)worldCoordinates.x, (int)worldCoordinates.y);
//		InputState.mouse.setXY(x, y);
		return true;
	}

	@Override
	public boolean scrolled(int arg0) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		Vector3 worldCoordinates = viewport.unproject(new Vector3(x,y,0));
		InputState.mouse.setXY(worldCoordinates.x, worldCoordinates.y);	
//		InputState.mouse_state = "touch_down";
		if (button == Input.Buttons.LEFT) {
			InputState.left_click = true;
//			editors.get(MODE).touchDown((int)(worldCoordinates.x), (int)(worldCoordinates.y));
		}
		if(button == Input.Buttons.RIGHT) {
			InputState.right_click = true;
		}
		editors.get(MODE).handle_mouse("touch_down");
		
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int arg2) {
		Vector3 worldCoordinates = viewport.unproject(new Vector3(x,y,0));
//		editors.get(MODE).touchDragged((int)(worldCoordinates.x), (int)(worldCoordinates.y));
		InputState.mouse.setXY(worldCoordinates.x, worldCoordinates.y);
//		InputState.mouse_state = "touch_dragged";.
		editors.get(MODE).handle_mouse("touch_dragged");
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		Vector3 worldCoordinates = viewport.unproject(new Vector3(x,y,0));
		InputState.mouse.setXY(worldCoordinates.x, worldCoordinates.y);
//		InputState.mouse_state = "touch_up";
		if (button == Input.Buttons.LEFT) {
			InputState.left_click = false;
			editors.get(MODE).handle_mouse("touch_up");
//			editors.get(MODE).touchUp((int)(worldCoordinates.x), (int)(worldCoordinates.y));
		}
		if (button == Input.Buttons.RIGHT) {
			InputState.right_click = false;
			editors.get(MODE).handle_mouse("touch_up");
			editors.get(MODE).right_click();
		}
			
		return true;
	}
}

//public class HelloWorld
//{
//	public static void main(String args[])
//	{
//		new EditorMain().GameInit(false);
//	}
//}
