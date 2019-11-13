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

package jpass.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import jpass.util.DateUtils;
import jpass.xml.bind.Entries;
import jpass.xml.bind.Entry;

/**
 * Data model of the application data.
 *
 * @author Gabor_Bata
 *
 */
@SuppressWarnings("serial")
public class DataModel extends AbstractTableModel {
	private static volatile DataModel INSTANCE;

	public static final int TITLE_COLUMN = 0;
	public static final int URL_COLUMN = 1;
	public static final int USER_COLUMN = 2;
	public static final int PASSWORD_COLUMN = 3;
	public static final int NOTES_COLUMN = 4;
	public static final int CREATIONDATE_COLUMN = 5;
	public static final int UPDATEDATE_COLUMN = 6;
	public static final int ENABLE_COLUMN = 7;
	public static final int HIT_COLUMN = 8;

	private String[] columnNames = new String[] { "Title", "URL", "Username", "Password", "Notes", "Creation date", "Update date", "Enable", "Hit" };

	private Entries entries = new Entries();
	private String fileName = null;
	private transient byte[] password = null;
	private boolean modified = false;

	private DataModel() {
		// not intended to be instantiated
	}

	/**
	 * Gets the DataModel singleton instance.
	 *
	 * @return instance of the DataModel
	 */
	public static DataModel getInstance() {
		if (INSTANCE == null) {
			synchronized (DataModel.class) {
				if (INSTANCE == null) {
					INSTANCE = new DataModel();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * Gets list of entries.
	 *
	 * @return list of entries
	 */
	public final Entries getEntries() {
		return this.entries;
	}

	/**
	 * Sets list of entries.
	 *
	 * @param entries
	 *            entries
	 */
	public final void setEntries(final Entries entries) {
		System.out.println("On fixe les entrées du model");
		this.entries = entries;
		this.fireTableDataChanged();
	}

	/**
	 * Gets the file name for the data model.
	 *
	 * @return file name
	 */
	public final String getFileName() {
		return this.fileName;
	}

	/**
	 * Sets the file name for the data model.
	 *
	 * @param fileName
	 *            file name
	 */
	public final void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the modified state of the data model.
	 *
	 * @return modified state of the data model
	 */
	public final boolean isModified() {
		return this.modified;
	}

	/**
	 * Sets the modified state of the data model.
	 *
	 * @param modified
	 *            modified state
	 */
	public final void setModified(final boolean modified) {
		this.modified = modified;
	}

	public byte[] getPassword() {
		return this.password;
	}

	public void setPassword(byte[] password) {
		this.password = password;
	}

	/**
	 * Clears all fields of the data model.
	 */
	public final void clear() {
		this.entries.getEntries().clear();
		this.fileName = null;
		this.password = null;
		this.modified = false;
	}

	/**
	 * Gets the list of entry titles.
	 *
	 * @return list of entry titles
	 */
	public List<String> getListOfTitles() {
		List<String> list = new ArrayList<String>(this.entries.getEntries().size());
		for (Entry entry : this.entries.getEntries()) {
			list.add(entry.getTitle());
		}
		return list;
	}

	/**
	 * Gets entry index by title.
	 *
	 * @param title
	 *            entry title
	 * @return entry index
	 */
	public int getEntryIndexByTitle(String title) {
		System.out.println("on recherche: " + title);
		return getListOfTitles().indexOf(title);
	}

	/**
	 * Gets entry by title.
	 *
	 * @param title
	 *            entry title
	 * @return entry
	 */
	public Entry getEntryByTitle(String title) {
		int index = getEntryIndexByTitle(title);
		if (index >= 0) {
			return this.entries.getEntries().get(index);
		} else
			return null;
	}

	public Entry findEntry(String title) {
		Entry foundedEntry = null;
		for (Entry entry : this.entries.getEntries()) {
			if (entry.getTitle().contains(title)) {
				foundedEntry = entry;
				break;
			}
		}
		return foundedEntry;
	}

	public int findEntryIndex(String title) {
		int index = 0;
		boolean founded = false;
		for (Entry entry : this.entries.getEntries()) {
			index++;
			if (entry.getTitle().equalsIgnoreCase(title)) {
				founded = true;
				break;
			}
		}
		if (founded)
			return index;
		else
			return -1;
	}

	@Override
	public int getRowCount() {
		return this.entries.getEntries().size();
	}

	@Override
	public int getColumnCount() {
		return this.columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		String value = null;

		if (rowIndex < 0 && rowIndex > getRowCount() - 1)
			return null;
		if (columnIndex < 0 && columnIndex > getColumnCount() - 1)
			return null;

		if (columnIndex == TITLE_COLUMN) {
			value = this.entries.getEntries().get(rowIndex).getTitle();
		} else if (columnIndex == URL_COLUMN) {
			value = this.entries.getEntries().get(rowIndex).getUrl();
		} else if (columnIndex == USER_COLUMN) {
			value = this.entries.getEntries().get(rowIndex).getUser();
		} else if (columnIndex == PASSWORD_COLUMN) {
			value = this.entries.getEntries().get(rowIndex).getPassword();
		} else if (columnIndex == NOTES_COLUMN) {
			value = this.entries.getEntries().get(rowIndex).getNotes();
		} else if (columnIndex == CREATIONDATE_COLUMN) {
			Date creationDate = this.entries.getEntries().get(rowIndex).getCreationDate();
			value = creationDate == null ? null : DateUtils.dateToString(creationDate);
		} else if (columnIndex == UPDATEDATE_COLUMN) {
			Date updateDate = this.entries.getEntries().get(rowIndex).getUpdateDate();
			value = updateDate == null ? null : DateUtils.dateToString(updateDate);
		} else if (columnIndex == ENABLE_COLUMN) {
			return !this.entries.getEntries().get(rowIndex).isDisabled();
		} else if (columnIndex == HIT_COLUMN) {

			value = this.entries.getEntries().get(rowIndex).getHit().toString();
		} else {
		}

		return value;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case TITLE_COLUMN:
			return String.class;
		case URL_COLUMN:
			return String.class;
		case USER_COLUMN:
			return String.class;
		case PASSWORD_COLUMN:
			return String.class;
		case NOTES_COLUMN:
			return String.class;
		case CREATIONDATE_COLUMN:
			return String.class;
		case UPDATEDATE_COLUMN:
			return String.class;
		case ENABLE_COLUMN:
			return Boolean.class;
		case HIT_COLUMN:
			return String.class;
		default:
			return String.class;
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		return this.columnNames[columnIndex];
	}
}
