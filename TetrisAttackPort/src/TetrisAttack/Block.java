
package TetrisAttack;

import java.util.Vector;

public class Block {
	
	public TAGraphic associatedGraphic;
	public String colour;
	public int grid_x; public int grid_y; 
	public int graphic_x; public int graphic_y;
	private boolean active;
	private boolean falling;
	private boolean garbage;
	private boolean fallRequest;
	private Block comboOrigin;
				
	public Block() {
		active = false;
		falling = false;
		garbage = false;
		comboOrigin = null;
		fallRequest = false;	// Used in GarbageBlocks only.
	}
		
	public Block(char c) {
		this();
		
		// We need to decide what type of block we are going to create!
		// ------------------------------------------------------------
		colour = convertCharToColour(c);
		
		// Regular block:
		//   Create our block graphic
		//   - Even none existent (empty) blocks are created so that when
		//     we need to animated blocks switching with empty spaces,
		//     a graphic object at least exists to use for reference.
		associatedGraphic = new TAGraphic(colour);
		
		//   - Add our graphic to the image list
		Game.screenHandle.addGraphic(associatedGraphic);
	}
	
	public void setGridLocation(int x, int y) {
		// Store our block's location and have the graphic reflect it.
		grid_x = x; grid_y = y;
		//System.out.println("Game.setGridLocation(): " + x + " " + y);
	}
				
	public void setGraphicLocation(double x, double y) {
		graphic_x = (int)x; graphic_y = (int)y;
		associatedGraphic.location(graphic_x, graphic_y);
		//System.out.println(associatedGraphic.getName() + "1: [" + graphic_x + "][" + graphic_y + "]");
		//System.out.println(associatedGraphic.getName() + "2: [" + associatedGraphic.getX() + "][" + associatedGraphic.getY() + "]");
	}
	
	public void setGraphicLocationFuture(double x, double y) {
		graphic_x = (int)x; graphic_y = (int)y;
	}
	
	public int getGraphicX() { return graphic_x; }
	public int getGraphicY() { return graphic_y; }
	
	public void deactivateBlock() { 
		active = false;
	}
	
	public void activateBlock() {
		if (!active) {
			active = true;
			associatedGraphic.nextImage();
		}
	}
	
	public void setGarbage(boolean set) { garbage = set; }
	public void setActive(boolean set) { active = set; }
	public void setFalling(boolean set) { falling = set; }
	public void setFallRequest(boolean set) { fallRequest = set; }
	public void setColour(String set) { colour = set; }
	public void setVisible(boolean set) { associatedGraphic.setVisible(set); }
	public void setComboOrigin(Block set) { comboOrigin = set; }
	public boolean isFalling() { return falling; }
	public boolean isActive() { return active; }
	public boolean isGarbage() { return garbage; }
	public boolean isFallRequested() { return fallRequest; }
	public Block getComboOrigin() { return comboOrigin; }
	
	public String convertCharToColour(char c) {
		if (c == '1') {
			return "redHeart";
		} else if (c == '2') {
			return "greenSquare";
		} else if (c == '3') {
			return "yellowStar";
		} else if (c == '4') {
			return "purpleDiamond";
		} else if (c == '5') {
			return "tealTriangle";
		} else if (c == '6') {
			return "blueTriangle";
		} else if (c == 'e') {
			return "emptyBlock";
		} else if (c == 'd') {
			return "disappearingBlock";
		} else if (c == 's') {
			return "greySteel";
		} else {
			return "";
		}
	}
}