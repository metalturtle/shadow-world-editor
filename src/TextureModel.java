import java.util.LinkedList;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import com.badlogic.gdx.graphics.Texture;

class TextureType {
	String name,file_name;
	Texture texture;
}

public class TextureModel implements ListModel<String>{
    
    private LinkedList<TextureType> textureTypes;
    private LinkedList<ListDataListener> listeners;

    public TextureModel() {
    	textureTypes = new LinkedList<TextureType>();
        listeners = new LinkedList<ListDataListener>();
    }
    
    public void addElement(TextureType element){
    	textureTypes.add(element);
    }
    
    @Override
    public int getSize() {
        return textureTypes.size();
    }

    @Override
    public String getElementAt(int index) {
        return textureTypes.get(index).name;
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
    
}