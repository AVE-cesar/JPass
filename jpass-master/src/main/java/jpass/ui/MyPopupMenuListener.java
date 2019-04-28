package jpass.ui;

import java.awt.Point;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * In case you want to automatically select the row where the right-click was made, add the following snippet:
 * 
 * @author avebertrand
 *
 */
public class MyPopupMenuListener implements PopupMenuListener {

	@Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	JTable table = JPassFrame.getInstance().getEntryTable();
                int rowAtPoint = table.rowAtPoint(SwingUtilities.convertPoint(JPassFrame.getInstance().getPopupMenu(), new Point(0, 0), table));
                if (rowAtPoint > -1) {
                	table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                }
            }
        });
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        // TODO Auto-generated method stub

    }

}
