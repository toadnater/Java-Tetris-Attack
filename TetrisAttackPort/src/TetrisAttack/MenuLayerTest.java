package TetrisAttack;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MenuLayerTest {
	
	private MenuLayer testMenuLayer;
	private MenuLayer testML_TOP; 
	private MenuLayer testML_1PGAME;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
		
	//ML_TOP = new MenuLayer(null, "menu_top");
	//ML_1PGAME = new MenuLayer(ML_TOP, "1playergame");
	//	ML_STAGECLEAR = new MenuLayer(ML_1PGAME, "stageclear");
	//		ML_STAGECLEAR_PASSWORD = new MenuLayer(ML_STAGECLEAR, "stageclear_password");
	//	ML_PUZZLE = new MenuLayer(ML_1PGAME, "puzzle");
	//		ML_PUZZLE_PASSWORD = new MenuLayer(ML_PUZZLE, "puzzle_password");
	//	ML_VERSUS = new MenuLayer(ML_1PGAME, "versus");
	//		ML_VERSUS_PASSWORD = new MenuLayer(ML_VERSUS, "versus_password");
	//ML_2PGAME = new MenuLayer(ML_TOP, "2playergame");
	//ML_HOWTOPLAY = new MenuLayer(ML_TOP, "howtoplay");
	//ML_HOWTOIMPROVE = new MenuLayer(ML_TOP, "howtoimprove");
	//ML_OPTION = new MenuLayer(ML_TOP, "option");
	
	
	@Before
	public void setUp() throws Exception {
		
		 testML_TOP = new MenuLayer(null, "menu_top");
		 testML_1PGAME= new MenuLayer(testML_TOP, "1playergame");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddChildMenu() {   
		assertEquals(testML_TOP.numberOfChildren(),0);
		testML_TOP.addChildMenu("1playergame", testML_1PGAME);
		assertEquals(testML_TOP.numberOfChildren(),1);
	}

	@Test
	public void testSetParentLayer() {       
		testML_1PGAME.setParentLayer(testML_TOP);
		assertEquals(testML_1PGAME.getParentMenu(),testML_TOP);
	}
	
	@Test
	public void testHasNext() {
		assertEquals(testML_TOP.numberOfChildren(),0);
		assertFalse(testML_TOP.hasNext());
		testML_TOP.addChildMenu("1playergame", testML_1PGAME);
		assertTrue(testML_TOP.hasNext());
	}
	
	@Test
	public void testHasPrevious() {
		assertFalse(testML_1PGAME.hasPrevious());
		testML_TOP.addChildMenu("1playergame", testML_1PGAME);
		assertTrue(testML_1PGAME.hasPrevious());
	}
	
	@Test
	public void testGetChildMenu() {
		testML_TOP.addChildMenu("1playergame", testML_1PGAME);
		assertEquals(testML_TOP.getChildMenu(),testML_1PGAME);
	}
	
	@Test
	public void testNumberOfChildren() {
		testML_TOP.addChildMenu("1playergame", testML_1PGAME);
		assertEquals(testML_TOP.numberOfChildren(),1);
	}
	
	@Test
	public void testGetChildName() {
		testML_TOP.addChildMenu("1playergame", testML_1PGAME);
		assertTrue(testML_TOP.getChildName().equals("1playergame"));
	}
	
	@Test
	public void testGetParentMenu() {
		testML_TOP.addChildMenu("1playergame", testML_1PGAME);
		assertEquals(testML_1PGAME.getParentMenu(), testML_TOP);
	}
	
	@Test
	public void testGetPosition() {
		assertEquals(testML_TOP.getPosition(),0);
		testML_TOP.addChildMenu("1playergame", testML_1PGAME);
		fail("I don't understand the point of this method, or really how it is used.");
	}
	
	@Test
	public void testGetSelected() {
		fail("I don't understand the point of the position field, or really how it is used.");
	}
	
	@Test
	public void testNext() {
		fail("I don't understand the point of the position field, or really how it is used.");
	}
	
	@Test
	public void testPrevious() {
		fail("I don't understand the point of the position field, or really how it is used.");
	}
	
}
