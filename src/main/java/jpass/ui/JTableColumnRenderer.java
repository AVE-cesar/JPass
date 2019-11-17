package jpass.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import jpass.data.DataModel;

public class JTableColumnRenderer extends JLabel implements TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2053761972685786913L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (DataModel.HIT_COLUMN == column) {
			((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
		} else if (DataModel.CREATIONDATE_COLUMN == column || DataModel.UPDATEDATE_COLUMN == column) {
			((JLabel) c).setEnabled(false);
		}
		return c;
	}

}
