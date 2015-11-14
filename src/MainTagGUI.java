import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import aberscan.*;


public class MainTagGUI extends JFrame {
	static final long serialVersionUID = 1;
	ImagePane iPane;
	TagPane tPane;
	LogPane lPane;
	ControlPane cPane;
	FilePrompterGUI fileViewer;
	Directories dirs;
	
	MainTagGUI() {
        //Set up the main frame 
        super("Photo Tagging Tool");
        setSize(1200, 600);
        Dimension frameSize = this.getSize();
        int logHeight = 0;
        int buffer = 100;
        int horizBuffer = 10;
        int panelHeight = (int) frameSize.getHeight() - logHeight - buffer;
        int panelWidth = (int) frameSize.getWidth() -2*horizBuffer;
        int imagePanelWidth = (int) frameSize.getWidth()/2 - horizBuffer;
        int tagPanelWidth = (int) frameSize.getWidth()/2 - 2*horizBuffer;
        int controlPanelHeight = 50;
        
        dirs = new Directories();
		
        //Set up the content pane
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
        
	    //Add the image panel on the left and the tag panel on the right
        JPanel centerPane = new JPanel();
        centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.LINE_AXIS));     
        
        iPane = new ImagePane(this);
        iPane.setImageAreaSize(new Dimension(imagePanelWidth, panelHeight));
        iPane.setPreferredSize(new Dimension(imagePanelWidth, panelHeight));
        centerPane.add(Box.createRigidArea(new Dimension(5,0)));
        centerPane.add(iPane);
        
	    //Add the tagging panel in the middle
        tPane = new TagPane(this);
        tPane.setTagAreaSize(new Dimension(tagPanelWidth, panelHeight));
        tPane.setPreferredSize(new Dimension(tagPanelWidth, panelHeight));
        centerPane.add(tPane);
        centerPane.add(Box.createRigidArea(new Dimension(5,0)));
        pane.add(centerPane);
        
	    //Add the control panel
        cPane = new ControlPane(this, dirs);
        cPane.setControlAreaSize(new Dimension(panelWidth, controlPanelHeight));
        cPane.setPreferredSize(new Dimension(panelWidth, controlPanelHeight));
        pane.add(cPane);
        
        //ask for the first file to be tagged and load up the photos etc.
        if(cPane.selectNewFolder() == false) {
        	System.exit(1);
        }
        
        setContentPane(pane);
        setFocusable(true);
        setVisible(true);
	}

	public static void main(String[] args) {
    	// Show the mapping GUI
    	final MainTagGUI frame = new MainTagGUI();
    	
		//ExitWindow exit = new ExitWindow();
        frame.addWindowListener(new WindowAdapter() {

        	public void windowClosing(WindowEvent e) {
				// check to see if you want to export tags before existing
        		if(frame.tPane.getTextChanged() == true) {
        			if(JOptionPane.showConfirmDialog(null, "Question : Before exiting, do you want to export the tags?" , "Unsaved Tags" , JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { 
        				frame.cPane.exportTags();
    				}
        		}
        		System.exit(0);
        	}
    	});
        
        //frame.setVisible(true);
	}
	
	private static void exportTags() {
	}
}

