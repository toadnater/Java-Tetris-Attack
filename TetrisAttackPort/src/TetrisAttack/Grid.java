
package TetrisAttack;

import java.util.Random;
import java.util.Vector;

public class Grid {
		
	private			int		BLOCK_VARIETY = 5;		// Different block colour count (based on difficulty)
	private 		int 	BLOCK_WIDTH = 16;
	private	final 	int 	GRID_WIDTH;				// Because 0 is actually the first line.
	private	final 	int 	GRID_HEIGHT; 			// Note: 11 Lines active at any given time.
	private final 	int 	STARTING_BLOCK_COUNT;
	private final	int 	WARNING_HEIGHT;			// When warning music would play or STOP would be triggered.
	private 		int 	STOP_TIME = 60 * 1;		// The time the grid would traditionally stop before automatically pushing up the grid again.
	private 		int 	GRACE_TIME = 60 * 3;	// The grace time when the blocks reach the top of the grid.
													// Measured in clock_cycles (60fps * seconds)
	
	//-----------------------------------------------------------------------------------------------
	
	// Semaphor used to see if there is a garbageBlock already being animated before we start animating another one.
	public static GarbageBlock GarbageSemaphor = null;

	private Vector<String> GRID_STATUS;
	private layoutGenerator rowGenerator;
	private TAGraphic gridPanel;
	private TAGraphic enemyPanel;
	private Game.Cursor associatedCursor;
	private Vector<Block> newRow;
	private Vector<Vector<Block>> gridLayout;
	private String defaultLayout;
	private int graceTimer;
	private	int specialFrequency;
	private int linesGenerated = 0;
	//private int stopTimer;		// Currently not implemented.
	
	public Grid(TAGraphic myPanel, TAGraphic yourPanel, Game.Cursor c, int[] constants, String startingLayout) {
		super();
				
		// Parse our grid constants
		BLOCK_VARIETY = constants[0];
		BLOCK_WIDTH = constants[1];
		GRID_WIDTH = constants[2];
		GRID_HEIGHT = constants[3];
		STARTING_BLOCK_COUNT = constants[4];
		WARNING_HEIGHT = constants[5];
		
		defaultLayout = startingLayout;
		gridLayout = new Vector<Vector<Block>>();
		gridPanel = myPanel;
		enemyPanel = yourPanel;
		associatedCursor = c;
		newRow = new Vector<Block>();
		rowGenerator = new layoutGenerator(constants);
		specialFrequency = 0;
		
		GRID_STATUS = new Vector<String>();
	}
	
	public boolean hasGridStatus(String status) { return GRID_STATUS.contains(status); }
	public boolean hasGridStatus() { return GRID_STATUS.isEmpty(); }
	
	public Vector<Vector<Block>> returnGrid() { return gridLayout; }
	
	public Vector<Block> createRandomRow() {
		// Returns a Vector of randomly generated blocks
		
		Vector<Block> tempRow = new Vector<Block>();
		double panelBottom = gridPanel.getRelativeY() + gridPanel.getRelativeHeight();
		double panelLeft = gridPanel.getRelativeX();
		String newRow = rowGenerator.addRow("", specialFrequency);
		
		for (int i = 0; i < GRID_WIDTH; i++) {
			Block newBlock = new Block(newRow.charAt(i));
			newBlock.setGridLocation(i, -1);
	
			double newX = panelLeft + (i * BLOCK_WIDTH);
			double newY = panelBottom;
			newBlock.setGraphicLocation(newX, newY);
			tempRow.add(newBlock);
		}
		
		return tempRow;
	}
	
	public Vector<Block> createEmptyRow(int y) {
		// Returns a vector of only Empty Blocks, with
		// grid Y values of the int value given.
		
		Vector<Block> tempRow = new Vector<Block>();
		double panelBottom = gridPanel.getRelativeY() + gridPanel.getRelativeHeight();
		double panelLeft = gridPanel.getRelativeX();
		
		for (int i = 0; i < GRID_WIDTH; i++) {
			Block newBlock = new Block('e');
			newBlock.setGridLocation(i, y);
	
			double newX = panelLeft + (i * BLOCK_WIDTH);
			double newY = panelBottom - (y * BLOCK_WIDTH);
			newBlock.setGraphicLocation(newX, newY);
			tempRow.add(newBlock);
		}
		
		return tempRow;
	}
	
	public void createGrid() {
		// Fill our entire grid with values.
		// If the layout given does not represent the entire grid, the
		// remaining spaces are filled with empty blocks.
		
		int stringIndex = 0;
		for (int i = 0; i < GRID_HEIGHT; i++) {
			Vector<Block> row = new Vector<Block>();
			for (int j = 0; j < GRID_WIDTH; j++) {
				Block newBlock;
				if (stringIndex < defaultLayout.length()) {
					newBlock = new Block(defaultLayout.charAt(stringIndex++));
					newBlock.setGridLocation(j, i);
					row.add(newBlock);
				} else {
					newBlock = new Block('e');
					newBlock.setGridLocation(j, i);
					row.add(newBlock);
				}
			}
			gridLayout.add(row);
		}
		
		newRow = createRandomRow();
		
	}
	
	public void layoutGrid() {
		
		double panelBottom = gridPanel.getRelativeY() + gridPanel.getRelativeHeight();
		double panelLeft = gridPanel.getRelativeX();
		
		for (int i = 0; i < GRID_HEIGHT; i++) {
			for (int j = 0; j < GRID_WIDTH; j++) {
				double newX = panelLeft + (j * BLOCK_WIDTH);
				double newY = panelBottom - ((i + 1) * BLOCK_WIDTH);
				gridLayout.get(i).get(j).setGraphicLocation(newX, newY);
				gridLayout.get(i).get(j).activateBlock();
			}
		}
	}
	
	// Debugging purposes
	public void printGrid() {
		for (Vector<Block> a : gridLayout) {
			String line = "";
			for (Block b : a) {
				line += (" " + b.colour);
			}
			System.out.println("Line: " + line);
		}
	}
			
	public String getBlockColour(int x, int y) {
		return gridLayout.get(y).get(x).colour;
	}
	
	public void setBlockInactive(int x, int y) {
		gridLayout.get(y).get(x).deactivateBlock();
	}
	
	// Horizontal Swap for User action
	public void swapBlocks(int x, int y) {
		
		// Store the old values of Left and Right blocks
		Block tempBlockL = new Block();
		Block tempBlockR = new Block();
		tempBlockL = gridLayout.get(y).get(x);
		tempBlockR = gridLayout.get(y).get(x + 1);			
		
		if (tempBlockL.isGarbage() || tempBlockR.isGarbage()) {
			return;
		}
		
		if (!tempBlockL.isActive() || !tempBlockR.isActive()) {
			return;
		}
		
		if (tempBlockL.isFalling() || tempBlockR.isFalling()) {
			return;
		}
		
		// If we haven't found a reason not to do so, animate the swap with the graphics!
		animateSwap(tempBlockL, tempBlockR, "square_slide");
	}
	
	public void animateSwap(Block leftBlock, Block rightBlock, String blockAnimation) {
		
		// Get each of the block's graphics
		TAGraphic leftGraphic = new TAGraphic();
		TAGraphic rightGraphic = new TAGraphic();
		leftGraphic = leftBlock.associatedGraphic;
		rightGraphic = rightBlock.associatedGraphic;
		
		// Load our default sliding animation into each graphic			
		leftGraphic.reloadAnimation(blockAnimation);
		rightGraphic.reloadAnimation(blockAnimation);
		
		// Now we need to overwrite the values in each of the animations
		// Load the first instructions (of 2 pre-determined by "square_slide")
		AnimationInstruction leftAI = new AnimationInstruction();
		AnimationInstruction rightAI = new AnimationInstruction();
		leftAI = leftGraphic.animation.getInstruction(0);
		rightAI = rightGraphic.animation.getInstruction(0);
		
		// Overwrite the X and Y values to that of each current graphic,
		// because those are their starting points in our slides!
		leftAI.x = leftBlock.getGraphicX(); leftAI.y = leftBlock.getGraphicY();
		rightAI.x = rightBlock.getGraphicX(); rightAI.y = rightBlock.getGraphicY();
		
		// Make sure the width + height are preserved. 
		//(in case of resize this will be different)
		// - Image indices and alpha values are good at default.
		leftAI.width = leftGraphic.getWidth(); leftAI.height = leftGraphic.getHeight();
		rightAI.width = rightGraphic.getWidth(); rightAI.height = rightGraphic.getHeight();
				
		// Update the old instructions with the new ones
		leftGraphic.animation.updateInstruction(0, leftAI);
		rightGraphic.animation.updateInstruction(0, rightAI);
		
		// ----------------------------------------------------------
		// At this point, we need to do the same thing with the second instruction,
		// We load up the second instructions (because they contain our pre-determined
		// end frame) and configure that to have the destination parameters.
		// ----------------------------------------------------------
		
		leftAI = leftGraphic.animation.getInstruction(1);
		rightAI = rightGraphic.animation.getInstruction(1);
		leftAI.width = leftGraphic.getWidth(); leftAI.height = leftGraphic.getHeight();
		rightAI.width = rightGraphic.getWidth(); rightAI.height = rightGraphic.getHeight();
		
		leftAI.x = rightBlock.getGraphicX(); leftAI.y = rightBlock.getGraphicY();
		rightAI.x = leftBlock.getGraphicX(); rightAI.y = leftBlock.getGraphicY();
	
		leftGraphic.animation.updateInstruction(1, leftAI);
		rightGraphic.animation.updateInstruction(1, rightAI);
		
		// Apply graphic position changes for future animations
		leftBlock.setGraphicLocationFuture(leftAI.x, leftAI.y);
		rightBlock.setGraphicLocationFuture(rightAI.x, rightAI.y);
		
		// At this point, we need to make sure the zDepths of the items are in the
		// right order, so that the animation looks consistently as if its the
		// Left block going overtop of the Right block.
		// 4 & 5 are arbitrary numbers picked. (Because the cursors needs to be the
		// foremost graphics displayed, of course)
		
		// ======= ======================================================================			
		// ======= Current issue: onResize(), items are no longer a part of the same parent container?
		/*
		System.out.println("leftGraphic.getParent(): " + leftGraphic.getParent());
		System.out.println("rightGraphic.getParent(): " + rightGraphic.getParent());
		
		setComponentZOrder(leftGraphic, getComponentCount() - 5);
		setComponentZOrder(rightGraphic, getComponentCount() - 4);
		*/
		// ======= ======================================================================
		
		// Create smooth transitions with our handy animation function!
		leftGraphic.animation.CreateSlide(0, 1);
		rightGraphic.animation.CreateSlide(0, 1);
	
		// Now we need to add these animations to our queuing list and play them!
		// Make sure that the graphics don't get added to the list more than once
		// or else the animation will linearly get faster since it happens n times
		// per cycle (where n is the number of times it is added).
		
		if (!Game.screenHandle.animatedGraphics.contains(leftGraphic)) {
			Game.screenHandle.animatedGraphics.add(leftGraphic);
		}
		if (!Game.screenHandle.animatedGraphics.contains(rightGraphic)) {
			Game.screenHandle.animatedGraphics.add(rightGraphic);
		}
		
		leftGraphic.startAnimation();
		rightGraphic.startAnimation();
		
		finalizeSwap(leftBlock, rightBlock);
		
	}
	
	private void finalizeSwap(Block B1, Block B2) {
		
		int X1 = B1.grid_x; int Y1 = B1.grid_y;
		int X2 = B2.grid_x; int Y2 = B2.grid_y;
		
		// Set each block's grid locations to new settings
		gridLayout.get(Y1).get(X1).setGridLocation(X2, Y2);
		gridLayout.get(Y2).get(X2).setGridLocation(X1, Y1);
		
		// Swap them in our copy of the grid.
		gridLayout.get(Y1).set(X1, B2);
		gridLayout.get(Y2).set(X2, B1);
		
	}
	
	public void applyGravity() {
		// Basically, this function is going to see if there are any empty blocks
		// in the grid. If there are, then we check the block above it to see if
		// that is empty.
		//		If it is empty: We do nothing :)
		//		If it is NOT empty: We drag the block above it down, and set the old
		//		one in its place to being empty. This way, we can check that block
		//		space on clock turn later and keep a continuous stream of blocks
		//		falling, instead of having to wait for the first block to swap,
		//		and so on.
		// Block must not be in animation for this to happen. 
		// animation.currentState holds this information for us :)
		
		// The grid can exceed GRID_HEIGHT for various reasons (garbage blocks),
		// so we're checking each row in the grid so that nothing is left up top.
		
		for (Vector<Block> row : gridLayout) {
			if (gridLayout.lastElement() == row) { break; }			// Because if its the very top of the stack there should be noting falling from above :P
			for (Block b : row) {
				if (b.colour.equals("emptyBlock")) {
					int aboveY = b.grid_y + 1;
					int thisX = b.grid_x;
					if (!getBlockColour(thisX, aboveY).equals("emptyBlock")) {
						Block blockAbove = gridLayout.get(aboveY).get(thisX);
						if (!blockAbove.associatedGraphic.animation.isPlaying() && blockAbove.isActive()) {
							blockAbove.setFalling(true);
							if (blockAbove.isFalling()) {
								animateSwap(b, blockAbove, "square_drop");
							}
						}
					}
				}
			}
		}
		
	}
	
	public void checkCombos() {
		// This will use code similar to our applyGravity() method.
		// Instead of checking for empty blocks above though, we're
		// going to check for blocks of the same kind above and if there
		// are at least 3 blocks in a row, we need to do stuff!
		
		Vector<Block> allComboBlocks = new Vector<Block>();
		int comboCount = 0;
		
		for (Vector<Block> row : gridLayout) {
			//if (gridLayout.lastElement() == row) { break; }
			for (Block baseBlock : row) {
				if (!(baseBlock.colour.equals("emptyBlock")) && !(baseBlock.isGarbage())) {	
					int baseY = baseBlock.grid_y;
					int baseX = baseBlock.grid_x;
					String baseColour = baseBlock.colour;
					
					// For each block that exists above our Base, let's
					// see if there is another one above it that is like it.
					// When we reach a block that is not, we break the loop
					// and figure out how many we encountered before breaking.
					
					Vector<Block> horizComboBlocks = new Vector<Block>();
					Vector<Block> vertComboBlocks = new Vector<Block>();
					
					// Add the first block because we assume it is valid, and this is
					// confirmed later on by the addition of future blocks.
					horizComboBlocks.add(baseBlock);
					vertComboBlocks.add(baseBlock);
					
					// Checking for vertical line combos
					
					int vertComboCount = 1;
					boolean comboFromFall = false;
					for (int i = baseY + 1; i < GRID_HEIGHT; i++) {
						if (getBlockColour(baseX, i).equals(baseColour)) {
							// If this block isn't already in some sort of animation
							// (like another combo), then we can use it.
							if (gridLayout.get(i).get(baseX).isActive() &&
								!gridLayout.get(i).get(baseX).isFalling() ) {
								if (gridLayout.get(i).get(baseX).associatedGraphic.animation.isPlaying()) {
									comboFromFall = true;
								}
								vertComboBlocks.add(gridLayout.get(i).get(baseX));
								vertComboCount++;
							}
						} else {
							break;
						}
					}
					
					// Checking for horizontal line combos

					int horizComboCount = 1;
					for (int j = baseX + 1; j < GRID_WIDTH; j++) {
						if (getBlockColour(j, baseY).equals(baseColour)) {
							if (gridLayout.get(baseY).get(j).isActive() &&
								!gridLayout.get(baseY).get(j).isFalling() ) {
								boolean columnHasEmpty = false;
								for (int h = baseY; h > 0; h--) {
									if (getBlockColour(j, h).equals("emptyBlock")) {
										columnHasEmpty = true;
										break;
									}
								}
								if (!columnHasEmpty) {
									horizComboBlocks.add(gridLayout.get(baseY).get(j));
									horizComboCount++;
								}
							}
						} else {
							break;
						}
					}
					
					// If we have either a vertical OR horizontal combo, we should animate it.
					// - This is really all being done for one special case: L shape combo (5)
											
					if (vertComboCount >= 3) {
						for (Block b : vertComboBlocks) {
							if (!allComboBlocks.contains(b)) {
								allComboBlocks.add(b);
								comboCount++;
							}
						}
					}
					
					if (horizComboCount >= 3) {
						for (Block b : horizComboBlocks) {
							if (!allComboBlocks.contains(b)) {
								allComboBlocks.add(b);
								comboCount++;
							}
						}
					}
					
					if (comboFromFall) { System.out.println("Combo from a fall?"); }
				}
			}
		}
		
		if (!allComboBlocks.isEmpty()) {
			if (comboCount > 3) { 
				animateCombo(allComboBlocks, Integer.toString(comboCount)); 
				//do the attack :)
			}
			checkForSpecial(allComboBlocks);
			//if (comboCount => 3 && GRID_STATUS.equals("FREEZE")
			GRID_STATUS.add("FREEZE");
			if (isTouchingGarbage(allComboBlocks)) { GRID_STATUS.add("GARBAGE_CLEAR"); }
			// If there is a combo with more than 4 blocks in it, then we declare it as a uniform combo!
			
			animateFlicker(allComboBlocks);								
			// animate them here so that they become "PLAYING" and are not calculated
			// in upcoming checks.
		}
	}
	
	// Overload for checking garbage blocks using the parent
	private void isTouchingGarbage(GarbageBlock g) {
		if (g.parentBlock == null) {
			GarbageBlock nextBlock = null;
			for (GarbageBlock b : g.associatedBlocks.firstElement()) {
				nextBlock = isTouchingSurroundings(b);				
				if (nextBlock != null) {
					nextBlock.garbageHit();
					isTouchingGarbage(nextBlock);
				}
			}
			for (GarbageBlock b : g.associatedBlocks.lastElement()) {
				nextBlock = isTouchingSurroundings(b);
				if (nextBlock != null) {
					nextBlock.garbageHit();
					isTouchingGarbage(nextBlock);
				}
			}
		} else {
				// Because we want to sift through the entire garbage block
				// we need the parent garbage block.
				isTouchingGarbage(g.parentBlock);
				return;
		}
		g.determineNewBlocks();
	}
	
	private boolean isTouchingGarbage(Vector<Block> comboBlocks) {
		GarbageBlock gBlock = null;
		for (Block b : comboBlocks) {
			int x = b.grid_x;
			int y = b.grid_y;
			if (x > 0) {
				if (gridLayout.get(y).get(x - 1).isGarbage()) {
					gBlock = (GarbageBlock)gridLayout.get(y).get(x - 1);
				}
			}
			if (x < GRID_WIDTH - 1) {
				if (gridLayout.get(y).get(x + 1).isGarbage()) {
					gBlock = (GarbageBlock)gridLayout.get(y).get(x + 1);
				}
			}
			if (y > 0) {
				if (gridLayout.get(y - 1).get(x).isGarbage()) {
					gBlock = (GarbageBlock)gridLayout.get(y - 1).get(x);
				}
			}
			if (y < gridLayout.size()) {
				if (gridLayout.get(y + 1).get(x).isGarbage()) {
					gBlock = (GarbageBlock)gridLayout.get(y + 1).get(x);
				}
			}
			
			if (gBlock != null) {
				gBlock.garbageHit();
				isTouchingGarbage(gBlock);
				return true;
			}
		}	
		return false;
	}
	
	private GarbageBlock isTouchingSurroundings(GarbageBlock b) {
		
		GarbageBlock nextBlock = null;
		Block tempBlock;
		int x = b.grid_x;
		int y = b.grid_y;
		if (x > 0) {
			tempBlock = gridLayout.get(y).get(x - 1);
			if (tempBlock.isGarbage()) {
				nextBlock = (GarbageBlock)tempBlock;
				if (nextBlock.parentBlock == b.parentBlock || nextBlock.isHit() || !nextBlock.colour.equals(b.colour)) {
					nextBlock = null;
				}
			}
		}
		if (x < GRID_WIDTH - 1) {
			tempBlock = gridLayout.get(y).get(x + 1);
			if (tempBlock.isGarbage()) {
				nextBlock = (GarbageBlock)tempBlock;
				if (nextBlock.parentBlock == b.parentBlock || nextBlock.isHit() || !nextBlock.colour.equals(b.colour)) {
					nextBlock = null;
				}
			}
		}
		if (y > 0) {
			tempBlock = gridLayout.get(y - 1).get(x);
			if (tempBlock.isGarbage()) {
				nextBlock = (GarbageBlock)tempBlock;
				if (nextBlock.parentBlock == b.parentBlock || nextBlock.isHit() || !nextBlock.colour.equals(b.colour)) {
					nextBlock = null;
				}
			}
		}
		if (y < gridLayout.size()) {
			tempBlock = gridLayout.get(y + 1).get(x);
			if (tempBlock.isGarbage()) {
				nextBlock = (GarbageBlock)tempBlock;
				if (nextBlock.parentBlock == b.parentBlock || nextBlock.isHit() || !nextBlock.colour.equals(b.colour)) {
					nextBlock = null;
				}
			}
		}
		return nextBlock;
	}
	
	private void checkForSpecial(Vector<Block> comboBlocks) {
		int numberOfSpecials = 0;
		for (Block b : comboBlocks) {
			if (b.colour.equals("greySteel")) {
				if (numberOfSpecials++ == 0) {
					animateAttack(b.associatedGraphic);
				}
			}
		}
	}
	
	private void animateFlicker(Vector<Block> comboBlocks) {
		
		for (Block b : comboBlocks) {			
			TAGraphic bGraphic = b.associatedGraphic;
			b.deactivateBlock();
			//System.out.println("Game.animateFlicker(): " + b.colour + " " + b.grid_x + " " + b.grid_y);
			bGraphic.reloadAnimation("square_flashing");
			
			// Because the preloaded instructions don't have any of the
			// current graphic information in them, we have to load it all.
			AnimationHandle bAnimation = bGraphic.animation;	
			
			for (int i = 0; i < bAnimation.numberOfInstructions(); i++) {
				AnimationInstruction tempAI = new AnimationInstruction();
				tempAI = bAnimation.getInstruction(i);					
				
				tempAI.x = b.getGraphicX();
				tempAI.y = b.getGraphicY();
				tempAI.width = bGraphic.getWidth();
				tempAI.height = bGraphic.getHeight();
				
				bAnimation.updateInstruction(i, tempAI);
			}
			
			if (!Game.screenHandle.animatedGraphics.contains(bGraphic)) {
				Game.screenHandle.animatedGraphics.add(bGraphic);
			}
			
			bGraphic.startAnimation();
		}
	}
	
	// Animates the spiraling shape (little eggs) animation, the combo box appearing / disappearing
	// and then the resulting complete shape (big egg) flying to the opposing player's panel.
	// - All the blocks are passed to us because the combo box goes in the place of the top-most block
	//   which we must still determine.
	private void animateCombo(Vector<Block> comboBlocks, String comboType) {
		
		// Find top-most block
		int y = this.gridPanel.getRelativeHeight(); 
		int x = 0;
		for (Block b : comboBlocks) {
			if (y > b.graphic_y) {
				y = b.graphic_y;
				x = b.graphic_x;
			}
		}

		TAGraphic comboGraphic = new TAGraphic("combo" + comboType);
		animateCombobox(x, y, comboGraphic);
		animateSpiral(x, y, comboGraphic);
		animateAttack(comboGraphic);
	}
	
	public void animateCombobox(int x, int y, TAGraphic comboGraphic) {
		
		// Now that we have the top-most block, let's create a graphic there that has
		// the spiraling animation and another with the combo box.
		comboGraphic.forceAnimated();	// Override - See TAGraphic.forceAnimated()
		comboGraphic.reloadAnimation("comboBox");
		
		// Because the preloaded instructions don't have any of the
		// current graphic information in them, we have to load it all.
		// ** However, we know the number of animation instructions beforehand :)
		AnimationHandle cAnimation = new AnimationHandle();
		cAnimation = comboGraphic.animation;
		
		AnimationInstruction tempAI = new AnimationInstruction();
		tempAI = cAnimation.getInstruction(0);					
		tempAI.x = x;
		tempAI.y = y;
		cAnimation.updateInstruction(0, tempAI);
		
		tempAI = new AnimationInstruction();
		tempAI = cAnimation.getInstruction(1);
		tempAI.y = y - BLOCK_WIDTH;
		tempAI.x = x;
		cAnimation.updateInstruction(1, tempAI);
		
		tempAI = new AnimationInstruction();
		tempAI = cAnimation.getInstruction(2);
		tempAI.y = y - BLOCK_WIDTH;
		tempAI.x = x;
		cAnimation.updateInstruction(2, tempAI);
		
		cAnimation.CreateSlide(0, 1);
		
		if (!Game.screenHandle.animatedGraphics.contains(comboGraphic)) {
			Game.screenHandle.addGraphic(comboGraphic);
			Game.screenHandle.setComponentZOrder(comboGraphic, 1);
			Game.screenHandle.animatedGraphics.add(comboGraphic);
		}
		
		comboGraphic.startAnimation();
	}
	
	// comboGraphic is passed so we know how to make this animation in relation
	// to the comboBox graphic.
	private void animateSpiral(int x, int y, TAGraphic comboGraphic) {
		
		// Now we need to build the animation for the spinning eggs (yoshi specific)
		TAGraphic eggComboGraphic = new TAGraphic("eggCombo");
		x = x - (eggComboGraphic.getRelativeWidth() / 2) + (BLOCK_WIDTH / 2);
		y = y - BLOCK_WIDTH - (eggComboGraphic.getRelativeHeight() / 2) + 4; // 4 is correctional.
		eggComboGraphic.reloadAnimation("eggCombo");
		
		AnimationHandle cAnimation = new AnimationHandle();
		cAnimation = eggComboGraphic.animation;
		
		for (int i = 0; i < cAnimation.numberOfInstructions(); i++) {
			AnimationInstruction spiralAI = new AnimationInstruction();
			spiralAI = cAnimation.getInstruction(i);
			
			spiralAI.x = x;
			spiralAI.y = y;
			cAnimation.updateInstruction(i, spiralAI);
			// We need to increase the values of our animation based on where the
			// comboBox is (because this spiral goes around it), which is also
			// dynamically created, so we will measure this change dynamically.
			// ** Relies on number of instructions being equal to or greater than eggCombo.			
			int frame = spiralAI.frameNumber;
			
			int changeInX = comboGraphic.animation.getInstruction(frame).x - comboGraphic.animation.getInstruction(frame + 1).x;
			int changeInY = comboGraphic.animation.getInstruction(frame).y - comboGraphic.animation.getInstruction(frame + 1).y;
			y = y + changeInY;
			x = x + changeInX;
		}
		
		//System.out.println("Grid.animateCombo() - Animation Spiral: "); cAnimation.printAllInstructions();
		
		if (!Game.screenHandle.animatedGraphics.contains(eggComboGraphic)) {
			Game.screenHandle.addGraphic(eggComboGraphic);
			Game.screenHandle.setComponentZOrder(eggComboGraphic, 2);
			Game.screenHandle.animatedGraphics.add(eggComboGraphic);
		}
		
		eggComboGraphic.startAnimation();
		//animateAttack goes here I believe?
		
	}
	
	// comboGraphic is passed so we know how to make this animation in relation
	// to the comboBox graphic.
	public void animateAttack(TAGraphic comboGraphic) {
		
		// TAGraphic enemyPanel is defined as the background panel that houses the enemy
		// player's grid. That is, enemyPanel's X, Y is the top left corner of the grid,
		// and WIDTH + HEIGHT is the bottom and right sides of the panel that holds the
		// blocks.
				
		TAGraphic attackEgg = new TAGraphic("attackEgg");
		attackEgg.forceAnimated();
		
		// Code to manipulate each of the instructions should go here, and then
		// update all the instructions.
		
		if (!Game.screenHandle.animatedGraphics.contains(attackEgg)) {
			Game.screenHandle.addGraphic(attackEgg);
			Game.screenHandle.setComponentZOrder(attackEgg, 2);
			Game.screenHandle.animatedGraphics.add(attackEgg);
		}
		
		attackEgg.startAnimation();
	}
	
	public void disappearBlocks() {
		// Take a block that has an invisible graphic and replace
		// it with an Empty Block, so then gravity will work!
		
		for (int i = 0; i < GRID_HEIGHT; i++) {
			for (int j = 0; j < GRID_WIDTH; j++) {
				Block b = gridLayout.get(i).get(j);
				
				if (b.isGarbage()) {
					GarbageBlock g = (GarbageBlock)b;
					// Our garbage semaphor makes sure that only one garbage disappearing animation
					// occurs at a time. I don't know if this was an intended gameplay mechanic, but
					// this is how it works in the game so we'll be using it here too.
										
					if (g.isHit() && !g.overlapGraphic.animation.isPlaying() && GarbageSemaphor == null) {
						GarbageSemaphor = g.revealBlock();
						if (GarbageSemaphor == null) {
							// All the blocks for a given garbage block are done revealing :)
							finishedGarbage(g);
							GRID_STATUS.remove("GARBAGE_CLEAR");
						}
						//System.out.println((g.associatedGraphic.isVisible));
					} else if (GarbageSemaphor != null) {
						if (!GarbageSemaphor.overlapGraphic.animation.isPlaying()) {
							GarbageSemaphor.setRevealing(false);
							GarbageSemaphor = null;
						}
					}
				}
				
				if (!b.associatedGraphic.isVisible()) {	// ANIMATION 2 = POOF
					if (b.colour.equals("disappearingBlock")) {
						// reverting back from offset
						int newX = b.getGraphicX() + 14;
						int newY = b.getGraphicY() + 11;
						
						Block swapToEmpty = new Block('e');
						swapToEmpty.setGridLocation(j, i);
						gridLayout.get(i).set(j, swapToEmpty);
						gridLayout.get(i).get(j).setGraphicLocation(newX, newY);
						gridLayout.get(i).get(j).setActive(true);
						// Because clearing garbage takes longer than regular clears, so we will allow
						// those to determine when we should start running again.
						GRID_STATUS.remove("FREEZE");
					} else {						// ANIMATION 1 = FLICKER
						// offset because animation is not 16 x 16 like blocks
						int newX = b.getGraphicX() - 14;
						int newY = b.getGraphicY() - 11;
						
						Block swapToDisappear = new Block('d');
						swapToDisappear.setGridLocation(j, i);
						swapToDisappear.associatedGraphic.loadAnimation();
						
						// Update animation for offset graphic x and y.
						for (int index = 0; index < swapToDisappear.associatedGraphic.animation.numberOfInstructions(); index++) {
							AnimationInstruction ai = new AnimationInstruction();
							ai = swapToDisappear.associatedGraphic.animation.getInstruction(index);
							ai.x = newX;
							ai.y = newY;
							swapToDisappear.associatedGraphic.animation.updateInstruction(index, ai);
						}
						
						gridLayout.get(i).set(j, swapToDisappear);
						gridLayout.get(i).get(j).setGraphicLocation(newX, newY);
						if (!Game.screenHandle.animatedGraphics.contains(swapToDisappear.associatedGraphic)) {
							Game.screenHandle.setComponentZOrder(swapToDisappear.associatedGraphic, 1);
							Game.screenHandle.animatedGraphics.add(swapToDisappear.associatedGraphic);
						}
						gridLayout.get(i).get(j).associatedGraphic.startAnimation();
					}
				} 
			}
		}
	}
	
	public void pushGrid(double increment) {			
		// This method moves the grid upwards. If there is no row underneath
		// our grid, then we will make up a row to go underneath it. The row
		// will be added to the grid once it becomes active and at that time
		// we will also push all of the grid up by one unit.
		
		// Range: 0.00 - 1.00 (percent %)
		// Increment is essentially the speed or rate at which we push up all
		// the graphics for this new row and the grid.
		
		if (hasGridStatus("GARBAGE_CLEAR") || hasGridStatus("FREEZE")) {
			return;
		}
		
		int index = 0;
		int moves = (int)(BLOCK_WIDTH * increment);
		boolean push = true;
		
		while (push && index < moves) {
			for (Vector<Block> row : gridLayout) {
				for (Block b : row) {
					b.setGraphicLocation(b.getGraphicX(), b.getGraphicY() - 1);	// See if this matches the global ratio?
				}
			}
			for (Block b : newRow) {
				b.setGraphicLocation(b.getGraphicX(), b.getGraphicY() - 1);
				int graphicEnd = b.associatedGraphic.getY() + b.associatedGraphic.getHeight();
				int panelEnd = gridPanel.getY() + gridPanel.getHeight();
				if (graphicEnd <= panelEnd) {
					push = false;
				}
			}
			associatedCursor.image.offset(0, -1);
			index++;
			
			// Is this where we should see if the game 
			// should end if the grid gets too high?
		}
		
		if (!push) {
			advanceGrid();
		}
		
		graceTimer = 0;
		
	}
	
	private void advanceGrid() {
		for (Block b : newRow) {
			b.activateBlock();
		}
		gridLayout.insertElementAt(newRow, 0);
		if (linesGenerated++ > 15) {
			specialFrequency = 14;
		} else if (linesGenerated > 10) {
			specialFrequency = 21;
		} else if (linesGenerated > 5) {
			specialFrequency = 28;
		}
		//this.printGrid();
		offsetGrid();
		Game.screenHandle.cursorOffset(associatedCursor, 0,1);
		newRow = createRandomRow();
	}
	
	// Advances grid upward by 1 unit
	private void offsetGrid() {
		for (Vector<Block> row : gridLayout) {
			for (Block b : row) {
				b.setGridLocation(b.grid_x, b.grid_y + 1);
			}
		}
	}
	
	public void checkFalling() {
		for (Vector<Block> row : gridLayout) {
			for (Block b : row) {
				// If our block is registered as having started falling but the animation
				// for the drop is no longer playing, let's see if there is a block underneath
				// still and if there is not, then the block is no longer falling :)
				//System.out.println("Colour " + b.colour);
				//if (b.isGarbage()) {
				//	System.out.println("Grid.checkFalling() - is our animation playing? " + b.associatedGraphic.animation.isPlaying());
				//}
				
				if (!b.associatedGraphic.animation.isPlaying()) {
					int currentX = b.grid_x;
					int belowY = b.grid_y - 1;
					if (belowY >= 0) {
						if (gridLayout.get(belowY).get(currentX).colour.equals("emptyBlock") || 
							gridLayout.get(belowY).get(currentX).colour.equals("disappearingBlock")) {
						} else {
							b.setFalling(false);
						}
					} else {
						b.setFalling(false);
					}
				}
			}
		}
	}
	
	// Should have two parameters: Width and Height of garbage block to create.
	public void createGarbage() {
		
		int g_gridX = 0;	 // Or a random number between 0 and (grid_width - garbage_width).
		int g_gridY = gridLayout.size() - 1;
		int g_graphicX = gridPanel.getRelativeX() + (BLOCK_WIDTH * g_gridX);
		int g_graphicY = gridPanel.getRelativeY() + gridPanel.getRelativeHeight() -
						(BLOCK_WIDTH * g_gridY);
		int g_width = 6;	// Or number based on combo
		int g_height = 3;	// Or number based on chain
		
		int[] garbageInfo = { 	g_gridX,
								g_gridY,
								g_graphicX,
								g_graphicY,
								g_width,
								g_height };	
		String garbageColour = "garbageBlue";
		
		/*
		if (gridLayout.size() > 14) {
			garbageColour = "garbageSpecial";
		}*/
		
		// Add height - 1 rows to our grid for the garbageBlock insertion.
		for (int i = g_gridY; i < g_gridY + g_height; i++) {
			gridLayout.add(createEmptyRow(i));
		}
		
		GarbageBlock garbage = new GarbageBlock(garbageInfo, garbageColour);
		setGarbageOnGrid(garbage);		
	}
		
	// Sets all the spaces a garbage block takes up on the grid.
	private void setGarbageOnGrid(GarbageBlock gBlock) {
		gridLayout.get(gBlock.grid_y).set(gBlock.grid_x, gBlock);
		for (Vector<GarbageBlock> row : gBlock.associatedBlocks) {
			for (GarbageBlock b : row) {
				//System.out.println("Size: " + gridLayout.size());
				//System.out.println("Grid Y: " + b.grid_y + " X " + b.grid_x);
				gridLayout.get(b.grid_y).set(b.grid_x, b);
			}
		}
	}	
	
	private void finishedGarbage(GarbageBlock g) {
		// We only want to deal with the parent block.		
		if (g.parentBlock != null) { return; }
		
		for (GarbageBlock b : g.associatedBlocks.get(0)) {
			System.out.println("Block: " + b.colour + " " + b.grid_x + " " + b.grid_y);
			b.setGarbage(false);
			b.setActive(true);
			b.setHit(false);
		}
		
		g.setGarbage(false);
		g.setActive(true);
		g.setHit(false);
		
		g.renegotiateParent();
		
		g.associatedBlocks.removeAllElements();
	}
	
	private int trimGrid() {
		int row = 0;
		int filledRow = gridLayout.size();
		while(row < gridLayout.size()) {
			// For each row above the screen, let's see if it's an empty row.
			boolean rowIsEmpty = true;
			for (Block b : gridLayout.get(row)) {
				if (!b.colour.equals("emptyBlock")) {
					rowIsEmpty = false;
					break;
				}
			}
			if (rowIsEmpty) {
				//if (row > GRID_HEIGHT) {
					/*System.out.println("Grid.trimGrid() - Trimming");
					gridLayout.removeElementAt(row);
					for (Vector<Block> r : gridLayout) {
						for (Block b : r) {
							b.setGridLocation(b.grid_x, b.grid_y - 1);
						}
					}
					printGrid();*/
				//} else {
					row++;
					filledRow--;
				//}
			} else {
				row++;
			}
		}
		return filledRow;
	}
	
	// Also handles any warning systems concerning the player's grid.
	public boolean checkGameOver() {
		int filledHeight = trimGrid();
		
		if (filledHeight > GRID_HEIGHT - 1) {
			if (!hasGridStatus("STOP")) {
				GRID_STATUS.add("STOP");
				graceTimer = GRACE_TIME;
			} else {
				graceTimer--;
			}
		} else {
			if (hasGridStatus("STOP")) {
				GRID_STATUS.remove("STOP");
			}
		}
		
		if (graceTimer <= 0 && hasGridStatus("STOP")) {
			// Grace timer reached 0, therefore you lost.
			for (Vector<Block> row : gridLayout) {
				for (Block b : row) {
					if (!b.isGarbage() && !b.colour.equals("emptyBlock")) {
						b.associatedGraphic.setImage(3);
					}
				}
			}
			return true;
		}
		
		/*
		if (filledHeight >= WARNING_HEIGHT) {
			for (Block b : gridLayout.get(WARNING_HEIGHT)) {
				if (!b.isGarbage() && !b.colour.equals("emptyBlock")) {
					for (int i = Math.min(filledHeight - 1, GRID_HEIGHT - 1); i >= 0; i--) {
						Block getBlock = gridLayout.get(i).get(b.grid_x);
						if (!getBlock.associatedGraphic.animation.isPlaying()) {
							if (!getBlock.associatedGraphic.animation.hasInstructions()) {
								getBlock.associatedGraphic.reloadAnimation("square_jumping");
							}
							if (!Game.screenHandle.animatedGraphics.contains(getBlock.associatedGraphic)) {
								//Game.screenHandle.addGraphic(b.associatedGraphic);
								//Game.screenHandle.setComponentZOrder(b.associatedGraphic, 1);
								Game.screenHandle.animatedGraphics.add(getBlock.associatedGraphic);
							}
							System.out.println("Starting animation");
							getBlock.associatedGraphic.startAnimation();
						}
					}
				}
			}
		} */
		return false;
	}
}