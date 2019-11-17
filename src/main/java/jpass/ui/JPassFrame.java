/*
 * JPass
 *
 * Copyright (c) 2009-2015 Gabor Bata
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jpass.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import jpass.data.DataModel;
import jpass.ui.action.AbstractMenuAction;
import jpass.ui.action.Callback;
import jpass.ui.action.CloseListener;
import jpass.ui.action.MenuActionType;
import jpass.ui.helper.EntryHelper;
import jpass.ui.helper.FileHelper;
import jpass.util.DateUtils;
import jpass.util.FileUtils;

/**
 * The main frame for JPass.
 *
 * @author Gabor_Bata
 *
 */
public final class JPassFrame extends JFrame {
	private static final long serialVersionUID = -4114209356464342368L;

	private static final Logger LOGGER = Logger.getLogger(JPassFrame.class.getName());

	private static volatile JPassFrame INSTANCE;

	public static final String PROGRAM_NAME = "JPass Password Manager";
	public static final String PROGRAM_VERSION = "1.0.9";

	public static final String DEFAULT_FILENAME = "MesPasswords.jpass";

	/**
	 * Popup menu sur la table.
	 */
	private JPopupMenu popup = null;

	private JMenuBar menuBar;
	private JMenu fileMenu = null;
	private JMenu editMenu = null;
	private boolean editMenuActivated = false;
	private AbstractMenuAction editAction = null;
	private AbstractMenuAction duplicateAction = null;
	private AbstractMenuAction deleteAction = null;
	private AbstractMenuAction copyUrlAction = null;
	private AbstractMenuAction copyUserAction = null;
	private AbstractMenuAction copyPasswordAction = null;

	private JMenu toolsMenu = null;
	private JMenu helpMenu = null;
	private JToolBar toolBar = null;
	private final JScrollPane scrollPane;

	private JTable table;
	private TableRowSorter<DataModel> sorter;

	/**
	 * Zone pour filter les entrées du tableau.
	 */
	private JTextField filterText;

	private final DataModel model = DataModel.getInstance();
	private final StatusPanel statusPanel;
	private volatile boolean processing = false;

	/**
	 * Création de la fenêtre principale en passant les paramètres de la ligne de
	 * commmande.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public JPassFrame(String[] args) {
		try {
			setIconImage(MessageDialog.getIcon("lock").getImage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		fillToolbar();

		this.menuBar = new JMenuBar();

		fillFileMenu();
		this.menuBar.add(this.fileMenu);

		fillEditMenu();
		this.menuBar.add(this.editMenu);

		fillToolsMenu();
		this.menuBar.add(this.toolsMenu);

		fillHelpMenu();
		this.menuBar.add(this.helpMenu);

		fillPopup();

		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(Object.class, new JTableColumnRenderer());
		table.getColumnModel().getColumn(DataModel.TITLE_COLUMN).setPreferredWidth(200);
		table.getColumnModel().getColumn(DataModel.NOTES_COLUMN).setPreferredWidth(5);
		table.getColumnModel().getColumn(DataModel.ENABLE_COLUMN).setPreferredWidth(5);
		table.getColumnModel().getColumn(DataModel.HIT_COLUMN).setPreferredWidth(5);
		table.addMouseListener(new MyJTableMouseListener());
		table.setAutoCreateRowSorter(true);
		table.setFillsViewportHeight(true);
		table.getTableHeader().setToolTipText("Click to sort; Shift-Click to sort in reverse order");
		// tri sur la première colonne par défaut
		sorter = new TableRowSorter<DataModel>(model);
		table.setRowSorter(sorter);
		table.getRowSorter().toggleSortOrder(0);
		// sets the popup menu for the table
		table.setComponentPopupMenu(popup);
		table.getColumnModel().getColumn(DataModel.TITLE_COLUMN).setCellRenderer(new TitleCellRenderer());

		this.scrollPane = new JScrollPane(this.table);

		MenuActionType.bindAllActions(this.table);

		// Create a separate form for filterText and statusText
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("Specify a word to match:"), BorderLayout.WEST);

		filterText = new JTextField();
		// Whenever filterText changes, invoke newFilter.
		filterText.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				newFilter();
			}

			public void insertUpdate(DocumentEvent e) {
				newFilter();
			}

			public void removeUpdate(DocumentEvent e) {
				newFilter();
			}
		});
		panel.add(filterText, BorderLayout.CENTER);

		this.statusPanel = new StatusPanel();
		panel.add(this.statusPanel, BorderLayout.SOUTH);

		refreshAll();

		getContentPane().add(this.toolBar, BorderLayout.NORTH);
		getContentPane().add(this.scrollPane, BorderLayout.CENTER);
		getContentPane().add(panel, BorderLayout.SOUTH);

		setJMenuBar(this.menuBar);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		int width = 1000;
		// largeur de la fenêtre
		if (args.length >= 2) {
			width = Integer.parseInt(args[1]);
		}
		int height = 1000;
		// hauteur de la fenêtre
		if (args.length >= 3) {
			height = Integer.parseInt(args[2]);
		}
		setSize(width, height);
		setMinimumSize(new Dimension(width, height));
		addWindowListener(new CloseListener());
		setLocationRelativeTo(null);
		setVisible(true);

		if (args.length >= 1) {
			try {
				LOGGER.info("on précharge le fichier passé en ligne de commande: " + new File(args[0]).getCanonicalPath());
			} catch (Exception e) {
			}
			FileHelper.doOpenFile(args[0], this);
		} else {
			// on recherche un fichier .jpass au même endroit
			LOGGER.info("on précharge le premier fichier jpass situé dans le même répertoire.");
			try {
				LOGGER.info("Ce fichier est: " + FileUtils.findJPassFile("./" + DEFAULT_FILENAME));
				FileHelper.doOpenFile(FileUtils.findJPassFile("./" + DEFAULT_FILENAME), this);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "", e);
			}
		}

		// set focus to the list for easier keyboard navigation
		this.table.requestFocusInWindow();
	}

	private void fillPopup() {
		this.popup = new JPopupMenu();
		this.popup.add(MenuActionType.ADD_ENTRY.getAction());
		this.popup.add(MenuActionType.EDIT_ENTRY.getAction());
		this.popup.add(MenuActionType.DUPLICATE_ENTRY.getAction());
		this.popup.add(MenuActionType.DELETE_ENTRY.getAction());
		this.popup.addSeparator();
		this.popup.add(MenuActionType.COPY_URL.getAction());
		this.popup.add(MenuActionType.COPY_USER.getAction());
		this.popup.add(MenuActionType.COPY_PASSWORD.getAction());
		this.popup.add(MenuActionType.GOTO.getAction());
		this.popup.addPopupMenuListener(new MyPopupMenuListener());
	}

	private void fillHelpMenu() {
		this.helpMenu = new JMenu("Help");
		this.helpMenu.setMnemonic(KeyEvent.VK_H);
		this.helpMenu.add(MenuActionType.LICENSE.getAction());
		this.helpMenu.addSeparator();
		this.helpMenu.add(MenuActionType.ABOUT.getAction());
	}

	private void fillToolsMenu() {
		this.toolsMenu = new JMenu("Tools");
		this.toolsMenu.setMnemonic(KeyEvent.VK_T);
		this.toolsMenu.add(MenuActionType.GENERATE_PASSWORD.getAction());
		this.toolsMenu.add(MenuActionType.CLEAR_CLIPBOARD.getAction());
		this.toolsMenu.add(MenuActionType.TOGGLE_ENABLED_ENTRIES.getAction());
	}

	private void fillEditMenu() {
		this.editMenu = new JMenu("Edit");
		this.editMenu.setMnemonic(KeyEvent.VK_E);
		this.editMenu.add(MenuActionType.ADD_ENTRY.getAction());

		this.editAction = MenuActionType.EDIT_ENTRY.getAction();
		this.editMenu.add(editAction);

		this.duplicateAction = MenuActionType.DUPLICATE_ENTRY.getAction();
		this.editMenu.add(duplicateAction);

		this.deleteAction = MenuActionType.DELETE_ENTRY.getAction();
		this.editMenu.add(deleteAction);
		this.editMenu.addSeparator();

		this.copyUrlAction = MenuActionType.COPY_URL.getAction();
		this.editMenu.add(copyUrlAction);
		this.copyUserAction = MenuActionType.COPY_USER.getAction();
		this.editMenu.add(copyUserAction);
		this.copyPasswordAction = MenuActionType.COPY_PASSWORD.getAction();
		this.editMenu.add(copyPasswordAction);
		this.toggleEditMenu(false);
		this.editMenuActivated = false;
	}

	public void toggleEditMenu(boolean enable) {
		if (this.editMenuActivated && enable) {
			return;
		}
		if (!this.editMenuActivated && !enable) {
			return;
		}

		if (this.editAction != null) {
			this.editAction.setEnabled(enable);
		}
		if (this.duplicateAction != null) {
			this.duplicateAction.setEnabled(enable);
		}
		if (this.deleteAction != null) {
			this.deleteAction.setEnabled(enable);
		}
		if (this.copyUrlAction != null) {
			this.copyUrlAction.setEnabled(enable);
		}
		if (this.copyUserAction != null) {
			this.copyUserAction.setEnabled(enable);
		}
		if (this.copyPasswordAction != null) {
			this.copyPasswordAction.setEnabled(enable);
		}
		this.editMenuActivated = enable;
	}

	private void fillFileMenu() {
		this.fileMenu = new JMenu("File");
		this.fileMenu.setMnemonic(KeyEvent.VK_F);
		this.fileMenu.add(MenuActionType.NEW_FILE.getAction());
		this.fileMenu.add(MenuActionType.OPEN_FILE.getAction());
		this.fileMenu.add(MenuActionType.SAVE_FILE.getAction());
		this.fileMenu.add(MenuActionType.SAVE_AS_FILE.getAction());
		this.fileMenu.addSeparator();
		this.fileMenu.add(MenuActionType.EXPORT_XML.getAction());
		this.fileMenu.add(MenuActionType.IMPORT_XML.getAction());
		this.fileMenu.addSeparator();
		this.fileMenu.add(MenuActionType.CHANGE_PASSWORD.getAction());
		this.fileMenu.addSeparator();
		this.fileMenu.add(MenuActionType.EXIT.getAction());
	}

	private void fillToolbar() {
		this.toolBar = new JToolBar();
		this.toolBar.setFloatable(false);
		this.toolBar.add(MenuActionType.NEW_FILE.getAction());
		this.toolBar.add(MenuActionType.OPEN_FILE.getAction());
		this.toolBar.add(MenuActionType.SAVE_FILE.getAction());
		this.toolBar.addSeparator();
		this.toolBar.add(MenuActionType.ADD_ENTRY.getAction());
		this.toolBar.add(MenuActionType.EDIT_ENTRY.getAction());
		this.toolBar.add(MenuActionType.DUPLICATE_ENTRY.getAction());
		this.toolBar.add(MenuActionType.DELETE_ENTRY.getAction());
		this.toolBar.addSeparator();
		this.toolBar.add(MenuActionType.COPY_URL.getAction());
		this.toolBar.add(MenuActionType.COPY_USER.getAction());
		this.toolBar.add(MenuActionType.COPY_PASSWORD.getAction());
		this.toolBar.add(MenuActionType.CLEAR_CLIPBOARD.getAction());
		this.toolBar.addSeparator();
		this.toolBar.add(MenuActionType.ABOUT.getAction());
		this.toolBar.add(MenuActionType.EXIT.getAction());
	}

	public static JPassFrame getInstance() {
		return getInstance(null);
	}

	public static JPassFrame getInstance(String[] args) {
		if (INSTANCE == null) {
			synchronized (JPassFrame.class) {
				if (INSTANCE == null) {
					INSTANCE = new JPassFrame(args);
				}
			}
		}
		return INSTANCE;
	}

	public JTable getEntryTable() {
		return this.table;
	}

	public JPopupMenu getPopupMenu() {
		return this.popup;
	}

	/**
	 * Gets the data model of this frame.
	 *
	 * @return data model
	 */
	public DataModel getModel() {
		return this.model;
	}

	/**
	 * Refresh frame title based on data model.
	 */
	public void refreshFrameTitle() {
		setTitle((getModel().isModified() ? "* " : "") + (getModel().getFileName() == null ? "Untitled" : getModel().getFileName()) + " - "
				+ DateUtils.dateToString(getModel().getEntries().getUpdateDate()) + " - " + PROGRAM_NAME);
	}

	/**
	 * Refresh the entry titles based on data model.
	 *
	 * @param selectTitle
	 *            title to select, or {@code null} if nothing to select
	 */
	public void refreshEntryTitleList(String selectTitle) {

		this.getModel().fireTableDataChanged();

		if (selectTitle != null) {
			// FIXMEthis.entryTitleList.setSelectedValue(selectTitle, true);
		}
		updateStatusText("Entries count: " + this.getModel().getRowCount(), true);
	}

	public void updateStatusText(String message, boolean permanent) {

		if (permanent) {
			this.statusPanel.setText(message);
		} else {
			String oldText = this.statusPanel.getText();
			this.statusPanel.setText(message);
			// FIXME la suite ne marche pas
			/*
			 * try { Thread.sleep(1000*1); } catch (InterruptedException e) { }
			 * this.statusPanel.setText(oldText);
			 */
		}
	}

	/**
	 * Refresh frame title and entry list.
	 */
	public void refreshAll() {
		refreshFrameTitle();
		refreshEntryTitleList(null);
	}

	/**
	 * Exits the application.
	 */
	public void exitFrame() {
		// Clear clipboard on exit
		EntryHelper.copyEntryField(this, null);

		if (this.processing) {
			return;
		}
		if (this.model.isModified()) {
			int option = MessageDialog.showQuestionMessage(this, "The current file has been modified.\n" + "Do you want to save the changes before closing?", MessageDialog.YES_NO_CANCEL_OPTION);
			if (option == MessageDialog.YES_OPTION) {
				FileHelper.saveFile(this, false, new Callback() {
					@Override
					public void call(boolean result) {
						if (result) {
							System.exit(0);
						}
					}
				});
				return;
			} else if (option != MessageDialog.NO_OPTION) {
				return;
			}
		}
		System.exit(0);
	}

	public JPopupMenu getPopup() {
		return this.popup;
	}

	/**
	 * Sets the processing state of this frame.
	 *
	 * @param processing
	 *            processing state
	 */
	public void setProcessing(boolean processing) {
		this.processing = processing;
		for (MenuActionType actionType : MenuActionType.values()) {
			// actionType.getAction().setEnabled(!processing);
		}

		this.table.setEnabled(!processing);
		this.statusPanel.setProcessing(processing);
	}

	/**
	 * Gets the processing state of this frame.
	 *
	 * @return processing state
	 */
	public boolean isProcessing() {
		return this.processing;
	}

	/**
	 * Update the row filter regular expression from the expression in the text box.
	 */
	private void newFilter() {
		RowFilter<DataModel, Object> rf = null;
		List<RowFilter<Object, Object>> rfs = new ArrayList<RowFilter<Object, Object>>();
		// If current expression doesn't parse, don't update.
		int columns[] = { DataModel.TITLE_COLUMN, DataModel.URL_COLUMN, DataModel.USER_COLUMN, DataModel.PASSWORD_COLUMN, DataModel.NOTES_COLUMN };
		String text = filterText.getText();
		String[] textArray = text.split(" ");
		try {
			// le préfixe permet d'être insensible à la casse
			for (int i = 0; i < textArray.length; i++) {
				rfs.add(RowFilter.regexFilter("(?i)" + textArray[i], columns));
			}

			rf = RowFilter.andFilter(rfs);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		sorter.setRowFilter(rf);
	}

	public void filterEnableEntries(boolean enableOnly) {
		sorter.setRowFilter(RowFilter.regexFilter("(?i)" + enableOnly, DataModel.ENABLE_COLUMN));
	}
}