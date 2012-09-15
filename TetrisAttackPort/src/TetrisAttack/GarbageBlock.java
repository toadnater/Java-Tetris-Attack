
package TetrisAttack;

import java.util.Vector;

class GarbageBlock extends Block {
	
	public Vector<Vector<GarbageBlock>> associatedBlocks;	// 2 Dimensional block configuration
	public GarbageBlock parentBlock;
	public TAGraphic overlapGraphic;
	public int garbageWidth;
	public int garbageHeight;
	private boolean isOnGrid;
	private boolean isHit;
	private boolean isRevealing;
	
	public GarbageBlock(GarbageBlock parent) {
		super();
		this.overlapGraphic = null;
		this.isOnGrid = false;
		this.colour = parent.colour;
		this.setActive(true);
		parentBlock = parent;
		setGarbage(true);
	}
	
	public GarbageBlock(int[] info, String garbageColour) {	
		// ===  GIVEN PARAMETERS === 
		// info[] = { 	[0] gridX,
		//				[1] gridY,
		//				[2] graphicX,
		//				[3] graphicY,
		//				[4] width,
		//				[5] height 	};
		// =========================
		
		super();
		
		this.colour = garbageColour;
		this.associatedGraphic = decideGraphic();
		this.overlapGraphic = null;
		garbageWidth = info[4];
		garbageHeight = info[5];
		parentBlock = null;
		setGarbage(true);
		
		if (garbageWidth <= 0 || garbageHeight <= 0) {
			System.out.println("GarbageBlock(): w: " + garbageWidth + " h: " + garbageHeight + " ... Creating empty block");
			return;
		}
		
		associatedBlocks = new Vector<Vector<GarbageBlock>>(garbageHeight);
		
		for (int i = 0; i < garbageHeight; i++) {
			Vector<GarbageBlock> row = new Vector<GarbageBlock>(garbageWidth);
			for (int j = 0; j < garbageWidth; j++) {
				if (i == 0 && j == 0) {
					// Specific to this first block
					Game.screenHandle.addGraphic(this.associatedGraphic);
				} else {
					int xCoord;
					if (i == 0) {
						xCoord = this.grid_x + j;	// Offset for the very first row from the parent.
					} else {
						xCoord = j;
					}
					// Every other block we are about to add
					GarbageBlock cell = new GarbageBlock(this);
					cell.associatedGraphic = decideGraphic();
					cell.setGridLocation(xCoord, this.grid_y + i);
					Game.screenHandle.addGraphic(cell.associatedGraphic);
					row.add(cell);
				}		
			}
			associatedBlocks.add(row);
		}
		
		this.setGridLocation(info[0], info[1]);
		this.setGraphicLocation(info[2], info[3]);
		setActive(true);
	}
	
	// TAGraphic decideGraphic(char c, int x, int y, int width, int height) {
	public TAGraphic decideGraphic() {
		return new TAGraphic("garbagePlaceholder");
	}
	
	@Override
	public void setGraphicLocation(double x, double y) {
		graphic_x = (int)x; graphic_y = (int)y;
	
		if (parentBlock == null) {
			associatedGraphic.location(graphic_x, graphic_y);
			for (Vector<GarbageBlock> row : associatedBlocks) {
				for (GarbageBlock cell : row) {
					// You'll notice newCellY has a +1 on it. This fixes a weird offset that happens to the child blocks otherwise.
					int newCellX = this.graphic_x + this.associatedGraphic.getRelativeWidth() * (cell.grid_x - this.grid_x);
					int newCellY = this.graphic_y + 1 + this.associatedGraphic.getRelativeHeight() * (this.grid_y - cell.grid_y);
					cell.setGraphicLocation(newCellX, newCellY);
				}
			}
		} else {
			super.setGraphicLocation(graphic_x, graphic_y);
		}
	}
	
	@Override
	public void setGridLocation(int x, int y) {

		super.setGridLocation(x, y);
		
		if (parentBlock == null) {
			int countRow = 0;
			for (Vector<GarbageBlock> row : associatedBlocks) {
				int countCol;
				if (countRow == 0) {
					countCol = 1;	// Offset for the very first row from the parent.
				} else {
					countCol = 0;
				}
				for (GarbageBlock cell : row) {
					if (!cell.isOnGrid()) {
						int newCellX = this.grid_x + countCol;
						int newCellY = this.grid_y + countRow;
						System.out.println("newX: " + newCellX + " newY: " + newCellY);
						cell.setGridLocation(newCellX, newCellY);
						cell.setOnGrid(true);
					}
					countCol++;
				}
				countRow++;
			}
		}
	}
	
	@Override
	public void setFalling(boolean set) {
		if (!this.isGarbage()) {
			super.setFalling(set);
			return;
		}
		
		// -- Now assumes set = true.
		// Tell our parent block that this block is ready to fall		
		setFallRequest(set);
		// Order of operations is very important to understanding how we're going to make
		// this work. Because the grid checks blocks from bottom to top, left to right, we
		// know that the parentBlock will always be checked first. The parent block will always
		// refuse (reset) to fall the first time around, giving our Grid.applyGravity method a chance
		// to check every other block, and thus every block associated with this parent block.
		
		// When Grid.applyGravity() starts again after having gone through all the blocks, it
		// will reach the parent block again, but the values for the children will be different
		// based on what the grid wanted to originally do.
		
		// If ALL the child blocks are queued to fall, and the parent block as well, then we will
		// set Falling to be TRUE for all the blocks. This way, after the parent block is set to true,
		// the Grid will see isFalling() to be true and load the swap / falling animation. In turn,
		// every block the grid examines afterwards that is associated with this garbage block
		// will also be set isFalling() and then queue those animations. This will result in a garbage
		// block falling if all the blocks underneath it in its entirety are gone :).
		
		// Also, we only need to do this to the first row of the garbage block, because successive
		// rows will obviously say that the row under it is not empty and it will never want to fall.

		if (parentBlock == null) {
			for (GarbageBlock b : associatedBlocks.firstElement()) {
				// Are any of our garbage blocks requesting they fall?
				// System.out.println("==== X: " + b.grid_x + " Y: " + b.grid_y + "request: " + b.isFallRequested());
				if (!b.isFallRequested()) {
					setAllRequests(false);
					set = false;
					break;
				}
			}
			super.setFalling(set);
		} else {
			// System.out.println("GarbageBlock.setFalling() - is my parent falling? " + parentBlock.isFalling());
			if (parentBlock.isFalling()) {
				super.setFalling(true);
			} else {
				super.setFalling(false);
			}
		}
	}
	
	private void setAllRequests(boolean set) {
		this.setFallRequest(set);
		for (Vector<GarbageBlock> row : associatedBlocks) {
			for (GarbageBlock b : row) {
				b.setFallRequest(set);
			}
		}
	}
	
	// A fix to make sure we only place ourselves on the grid once.
	// This way we do not overwrite any settings that the Grid methods
	// decide for us (because it should know the layout of the grid and
	// not have our complicated overriding methods mess with it).
	private void setOnGrid(boolean set) { isOnGrid = set; }
	private boolean isOnGrid() { return isOnGrid; }
	
	// This is what happens when a combo is activated that is touching a
	// garbage block. If it is a child, it will trigger the parent if it has
	// not already been triggered. 
	// *** A block should set its isHit variable to false when the graphic for
	//     the garbage block is set to be visible, or something like that.
	public void garbageHit() {
		if (parentBlock == null) {
			if (!isHit) {
				setHit(true);
				System.out.println("Garbage block hit. Parent at: " + this.grid_x + " " + this.grid_y);
				this.setHitGraphic();
				this.setRevealing(true);
				for (Vector<GarbageBlock> row : associatedBlocks) {
					for (GarbageBlock b : row) {
						b.setHitGraphic();
						b.setRevealing(true);
					}
				}
				// Do block is hit stuff to every associated block and itself.
			}
		} else {
			if (!parentBlock.isHit) {
				parentBlock.garbageHit();
			}
		}
	}
	
	public GarbageBlock revealBlock() {
		if (parentBlock == null) {
			if (this.isRevealing()) {
				this.setPoofGraphic();
				return this;
			}
			// Check to see if there are any of our child blocks that still need revealing
			for (Vector<GarbageBlock> row : associatedBlocks) {
				for (GarbageBlock b : row) {
					if (b.isRevealing()) {
						b.setPoofGraphic();
						return b;
					}
				}
			}
		} else {
			return parentBlock.revealBlock();
		}
		return null;
	}
	
	private void setHitGraphic() {
		
		this.setActive(false);	// Prevents animating garbage blocks from falling with gravity.
		
		this.overlapGraphic = new TAGraphic("garbageHit", this.graphic_x, this.graphic_y);
		this.overlapGraphic.loadAnimation();
		for (int i = 0; i < this.overlapGraphic.animation.numberOfInstructions(); i++) {
			AnimationInstruction AI = new AnimationInstruction();
			AI = this.overlapGraphic.animation.getInstruction(i);
			AI.x = graphic_x;
			AI.y = graphic_y;
			this.overlapGraphic.animation.updateInstruction(i, AI);	
		}
		if (!Game.screenHandle.animatedGraphics.contains(this.overlapGraphic)) {
			Game.screenHandle.addGraphic(this.overlapGraphic);
			Game.screenHandle.setComponentZOrder(this.overlapGraphic, 1);
			Game.screenHandle.animatedGraphics.add(this.overlapGraphic);
		}
		this.overlapGraphic.startAnimation();
	}
	
	private void setPoofGraphic() {
		try {
			Game.screenHandle.remove(this.overlapGraphic);
		} catch (Exception e) {
			System.out.println("GarbageBlock.setPoofGraphic(): Attempted to remove a graphic that did not exist. Poof before garbage hit graphic?");
			return;
		}
		
		this.overlapGraphic.setVisible(false);
		this.overlapGraphic = new TAGraphic("disappearingBlock", this.graphic_x, this.graphic_y);
		int x = graphic_x - 14;
		int y = graphic_y - 11;
				
		TAGraphic g = this.overlapGraphic;
		g.loadAnimation();
		
		for (int i = 0; i < g.animation.numberOfInstructions(); i++) {
			AnimationInstruction ai = new AnimationInstruction();
			ai = g.animation.getInstruction(i);
			ai.x = x;
			ai.y = y;
			g.animation.updateInstruction(i, ai);
		}
		if (!Game.screenHandle.animatedGraphics.contains(this.overlapGraphic)) {
			Game.screenHandle.addGraphic(this.overlapGraphic);
			Game.screenHandle.setComponentZOrder(this.overlapGraphic, 1);
			Game.screenHandle.animatedGraphics.add(this.overlapGraphic);
		}
		
		g.startAnimation();
		
	}
	
	public void setHit(boolean set) { 
		isHit = set; 
		if (parentBlock == null) {
			for (Vector<GarbageBlock> row : associatedBlocks) {
				for (GarbageBlock b : row) {
					b.setHit(set);
				}
			}
		} else {
			if (parentBlock.isHit() != set) {
				parentBlock.setHit(set);
			}
		}
	}
	
	// This function will take the parent garbageBlock, turn the bottom row into a new random row
	// using layoutGenerator, then determine the new garbage block's layout, IF a new garbage block
	// will exist, and it must assign a new block to be the parent of this garbage block (while
	// removing the blocks that are no longer associated with it from the old one).
	// -- An easier approach would be to just make the old garbage go away, create the new blocks and
	//    a brand new garbage block in its place, but that would be too easy - and wasteful on resources.
	// -- playerGrid is basically a "I'm telling you I need new blocks determined here" with a pass back
	//    with "Hey grid, here's the new block arrangement you asked for".
	public void determineNewBlocks() {
		if (parentBlock != null) { return; }
		
		layoutGenerator makeBlocks = new layoutGenerator();
		String randomRow = makeBlocks.addRow("");
		int counter = 0;
		
		System.out.println("GarbageBlock.determineNewBlocks() - occurring");
		
		this.colour = convertCharToColour(randomRow.charAt(counter++));
		Game.screenHandle.remove(this.associatedGraphic);
		this.associatedGraphic = new TAGraphic(colour);
		this.associatedGraphic.nextImage();
		this.setGraphicLocation(graphic_x, graphic_y);
		Game.screenHandle.add(this.associatedGraphic);
		Game.screenHandle.setComponentZOrder(this.associatedGraphic, Game.screenHandle.getComponentZOrder(this.overlapGraphic) + 1);
		
		for (GarbageBlock b : associatedBlocks.firstElement()) {
				b.colour = convertCharToColour(randomRow.charAt(counter++));
				Game.screenHandle.remove(b.associatedGraphic);
				b.associatedGraphic = new TAGraphic(b.colour);
				b.associatedGraphic.nextImage();
				Game.screenHandle.add(b.associatedGraphic);
				Game.screenHandle.setComponentZOrder(b.associatedGraphic, Game.screenHandle.getComponentZOrder(b.overlapGraphic) + 1);
				b.setGraphicLocation(b.graphic_x, b.graphic_y);
		}		
		
		if (associatedBlocks.size() > 1) {
			for (Vector<GarbageBlock> row : associatedBlocks) {
				if (row != associatedBlocks.firstElement()) {
					for (GarbageBlock b : row) {
						Game.screenHandle.remove(b.associatedGraphic);
						b.associatedGraphic = decideGraphic();
						Game.screenHandle.add(b.associatedGraphic);
						Game.screenHandle.setComponentZOrder(b.associatedGraphic, Game.screenHandle.getComponentZOrder(b.overlapGraphic) + 1);
						b.setGraphicLocation(b.graphic_x, b.graphic_y);
					}
				}
			}
		}
	}
	
	public void renegotiateParent() { 
		if (associatedBlocks.size() > 1) {
			// Second row, first block
			GarbageBlock newParent = associatedBlocks.get(1).get(0);
			newParent.parentBlock = null;
			newParent.associatedBlocks = new Vector<Vector<GarbageBlock>>();
			// The new parent will not actually be associated with the blocks under it.
			for (Vector<GarbageBlock> row : associatedBlocks) {
				if (row != associatedBlocks.firstElement()) {
					Vector<GarbageBlock> tempRow = new Vector<GarbageBlock>();
					for (GarbageBlock b : row) {
						if (b != newParent) {
							b.setActive(true);
							tempRow.add(b);
						}
					}
					newParent.associatedBlocks.add(tempRow);
				}
			}
			
			for (Vector<GarbageBlock> row : newParent.associatedBlocks) {
				for (GarbageBlock b : row) {
					b.parentBlock = newParent;
				}
			}
			
			newParent.setHit(false);
			newParent.setActive(true);
			newParent.setFalling(false);
			//newParent.printBlocks();
		}
	}
	
	public void setRevealing(boolean set) { isRevealing = set; }
	public boolean isHit() { return isHit; }
	public boolean isRevealing() { return isRevealing; }
	
	public void printBlocks() {
		System.out.println("GarbageBlock  : THIS---- " + " x=" + grid_x + " y=" + grid_y + " colour=" + colour + " parent=" + this.toString() +
				" isGarbage=" + isGarbage() + " isFalling=" + isFalling() + " isHit=" + isHit() + " isActive=" + isActive() + " isRevealing=" + 
				isRevealing() + " isVisible=" + associatedGraphic.isVisible() + " requestingFall=" + isFallRequested());
		for (Vector<GarbageBlock> row : associatedBlocks) {
			for (GarbageBlock g : row) {
				System.out.println("GarbageBlock.printBlocks() : x=" + g.grid_x + " y=" + g.grid_y + " colour=" + g.colour + " parent=" + g.parentBlock.toString() + 
						" isGarbage=" + g.isGarbage() + " isFalling=" + g.isFalling() + " isHit=" + g.isHit() + " isActive=" + g.isActive() + " isRevealing=" + 
						g.isRevealing()+ " isVisible=" + g.associatedGraphic.isVisible() + "requestedFalling=" + g.isFallRequested());
			}
		}
	}
}