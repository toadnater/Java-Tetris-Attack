
package TetrisAttack;

import java.awt.*;
import java.awt.event.*;

// Interface that handles keyboard input
interface InputHandler {
	public class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
            	Main.Update_onKey(e);
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
            } else if (e.getID() == KeyEvent.KEY_TYPED) {
            }
            return false;
        }
    }
	
	// Adds mouse clicks and move detectors (if needed)
	/*
	addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			System.out.println("Clicked");
		}
	});
	addMouseMotionListener(new MouseAdapter() {
        public void mouseMoved(MouseEvent e) {
        }
    });
    */
}