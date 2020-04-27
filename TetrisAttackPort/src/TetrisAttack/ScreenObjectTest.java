package TetrisAttack;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
public class ScreenObjectTest {

	static ScreenObject testScreenObject;
	static String testName = "Test Name";
	static String testText = "Test Text";
	static String testMenuGroup = "Test Menu Group";
	static int testX = 10;
	static int testY = 10;
	static int initX = 0;
	static int initY = 0;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {	
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		testScreenObject = new ScreenObject();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetName() {
		testScreenObject.setName(testName);
		assertTrue(testScreenObject.getName().equals(testName));
	}

	@Test
	public void testSetX() {
		testScreenObject.setX(testX);
		assertEquals(testScreenObject.getX(),testX);
	}
	
	@Test
	public void testSetY() {
		testScreenObject.setY(testY);
		assertEquals(testScreenObject.getY(),testY);
	}
	
	@Test
	public void testSetTiles() {
		testScreenObject.setTiles(true);  
		assertTrue(testScreenObject.getTiles());
	}
	
	@Test
	public void testSetVisible() {
		testScreenObject.setVisible(false);  
		assertFalse(testScreenObject.isVisible());
	}
	
	@Test
	public void testSetBackground() {
		testScreenObject.setBackground(true);  
		assertTrue(testScreenObject.isBackground());
	}
	
	@Test
	public void testSetCursor() {
		testScreenObject.setCursor(true);  
		assertTrue(testScreenObject.isCursor());
	}
	
	@Test
	public void testSetAnimated() {
		testScreenObject.setAnimated(true);  
		assertTrue(testScreenObject.isAnimated());
	}
	
	@Test
	public void testSetLooped() {
		testScreenObject.setLooped(true);  
		assertTrue(testScreenObject.isLooped());
	}
	
	@Test
	public void testSetText() {
		testScreenObject.setText(testText);
		assertTrue(testScreenObject.getText().equals(testText));
	}
	
	@Test
	public void testSetMenu() {
		//testScreenObject = new ScreenObject(testName, testMenuGroup, initX, initY);
		testScreenObject.setMenu(testMenuGroup);
		assertTrue(testScreenObject.getMenu().equals(testMenuGroup));
	}
	
	@Test
	public void testGetOriginXY() {
		testScreenObject = new ScreenObject(testName, testMenuGroup, initX, initY);
		assertEquals(testScreenObject.getOriginX(),initX);
		assertEquals(testScreenObject.getOriginY(),initY);
	}
}
