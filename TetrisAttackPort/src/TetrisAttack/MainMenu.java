
package TetrisAttack;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Vector;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class MainMenu extends Screen {

	public int cursor_offset = 16;
	public ArrayList<MenuLayer> menus;
	public MenuLayer currentMenu;
	public MenuLayer 	ML_TOP, 
							ML_1PGAME, 
								ML_STAGECLEAR,
									ML_STAGECLEAR_PASSWORD,
								ML_PUZZLE,
									ML_PUZZLE_PASSWORD,
								ML_VERSUS,
									ML_VERSUS_PASSWORD,
							ML_2PGAME, 
							ML_HOWTOPLAY, 
							ML_HOWTOIMPROVE, 
							ML_OPTION;
	
	private static final ArrayList<ScreenObject> items = new ArrayList<ScreenObject>(
			Arrays.asList(
				new ScreenObject("menu_cursor", "all", 40, 54, "cursor"),
				new ScreenObject("menu_background", "all", 0, 0, "tiles background"),
				new ScreenObject("overlay_blue", "all",  0, 0, "tiles"),
				new ScreenObject("selectWindow_shadow", "all", 21, 18),
				new ScreenObject("selectWindow", "all", 16, 15),
				new ScreenObject("menu_yoshi", "all", 173, 102, "animated loop"),
				new ScreenObject("menu_yoshiShadow", "all", 194, 209, "animated loop"),
				new ScreenObject("menu_speechWindow", "all", 161, 49, "animated loop"),
				new ScreenObject("1playergame", "top 1pgame", 38, 53, "animated"),
				new ScreenObject("2playergame", "top 2pgame", 38, 69, "animated"),
				new ScreenObject("howtoplay", "top howtoplay", 38, 85, "animated"),
				new ScreenObject("howtoimprove", "top howtoimprove", 38, 101, "animated"),
				new ScreenObject("option", "top option", 38, 117, "animated"),		
				new ScreenObject("menu_top", "top", 32, 47, "animated"),
				new ScreenObject("menu_box1Shadow", "top", 32, 47),
				new ScreenObject("aisokbutton", "all", 144, 28),
				new ScreenObject("biscancelbutton", "all", 183, 28),
				
				// 1 Player Menu
				new ScreenObject("endless", "1pgame", 53, 76, "animated visible=false"),
				new ScreenObject("stageclear", "1pgame stageclear", 53, 108, "animated visible=false"),
				new ScreenObject("puzzle", "1pgame puzzle", 53, 120, "animated visible=false"), 
				
				// 1P & 2P Menu
				new ScreenObject("timetrial", "1pgame 2pgame", 53, 92, "animated visible=false"),
				new ScreenObject("versus", "1pgame 2pgame versus", 53, 132, "animated visible=false")
			)
		);

	
	public MainMenu(String menuLevel) {
		super(items);
		createdMenuLayers();
		createMenuStructure();
		loadAnimations();
		currentMenu = ML_TOP;
		
		specialReloadsOnResize();
		
		if (menuLevel != null) {
			// alter menu displayed
			// (this is used if the player is returning to the menu from
			//  another menu, for example by quitting a 1P game)
		}
	}
	
	public void createdMenuLayers() {	
		ML_TOP = new MenuLayer(null, "menu_top");
		ML_1PGAME = new MenuLayer(ML_TOP, "1playergame");
			ML_STAGECLEAR = new MenuLayer(ML_1PGAME, "stageclear");
				ML_STAGECLEAR_PASSWORD = new MenuLayer(ML_STAGECLEAR, "stageclear_password");
			ML_PUZZLE = new MenuLayer(ML_1PGAME, "puzzle");
				ML_PUZZLE_PASSWORD = new MenuLayer(ML_PUZZLE, "puzzle_password");
			ML_VERSUS = new MenuLayer(ML_1PGAME, "versus");
				ML_VERSUS_PASSWORD = new MenuLayer(ML_VERSUS, "versus_password");
		ML_2PGAME = new MenuLayer(ML_TOP, "2playergame");
		ML_HOWTOPLAY = new MenuLayer(ML_TOP, "howtoplay");
		ML_HOWTOIMPROVE = new MenuLayer(ML_TOP, "howtoimprove");
		ML_OPTION = new MenuLayer(ML_TOP, "option");
	}

	public void createMenuStructure() {

		ML_TOP.addChildMenu("1playergame", ML_1PGAME);
			ML_1PGAME.addChildMenu("endless", null);
			ML_1PGAME.addChildMenu("timetrial", null);
			ML_1PGAME.addChildMenu("stageclear", ML_STAGECLEAR);
				ML_STAGECLEAR.addChildMenu("newgame", null);
				ML_STAGECLEAR.addChildMenu("password", ML_STAGECLEAR_PASSWORD);
			ML_1PGAME.addChildMenu("puzzle", null);
				ML_PUZZLE.addChildMenu("newgame", null);
					ML_PUZZLE.addChildMenu("password", ML_PUZZLE_PASSWORD);
			ML_1PGAME.addChildMenu("versus", ML_1PGAME);
				ML_VERSUS.addChildMenu("newgame", null);
					ML_VERSUS.addChildMenu("password", ML_VERSUS_PASSWORD);	
		ML_TOP.addChildMenu("2playergame", ML_2PGAME);
			ML_2PGAME.addChildMenu("timetrial", null);
			ML_2PGAME.addChildMenu("versus", null);
		ML_TOP.addChildMenu("howtoplay", ML_HOWTOPLAY);
		ML_TOP.addChildMenu("howtoimprove", ML_HOWTOIMPROVE);
		ML_TOP.addChildMenu("option", ML_OPTION);
		
		menus = new ArrayList<MenuLayer>(
				Arrays.asList(
					ML_TOP, 
						ML_1PGAME, 
							ML_STAGECLEAR,
								ML_STAGECLEAR_PASSWORD,
							ML_PUZZLE,
								ML_PUZZLE_PASSWORD,
							ML_VERSUS,
								ML_VERSUS_PASSWORD,
						ML_2PGAME, 
						ML_HOWTOPLAY, 
						ML_HOWTOIMPROVE, 
						ML_OPTION
				));
		
		System.out.println("MainMenu.createMenuStructure(): Completed Successfully");
	}
	
	// Special Events called by the Screen's timer
	// gives us the current frame number so we can control things like
	// cursor blinking and other animation for the menus.
	public void specialEvents(int time) {
		if (time % 15 == 0) {					// Performed 4 times a second
			blinkCursor();
		}
		playAnimations();
	}
	
	private void blinkCursor() {
		if (cursor != null) {
			cursor.setVisible(cursor.isVisible() ? false : true);
		}
	}
		
	public void specialReloadsOnResize() {	
		YoshiMovements();
		StartAnimations();
	}
	
	public void StartAnimations() {
    	for (TAGraphic g : animatedGraphics) {
    		//if (g.getName().equals("menu_yoshi")) {
    			System.out.println("MainMenu.actionLeft(): Starting " + g.getName() + " animation...");
    			g.playIfReady();
    			//g.stretchGraphic(1, 2);
    		//}
    	}
	}
	
	private void YoshiMovements() {
    	for (TAGraphic g: animatedGraphics) {    	
    		if (g.getName().equals("menu_yoshi")) {
    	    	// Creating Yoshi Transitions!
    			g.animation.CreateSlide(2, 3);
    	    	g.animation.CreateSlide(1, 2);
    	    	g.animation.CreateSlide(0, 1);
    	    	g.animation.start();
    		} else if (g.getName().equals("menu_yoshiShadow")) {
    			// Start his shadow
    			g.animation.start();
    		} else if (g.getName().equals("menu_speechWindow")) {
    			g.animation.CreateSlide(2, 3);
    	    	g.animation.CreateSlide(1, 2);
    	    	g.animation.CreateSlide(0, 1);
    			g.animation.start();
    		}
    	}
	}
	
    private void appendSlide(TAGraphic g, String animationName) {
		AnimationInstruction ai = new AnimationInstruction();
		int lastIndex = g.animation.numberOfInstructions() - 1;
		
		ai = g.animation.getInstruction(lastIndex);
		
		if (g.loadAnimation(animationName)) {
			lastIndex = g.animation.numberOfInstructions() - 1;
			g.animation.updateInstruction(lastIndex - 1, ai);
			ai = g.animation.getInstruction(lastIndex);
			ai.frameNumber += currentMenu.getPosition() * 2;
			g.animation.CreateSlide(lastIndex - 2, lastIndex);
			lastIndex = g.animation.numberOfInstructions() - 1;
			g.animation.removeInstruction(lastIndex - 1);
		} else {
			System.out.println("MainMenu.appendSlide(): animation specified not loaded properly - aborting appendSlide()");
		}
    }
	
	private void transitionMenu(String itemName) {

		TAGraphic graphic;
		if ((graphic = getGraphic(itemName)) != null) {
			
			double numberOfItems = 	currentMenu.numberOfChildren();
			double exponent = 		1;		//Default if element is the middle in an odd-numbered list.
			double pos = 			currentMenu.getPosition();
			
			if (pos > Math.ceil(numberOfItems / 2)) {
				exponent = (numberOfItems - pos);
			} else if (pos < Math.floor(numberOfItems / 2)) {
				exponent = (1 / (pos + 1));
			}
			
			double existingKeyframes = graphic.animation.numberOfInstructions();
			TAGraphic menuItem = getGraphic(currentMenu.getChildName());
			int border = 5; // constant based on menu border size;
						
			for (int i = 0; i < existingKeyframes; i++) {
    			AnimationInstruction updateInstruction = new AnimationInstruction();
    			double destY = (menuItem.getRelativeY() - graphic.getRelativeY() - border);
    			double newPositionY = graphic.getRelativeY() + (destY) * (Math.pow(((i + 1) / existingKeyframes),exponent));
    			updateInstruction = graphic.animation.getInstruction(i);
    			updateInstruction.y = (int)Math.round(newPositionY);
    			graphic.animation.updateInstruction(i, updateInstruction);
			}
			
	    	graphic.animation.start();
		} else {
			System.out.println("MainMenu.transitionMenu(): getGraphic() returned null");
			System.out.println(" --- Graphic is not within allGraphics list");
		}
	}
	// ---------------------------------------
	// KEYBOARD FUNCTIONS FOR THIS OBJECT
	// ---------------------------------------
	
    public void actionDown() {
    	if (currentMenu.hasNext()) {
    		cursor.offset(0, (int)(cursor_offset * Main.globalScale));
    		currentMenu.next();
    	}
    }
    
    public void actionUp() {
    	if (currentMenu.hasPrevious()) {
    		cursor.offset(0, (int)(-cursor_offset * Main.globalScale));
    		currentMenu.prev();
    	}
    }
    
    public void actionLeft() {
    	//cursor.offset((int)(-cursor_offset * Main.globalScale), 0);
    }
    
    public void actionRight() {
    	//cursor.offset((int)(cursor_offset * Main.globalScale), 0);
    }
    
    public void actionZ() {    	// Cancel Button
    	
    	for (MenuLayer i : menus) {
			if (i.getParentMenu() != null) {
    			if (i.getParentMenu().getMenuName().equals(currentMenu.getMenuName())) {
    				for (String j : currentMenu.childNames) {
	    				getGraphic(j).reverseAnimation();
	    				getGraphic(j).startAnimation();
    				}
    			}
			}
		}
    	
    	TAGraphic graphic = getGraphic("menu_top");
    	graphic.reloadAnimation();
    	graphic.reverseAnimation();
    	graphic.startAnimation();
    }
    
    public void actionX() {    	// Ok Button
    	if (currentMenu.getChildName() != null) {
    		TAGraphic gMenu = getGraphic(currentMenu.getMenuName());
    		gMenu.reloadAnimation();
    		transitionMenu(gMenu.getName());
    		appendSlide(gMenu, gMenu.getName() + "_slide");
    		gMenu.startAnimation();
			
    		//getGraphic(currentMenu.getMenuName()).animation.printAllInstructions();
    		for (MenuLayer i : menus) {
    			if (i.getParentMenu() != null) {
    				if (i.getParentMenu().getMenuName().equals(currentMenu.getMenuName())) {
    					getGraphic(i.getMenuName()).reloadAnimation();
	        			if (!i.getMenuName().equals(currentMenu.getSelected())) {
	    					getGraphic(i.getMenuName()).startAnimation();
	    				} else {
	    					gMenu = getGraphic(i.getMenuName());
	        				appendSlide(gMenu, "button_slide");
	        				getGraphic(i.getMenuName()).startAnimation();
	    				}
	    			}
    			}
    		}
    	} else {
    		System.out.println("No child menus = action :)");
    	}
    }
    public void actionS() {}
    public void actionA() {}
    public void actionP() {
    	// Unload graphics?
    	// Load Game()
    	
    	unloadScreen();
    	Main.newScreen("hi");
    }
    
    public void actionPlus() {
    	changeTimerSpeed(0.5);
    }
    
    public void actionMinus() {
    	changeTimerSpeed(2);
    }
}