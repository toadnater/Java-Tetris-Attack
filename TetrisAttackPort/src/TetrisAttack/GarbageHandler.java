
package TetrisAttack;

public class GarbageHandler {
	
	private int STOP_TIME = 60 * 1;		// The time the grid would traditionally stop before automatically pushing up the grid again.
	
	private int garbageTimer;
	private String[] garbageType = {"", "", "", ""};
	private int[] garbageWidth = {0, 0, 0, 0};
	private int[] garbageHeight = {0, 0, 0, 0};
	private int garbageSlotIndex = 0;
	
	boolean dropGarbage = false;
	GarbageHandler opponentHandler = null;
	
	public GarbageHandler() {}
	
	public String getGarbageType(int index) { return garbageType[index]; }
	public int getGarbageWidth(int index) {	return garbageWidth[index]; }
	public int getGarbageHeight(int index) { return garbageHeight[index]; }
	public boolean garbageDropCheck(int index) { return !garbageType[index].isEmpty(); }
	
	// Tells us what other GarbageHandler we should be sending our garbage to.
	public void sendTo(GarbageHandler enemyGarbageHandler) {
		opponentHandler = enemyGarbageHandler;
	}
	
	// Used when you specifically know what you're sending over
	// USE CASE: Special Grey Steel Blocks
	public void sendGarbage(int width, int height, String type) {
		opponentHandler.receiveGarbage(width, height, type);
	}
	
	// Used when you don't know what you're sending over or how
	// USE CASE: Regular Blocks Incoming
	public void sendGarbage(int count, String type) {
		opponentHandler.receiveGarbage(count, type);
	}
	
	// Special garbage has been sent from the other grid!
	// We need to make sure we have somewhere to put this garbage.
	// -- Worst case scenario you could build a cue of incoming garbage?
	public boolean receiveGarbage(int width, int height, String type) {
		// Start the grace period between receiving garbage from an attack and it actually dropping.
		startTimer();
		for (int i = 0; i < 4; i++) {
			if (garbageType[i].isEmpty()) {
				garbageType[i] = type;
				garbageWidth[i] = width;
				garbageHeight[i] = height;
				return true;
			} else if (garbageType[i].equals(type) && garbageWidth[i] == 6 && width == 6) {
				garbageHeight[i] += height;
				return true;
			}
		}
		// if it doesn't find a home, for now it is simply discarded
		return false;
	}
	
	// Regular garbage has been sent from the other grid!
	// We must deal with it by figuring out which column it belongs to!
	public void receiveGarbage(int count, String type) {
		// Start the grace period between receiving garbage from an attack and it actually dropping.
		startTimer();
		for (int i = 0; i < 4; i++) {
			if (garbageType[i].equals(type)) {
				if (garbageWidth[i] >= 6) {
					while (count % 6 > 2) {
						if (!receiveGarbage(6, 1, type)) {
							System.out.println("GarbageHandler.receiveGarbage(): could not add 6w1h to garbage piles");
							return;
						}
						count -= 6;
					}
				}
			} else if (garbageType[i].isEmpty()) {
				while (count - 6 > 2) {
					if (!receiveGarbage(6, 1, type)) {
						System.out.println("GarbageHandler.receiveGarbage(): could not add 6w1h to garbage piles");
						return;
					}
					count -= 6;
				}
				if (count > 0) {
					if (count > 6) {
						if (!receiveGarbage(3, 1, type)) {
							System.out.println("GarbageHandler.receiveGarbage(): could not add 3w1h to garbage piles");
							return;
						}
						if (!receiveGarbage(count - 3, 1, type)) {
							System.out.println("GarbageHandler.receiveGarbage(): could not add " + count + "w1h to garbage piles");
							return;
						}
						count = 0;
					}
					else {
						if (!receiveGarbage(count, 1, type)) {
							System.out.println("GarbageHandler.receiveGarbage(): could not add " + count + "w1h to garbage piles");
							return;
						}
					}
				}
				return;
			}
		}
		
	}
	
	
	private void startTimer() {
		garbageTimer = STOP_TIME;
	}
	
	public boolean canDropGarbage() {
		if (garbageTimer-- < 0) {
			for (int i = 0; i < 4; i++) {
				if (!garbageType[i].isEmpty()) {
					System.out.println("GarbageHandler.canDropGarbage(): Dropping garbage");
					return true;
				}
			}
		}
		return false;
	}
	
	public void printGarbageSlots() {
		String output = "";
		for (int i = 0; i < 4; i++) {
			output += (garbageWidth[i] + " " + garbageHeight[i] + " // ");
		}
		System.out.println("GarbageHandler.printGarbageSlots(): " + output);
	}
	
	public void emptyGarbageColumn(int index) {
		garbageWidth[index] = 0;
		garbageHeight[index] = 0;
		garbageType[index] = "";
	}	
}