package TetrisAttack;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.image.BufferedImage;

import javax.swing.*;

abstract class Screen extends JPanel implements InputHandler, ActionListener {
	
	static final long serialVersionUID = -1;
	private double oldScale;
	public int cursor_offset;
	public int cursor_posX = 0;				// Cursor parameters that mean more to us
	public int cursor_posY = 0;				// than their (X,Y) coordinates in pixels.
	protected TAGraphic cursor;
	protected TAGraphic backgroundImage;
	protected Vector<TAGraphic> animatedGraphics;
	protected Vector<TAGraphic> allGraphics;
	private ArrayList<ScreenObject> graphicList;		// Store list for future use (resizing)
		
	// Timer for menu animations
	private Timer timer;
	private int timerSpeed = 1000 / 60;			// Measured in milliseconds (1 cycle per frame)
	private int timerDelay = 10;
	private int timerFrameNumber = 0;
	
    // All classes associated with UpdateOnKey to be overriden
    // by classes that implement Screen.
    public abstract void actionDown();
    public abstract void actionUp();
    public abstract void actionLeft();
    public abstract void actionRight();
    public abstract void actionZ();
    public abstract void actionX();
    public abstract void actionS();
    public abstract void actionA();
    public abstract void actionP();
    public abstract void actionPlus();
    public abstract void actionMinus();
	
	// Constructor
    public Screen() {
    	cursor_posX = 0;
    	cursor_posY = 0;
    	oldScale = Main.globalScale;
    	cursor = null;
    	backgroundImage = null;
    	allGraphics = new Vector<TAGraphic>();
    	animatedGraphics = new Vector<TAGraphic>();
    	
        // Set the layoutManager to null so we can place everything manually.
        this.setLayout(null);
        
        // This takes care of creating a new space based on whether the window
        // has been resized in a previous instance of Screen().
        if (Main.windowDimension_resize == null) {
        	this.setBounds(Main.windowRectangle_default);
        } else {
        	this.setBounds(Main.windowRectangle_resize);
        }
        
        createTimer();
    }
    
    // Overload
	public Screen(ArrayList<ScreenObject> GraphicList) {

		this();	// Build a plain Screen object
		
		graphicList = GraphicList;
		buildImageVectors(graphicList);

		if (getCursor(allGraphics) == -1) {
			System.out.println("Screen(): Error getting cursor!");
		}
		if (getBackground(allGraphics) == -1) {
			System.out.println("Screen(): Error getting background!");
		}
				
		addImages(allGraphics);
		removeTiles(allGraphics);
	}
    
    private void buildImageVectors(ArrayList<ScreenObject> graphicList) {
		for (ScreenObject graphic : graphicList) {
			TAGraphic tempGraphic = new TAGraphic(graphic.getName());
			int graphicX = graphic.getX();
			int graphicY = graphic.getY();
			tempGraphic.location(graphicX, graphicY);
			// Handle extra parameters somehow (Set up a system?)
			tempGraphic.tiles = graphic.getTiles();
			tempGraphic.isCursor = graphic.isCursor();
			tempGraphic.isBackground = graphic.isBackground();
			tempGraphic.setVisible(graphic.isVisible());
			tempGraphic.animated = graphic.isAnimated();
			tempGraphic.loopAnimation = graphic.isLooped();
			tempGraphic.text(graphic.getText());		// Supposed to be useful for textboxes...
						
			allGraphics.addElement(tempGraphic);
		}		
    }
    
    protected void addImages(Vector<TAGraphic> images) {
    	for (TAGraphic i : images) {
    		this.add(i);
    	}
    }
    
    protected void addImage(TAGraphic image) {
    	allGraphics.add(image);
    }
    
    // Tell our screen what graphic to associate with as the cursor
    protected void setCursor(TAGraphic image) {
    	cursor = image;
    	cursor.isCursor = true;
    }
    
	// Set up a static background
	protected void setBackground(TAGraphic bg) {
		backgroundImage = bg;
		backgroundImage.isBackground = true;
	}
	
	// Overload for a tiling background
	private void setBackground(TAGraphic bg, boolean tiles) {
		backgroundImage = bg;
		backgroundImage.isBackground = true;
		backgroundImage.tiles = tiles;
	}
	
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (TAGraphic i : allGraphics) {
        	if (i.tiles) {
        		tileImage(g, i);
        	}
        }

    }  
    
    // Have to be careful to remember that getWidth() and getHeight()
    // are for the entire window - Using a Mask would be a good
    // idea if you only need the tile to be contained to a small area.
    // -- Currently hard-coded for one direction (down-right -> up-left) 
    protected void tileImage(Graphics g, TAGraphic image) {
        int iw = image.getCurrentImage().getIconWidth();
        int ih = image.getCurrentImage().getIconHeight();
        if (iw > 0 && ih > 0) {
            for (int x = image.getX(); x < getWidth(); x += iw) {
                for (int y = image.getY(); y < getHeight(); y += ih) {
                    g.drawImage(image.getCurrentImage().getImage(), x, y, iw, ih, this);
                }
            }
        }
    }
    
    public void UpdateOnKey(KeyEvent key) {
    	if (key.getKeyCode() == KeyEvent.VK_DOWN) { actionDown(); } 
    	else if (key.getKeyCode() == KeyEvent.VK_UP) { actionUp(); }
    	else if (key.getKeyCode() == KeyEvent.VK_LEFT) { actionLeft(); }
    	else if (key.getKeyCode() == KeyEvent.VK_RIGHT) { actionRight(); }
    	else if (key.getKeyCode() == KeyEvent.VK_P) { actionP(); }
    	else if (key.getKeyCode() == KeyEvent.VK_A) { actionA(); }
    	else if (key.getKeyCode() == KeyEvent.VK_S) { actionS(); }
    	else if (key.getKeyCode() == KeyEvent.VK_Z) { actionZ(); }
    	else if (key.getKeyCode() == KeyEvent.VK_X) { actionX(); }
    	else if (key.getKeyCode() == KeyEvent.VK_EQUALS) { actionPlus(); }
    	else if (key.getKeyCode() == KeyEvent.VK_MINUS) { actionMinus(); }
    }
    
    // Set up and start our timer for menu animations
    private void createTimer() {
        timer = new Timer(timerSpeed, this);
        timer.setInitialDelay(timerDelay);
        timer.start();
    }
    
    public void changeTimerSpeed(double factor) {
    	timer.stop();
    	timerSpeed = (int)((double)timerSpeed * factor);
    	System.out.println("Changing timer speed: " + timerSpeed + "ms per cycle");
    	createTimer();
    }
    
    abstract protected void specialEvents(int time);
    
    // Implements ActionListener for our Timer events
    // Will cue any animations for menu items.
    // Override in Timer() class.
    // -- CURRENT BUG: Too many resizes causes timer to slow down...
    public void actionPerformed(ActionEvent e) {
    	
    	if (++timerFrameNumber > 59) timerFrameNumber = 0;
    	
    	checkForTilingBackground();
    	specialEvents(timerFrameNumber);
    	
	    repaint();
    }
    
    public void addGraphic(TAGraphic g) {
    	this.add(g);
    	//allGraphics.insertElementAt(g, 0);
    	allGraphics.addElement(g);
    }
    
    private void checkForTilingBackground() {
    	if (backgroundImage != null) {
	    	if (backgroundImage.tiles) {
	    		double offsetValue = -1 * Main.globalScale; 
		    	backgroundImage.offset(offsetValue, offsetValue);
		    	// Because if we just keep on decrementing X, if this menu stays
		    	// on the screen too long the X value: 
		    	// ** Assumes background to be SQUARE repeating image.
		    	// - could reach its minimum integer value (-2147483648)
		    	// - will cause paintComponent's for() loops time to take longer as time passes.
		    	// -- The jitter in the background comes from here.
		    	if (backgroundImage.getX() % backgroundImage.getWidth() == 0) {
		    		backgroundImage.location(0,0);
		    	}
	    	}
    	}
    }
    
    // If the window is resized, what specials needs do your extension
    // of Screen() have that need to be re-applied?
    abstract public void specialReloadsOnResize();
    
    // Should probably be called reloadGraphics()...
    public void resizeGraphics(double Scale) {
    	
    	if (oldScale == Scale) {
    		return;
    	}
    	
    	ArrayList<ScreenObject> tempList = new ArrayList<ScreenObject>();
    	
		for (TAGraphic g : allGraphics) {
			this.remove(g);
			ScreenObject tempObject = new ScreenObject();
			tempObject.setName(g.getName());
			tempObject.setX((int)((double)g.getX() / oldScale));
			tempObject.setY((int)((double)g.getY() / oldScale));
			tempObject.setTiles(g.tiles);
			tempObject.setCursor(g.isCursor);
			tempObject.setBackground(g.isBackground);
			tempObject.setVisible(g.isVisible());
			tempObject.setText(g.getText());
			tempObject.setAnimated(g.animated);
			tempObject.setLooped(g.isLooped());
			tempList.add(tempObject);
		}
		allGraphics.removeAllElements();
		
		System.out.println("old: " + oldScale + " / new: " + Scale + " / global: " + Main.globalScale);
		oldScale = Scale;
		
		buildImageVectors(tempList);
		loadAnimations();
		
		specialReloadsOnResize();
		
		if (getCursor(allGraphics) == -1) {
			System.out.println("Screen.resizeGraphics(): Error getting cursor!");
		}
		if (getBackground(allGraphics) == -1) {
			System.out.println("Screen.resizeGraphics(): Error getting background!");
		}
		
		addImages(allGraphics);
		removeTiles(allGraphics);
		
		int Xcoord = (int)((double)this.getX() * Scale);
		int Ycoord = (int)((double)this.getY() * Scale);
		int Xlength = (int)((double)this.getWidth() * Scale);
		int Ylength = (int)((double)this.getHeight() * Scale);
		Rectangle resize = new Rectangle(Xcoord, Ycoord, Xlength, Ylength);
		this.setBounds(resize);
    }
    
	// Left outside of the TAGraphic class (where it could go in the constructor I
	// suppose) because what if different menus / screens have different animations
	// for the same assets? Do we use separate files and change the method to contain
	// it's path? ...
	public void loadAnimations() {
		for (TAGraphic g : allGraphics) {
			if (g.loadAnimation()) {
				animatedGraphics.add(g);
			}
		}
	}
	
	public void playAnimations() {
		for (TAGraphic g : animatedGraphics) {
			g.playIfReady();
		}
	}
    
    // Removes double instances of objects that tile
    // this is done because paintComponent handles their drawn status instead.
    private void removeTiles(Vector<TAGraphic> graphicList) {
    	for (TAGraphic g : graphicList) {
    		if (g.tiles) {
    			this.remove(g);
    		}
    	}
    }
    
    public TAGraphic getGraphic(String name) {
    	for (TAGraphic g : allGraphics) {
    		if (g.getName().equals(name)) {
    			return g;
    		}
    	}
    	return null;
    }
    
    private int getBackground(Vector<TAGraphic> graphicList) {
    	int index = 0;
    	for (TAGraphic g : graphicList) {
    		if (g.isBackground()) {
    			setBackground(allGraphics.get(index), allGraphics.get(index).tiles);
    			System.out.println("Screen.getBackground(): " + g.name);
    			return index;
    		}
    		index++;
    	}
    	return -1;
    }
	
    private int getCursor(Vector<TAGraphic> graphicList) {
    	int index = 0;
    	for (TAGraphic g : graphicList) {
    		if (g.isCursor()) {
    			setCursor(allGraphics.get(index));
    			System.out.println("Screen.getCursor(): " + g.name);
    			return index;
    		}
    		index++;
    	}
    	return -1;
    }
    
    // Overload for menus where only the vertical really concerns us at the moment.
    public void setCursorPosition(int y) {
    	cursor_posY = y;
    }
    
    public void setCursorPosition(int x, int y) {
    	cursor_posX = x;
    	cursor_posY = y;
    }
    
    public void transitionTo(Color colour, int layer) {
    	// This can be fade-to-black, overlay to green, etc.
    	// Layer specifies if it is just the background making the
    	// transition, or everything (first through to last), etc.
    }
    
    public void unloadScreen() {
    	for (TAGraphic a : allGraphics) {
    		this.remove(a);
    	}
    	allGraphics.removeAllElements();
    	
    	timer.stop();
    	cursor = null;
    	backgroundImage = null;
    	repaint();
    }
}