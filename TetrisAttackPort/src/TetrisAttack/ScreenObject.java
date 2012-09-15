
package TetrisAttack;

class ScreenObject {
	private String menu;
	private String name;
	private String text;
	private int x;
	private int y;
	private int origin_x;
	private int origin_y;
	private boolean tiles;
	private boolean isVisible;
	private boolean isBackground;
	private boolean isCursor;
	private boolean isAnimated;
	private boolean loopAnimation;
	
	// Create an empty ScreenObject() object.
	public ScreenObject() {
		name = "";
		text = "";
		x = 0;
		y = 0;
		origin_x = 0;
		origin_y = 0;
		tiles = false;
		isVisible = true;
		isBackground = false;
		isCursor = false;
		isAnimated = false;
		loopAnimation = false;
	}
	
	// Create a populated ScreenObject() object.
	public ScreenObject(String initName, String menuGroup, int initX, int initY) {
		this();
		setName(initName);
		setX(initX);
		setY(initY);
		origin_x = initX;
		origin_y = initY;
		setMenu(menuGroup);
	}
	
	// This could be altered to accommodate different attributes?
	public ScreenObject(String initName, String menuGroup, int initX, int initY, String attribute) {
		this(initName, menuGroup, initX, initY);
		if (attribute.contains("tiles")) { setTiles(true); }
		if (attribute.contains("background")) {	setBackground(true); }
		if (attribute.contains("cursor")) {	setCursor(true); }
		if (attribute.contains("visible=false")) { setVisible(false); }
		if (attribute.contains("animated")) { setAnimated(true); }
		if (attribute.contains("loop")) { setLooped(true); }
	}
	
	public void debugPrintAll() {
		System.out.println("ScreenObject.debugPrintAll(): " +
				"\n -- Name: " + name +
				"\n -- x: " + x +
				"\n -- y: " + y +
				"\n -- tiles: " + tiles + 
				"\n -- isVisible: " + isVisible +
				"\n -- isCursor: " + isCursor + 
				"\n -- isBackground: " + isBackground +
				"\n -- text: " + text
				);
	}
	
	public void setName(String set) { name = set; }
	public void setX(int set) { x = set; }	
	public void setY(int set) { y = set; }
	public void setTiles(boolean set) { tiles = set; }	
	public void setVisible(boolean set) { isVisible = set; }
	public void setBackground(boolean set) { isBackground = set; }
	public void setCursor(boolean set) { isCursor = set; }
	public void setAnimated(boolean set) { isAnimated = set; }
	public void setLooped(boolean set) { loopAnimation = set; }
	public void setText(String set) { text = set; }
	public void setMenu(String set) { menu = set; }
	public String getName() { return name; }
	public String getMenu() { return menu; }
	public int getX() { return x; }
	public int getY() { return y; }
	public int getOriginX() { return origin_x; }
	public int getOriginY() { return origin_y; }
	public boolean isAnimated() { return isAnimated; }
	public boolean getTiles() { return tiles; }
	public boolean isBackground() { return isBackground; }
	public boolean isCursor() { return isCursor; }
	public boolean isVisible() { return isVisible; }
	public boolean isLooped() { return loopAnimation; }
	public String getText() { return text; }
}