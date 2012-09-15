
package TetrisAttack;

import java.util.Vector;

public abstract class AIController {
	
	public enum AIInstruction {	MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN, IDLE, PUSH, SWAP }
	private Vector<AIInstruction> instructions;
	private Vector<Vector<Block>> homeGrid;		// Our grid
	private Vector<Vector<Block>> awayGrid;		// Opponent's grid
	private int cursor_x, cursor_y;
	
	public AIController() {
		instructions = new Vector<AIInstruction>();
	}
	
	public abstract void calculateNextInstruction();
	public abstract void logicalError();
	
	public final void updateHomeGrid(Vector<Vector<Block>> grid) { homeGrid = grid; }
	public final void updateAwayGrid(Vector<Vector<Block>> grid) { awayGrid = grid; }
	
	public final boolean hasGarbage() { return hasGarbage(homeGrid); }
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
	
	public final int getHeight() { return getHeight(homeGrid); }
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
	
	public final int getBlockCount() { return getBlockCount(homeGrid); }
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
	
	public final int getSpecialBlockCount() { return getSpecialBlockCount(homeGrid); }
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
	
	public final void updateCursor(int x, int y) { cursor_x = x; cursor_y = y; }
	public final int getCursorX() { return cursor_x; }
	public final int getCursorY() { return cursor_y; }
	
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
	
	public final AIInstruction getNextInstruction() {
		if (instructions.size() > 0) {
			return instructions.remove(0);
		}
		return AIInstruction.IDLE;
	}
	
	public final void resetInstructions() {
		if (instructions.size() > 0) {
			instructions.removeAllElements();
		}
	}
	
	public final int addInstruction(AIInstruction i) {
		AIInstruction tempI = i;
		instructions.add(i);
		return instructions.indexOf(tempI);
	}
	
	public final boolean removeInstruction(int index) {
		try {
			instructions.removeElementAt(index);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/*
	 * Removes first instance of the instruction given.
	 * Returns true if instruction is removed, false otherwise.
	 */
	public final boolean removeInstruction(AIInstruction i) {
		return instructions.removeElement(i);
	}
}