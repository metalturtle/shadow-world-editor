import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.*;

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;

public class EditorApp extends JFrame {

    public EditorApp() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Container container = getContentPane();
        container.setLayout(new BorderLayout());

        EditorMain editMain = new EditorMain();
        editMain.GameInit(false);
        LwjglAWTCanvas canvas = new LwjglAWTCanvas(editMain, editMain.cfg);
        JPanel editorPanel = new JPanel();
//        editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));
        editMain.set_editor_panel(editorPanel);
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout());
        containerPanel.add(canvas.getCanvas(), BorderLayout.CENTER);
        editorPanel.setBounds(0,0,50,50);
        containerPanel.add(editorPanel,BorderLayout.EAST);
        container.add(containerPanel);
        
        pack();
        setVisible(true);
        setSize(800, 600);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EditorApp();
            }
        });
    }
}