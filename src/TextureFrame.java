import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;

import com.badlogic.gdx.graphics.Texture;

class TextureData{
	Texture texture;
	String name,filename;
}

public class TextureFrame {
	JFrame frame;
	LinkedList <TextureData> textureList = new LinkedList<TextureData>();
	JList <String> jlTextureList;
	
	TextureFrame() {
		DefaultListModel<String> listModel = new DefaultListModel<String>(); 
	      listModel.addElement("USA");
	        listModel.addElement("India");
	        listModel.addElement("Vietnam");
	        listModel.addElement("Canada");
	        listModel.addElement("Denmark");
	        listModel.addElement("France");
	        listModel.addElement("Great Britain");
	        listModel.addElement("Japan");
	 
	        
		frame = new JFrame();
		frame.setBounds(50,50,100,100);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		jlTextureList = new JList<String>(listModel);
		frame.setLayout(new FlowLayout());
		frame.add(jlTextureList);
		
	}
}
