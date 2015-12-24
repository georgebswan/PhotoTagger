import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import aberscan.Photo;
import aberscan.TagList;

public class TagPane extends JPanel implements FocusListener, KeyListener {
	static final long serialVersionUID = 7;
	private static final int VK_ENTER = 0;
	MainTagGUI frame;
    Dimension tagAreaSize;
	JComponent[] fields;
	boolean textChanged;
	
	public Dimension getTagAreaSize() { return(tagAreaSize); }
	public void setTagAreaSize(Dimension dim) { tagAreaSize = dim; }
	public boolean getTextChanged() { return textChanged; }
	public void setTextChanged(boolean flag) {textChanged = flag; } 
	
	public TagPane(MainTagGUI parent) {
        super(new BorderLayout());
        
	frame = parent;
	textChanged = false;
	String[] labelStrings = { "Tag 1", "Tag 2", "Tag 3", "Tag 4", "Tag 5", "Tag 6", "Tag 7", "Tag 8", "Tag 9", "Tag 10", "Tag 11", "Tag 12", "Tag 13", "Tag 14", "Tag 15", "Tag 16", "Tag 17", "Tag 18", "Tag 19", "Tag 20" };
        JLabel[] labels = new JLabel[labelStrings.length];
        fields = new JComponent[labelStrings.length];
        JPanel[] tagItems = new JPanel[labelStrings.length];
        Font textFont = new Font("SansSerif", Font.PLAIN, 14);
        
        JPanel tagPanel = new JPanel();
        tagPanel.setMaximumSize( tagAreaSize);
        tagPanel.setLayout(new BoxLayout(tagPanel, BoxLayout.PAGE_AXIS));
        tagPanel.add(Box.createRigidArea(new Dimension(0,20)));
        
        //create the text fields and associate the labels
        for (int i = 0; i < labelStrings.length; i++) {
            tagItems[i] = new JPanel();
            tagItems[i].setLayout(new BoxLayout(tagItems[i], BoxLayout.LINE_AXIS));
        	
            //set up the field
            fields[i] = new JTextField(20);
            fields[i].setMaximumSize(new Dimension(550, 30));
            fields[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            fields[i].setName(String.valueOf(i));
            fields[i].setFont(textFont);
        	
        	//set up the label and attach it to the field
            labels[i] = new JLabel(labelStrings[i],
                                   JLabel.TRAILING);
            labels[i].setLabelFor(fields[i]);
        	labels[i].setMaximumSize(new Dimension(50, 20));
        	labels[i].setAlignmentX(Component.RIGHT_ALIGNMENT);
            
            //add them to the tagItem panel, then add the listeners
            tagItems[i].add(labels[i]);
            tagItems[i].add(Box.createRigidArea(new Dimension(5,0)));
            tagItems[i].add(fields[i]);
            fields[i].addFocusListener(this);
            fields[i].addKeyListener((KeyListener) this);
            
            //add tagItem to tagPanel
            tagPanel.add(tagItems[i]);
            tagPanel.add(Box.createRigidArea(new Dimension(0,10)));
        }
        
        //add the tag panel
        add(tagPanel);
    }
	
	public void preloadTextFields(TagList tags) {
		Color color = new Color(0,0,0);	//BLACK
		 
        	//Set JTextField text color to color that you choose
		for(int i = 0 ; i < tags.getNumTags(); i++) {
		    ((JTextField) fields[i]).setText(tags.getText(i));
		    ((JTextField) fields[i]).setForeground(color);
		}
		
		//set the focus on the first field
		fields[0].requestFocusInWindow();
	}
	
	public TagList captureTagText() {
		//go through all the fields and build up a tagList
		TagList tags = new TagList();
		for(int i = 0 ; i < tags.getNumTags(); i++) {
		    tags.setText(i, ((JTextField) fields[i]).getText() );
		}
		return(tags);
	}
	

	//public String getText(int i) {
	//	return (((JTextField)fields[i]).getText());
	//}
	
	public void paintComponent(Graphics g){
	    super.paintComponent(g);
	}
	
	private void drawRectangle(Graphics g, Rectangle rect) {
		 Graphics2D g2 = (Graphics2D) g;
	     g2.setColor(Color.GREEN);
	     Stroke oldStroke = g2.getStroke();
	     g2.setStroke(new BasicStroke(2));

	     //System.out.println("TagPane (drawRectangle): rect = " + rect.toString());
	     g2.draw(rect);
	     
	     g2.setStroke(new BasicStroke(1));
	     g2.setStroke(oldStroke);
	}
	

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent arg0) {
		//first, find out which field we just lost focus on, and what the text was
		int i = Integer.parseInt(arg0.getComponent().getName());
    	//System.out.println("tagPane (focusLost): name = " + arg0.getComponent().getName());
		String text = ((JTextField)arg0.getComponent()).getText();
		
		//check that the entered text is valid
		testValidity(i, text);
		
		//note that something changed with the tag text
		textChanged = true;
	}
	
	public void testValidity(int tagNum, String text) {
		if(text.contains(",") || text.contains("\"")) {
			JOptionPane.showMessageDialog(frame, "Text entered for Tag " + (tagNum + 1) + " contains illegal characters");
		}
	}
	
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			//caught the ENTER key being pressed. Treat it like we just pressed the 'next' button on the control pane
	        //System.out.println("tagPanel(keyPressed): caught the enter command");
			frame.cPane.moveToNextPhoto();
		}
	}
	
	public void keyReleased(KeyEvent arg0) {
	}
	
	public void keyTyped(KeyEvent arg0) {
	}
}
