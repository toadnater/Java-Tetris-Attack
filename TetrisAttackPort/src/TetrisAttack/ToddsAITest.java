package TetrisAttack;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ToddsAITest {

	private ToddsAI testToddsAI;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		testToddsAI = new ToddsAI();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testCalculateNextInstruction() {
		//Can not test this until the method is written.
		assertTrue(false);
	}
	
	@Test
	public void testUpdateHomeGrid() {
		assertTrue(false);
	}
	
	@Test
	public void testUpdateAwayGrid() {
		assertTrue(false);
	}
	
	@Test
	public void testHasGarbageInHomeGrid() {
		assertTrue(false);
	}
	
	@Test
	public void testHasGarbageInArgumentGrid() {
		assertTrue(false);
	}
	
	@Test
	public void testGetHeightInHomeGrid() {
		assertTrue(false);
	}
	
	@Test
	public void testGetHeightInArgumentGrid() {
		assertTrue(false);
	}
	
	@Test
	public void testGetBlockCount() {
		assertTrue(false);
	}
	
	
	@Test
	public void testGetSpecialBlockCountInHomeGrid() {
		assertTrue(false);
	}
	
	@Test
	public void testGetSpecialBlockCountInArgumentGrid() {
		assertTrue(false);
	}
	
	@Test
	public void testGetSpecialBlockCount() {
		assertTrue(false);
	}
	
	@Test
	public void testMoveCursorTo() {
		//Not testing till TODO is to be completed.
	}
	
	@Test
	public void testGetNextInstructionWhileInstructionsExists() {	
		for(int i=0; i<10; i++) {		
			testToddsAI.addInstruction(AIInstruction.MOVE_UP);
		}
		
		AIInstruction nextInstruction = testToddsAI.getNextInstruction();
		assertEquals(AIInstruction.MOVE_UP, nextInstruction);
	}
	
	@Test
	public void testGetNextInstructionWhenNoInstructionsOnStack() {	
		for(int i=0; i<10; i++) {		
			testToddsAI.addInstruction(AIInstruction.MOVE_UP);
		}
		
		testToddsAI.resetAllInstructions();
		AIInstruction nextInstruction = testToddsAI.getNextInstruction();
		assertTrue(testToddsAI.getInstructions().isEmpty());
		assertEquals(AIInstruction.IDLE,nextInstruction);
	}
	
	@Test
	public void testResetAllInstructions() {
		
		for(int i=0; i<10; i++) {		
			testToddsAI.addInstruction(AIInstruction.MOVE_UP);
		}
		testToddsAI.resetAllInstructions();
		assertTrue(testToddsAI.getInstructions().isEmpty());
	}
	
	@Test
	public void testAddInstruction() {
		boolean returnedValue = testToddsAI.addInstruction(AIInstruction.MOVE_UP);
		assertTrue(returnedValue);
	}

}
