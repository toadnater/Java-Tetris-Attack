
package TetrisAttack;

import java.util.Vector;
import java.util.Random;

import javax.swing.Timer;

class Game extends Screen {

	// This static Screen variable is used for the creation of new blocks.
	// When a new block is made it is added to an allGraphics vector that belongs
	// to the screen (which Game is derived from). This handle can then be reached by
	// any block so it can be added to the right screen.
	// -- Previously tried to use Main.currentScreen, but memory pointer errors prevent
	//    that from being successful.
	public static Game screenHandle;
	
	public Grid playerGrid;
	
	public double cursor_offset = 16;
	public int cursor_posX = 0;				// Cursor parameters that mean more to us
	public int cursor_posY = 0;				// than their (X,Y) coordinates in pixels.
	
	private			int		BLOCK_VARIETY = 5;		// Different block colour count (based on difficulty)
	private 		int 	BLOCK_WIDTH = 16;
	private final 	int 	GRID_WIDTH = 6;			// Because 0 is actually the first line.
	private final 	int 	GRID_HEIGHT = 12; 		// Note: 11 Lines active at any given time.
	private final 	int 	STARTING_BLOCK_COUNT = 30;
	private final	int 	WARNING_HEIGHT = 10;
	public 	static	String	STATUS;
	
	//public static final TAGraphic greenBlock = new TAGraphic("greenSquare");
	//public static final TAGraphic redBlock = new TAGraphic("redHeart");
	//public static final TAGraphic yellowBlock = new TAGraphic("yellowStar");
	//public static final TAGraphic tealBlock = new TAGraphic("tealTriangle");
	//public static final TAGraphic purpleBlock = new TAGraphic("purpleDiamond");
		
	TAGraphic panel1;
	
	public Game() {
		super();	
		screenHandle = this;
		
		// --- Things that need to be done when a game is started
		
		// We need a few starting conditions before we start:
		// - Rules for the game (Puzzle, Versus, Time Attack, etc.)
		// - End conditions
		// - The menu we go back to when the game is over; are there rounds?
		// - The number of players
		// - If there are CPU opponents, we need their AI settings.
		// - We need a way of getting shared graphics like Blocks.
		// - We need a way of getting graphics specific to what each player in the
		//   game will be represented by: Characters, backgrounds, etc.
		
		// -- This becomes a lot more complicated when playing over the internet
		//    I would imagine.
		
		// -- Remember: Add foreground to background.
		// Maybe get a process to take care of all the z-orders?
		
		TAGraphic redBrick = new TAGraphic("redBrick", 8, 215);
		TAGraphic blueBrick = new TAGraphic("blueBrick", 152, 215);
		addImage(redBrick);
		addImage(blueBrick);
		
		// Animation moving cursor from top of screen to middle missing.
		panel1 = new TAGraphic("panel_yoshi");
		TAGraphic panel2 = new TAGraphic("panel_lakitu");
		setCursor(new TAGraphic("game_cursor"));
		panel1.location(8, 23);
		panel2.location(152, 23);
		cursor.location(panel1.getRelativeCenterX() - 1 - (cursor.getRelativeWidth() / 2), 
				(8 * 16) - 1 - (cursor.getRelativeHeight() / 2));
		cursorPosition(2, 5);	
		addImage(cursor);
		
		// We need to set up each player's working space with blocks laid out.
		
		Player player1 = new Player();
		
		int[] gridConstants = { BLOCK_VARIETY, 
								BLOCK_WIDTH,
								GRID_WIDTH,
								GRID_HEIGHT,
								STARTING_BLOCK_COUNT,
								WARNING_HEIGHT
								};
		
		layoutGenerator gridGenerator = new layoutGenerator(gridConstants);
		String startingLayout = gridGenerator.randomStartLayout();
		
		playerGrid = new Grid(panel1, panel2, gridConstants, startingLayout);
		playerGrid.createGrid();
		playerGrid.layoutGrid();
		
		setBackground(new TAGraphic("vs_bg_lakitu")); 
		addImage(panel1);					// Done over here because of foreground -> background rules
		addImage(panel2);
		addImage(backgroundImage);
		
		// After we have our preliminary images registered in our allGraphics
		// list, we need to have allGraphics added to the screen.		
		addImages(allGraphics);
		
		STATUS = "RUNNING";
	}
	
	public void cursorOffset(int x, int y) {
		cursor_posX += x;
		cursor_posY += y;
	}
	
	public void cursorPosition(int x, int y) {
		cursor_posX = x;
		cursor_posY = y;
	}
	
	public boolean cursorHasLeft() { return (cursor_posX - 1 >= 0);	}
	public boolean cursorHasRight() { return (cursor_posX + 1 < GRID_WIDTH - 1); }	// Because the cursor is 2 lengths wide
	public boolean cursorHasDown() { return (cursor_posY - 1 >= 0); }
	public boolean cursorHasUp() { return (cursor_posY + 1 < GRID_HEIGHT); }
	
	// ===================================================
	
    protected void specialEvents(int time) {
    	if (time % 15 == 0 && STATUS.equals("RUNNING")) {
    		if (playerGrid.hasGridStatus()) {
    			playerGrid.pushGrid(0.1);
    		}
    	}
    	
    	if (!STATUS.equals("PAUSED")) {
    		playAnimations();
    	}
    	
    	if (STATUS.equals("RUNNING")) {
    		playerGrid.checkFalling();
    		playerGrid.applyGravity();
    		playerGrid.checkCombos();
    		playerGrid.disappearBlocks();
    		if (playerGrid.checkGameOver()) {
    			STATUS = "GAMEOVER";
    			// Do game over stuff here :)
    		}
    	}
    	setComponentZOrder(cursor, 1);
		setComponentZOrder(panel1, getComponentCount() - 1);
		setComponentZOrder(backgroundImage, 1);
    }
    
    public void specialReloadsOnResize() {
    }
    
    // ---------------------------------------
	// KEYBOARD FUNCTIONS FOR THIS OBJECT
	// ---------------------------------------
    
    public void actionDown() {
    	if (STATUS.equals("RUNNING")) {
			if (cursorHasDown()) {
				cursor.offset(0, (int)(cursor_offset * Main.globalScale));
				cursorOffset(0, -1);
			}
    	}
    }
    
    public void actionUp() {
    	if (STATUS.equals("RUNNING")) {
	    	if (cursorHasUp()) {
	    		cursor.offset(0, (int)(-cursor_offset * Main.globalScale));
	    		cursorOffset(0, 1);
	    	}
    	}
    }
    
    public void actionLeft() {
    	if (STATUS.equals("RUNNING")) {
	    	if (cursorHasLeft()) {
	    		cursor.offset((int)(-cursor_offset * Main.globalScale), 0);
	    		cursorOffset(-1, 0);
	    	}
    	}
    }
    
    public void actionRight() {
    	if (STATUS.equals("RUNNING")) {
	    	if (cursorHasRight()) {
	    		cursor.offset((int)(cursor_offset * Main.globalScale), 0);
	    		cursorOffset(1, 0);
	    	}
    	}
    }
    	
    public void actionZ() {
    	System.out.println("Screen? " + this.getName());
    			//isAncestorOf
    }
    
    public void actionX() {						// Ok button
    	if (STATUS.equals("RUNNING")) {
    		System.out.println("Left: " + playerGrid.getBlockColour(cursor_posX, cursor_posY));
    		System.out.println("Right: " + playerGrid.getBlockColour(cursor_posX + 1, cursor_posY));
    		playerGrid.swapBlocks(cursor_posX, cursor_posY);
    	}
    }	
    
    public void actionS() {
    	if (STATUS.equals("RUNNING")) {
    		// For loop makes it look smooth and also forces the issue
    		playerGrid.pushGrid(1);
    	}
    }
    
    public void actionA() {
    	if (STATUS.equals("RUNNING")) {
    		playerGrid.createGarbage();
    	}
    	
    }
    public void actionP() {
    	// Pause button :)
    	if (!STATUS.equals("PAUSED")) {
    		STATUS = "PAUSED";
    	} else {
    		STATUS = "RUNNING";
    	}
		playerGrid.printGrid();
    }
    
    public void actionPlus() {
    	changeTimerSpeed(0.5);
    }
    
    public void actionMinus() {
    	changeTimerSpeed(2);
    }

}

