import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import aberscan.Directories;
import aberscan.FilePrompterGUI;
import aberscan.Photo;
import aberscan.PhotoList;
import aberscan.TagList;

public class ControlPane extends JPanel {
	static final long serialVersionUID = 2;
	
    JButton exportButton, prevButton, nextButton, copyButton, resetButton, saveButton, folderButton;
    MainTagGUI frame;
    Dimension controlAreaSize;
    JFileChooser expChooser;
    //boolean exportFlag = false;
    File startFile = null;
    String[] fileExtensions = {"jpg", "JPG", "tif", "TIF"};
	String tagFileName = "aberscanTagFile.cvs";
 
    public void setControlAreaSize( Dimension dim) { controlAreaSize = dim; }
    
    public ControlPane(final MainTagGUI frame, Directories dirs) {
        super(new BorderLayout());
        this.frame = frame;
        //exportFlag = false;
       
        JPanel cPanel = new JPanel();
        cPanel.setMaximumSize( controlAreaSize);
        cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.LINE_AXIS));
        cPanel.add(Box.createRigidArea(new Dimension(100,0)));
        
        //Create  the 'export' file chooser
        expChooser = new JFileChooser();
        expChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        expChooser.setMultiSelectionEnabled(false);
        expChooser.setCurrentDirectory(dirs.getImageDirectory());
        expChooser.setDialogTitle("Select the Folder you want to write the csv file to");
        
        //Create the export button.
    	//ImageIcon destIcon = new ImageIcon("images/selectDestFile.jpg");
        exportButton = new JButton("Export tag file");
        exportButton.setEnabled(true);
        exportButton.addActionListener(
        	new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			exportTags();
        		}
        	}
        );
        
        //Create the new folder button.
    	//ImageIcon destIcon = new ImageIcon("images/selectDestFile.jpg");
        folderButton = new JButton("Tag Another Folder");
        folderButton.setEnabled(true);
        folderButton.addActionListener(
        	new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			//first, save off the tags that were entered and check if valid
        			if(saveTags() == true) {
			
		    			// check to see if you want to export tags for the prior folder before moving to the new one
		        		if(frame.tPane.getTextChanged() == true) {
		        			if(JOptionPane.showConfirmDialog(null, "Question : Unsaved Tags exist. Do you want to export the tags for the current folder?" , "Unsaved Tags" , JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { 
		        				frame.cPane.exportTags();
		        			}
		        		}
		    			
		        		//reset the photoList and select the new folder
		        		startFile = null;	//reset so that the prompt is generated
		    			selectNewFolder();
        			}
        		}
        	}
        );
        
        //Create the prev button.
    	//ImageIcon prevIcon = new ImageIcon("images/previous.jpg");
        prevButton = new JButton("Previous");
        prevButton.setEnabled(true);
        prevButton.addActionListener(
        	new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
		        	moveToPrevPhoto();
        		}
        	}
        );
        
        //Create the next button.
       	//ImageIcon nextIcon = new ImageIcon("images/next.jpg");
        nextButton = new JButton("Next");
        nextButton.setEnabled(true);
        nextButton.addActionListener(
           	new ActionListener() {
           		public void actionPerformed(ActionEvent e) {
   		        	moveToNextPhoto();
           		}
           	}
        );
                
        //Create the copy button.
       	//ImageIcon copyIcon = new ImageIcon("images/copy.jpg");
        copyButton = new JButton("Reuse Tags");
        copyButton.setEnabled(true);
        copyButton.addActionListener(
           	new ActionListener() {
           		public void actionPerformed(ActionEvent e) {
   		        	copyTags();
           		}
           	}
        );
               
        //Create the reset button.
       	//ImageIcon resetIcon = new ImageIcon("images/reset.jpg");
        resetButton = new JButton("Reset Tags");
        resetButton.setEnabled(true);
        resetButton.addActionListener(
           	new ActionListener() {
           		public void actionPerformed(ActionEvent e) {
   		        	resetTags();
           		}
           	}
        );
   
        //Create the save button.
        //ImageIcon enterIcon = new ImageIcon("images/enter.jpg");
        saveButton = new JButton("Save Tags");
        saveButton.setEnabled(true);
        saveButton.addActionListener(
        	new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
		        	saveTags();
        		}
        	}
        );
        
        //For layout purposes, put the buttons in a separate panel
        int buttonSpacing = 5;
        cPanel.add(prevButton);
        cPanel.add(Box.createRigidArea(new Dimension(buttonSpacing,0)));
        cPanel.add(nextButton);
        cPanel.add(Box.createRigidArea(new Dimension(20,0)));
        cPanel.add(copyButton);
        cPanel.add(Box.createRigidArea(new Dimension(buttonSpacing,0)));
        cPanel.add(resetButton);
        cPanel.add(Box.createRigidArea(new Dimension(buttonSpacing,0)));
        cPanel.add(saveButton);
        cPanel.add(Box.createRigidArea(new Dimension(20,0)));
        cPanel.add(exportButton);
        cPanel.add(Box.createRigidArea(new Dimension(20,0)));
        cPanel.add(folderButton);
        
        add(cPanel);
    }
    
    public void moveToNextPhoto() {
	
    	//first, save off the tags that were entered. Check if valid
		if(saveTags() == false) { 
			return; 
		}
		
		//move to the next photo
		if(frame.iPane.endOfPhotos() == true) {
			JOptionPane.showMessageDialog(frame, "At the end");
		}
		else {	
			Photo photo = frame.iPane.getNextPhoto();
			frame.iPane.setScreenImage(photo);
			frame.tPane.preloadTextFields(photo.getTags());
			//System.out.println("ControlPane (nextPhoto):------------------");
			//photo.print();

			frame.setTitle(photo.getName());
			frame.repaint();
		}
	}
    
    private void moveToPrevPhoto() {
    	//first, save off the tags that were entered. Check if valid
		if(saveTags() == false) { 
			return; 
		}
		
		if(frame.iPane.startOfPhotos() == true) {
			JOptionPane.showMessageDialog(frame, "At the beginning");
		}
		else {
			Photo photo = frame.iPane.getPrevPhoto();
			frame.iPane.setScreenImage(photo);
			frame.tPane.preloadTextFields(photo.getTags());
			//System.out.println("ControlPane (prevPhoto):------------------");
			//photo.print();
			
			frame.setTitle(photo.getName());
			frame.repaint();
		}
	}
    
    private boolean saveTags() {
		Photo photo = frame.iPane.getCurPhoto();
		
  		//get the current set of tags from the tagPane
		TagList curTags = frame.tPane.captureTagText();
		
		//do one final check to ensure the tag text is legal
		if(curTags.containsValidText(frame) == false) {
			return(false);
		}
		else {
			photo.setTags(curTags);
			return(true);
		}
    	
    	//if any tag is non-null, then set the exportFlag to note that it needs to be written out to file
    	//if(curTags.containsNonNullTag() == true) {
    	//	exportFlag = true;
    	//}
  	}
    
    private void resetTags() {
    	//System.out.println("ControlPane (resetTags):------------------");
    	frame.tPane.preloadTextFields(new TagList());
    	frame.repaint();
    	frame.tPane.setTextChanged(true);
  	}
    
    private void copyTags() {
    	// this method is going to prepopulate the current screen with the values used for the previous photo
    	//System.out.println("ControlPane (resetTags):------------------");
    	
    	if(frame.iPane.startOfPhotos() == true) {
			JOptionPane.showMessageDialog(frame, "No previous image exists from which to copy tags");
		}
		else {
			Photo photo = frame.iPane.getPrevPhoto();
			frame.tPane.preloadTextFields(photo.getTags());
			frame.iPane.getNextPhoto();
			frame.repaint();
		}
  	}
    
    public void exportTags() {
    	//System.out.println("ControlPane (exportTags):------------------");
		PrintWriter csvFile = null;
	    File exportDir = frame.dirs.getImageDirectory();
    	
    	//first, save off the tags that were entered. Check if valid
		if(saveTags() == false) { 
			return; 
		}
    		
		//write out the csv file into the old folder if we have now moved to a new one
		if(frame.tPane.getTextChanged() == true) {
			try {
				csvFile = new PrintWriter(new FileWriter(exportDir.getAbsolutePath() + "\\" + tagFileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				System.exit(2);
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			csvFile.println("Photo,Tag1,Tag2,Tag3,Tag4,Tag5,Tag6,Tag7,Tag8,Tag9,Tag10");
			csvFile.println(exportDir.getAbsolutePath());
			//csvFile.println(",NOTE: tags can't contain double quotes (\"\") as part of the text. Use single quotes instead ('). Secondly, if a comma must be used, then surround the whole tag with double quotes at beginning and end,,,,,,,,,");
	        frame.iPane.photos.exportPhotoTags(csvFile);
	        csvFile.close();
	        
	        JOptionPane.showMessageDialog(null, "Export file '" + exportDir.getAbsolutePath() + "\\" + tagFileName + "' created");
	        frame.tPane.setTextChanged(false);	//reset the flag saying there is new tag text
        } 
		else {
			JOptionPane.showMessageDialog(null, "No new tag data exists. Export file not created");
		}
  	}
    
    public void importTags(Directories dirs, File csvFile, PhotoList photos) {
    	//System.out.println("ControlPane (importTags):------------------");
    	String[] fields;
    	TagList tagList;
    	

    	//ask the user if he wants to import this file, or skip it
	    //if(JOptionPane.showConfirmDialog(null, "Question : Found tag File in this folder. Do you want to import it?" , "Import Tags" , JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { 
	    if(1 == 1) {
	    	JOptionPane.showMessageDialog(null, "Found tag file in this folder and it has been imported");
	    	
	    	//read the contents of the csv file and store as a PhotoList
	    	try (BufferedReader reader = new BufferedReader(new FileReader(csvFile.getAbsolutePath()))) {
		        String line = null;
		        
		        //get rid of the first two lines
		        reader.readLine();
		        //reader.readLine();
		        
		        //now read the good stuff. format should be photo,tag1, ... , tag10
		        while ((line = reader.readLine()) != null) {
		            //System.out.println(line);
		        	fields = line.split(",");	//note - split only returns a string array of non-empty tags. List could just the photo name
		        	//System.out.println("fields = " + java.util.Arrays.toString(line.split(",")));
		        	
		        	//find the photo in the PhotoList
		        	//System.out.println("ControlPane: (importTags): photoName = " + fields[0]);
		        	int i = photos.findPhoto(new Photo(new File(dirs.getImageDirectory() + "\\" + fields[0])));
		        	//System.out.println("ControlPane: (importTags): i = " + i);
		        	if(i != -1) {
		        		tagList = new TagList();
		        		
		        		//build up the tags - knowing that the fields list contains only non-empty fields
		        		for(int j = 1 ; j < fields.length; j++ ) {
		        			tagList.setText(j-1, fields[j]);
		        		}
		        		for(int j = fields.length; j < tagList.getNumTags(); j++) {
		        			tagList.setText(j, "");
		        		}
		        		//store the Tag info with the photo
		        		photos.getPhoto(i).setTags(tagList);
		        		//photos.getPhoto(i).print();
		        		
		        		//pre-load the tags for the first photo. The rest will be handled by the next/prev actions
		        		if(i == 0) { frame.tPane.preloadTextFields(photos.getPhoto(i).getTags());}
		        	}
		        }
		    } catch (IOException x) {
		        System.err.format("IOException: %s%n", x);
		    }

	    	//photos.printPhotos();
	    }
  	}
    
    public boolean selectNewFolder() {
        FilePrompterGUI fileViewer;

    	//prompt for which file to start with (and therefore also the folder)
		if(startFile == null) {
	        fileViewer = new FilePrompterGUI(false, frame.dirs.getImageDirectory(), "Select the Starting Photo",  fileExtensions);
			if(fileViewer.isFileSelected() == true) {
				//note the new folder and starting file
				frame.dirs.setImageDirectory(fileViewer.getImageDir());
				
				//load all the photos found in that dir, and pre-load the images for these photos
				frame.iPane.photos.emptyList();	//clear out the photoList that might already exist from prior folders
				frame.iPane.loadPhotos(frame.dirs.getImageDirectory()); 	//this gets the photo info all loaded asap - with the images
				frame.iPane.preloadImages("gbs-tag");			// load of images run as a background thread  	
				frame.iPane.setCopyFlags(false);		//start with none of the photos being tagged
				//startingPhoto = new Photo(fileViewer.getStartFile());
				frame.iPane.setStartPhoto(new Photo(fileViewer.getStartFile()));	//find the start photo based on the user selection
				frame.iPane.setScreenImage(frame.iPane.getFirstPhoto());	// load up the first image
				
				//was there already a tagFile in this folder that we should use?
		        File tagFile = new File(frame.dirs.getImageDirectory().getAbsolutePath() + "\\" + tagFileName);
		        if(tagFile.exists() == true) {
		        	importTags(frame.dirs, tagFile, frame.iPane.photos);
		        }
		        
				frame.tPane.preloadTextFields(frame.iPane.getFirstPhoto().getTags());
				frame.setTitle(frame.iPane.getFirstPhoto().getName());	//put the name of the photo in the title
				return(true);
	        }
			else {
				return(false);
			}
		}
		return(false);
  	}
    
    public void writeTagsToPhoto() {
    	
  	}
}
