package TetrisAttack;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import TetrisAttack.Game.Cursor;

public class GridTest {

	private Game testGame;
	private Grid testGrid;
	private TAGraphic testMyPanel;
	private TAGraphic testYourPanel;
	private Cursor testCursor;
	private int[] testConstants;
	private String testStartingLayout;
		
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		testGame = new Game();
		testConstants = testGame.getGridConstants();
		testGrid = new Grid(testMyPanel,testYourPanel, testCursor, testConstants, testStartingLayout);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertTrue(true);
	}

}