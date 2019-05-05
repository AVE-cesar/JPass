package jpass.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import jpass.ui.helper.EntryHelper;

public class MyJTableMouseListener implements MouseListener {

	/**
     * Show entry on double click.
     */ 
	@Override
	public void mouseClicked(MouseEvent event) {
				
		if (JPassFrame.getInstance().isProcessing()) {
            return;
        }
		
		int selectedRow = JPassFrame.getInstance().getEntryTable().getSelectedRow();
		if (selectedRow >= 0) {
			JPassFrame.getInstance().toggleEditMenu(true);
		
	        if (SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2) {
	            EntryHelper.editEntry(JPassFrame.getInstance());
	        }
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
