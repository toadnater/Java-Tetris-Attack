
package TetrisAttack;

import java.util.Vector;

/**
 * 
 * @author Toadnater
 * Huzzah!
 */
public abstract class AIController {
	
	
	public enum AIInstruction {	MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN, IDLE, PUSH, SWAP }
	private Vector<AIInstruction> instructions;
	private Vector<Vector<Block>> homeGrid;		// Our grid
	private Vector<Vector<Block>> awayGrid;		// Opponent's grid
	private int cursor_x, cursor_y;
	
	
	/**
	 * 
	 */
	public AIController() {
		instructions = new Vector<AIInstruction>();
	}
	
	/**
	 *  Abstract method to calculate next instruction.
	 *  <p>
	 *  This method is used to calculate the next instruction. This instruction is called 60 
	 *  times a second.
	 */
	public abstract void calculateNextInstruction();
	
	/**
	 *  Abstract method called when the instruction cannot be completed due to a logical error.
	 */
	public abstract void logicalError();
	
	/**
	 * 
	 * Updates the home grid that the AIController bases instructions from.
	 * <p>
	 * This method is called when the home grid is updated, either through a new row at the 
	 * bottom, a clear, or if the opposing player sends garbage over.
	 * 
	 * @param grid	The home grid.
	 */
	public final void updateHomeGrid(Vector<Vector<Block>> grid) { homeGrid = grid; }
	
	/**
	 * Updates the away grid that the AIController bases instructions from.
	 * <p>
	 * This method is called when the away grid is updated, either through a new row at the 
	 * bottom, a clear, or if the current player sends garbage over.
	 * 
	 * @param grid	The away grid.
	 */
	public final void updateAwayGrid(Vector<Vector<Block>> grid) { awayGrid = grid; }
	
	/**
	 * Checks if the home grid has any garbage blocks.
	 * 
	 * @return		true if there are garbage blocks in the grid.
	 */
	public final boolean hasGarbage() { return hasGarbage(homeGrid); }
	
	/**
	 * Checks if a grid has any garbage blocks.
	 * <p>
	 * Checks if there are any garbage blocks in the argument grid.
	 * 
	 * @param grid	The grid to check for garbage.
	 * @return
	 */
	public final boolean hasGarbage(Vector<Vector<Block>> grid) {
		for (Vector<Block> row : grid) {
			for (Block b : row) {
				if (b.isGarbage()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns the number of rows of blocks there are in the home grid. This method counts from
	 * the bottom up to the current height.
	 * 
	 * @return 		The height of the home grid.
	 */
	public final int getHeight() { return getHeight(homeGrid); }
	
	/**
	 * Returns the number of rows of blocks there are in the argument grid. This method counts
	 * from the bottom up to the current height.
	 * 
	 * @param grid	The grid which you are checking the height of.
	 * @return		The height of the argument grid.
	 */
	public final int getHeight(Vector<Vector<Block>> grid) {
		int height = 0;
		for (Vector<Block> row : grid) {
			for (Block b : row) {
				if (!b.isGarbage() && !b.colour.equals("emptyBlock")) {
					break;
				}
			}
			height++;
		}
		return height;
	}
	
	/**
	 * Counts all the blocks in the home grid.
	 * 
	 * @return		The number of blocks in the home grid.
	 */
	public final int getBlockCount() { return getBlockCount(homeGrid); }
	
	/**
	 * Counts all the blocks in the argument grid.
	 * <p>
	 * This method counts all the blocks in the argument grid. This does not take into account
	 * block position or block type.
	 * 
	 * @param grid	The grid which you are getting the number of blocks of.
	 * @return		The number of blocks in the grid.
	 */
	public final int getBlockCount(Vector<Vector<Block>> grid) {
		int count = 0;
		for (Vector<Block> row : grid) {
			for (Block b : row) {
				if (!b.isGarbage() && !b.colour.equals("emptyBlock")) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * Counts the number of special blocks in the home grid.
	 * 
	 * @return		The number of special blocks in the home grid.
	 */
	public final int getSpecialBlockCount() { return getSpecialBlockCount(homeGrid); }
	
	/**
	 * Counts the number of special blocks in the argument grid.
	 * <p>
	 * This method counts the number of special blocks in the argument grid. This helps in
	 * determining how to get rid of special garbage.
	 * 
	 * @param grid	The grid which you are getting the number of special blocks of.
	 * @return		The number of special blocks in the grid.
	 */
	public final int getSpecialBlockCount(Vector<Vector<Block>> grid) {
		int count = 0;
		for (Vector<Block> row : grid) {
			for (Block b : row) {
				if (b.colour.equals("greySteel")) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * This method should not be called by the AI script.
	 * 
	 * @param x
	 * @param y
	 */
	public final void updateCursor(int x, int y) { cursor_x = x; cursor_y = y; }
	
	/**
	 * Get the location of the X coordinate of the cursor.
	 * <p>
	 * This gets the X coordinates for the leftmost square of the cursor. 
	 * 
	 * @return		The X coordinate of the leftmost square of the cursor.
	 */
	public final int getCursorX() { return cursor_x; }
	
	/**
	 * Get the location of the Y coordinate of the cursor.
	 * <p>
	 * This gets the X coordinates for the leftmost square of the cursor. 
	 * 
	 * @return		The Y coordinate of the leftmost square of the cursor.
	 */
	public final int getCursorY() { return cursor_y; }
	
	/**
	 * Move the cursor to the specified location.
	 * <p>
	 * This moves the cursor to the specified location. This is done by putting the relevant
	 * move instructions on the stack. Note that the move is not instantaneous, if the grid is
	 * updated before all the move instructions are complete, then the stack is cleared and the
	 * next move must be recalculated.
	 * 
	 * @param x		The X coordinate to move the cursor to
	 * @param y		The Y coordinate to move the cursor to
	 */
	
	//TODO create the instance where there is a change to the grid before this method is resolved
	public final void moveCursorTo(int x, int y) {
		int changeInX = cursor_x - x;
		int changeInY = cursor_y - y;
		// Horizontal Movement
		if (changeInX < 0) {
			// Move right
			for (int i = 0; i < Math.abs(changeInX); i++) {
				instructions.add(AIInstruction.MOVE_RIGHT);
			}
		} else if (changeInX > 0) {
			// Move left
			for (int i = 0; i < Math.abs(changeInX); i++) {
				instructions.add(AIInstruction.MOVE_LEFT);
			}
		}
		if (changeInY < 0) {
			// Move up
			for (int i = 0; i < Math.abs(changeInY); i++) {
				instructions.add(AIInstruction.MOVE_UP);	
			}
		} else if (changeInY > 0) {
			// Move down
			for (int i = 0; i > Math.abs(changeInY); i++) {
				instructions.add(AIInstruction.MOVE_DOWN);
			}
		}
	}
	
	/**
	 * Grab the next instruction to go on the stack.
	 *
	 * @return		The next instruction to go on the stack.
	 */
	public final AIInstruction getNextInstruction() {
		if (instructions.size() > 0) {
			return instructions.remove(0);
		}
		return AIInstruction.IDLE;
	}
	
	/**
	 * Clear all instructions on the stack.
	 * <p>
	 * This method is used by the game to reset the stack. This will clear all instructions that
	 * have not yet been executed.
	 */
	public final void resetAllInstructions() {
		if (instructions.size() > 0) {
			instructions.removeAllElements();
		}
	}
	
	/**
	 * Add an instruction on the stack.
	 * <p>
	 * This method is used by the AI script to add an instruction on the stack. Note that
	 * instructions added on the stack might not be resolved.
	 * 
	 * @param i		The instruction to be added.
	 * @return		The position on the stack where the instruction is.
	 */
	public final boolean addInstruction(AIInstruction i) {
		return instructions.add(i);
	}
}
