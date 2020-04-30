package TetrisAttack;

public class GridConstants {
	private			int		BLOCK_VARIETY = 5;		// Different block colour count (based on difficulty)
	private 		int 	BLOCK_WIDTH = 16;
	private final 	int 	GRID_WIDTH = 6;			// Because 0 is actually the first line.
	private final 	int 	GRID_HEIGHT = 12; 		// Note: 11 Lines active at any given time.
	private final 	int 	STARTING_BLOCK_COUNT = 30;
	private final	int 	WARNING_HEIGHT = 10;
	
	GridConstants(){}
	
	GridConstants(int blockVarity, int blockWidth){
		setBLOCK_VARIETY(blockVarity);
		setBLOCK_WIDTH(blockWidth);
	}

	public int getBLOCK_VARIETY() {
		return BLOCK_VARIETY;
	}

	public void setBLOCK_VARIETY(int bLOCK_VARIETY) {
		BLOCK_VARIETY = bLOCK_VARIETY;
	}

	public int getBLOCK_WIDTH() {
		return BLOCK_WIDTH;
	}

	public void setBLOCK_WIDTH(int bLOCK_WIDTH) {
		BLOCK_WIDTH = bLOCK_WIDTH;
	}
	
	
	public int getGRID_WIDTH() { return GRID_WIDTH;	}
	public int getGRID_HEIGHT() { return GRID_HEIGHT;	}
	public int getSTARTING_BLOCK_COUNT() { return STARTING_BLOCK_COUNT;	}
	public int getWARNING_HEIGHT() { return WARNING_HEIGHT;	}
	
	public int[] getGridConstantsArray() { return new int[] { getBLOCK_VARIETY(),
			getBLOCK_WIDTH(),
			getGRID_WIDTH(),
			getGRID_HEIGHT(),
			getSTARTING_BLOCK_COUNT(),
			getWARNING_HEIGHT()};
			};
	

}