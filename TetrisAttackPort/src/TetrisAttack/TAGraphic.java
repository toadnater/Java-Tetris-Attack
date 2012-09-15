/* Graphics.java
 * 
 * Contains all the information for various graphical pieces. 
 */

package TetrisAttack;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Tetris Attack Graphic
class TAGraphic extends JLabel {
	
	// Vector to hold all the images.
	Vector<ImageIcon> images;
	
	String name;
	int currentImageIndex;
	boolean animated;
	boolean loopAnimation;
	boolean tiles;
	boolean isCursor;
	boolean isBackground;
	
	// Handles keyframes and animation functions for a graphic
	AnimationHandle animation;
    
	// Constructor
	public TAGraphic() {
		images = new Vector<ImageIcon>();
		currentImageIndex = 0;
		animated = false;
		loopAnimation = false;
		tiles = false;
		isCursor = false;
		isBackground = false;
		animation = new AnimationHandle();
	}
	
	// Constructor Overload
	public TAGraphic(String graphicName) {
		this();
		name = "Default_Name";
		
		if (graphicName != null) {
			name = graphicName;
			if (LoadGraphics(graphicName)) {
				setIcon(getCurrentImage());
				//setSize((int)((double)getCurrentImage().getIconWidth() * Main.globalScale),
						//(int)((double)getCurrentImage().getIconHeight()* Main.globalScale));
				setSize(getCurrentImage().getIconWidth(), getCurrentImage().getIconHeight());
				if (images.size() > 1) {
					animated = true;
				}
			}
		}
	}
	
	// Constructor Overload
	public TAGraphic(String graphicName, int x, int y) {
		this(graphicName);
		location(x, y);
	}
	
	private boolean LoadGraphics(String graphicName) {
		try {
			String graphicPath = getPath(graphicName, "src/TetrisAttack/graphicList");
			// System.out.println("TAGraphic.LoadGraphics() - Creating image: " + graphicName + " @ " + graphicPath);
			
			if (!graphicPath.isEmpty()) {
				ArrayList<String> paths = loadPaths(graphicPath);
				for (String imageName : paths) {
					images.addElement(LoadImage(imageName));
				}
				return true;
			} else {
				System.out.println("         -Error: path for " + graphicName + " not defined");
			}
		} catch (Exception e) {
			System.out.println("TAGraphic.LoadGraphics() - Error - " + e.getMessage());
		}
		return false;
	}
	
	
	private ArrayList<String> loadPaths(String path) {
		
		ArrayList<String> eachPath = new ArrayList<String>(); 
		
		Scanner pathScanner = new Scanner(path);
		pathScanner.useDelimiter(" ");
		while (pathScanner.hasNext()) {
			eachPath.add(pathScanner.next());
		}
		pathScanner.close();
		return eachPath;
	}
	
	// Read our assetList file that contains all the filenames for
	// a given graphic type.
	private String getPath(String graphicName, String fileName) {
		String readLine = "";
		try {
			File assetFile = new File(fileName);
			Scanner assetScanner = new Scanner(new FileReader(assetFile));
			assetScanner.useDelimiter("\n");
			// Search the file until we find what we need, then extract it and break.
			while (assetScanner.hasNext()) {
				readLine = assetScanner.next();
				// Look for graphic header (<graphic>: <image> <image> <...>)
				// and check to see if our graphic header matches our graphic
				if (readLine.trim().length() > graphicName.length()) {
					if (readLine.trim().substring(0, graphicName.length() + 1).equals(graphicName + ":")) {
						assetScanner.close();
						return(readLine.trim().substring(graphicName.length() + 2).trim());
					}
				}
			}
		} catch (Exception e) {
			// If the file assetList cannot be opened or the FileReader / Scanner
			// is not successfully created, it will throw an exception here.
			System.out.println("TAGraphic.getPath() - Error: " + e);
		}
		
		// Return a null value if we do not find our path in the file
		return(new String());
	}
	
	private ImageIcon LoadImage(String imageName) {
	    BufferedImage readImage = null;
		try {
			// Note that this is not bound to a specific image type.
	    	readImage = ImageIO.read(new File("assets/img/"+imageName));
	    } catch (IOException ex){
	    	System.out.println("TAGraphic.LoadImage() - Error: Can't read file at assets/img/"+imageName);
	    }
	    ImageIcon image = new ImageIcon(readImage);
	    int imageWidth = image.getIconWidth();
	    int imageHeight = image.getIconHeight();
	    /*
	     * This mess of a return line takes the image we've read in, creates an instance of it
	     * that is to the scale we need it (either 1:1 on first load, or 1:X otherwise), and turns
	     * it into an ImageIcon type that we can actually display. This is then immediately
	     * returned to LoadGraphics(), where it is pushed into our vector containing all our
	     * saved instances of our opened (and perhaps modified) files.
	     */
	   	return (new ImageIcon( readImage.getScaledInstance((int)((double)imageWidth * Main.globalScale), (int)((double)imageHeight * Main.globalScale), Image.SCALE_SMOOTH) ));
	   	//return (new ImageIcon( readImage ));
	}
	
	public void text(String text) {
		this.setText(text);
	}
	
	// Refers to existing function while enforcing global scale and positioning
	public void location(double x, double y) {
		this.setLocation((int)(x * Main.globalScale) , (int)(y * Main.globalScale));
	}
	
	// Overload
	public void location(int x, int y) {
		location((double)x, (double)y);
	}
	
	// Note to self: Keep an eye on this function.
	public void offset(double x, double y) {	
		setLocation((int)(x + (double)this.getX()), (int)(y + (double)this.getY()));
	}
	
	public boolean loadAnimation() {
		if (isAnimated()) {
			return animation.loadInstructions(this.name);
		}
		return false;
	}
	
	// Overload
	// For images that only have 1 animation, this will typically be labeled
	// by its name. Otherwise, we can specify which it is. (Ex. Menu Open / Close)
	public boolean loadAnimation(String animationName) {
		if (isAnimated()) {
			return animation.loadInstructions(animationName);
		}
		return false;
	}
	
	public boolean reloadAnimation() {
		if (isAnimated()) {
			animation = new AnimationHandle();
			return animation.loadInstructions(this.name);
		}
		return false;
	}
	
	public boolean reloadAnimation(String animationName) {
		if (isAnimated()) {
			animation = new AnimationHandle();
			return animation.loadInstructions(animationName);
		}
		return false;
	}
	
	public void playIfReady() {
		if (isAnimated() && animation.isPlaying()) {
			//System.out.println("TAGraphic.playIfReady (" + this.name + "): Should play next frame...");
			playNextFrame();
			if (!animation.isPlaying()) {
				animation.end();
				if (this.isLooped()) {
					animation.start();
				}
			}
		}
	}
	
	public void stopAnimation() {
		if (isAnimated()) {
			animation.stop();
		}
	}
	
	public void startAnimation() {
		if (isAnimated()) {
			animation.start();
		}
	}
	
	public void startAnimationImmediately() {
		if (isAnimated()) {
			animation.start();
			playNextFrame();
		}
	}
	
	private void playNextFrame() {
		playFrame(animation.nextInstruction());
	}
	
	public void playFirstFrame() {
		playFrame(animation.resetToFirstFrame());
	}
	
	private void playFrame(AnimationInstruction i) {
		if (i == null) {
			//System.out.println("TAGraphic.playFrame: null instruction given");
			return;
		}
		if (currentImageIndex != i.imageNumber) {
			try {
				this.setIcon(images.get(i.imageNumber));
				currentImageIndex = i.imageNumber;
				// set index last because we do not want to apply the changes if
				// setIcon() throws an exception!
			} catch (Exception e) {
				System.out.println("TAGraphic.playFrame(): Array out of bounds index? " + e.toString());
				System.out.println(" -- Have you issued an instruction for an image that does not exist?");
			}
		}
		
		// Scale only if necessary - this can become costly over time.
		//if (this.getWidth() != i.width || this.getHeight() != i.height) {
			//stretchGraphic(i.width, i.height);
		//}
		
		if (i.x != -1 && i.y != -1 && i.width != -1 && i.height != -1) {
			this.setBounds(i.x, i.y, i.width, i.height); // Sets absolute
		}
		
		if (i.x != -1 && i.y != -1) {
			this.location(i.x, i.y);	// Sets based on window size!
		}
		
		if (i.alpha == 0.0) {
			this.setVisible(false);
		} else {
			this.setVisible(true);
		}
	}
	
	public void reverseAnimation() {
		animation.reverseInstructions();
	}
	
	public void stretchGraphic(int newWidth, int newHeight) {
		
		ImageIcon originalImage = images.get(currentImageIndex);
						
		Image newImage = originalImage.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
		originalImage.setImage(newImage);
	}
	
	public void firstImage() {
		this.setIcon(images.get(currentImageIndex = 0));
	}
	
	public void nextImage() {
		if (currentImageIndex + 1 < images.size()) {
			this.setIcon(images.get(++currentImageIndex));
		} else {
			firstImage();
		}
	}
	
	public void setImage(int index) {
		if (index >= 0 && index < images.size()) {
			this.setIcon(images.get(index));
			currentImageIndex = index;
		}
	}
	
	public int getCenterX() { return (int)(this.getX() + this.getWidth() / 2); }
	public int getCenterY() { return (int)(this.getX() + this.getHeight() / 2); }
	public int getRelativeCenterX() { return (int)(((double)this.getX() + (double)this.getWidth() / 2) / Main.globalScale); }
	public int getRelativeCenterY() { return (int)(((double)this.getY() + (double)this.getHeight() / 2) / Main.globalScale); }	
	public int getRelativeX() {	return (int)((double)this.getX() / Main.globalScale); }
	public int getRelativeY() {	return (int)((double)this.getY() / Main.globalScale); }	
	public int getRelativeWidth() { return (int)((double)this.getWidth() / Main.globalScale); }
	public int getRelativeHeight() { return (int)((double)this.getHeight() / Main.globalScale); }
	public ImageIcon getCurrentImage() { return images.get(currentImageIndex); }
	public boolean isBackground() { return isBackground; }
	public boolean isCursor() { return isCursor; }	
	public boolean isAnimated() { return animated; }
	public boolean isLooped() { return loopAnimation; }
	public void setLooped(boolean set) { loopAnimation = set; }
	
	// I believe this overrides Component.getName()
	public String getName() { return name; }
	public void setName(String str) { name = str; }
	
	public boolean delete() {
		/*
		 * Because Java does its own garbage cleaning, this function doesn't really do
		 * anything. There is no way to actually delete all the old instances of
		 * our graphics when we reload new instances for scaling when our window
		 * is resized, so they just continue to take up space in memory, however
		 * this does not seem to impact performance at all. If anything, having to
		 * reload all the images again because they aren't stored in memory is our
		 * biggest performance concern.
		 */
		try {
			images.removeAllElements();
			System.out.println("TAGraphic.delete(): Successfully removed all elements");
		} catch (Exception e) {
			System.out.println("TAGraphic.delete(): Failed to set image to null");
			return false;
		}
		return true;
	}	
	
	// Probably not the best way to go about this but it might give a performance boost?
	// Forces a graphic to be considered animated so we can load animations into it, 
	// regardless of how many images are associated with it. (Instead of just having
	// a double of an image loaded into the graphic to trigger it as animated)
	public void forceAnimated() { animated = true; }
}


