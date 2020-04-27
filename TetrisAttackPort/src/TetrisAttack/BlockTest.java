package TetrisAttack;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BlockTest {
	Block testBlock;	
	TAGraphic testTAGraphic;
	Game gameTest;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {	
	
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		gameTest = new Game();
		testBlock = new Block();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetGraphicLocation() {
		gameTest = new Game();
		testBlock = new Block('1', gameTest);
		int originalX = testBlock.getGraphicX();	
		int originalY = testBlock.getGraphicY();
		
		testBlock.setGraphicLocation(originalX + 1, originalY + 1);
		
		assertTrue(originalX != testBlock.getGraphicX());
		assertTrue(originalY != testBlock.getGraphicY());
	}
	
	@Test
	public void testSetGraphicLocationFuture() {
		//Don't know how to test now...
		assertTrue(false);
	}
	
	@Test
	public void testGetGraphicX() {
		
		try {
		gameTest = new Game();
		testBlock = new Block('1', gameTest);
		int newTestLocation = 10;
		testBlock.setGraphicLocation(newTestLocation, 0 );
		assertTrue(testBlock.getGraphicX() == newTestLocation);
		}
		 catch (AssertionError error) {
	            // Output expected AssertionErrors.
			 System.out.println(error);
	        } catch (Exception exception) {
	            // Output unexpected Exceptions.
	        	 System.out.println(exception);
	        }
	}
	
	@Test
	public void testGetGraphicY() {
		gameTest = new Game();
		testBlock = new Block('1', gameTest);
		int newTestLocation = 10;
		testBlock.setGraphicLocation(0, newTestLocation);
		
		assertTrue(testBlock.getGraphicY() == newTestLocation);
	}
	
	@Test
	public void testGetGraphicLocation() {
		gameTest = new Game();
		testBlock = new Block('1', gameTest);
		int originalX = testBlock.getGraphicX();	
		int originalY = testBlock.getGraphicY();
		
		testBlock.setGraphicLocation(originalX + 1, originalY + 1);
		
		assertTrue(originalX != testBlock.getGraphicX());
		assertTrue(originalY != testBlock.getGraphicY());
	}
	
	@Test
	public void testDeactivateBlock() {
		testBlock.setActive(true);
		testBlock.deactivateBlock();
		assertFalse(testBlock.isActive());
	}
	
	@Test
	public void testActivateBlock() {
		//Don't know how to test now...
		assertTrue(false);
	}
	
	@Test
	public void testSetGarbage() {
		testBlock.setGarbage(false);
		assertFalse(testBlock.isGarbage());
		testBlock.setGarbage(true);
		assertTrue(testBlock.isGarbage());
	}
	
	@Test
	public void testSetActivate() {
		testBlock.setActive(false);
		assertFalse(testBlock.isActive());
		testBlock.setActive(true);
		assertTrue(testBlock.isActive());
	}
	
	@Test
	public void testSetFalling() {
		testBlock.setFalling(false);
		assertFalse(testBlock.isFalling());
		testBlock.setFalling(true);
		assertTrue(testBlock.isFalling());
	}
	
	@Test
	public void testSetFallRequest() {
		testBlock.setFallRequest(false);
		assertFalse(testBlock.isFallRequested());
		testBlock.setFallRequest(true);
		assertTrue(testBlock.isFalling());
	}
	
	@Test
	public void testSetColour() {
		testBlock = new Block('1'); // should be "redHeart"
		assertTrue(testBlock.colour.equals("redHeart"));
		testBlock.setColour("tealTriangle");
		assertTrue(testBlock.colour.equals("tealTriangle"));
	}
	
	@Test
	public void testSetVisible() {
		//Don't know how to test now...
		assertTrue(false);
	}

	@Test
	public void testSetComboOrigin() {
		//Don't know how to test now...
		assertTrue(false);
	}
	
	@Test
	public void testConvertCharToColour() {
		
		Map<Character,String> colorsToMatch = new HashMap<Character,String>(); 
		colorsToMatch.put('1',"redHeart");       
		colorsToMatch.put('2',"greenSquare");     
		colorsToMatch.put('3',"yellowStar");
		colorsToMatch.put('4',"purpleDiamond");
		colorsToMatch.put('5',"tealTriangle");
		colorsToMatch.put('6',"blueTriangle"); 
		colorsToMatch.put('e',"emptyBlock");      
		colorsToMatch.put('d',"disappearingBlock"); 
		colorsToMatch.put('s',"greySteel");
		colorsToMatch.put('x',"");
		
		for(Map.Entry<Character,String> entry : colorsToMatch.entrySet()) {
			testBlock = new Block(entry.getKey(), gameTest);
			assertTrue(testBlock.colour.equals(entry.getValue()));
		}	
	}

}
