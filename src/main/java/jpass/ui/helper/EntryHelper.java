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

package jpass.ui.helper;

import java.util.Date;

import javax.swing.table.TableModel;

import jpass.data.DataModel;
import jpass.ui.EntryDialog;
import jpass.ui.JPassFrame;
import jpass.ui.MessageDialog;
import jpass.util.BrowserUtils;
import jpass.util.ClipboardUtils;
import jpass.xml.bind.Entry;

/**
 * Helper class for entry operations.
 *
 * @author Gabor_Bata
 *
 */
public final class EntryHelper {

	private EntryHelper() {
		// not intended to be instantiated
	}

	/**
	 * Deletes an entry.
	 *
	 * @param parent
	 *            parent component
	 */
	public static void deleteEntry(JPassFrame parent) {
		int selectedRow = JPassFrame.getInstance().getEntryTable().getSelectedRow();
		int convertedRow = JPassFrame.getInstance().getEntryTable().convertRowIndexToModel(selectedRow);

		String title = getTitle(convertedRow);

		showWarning(parent, selectedRow);
		int option = MessageDialog.showQuestionMessage(parent, "Do you really want to delete this entry?", MessageDialog.YES_NO_OPTION);
		if (option == MessageDialog.YES_OPTION) {

			parent.getModel().getEntries().getEntries().remove(parent.getModel().getEntryByTitle(title));
			parent.getModel().setModified(true);
			parent.getModel().getEntries().setUpdateDate(new Date());
			parent.refreshFrameTitle();
			parent.refreshEntryTitleList(null);
		}
	}

	/**
	 * Duplicates an entry.
	 *
	 * @param parent
	 *            parent component
	 */
	public static void duplicateEntry(JPassFrame parent) {
		int selectedRow = JPassFrame.getInstance().getEntryTable().getSelectedRow();
		int convertedRow = JPassFrame.getInstance().getEntryTable().convertRowIndexToModel(selectedRow);

		String title = getTitle(convertedRow);

		showWarning(parent, selectedRow);

		Entry oldEntry = parent.getModel().getEntryByTitle(title);
		EntryDialog ed = new EntryDialog(parent, "Duplicate Entry", oldEntry, true);
		if (ed.getFormData() != null) {
			parent.getModel().getEntries().getEntries().add(ed.getFormData());
			parent.getModel().setModified(true);
			parent.getModel().getEntries().setUpdateDate(new Date());
			parent.refreshFrameTitle();
			parent.refreshEntryTitleList(ed.getFormData().getTitle());
		}
	}

	/**
	 * Edits the entry.
	 *
	 * @param parent
	 *            parent component
	 */
	public static void editEntry(JPassFrame parent) {
		int selectedRow = JPassFrame.getInstance().getEntryTable().getSelectedRow();
		int convertedRow = JPassFrame.getInstance().getEntryTable().convertRowIndexToModel(selectedRow);

		String title = getTitle(convertedRow);

		showWarning(parent, convertedRow);

		Entry oldEntry = parent.getModel().getEntryByTitle(title);
		EntryDialog ed = new EntryDialog(parent, "Edit Entry", oldEntry, false);
		if (ed.getFormData() != null) {
			parent.getModel().getEntries().getEntries().remove(oldEntry);
			parent.getModel().getEntries().getEntries().add(ed.getFormData());
			// FIXME ne passer setModified à TRUE que si l'utilisateur a fait au moins une
			// modif
			parent.getModel().setModified(true);
			parent.getModel().getEntries().setUpdateDate(new Date());
			parent.refreshFrameTitle();
			parent.refreshEntryTitleList(ed.getFormData().getTitle());
		}
	}

	/**
	 * Adds an entry.
	 *
	 * @param parent
	 *            parent component
	 */
	public static void addEntry(JPassFrame parent) {
		EntryDialog ed = new EntryDialog(parent, "Add New Entry", null, true);
		if (ed.getFormData() != null) {
			parent.getModel().getEntries().getEntries().add(ed.getFormData());
			parent.getModel().setModified(true);
			parent.getModel().getEntries().setUpdateDate(new Date());
			parent.refreshFrameTitle();
			parent.refreshEntryTitleList(ed.getFormData().getTitle());
		}
	}

	/**
	 * Gets the selected entry.
	 *
	 * @param parent
	 *            the parent frame
	 * @return the entry or null
	 */
	public static Entry getSelectedEntry(JPassFrame parent) {
		int selectedRow = JPassFrame.getInstance().getEntryTable().getSelectedRow();
		int convertedRow = JPassFrame.getInstance().getEntryTable().convertRowIndexToModel(selectedRow);

		showWarning(parent, selectedRow);
		String title = getTitle(convertedRow);
		return parent.getModel().getEntryByTitle(title);
	}

	/**
	 * Copy entry field value to clipboard.
	 *
	 * @param parent
	 *            the parent frame
	 * @param content
	 *            the content to copy
	 */
	public static void copyEntryField(JPassFrame parent, String content) {
		try {
			ClipboardUtils.setClipboardContent(content);
		} catch (Exception e) {
			MessageDialog.showErrorMessage(parent, e.getMessage());
		}
	}

	public static void gotoEntry(JPassFrame parent, Entry entry) {
		try {
			BrowserUtils.displayURL(entry.getUrl());
		} catch (Exception e) {
			MessageDialog.showErrorMessage(parent, e.getMessage());
		}
	}

	private static void showWarning(JPassFrame parent, int selectedRow) {
		if (selectedRow < 0) {
			MessageDialog.showWarningMessage(parent, "Please select an entry.");
			return;
		}
	}

	private static String getTitle(int selectedRow) {
		TableModel model = JPassFrame.getInstance().getEntryTable().getModel();

		return model.getValueAt(selectedRow, DataModel.TITLE_COLUMN).toString();
	}

	public static void toggleEnabledEntries(JPassFrame parent, boolean enableOnly) {
		parent.filterEnableEntries(enableOnly);
	}
}
