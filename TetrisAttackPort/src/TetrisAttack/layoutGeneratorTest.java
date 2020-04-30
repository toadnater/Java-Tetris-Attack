package TetrisAttack;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class layoutGeneratorTest {

	private GridConstants gridConstants;
	private layoutGenerator testLayoutGenerator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		gridConstants = new GridConstants();
		testLayoutGenerator = new layoutGenerator(gridConstants.getGridConstantsArray());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRandomStartLayout() {
		String layoutString;
		layoutString = testLayoutGenerator.randomStartLayout();
		
		assertTrue(layoutString.length() > 0);
	}
	
	@Test
	public void testAddRow() {
		String layoutString;
		layoutString = testLayoutGenerator.randomStartLayout();
		String rowString;
		rowString = testLayoutGenerator.addRow(layoutString);
		assertTrue(rowString.length() == 6);
	}

	@Test
	public void testAddRowOverloadedForCreation() {
		String layoutString;
		layoutString = testLayoutGenerator.randomStartLayout();
		String rowString;
		rowString = testLayoutGenerator.addRow(layoutString, -99999999);
		assertTrue(rowString.length() == 6);
	}
	
	@Test
	public void testChkRow() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testPrint() {
		fail("Not yet implemented");
	}

}
