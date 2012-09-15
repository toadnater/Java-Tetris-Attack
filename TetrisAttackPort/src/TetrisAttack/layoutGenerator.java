package TetrisAttack;

public class layoutGenerator {
	
	/*	CONSTANT DEFINITIONS
	 * 		GRIDWIDTH  - number of blocks needed to fill a row
	 * 		GRIDHEIGHT - number of blocks to fill a column (*not used)
	 * 		NUMBLCKBEGIN - number of blocks to generate at the start of a game
	 * 		MAXBEGINHEIGHT - The column height that all blocks at the start must be below
	 * 		BLOCKS - A string array containing all string representations of the different blocks
	 * 		NUMDIFFBLCKS -  The number of different types of blocks used. (*used to index BLOCKS array)
	 */

	private static int GRIDWIDTH = 6;
//	private static int GRIDHEIGHT = 15;
	private static int NUMBLCKBEGIN = 30;
	private static int MAXBEGINHEIGHT = 10;
	private static String[] BLOCKS = {"e","1","2","3","4","5","6"}; //String placeholder for the empty block must be at index 0
	private static int NUMDIFFBLCKS = 6;
	
	public layoutGenerator(){}
	
	public layoutGenerator(int[] constants) {
		NUMDIFFBLCKS = constants[0] + 1; 
		GRIDWIDTH = constants[2];
		//GRIDHEIGHT = constants[3];
		NUMBLCKBEGIN = constants[4];
		MAXBEGINHEIGHT = constants[5];
	}
	
	/*	Method: randomStartLayout
	 *  Function: Generate a single randomized start layout based on the above definition
	 *  
	 *  returns: a string representation of a tetris attack layout with both empty and 
	 *  		non empty blocks represented by the above defined strings
	 */
	public String randomStartLayout(){
		int blckToAdd = NUMBLCKBEGIN;
		String layout = "";
		int loopCnt = 0;
		while(blckToAdd > 0){
			loopCnt++;
			if(layout.length() + blckToAdd >= GRIDWIDTH * MAXBEGINHEIGHT){
				layout = "";
				blckToAdd = NUMBLCKBEGIN;
			}
			
			int nb;
			if(layout.length()+1 > 3*GRIDWIDTH){
				nb = (int)(Math.random() * NUMDIFFBLCKS);
				if(layout.substring(layout.length()-GRIDWIDTH, layout.length()+1-GRIDWIDTH).equals(BLOCKS[0])){
					nb=0;
				}
			} else {
				nb = (int)(Math.random()*(NUMDIFFBLCKS-1))+1;
			}
			
			if(chckValid(layout,nb)){
				if( nb != 0 ){
					blckToAdd--;
				}
				layout = layout + BLOCKS[nb];	
			}
			if(loopCnt > 60){
				System.out.println("\nProblem:");
				System.out.println(nb + " not added to:");
				print(layout);
			}
		}
		return layout;
	}
	
	/*  Method: chckValid(String layout,
	 *  Function: Checks whether adding a proposed block to a layout would make the layout invalid.
	 *  			an invalid layout is one where:
	 *  				-Three blocks of the same colour are next to each other (row or column)
	 *  				-A non-empty block is on top of a empty block
	 *  Definition of parameters:
	 *  	layout - A string representation of the already generated layout
	 *      nb - the int of the index of proposed block in the constant array BLOCKS
	 *  
	 *  Returns: a boolean indicating whether the proposed block will make the given layout
	 *  			invalid
	 */
	private boolean chckValid(String layout, int nb){
		if(nb != 0){
			if(layout.length()+1 % GRIDWIDTH > 2){
				if(layout.substring(layout.length()-2, layout.length()-1).equals(BLOCKS[nb]) && 
						layout.substring(layout.length()-1, layout.length()).equals(BLOCKS[nb])){
					
					return false;
				}
			}
			if(layout.length()+1 > GRIDWIDTH){
				if(layout.substring(layout.length()-GRIDWIDTH, layout.length()+1-GRIDWIDTH).equals(BLOCKS[0]) && nb != 0){
					nb = 0; 
					return true;
				}
				if(layout.length()+1 > 2*GRIDWIDTH){
					if(layout.substring(layout.length()-GRIDWIDTH, layout.length()-GRIDWIDTH+1).equals(BLOCKS[nb]) &&
							layout.substring(layout.length()-2*GRIDWIDTH, layout.length()-2*GRIDWIDTH+1).equals(BLOCKS[nb])){
						return false;
					}
				}
			}
		
		}
		return true;
	}
	
	public String addRow(String layout){
		String newRow = "";
		while(newRow.length() < GRIDWIDTH)
		{
			int nb = (int)(Math.random() * (NUMDIFFBLCKS-1)+1);
			if(chkRow(layout,BLOCKS[nb],newRow)){
				newRow = newRow + BLOCKS[nb];
			} else {
				System.out.println("layoutGenerator.addRow(): **Element: "+ BLOCKS[nb] + " Failed, Current Generated Row: " + newRow);
			}
		}
		return newRow;
	}
	
	public boolean chkRow(String layout, String nb, String newRow){
		int rowPos = newRow.length();
		if(rowPos >= 2){
			if(newRow.subSequence(rowPos-1, rowPos).equals(nb) && newRow.subSequence(rowPos-2,rowPos-1).equals(nb)){
				return false;
			}
		}
		if(layout.length() > GRIDWIDTH+rowPos){
			if(layout.subSequence(rowPos, rowPos+1).equals(nb) && layout.subSequence(GRIDWIDTH+rowPos, GRIDWIDTH+rowPos+1).equals(nb)){
				return false;
			}
		}
		return true;
	}
	
	public void print(String layout){
		int numLines = layout.length() / GRIDWIDTH;
		String[] lines = new String[numLines+1];
		for(int i = 0;i <= numLines-1;i++){
			lines[i] = layout.substring(i*GRIDWIDTH, (i+1)*GRIDWIDTH);
		}
		lines[numLines] = layout.substring((numLines)*GRIDWIDTH,layout.length());
		for(int i = numLines; i >= 0; i--){
			System.out.println(lines[i]);
		}
	}
	

}



