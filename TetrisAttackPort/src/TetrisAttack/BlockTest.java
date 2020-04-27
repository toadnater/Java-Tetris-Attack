package TetrisAttack;
import java.awt.Point;
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
	private int testGridLocationX = 1;
	private int testGridLocationY = 1;
	private int testGraphicLocationX = 2;
	private int testGraphicLocationY = 2;
	private Block newBlock;
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
	public void testSetGridLocation() {
		testBlock.setGridLocation(testGridLocationX, testGridLocationY );
		assertEquals(testBlock.grid_x, testGridLocationX);
		assertEquals(testBlock.grid_y, testGridLocationY);
	}

	@Test
	public void testSetGraphicLocation() {
		//testBlock = new Block(entry.getKey(), gameTest);
		//testBlock.setGraphicLocation(testGraphicLocationX, testGraphicLocationY );
		//Point tempPoint = testBlock.associatedGraphic.getLocation();
		//tempPoint = null;
		//assertEquals(testBlock.associatedGraphic.getLocation(), testGraphicLocationX);
		//assertEquals(testBlock.getGraphicY(), testGraphicLocationY);
		assertTrue(false);
	}
	
	@Test
	public void testGetAndSetGraphicLocationFuture() {
		testBlock.setGraphicLocationFuture(testGraphicLocationX, testGraphicLocationY );
		assertEquals(testBlock.getGraphicX(), testGraphicLocationX);
		assertEquals(testBlock.getGraphicY(), testGraphicLocationY);
	}
	
	@Test
	public void testDeactivateBlock() {
		testBlock.setActive(true);
		testBlock.deactivateBlock();
		assertFalse(testBlock.isActive());
	}

	@Test
	public void testActivateBlock() {
		//testBlock.setActive(false);
		//testBlock.activateBlock();
		//assertTrue(testBlock.isActive());
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
		assertTrue(testBlock.isFallRequested());
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
	public void testSetAndGetComboOrigin() {
		//Don't know how to test now...
		testBlock.setComboOrigin(testBlock);
		newBlock = testBlock.getComboOrigin();
		assertEquals(newBlock, testBlock);
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


	//helper Methods

}
