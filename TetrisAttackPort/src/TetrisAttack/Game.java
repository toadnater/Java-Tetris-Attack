
package TetrisAttack;

import java.util.Vector;
import java.util.Random;

import javax.swing.Timer;

class Game extends Screen {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// This static Screen variable is used for the creation of new blocks.
	// When a new block is made it is added to an allGraphics vector that belongs
	// to the screen (which Game is derived from). This handle can then be reached by
	// any block so it can be added to the right screen.
	// -- Previously tried to use Main.currentScreen, but memory pointer errors prevent
	//    that from being successful.
	public static Game screenHandle;
	
	protected class Cursor {
		
		TAGraphic image;
		public double offset = 16;
		public int posX = 0;				// Cursor parameters that mean more to us
		public int posY = 0;				// than their (X,Y) coordinates in pixels.		
	
		public Cursor() {
			image = new TAGraphic("game_cursor");
		}	
	}
	
	public Grid grid[];
	public Cursor gridCursor[];
	public AIController AI;
	
	private			int		BLOCK_VARIETY = 5;		// Different block colour count (based on difficulty)
	private 		int 	BLOCK_WIDTH = 16;
	private final 	int 	GRID_WIDTH = 6;			// Because 0 is actually the first line.
	private final 	int 	GRID_HEIGHT = 12; 		// Note: 11 Lines active at any given time.
	private final 	int 	STARTING_BLOCK_COUNT = 30;
	private final	int 	WARNING_HEIGHT = 10;
	public 	static	String	STATUS;
	private final	int		NUMBER_OF_PLAYERS = 2;
	private			int		PLAYER_INDEX = 0;
	private 		int		AI_INDEX = 1;
	
	private 		boolean	checkForGameOver;
	
	private 		int		sendGarbageBlocks;
	
	//public static final TAGraphic greenBlock = new TAGraphic("greenSquare");
	//public static final TAGraphic redBlock = new TAGraphic("redHeart");
	//public static final TAGraphic yellowBlock = new TAGraphic("yellowStar");
	//public static final TAGraphic tealBlock = new TAGraphic("tealTriangle");
	//public static final TAGraphic purpleBlock = new TAGraphic("purpleDiamond");
		
	TAGraphic panel1;
	TAGraphic panel2;
	
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
		panel2 = new TAGraphic("panel_lakitu");
		//setCursor(new TAGraphic("game_cursor"));
		panel1.location(8, 23);
		panel2.location(152, 23);
		
		gridCursor = new Cursor[2];
		gridCursor[0] = new Cursor();
		gridCursor[1] = new Cursor();
		gridCursor[PLAYER_INDEX].image.location(panel1.getRelativeCenterX() - 1 - (gridCursor[PLAYER_INDEX].image.getRelativeWidth() / 2), 
				(8 * 16) - 1 - (gridCursor[PLAYER_INDEX].image.getRelativeHeight() / 2));
		gridCursor[1].image.location(panel2.getRelativeCenterX() - 1 - (gridCursor[1].image.getRelativeWidth() / 2), 
				(8 * 16) - 1 - (gridCursor[1].image.getRelativeHeight() / 2));
				
		cursorPosition(gridCursor[0], 2, 5);
		cursorPosition(gridCursor[1], 2, 5);
		
		addImage(gridCursor[0].image);
		addImage(gridCursor[1].image);
		
		// We need to set up each player's working space with blocks laid out.
		
		Player player1 = new Player();
		
		AI = new ToddsAI();
		checkForGameOver = false;
		
		int[] gridConstants = { BLOCK_VARIETY, 
								BLOCK_WIDTH,
								GRID_WIDTH,
								GRID_HEIGHT,
								STARTING_BLOCK_COUNT,
								WARNING_HEIGHT
								};
		
		layoutGenerator gridGenerator = new layoutGenerator(gridConstants);
		String startingLayout = gridGenerator.randomStartLayout();
				
		grid = new Grid[2];
		
		grid[0] = new Grid(panel1, panel2, gridCursor[0], gridConstants, startingLayout);
		grid[1] = new Grid(panel2, panel1, gridCursor[1], gridConstants, startingLayout);
		grid[0].createGrid();
		grid[0].layoutGrid();
		grid[1].createGrid();
		grid[1].layoutGrid();		
		
		// At this point, we're going to associate the GarbageHandlers to each other so
		// they know who to send garbage to :)
		
		grid[0].gHandle.sendTo(grid[1].gHandle);
		grid[1].gHandle.sendTo(grid[0].gHandle);
		
		setBackground(new TAGraphic("vs_bg_lakitu")); 
		addImage(panel1);					// Done over here because of foreground -> background rules
		addImage(panel2);
		addImage(backgroundImage);
		
		// After we have our preliminary images registered in our allGraphics
		// list, we need to have allGraphics added to the screen.		
		addImages(allGraphics);	
		
		STATUS = "RUNNING";
	}
	
	public void cursorOffset(Cursor c, int x, int y) {
		c.posX += x;
		c.posY += y;
	}
	
	public void cursorPosition(Cursor c, int x, int y) {
		c.posX = x;
		c.posY = y;
	}
	
	public boolean cursorHasLeft(Cursor c) { return (c.posX - 1 >= 0);	}
	public boolean cursorHasRight(Cursor c) { return (c.posX + 1 < GRID_WIDTH - 1); }	// Because the cursor is 2 lengths wide
	public boolean cursorHasDown(Cursor c) { return (c.posY - 1 >= 0); }
	public boolean cursorHasUp(Cursor c) { return (c.posY + 1 < GRID_HEIGHT); }
	
	// ===================================================
	
    protected void specialEvents(int time) {
    	if (time % 15 == 0 && STATUS.equals("RUNNING")) {
    		for (int t = 0; t < NUMBER_OF_PLAYERS; t++) {
	    		if (grid[t].hasGridStatus()) {
	    			grid[t].pushGrid(0.1);
	    		}
    		}

			AI.updateHomeGrid(grid[AI_INDEX].returnGrid());
			AI.updateAwayGrid(grid[PLAYER_INDEX].returnGrid());
    	}
    	
    	if (time % (60 / 16) == 0 && STATUS.equals("RUNNING")) {
    		parseAiInstruction(AI.getNextInstruction());
    	}
    	
    	if (!STATUS.equals("PAUSED")) {
    		playAnimations();
    		AI.calculateNextInstruction();
    	}
    	
    	if (STATUS.equals("RUNNING")) {
    		for (int t = 0; t < NUMBER_OF_PLAYERS; t++) {
	    		grid[t].checkFalling();
	    		grid[t].applyGravity();
	    		grid[t].checkCombos();
	    		grid[t].disappearBlocks();
	    		grid[t].garbageDropCheck();
	    		if (grid[t].checkGameOver() && checkForGameOver) {
	    			STATUS = "GAMEOVER";
	    			// Do game over stuff here :)
	    		}
    		}
    	}
    	
    	setComponentZOrder(gridCursor[0].image, 1);
    	setComponentZOrder(gridCursor[1].image, 1);
		setComponentZOrder(panel1, getComponentCount() - 1);
		setComponentZOrder(panel2, getComponentCount() - 1);
		setComponentZOrder(backgroundImage, 1);
    }
    
    public void specialReloadsOnResize() {
    }   
    
    // ---------------------------------------
	// KEYBOARD FUNCTIONS FOR THIS OBJECT
	// ---------------------------------------
    
    public void actionDown() { actionDown(PLAYER_INDEX); }
    public void actionDown(int index) {
    	if (STATUS.equals("RUNNING")) {
			if (cursorHasDown(gridCursor[index])) {
				gridCursor[index].image.offset(0, (int)(gridCursor[index].offset * Main.globalScale));
				cursorOffset(gridCursor[index], 0, -1);
			}
    	}
    }
    
    public void actionUp() { actionUp(PLAYER_INDEX); }
    public void actionUp(int index) {
    	if (STATUS.equals("RUNNING")) {
	    	if (cursorHasUp(gridCursor[index])) {
	    		gridCursor[index].image.offset(0, (int)(-gridCursor[index].offset * Main.globalScale));
	    		cursorOffset(gridCursor[index], 0, 1);
	    	}
    	}
    }
    
    public void actionLeft() { actionLeft(PLAYER_INDEX); }
    public void actionLeft(int index) {
    	if (STATUS.equals("RUNNING")) {
	    	if (cursorHasLeft(gridCursor[index])) {
	    		gridCursor[index].image.offset((int)(-gridCursor[index].offset * Main.globalScale), 0);
	    		cursorOffset(gridCursor[index], -1, 0);
	    	}
    	}
    }
    
    public void actionRight() { actionRight(PLAYER_INDEX); }
    public void actionRight(int index) {
    	if (STATUS.equals("RUNNING")) {
	    	if (cursorHasRight(gridCursor[PLAYER_INDEX])) {
	    		gridCursor[index].image.offset((int)(gridCursor[index].offset * Main.globalScale), 0);
	    		cursorOffset(gridCursor[index], 1, 0);
	    	}
    	}
    }
    	
    public void actionZ() { actionZ(PLAYER_INDEX); }
    public void actionZ(int index) {
    	System.out.println("Screen? " + this.getName());
    			//isAncestorOf
    }
    
    public void actionX() { actionX(PLAYER_INDEX); }
    public void actionX(int index) {						// Ok button
    	if (STATUS.equals("RUNNING")) {
    		System.out.println("Left: " + grid[index].getBlockColour(gridCursor[index].posX, gridCursor[index].posY));
    		System.out.println("Right: " + grid[index].getBlockColour(gridCursor[index].posX + 1, gridCursor[index].posY));
    		grid[index].swapBlocks(gridCursor[index].posX, gridCursor[index].posY);
    	}
    }	
    public void actionS() { actionS(PLAYER_INDEX); }
    public void actionS(int index) {
    	if (STATUS.equals("RUNNING")) {
    		// For loop makes it look smooth and also forces the issue
    		grid[index].pushGrid(1);
    	}
    }
    
    public void actionA() { actionA(PLAYER_INDEX); }
    public void actionA(int index) {
    	actionS(index);
    }
    public void actionP() {
    	// Pause button :)
    	if (!STATUS.equals("PAUSED")) {
    		STATUS = "PAUSED";
    	} else {
    		STATUS = "RUNNING";
    	}
    }
    
    public void actionPlus() {
    	changeTimerSpeed(0.5);
    }
    
    public void actionMinus() {
    	changeTimerSpeed(2);
    }

    public void parseAiInstruction(AIInstruction instruction) {
    	switch(instruction) {
    		case MOVE_LEFT: actionLeft(AI_INDEX);
    		case MOVE_RIGHT: actionRight(AI_INDEX);
    		case MOVE_UP: actionUp(AI_INDEX);
    		case MOVE_DOWN: actionDown(AI_INDEX);
    		case SWAP: actionX(AI_INDEX);
    		case PUSH: actionS(AI_INDEX);
    		case IDLE:	// Do nothing
    		default:	// Unhandled case 
    	}
    }   
}