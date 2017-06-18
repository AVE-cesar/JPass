package jpass.ui.action;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.SwingUtilities;

import jpass.ui.JPassFrame;
import jpass.ui.helper.EntryHelper;

public class KeyboadListener implements KeyListener {

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (JPassFrame.getInstance().isProcessing()) {
            return;
        }
        if (e.getKeyCode()==KeyEvent.VK_ENTER) {
            EntryHelper.editEntry(JPassFrame.getInstance());
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
