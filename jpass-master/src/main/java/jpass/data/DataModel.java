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
import java.util.List;

import javax.swing.table.AbstractTableModel;

import jpass.xml.bind.Entries;
import jpass.xml.bind.Entry;

/**
 * Data model of the application data.
 *
 * @author Gabor_Bata
 *
 */
public class DataModel extends AbstractTableModel {
    private static volatile DataModel INSTANCE;

    public static int TITLE_COLUMN = 0;
    public static int URL_COLUMN = 1;
    public static int USER_COLUMN = 2;
    public static int PASSWORD_COLUMN = 3;
    public static int NOTES_COLUMN = 4;
    
    private String[] columnNames = new String[] {"Title", "URL", "Username", "Password", "Notes"};
    
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
     * @param entries entries
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
     * @param fileName file name
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
     * @param modified modified state
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
        this.entries.getEntry().clear();
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
        List<String> list = new ArrayList<String>(this.entries.getEntry().size());
        for (Entry entry : this.entries.getEntry()) {
            list.add(entry.getTitle());
        }
        return list;
    }

    /**
     * Gets entry index by title.
     *
     * @param title entry title
     * @return entry index
     */
    public int getEntryIndexByTitle(String title) {
    	System.out.println("on recherche: "  + title);
        return getListOfTitles().indexOf(title);
    }

    /**
     * Gets entry by title.
     *
     * @param title entry title
     * @return entry
     */
    public Entry getEntryByTitle(String title) {
    	int index = getEntryIndexByTitle(title);
    	if (index >= 0) {
        return this.entries.getEntry().get(index);
    	} else return null;
    }
    
    public Entry findEntry(String title) {
    	Entry foundedEntry = null;
    	for (Entry entry : this.entries.getEntry()) {
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
    	for (Entry entry : this.entries.getEntry()) {
    		index++;
    		if (entry.getTitle().equalsIgnoreCase(title)) {
    			founded = true;
    			break;
    		}
    	}
    	if (founded) return index; else return -1;
    }

	@Override
	public int getRowCount() {
		return this.entries.getEntry().size();
	}

	@Override
	public int getColumnCount() {
		return this.columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		/*if (columnIndex ==0) {
			System.out.println("appel de getValueAt:" + rowIndex + ", " + columnIndex);
		
			System.out.println("Le model contient: " + this.entries.getEntry().size());
		}*/
		String value = null;
		
		if (rowIndex < 0 && rowIndex > getRowCount()-1) return null;
		if (columnIndex < 0 && columnIndex > getColumnCount()-1) return null;
		
		if (columnIndex == TITLE_COLUMN) {
			value = this.entries.getEntry().get(rowIndex).getTitle();
		} else if (columnIndex == URL_COLUMN) {
			value = this.entries.getEntry().get(rowIndex).getUrl();
		} else if (columnIndex == USER_COLUMN) {
			value = this.entries.getEntry().get(rowIndex).getUser();
		} else if (columnIndex == PASSWORD_COLUMN) {
			value = this.entries.getEntry().get(rowIndex).getPassword();
		} else if (columnIndex == NOTES_COLUMN) {
			value = this.entries.getEntry().get(rowIndex).getNotes();
		} else {
		}
		//System.out.println("Value:" + value);
		return value;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return this.columnNames[columnIndex];
	}
}
