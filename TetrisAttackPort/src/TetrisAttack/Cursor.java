package TetrisAttack;

public class Cursor {
	TAGraphic image;
	private double offset = 16;
	private int posX = 0;				// Cursor parameters that mean more to us
	private int posY = 0;				// than their (X,Y) coordinates in pixels.		

	public Cursor() {
		image = new TAGraphic("game_cursor");
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}
	
	public void setPosX(int posX) {
		this.posX = posX;
	}	
	
	public void setPosY(int posY) {	
		this.posY = posY; 
	}

	public double getOffset() {	return offset;	}
	public int getPosX() { return posX; }
	public int getPosY() { return posY;	}

}
