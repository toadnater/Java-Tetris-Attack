
package TetrisAttack;

import java.util.Vector;

class MenuLayer {
	
	Vector<String> childNames;
	Vector<MenuLayer> childMenus;
	MenuLayer parentLayer;
	String layerName;
	private int position;
	
	public MenuLayer(String name) {
		parentLayer = null;
		layerName = name;
		int position = 0;
		
		childNames = new Vector<String>();
		childMenus = new Vector<MenuLayer>();
	}
	
	public MenuLayer(MenuLayer before, String name) {
		this(name);
		parentLayer = before;
	}
	
	public void addChildMenu(String name, MenuLayer child) {
		if (child != null) {
			childMenus.addElement(child);
		}
		childNames.addElement(name);
	}

	public void setParentLayer(MenuLayer parent) {
		parentLayer = parent;
	}
	
	public boolean hasNext() {
		return (position + 1 < childNames.size());
	}
	
	public boolean hasPrevious() {
		return (position - 1 >= 0);
	}
	
	public MenuLayer getChildMenu() {
		//System.out.println("size: " + childMenus.size() + "position: " + position);
		if (childMenus.size() > 0 && position <= childMenus.size()) {
			return childMenus.elementAt(position);
		}
		return null;
	}
	
	public int numberOfChildren() {
		return childMenus.size();
	}
	
	public String getChildName() {
		if (childNames.size() > 0 && position <= childNames.size()) {
			return childNames.elementAt(position);
		}
		return null;
	}
	
	public String getMenuName() {
		return layerName;
	}
	
	public MenuLayer getParentMenu() {
		return parentLayer;
	}
	
	public int getPosition() {
		return position;
	}
	
	public String getSelected() {
		return childNames.get(position);
	}
	
	public void next() {
		if (hasNext()) {
			position++;
		}
	}
	
	public void prev() {
		if (hasPrevious()) {
			position--;
		}
	}
}