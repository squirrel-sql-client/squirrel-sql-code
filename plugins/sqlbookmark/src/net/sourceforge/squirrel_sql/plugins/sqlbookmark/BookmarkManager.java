/*
 * Copyright (C) 2003 Joseph Mocker
 * mock-sf@misfit.dhs.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

/**
 * Manages the users bookmarks. Including loading and saving to 
 * an XML file.
 *
 * @author      Joseph Mocker
 **/
public class BookmarkManager {

    /** The file to save/load bookmarks to/from */
    private File bookmarkFile;

    /** List of all the loaded bookmarks */
    private ArrayList bookmarks = new ArrayList();
    
    /** Index of bookmark names to indexes in the bookmarks array */
    private HashMap bookmarkIdx = new HashMap();
    
    public BookmarkManager(File settingsFolder) {
	bookmarkFile = new File(settingsFolder, "bookmarks.xml");
    }

    /**
     * Add a new bookmark, or replace an existing bookmark.
     *
     * @param       bookmark bookmark to add/change.
     * @return      true if a replacement, false if a new bookmark.
     **/
    protected boolean add(Bookmark bookmark) {
	Integer idxInt = (Integer) bookmarkIdx.get(bookmark.getName());
	if (idxInt != null) {
	    bookmarks.set(idxInt.intValue(), bookmark);
	    return true;
	} else {
	    bookmarks.add(bookmark);
	    idxInt = new Integer(bookmarks.size() - 1);
	    bookmarkIdx.put(bookmark.getName(), idxInt);
	    return false;
	}
    }

    /**
     * Retrieve a bookmark by name.
     *
     * @param       name Name of the bookmark.
     * @return      the bookmark.
     **/
    protected Bookmark get(String name) {
	Integer idxInt = (Integer) bookmarkIdx.get(name);
	if (idxInt != null) 
	    return (Bookmark) bookmarks.get(idxInt.intValue());
	
	return null;
    }

    /**
     * Load the stored bookmarks.
     *
     **/
    protected void load() throws IOException {

	try {
	    XMLBeanReader xmlin = new XMLBeanReader();
	    xmlin.load(bookmarkFile, getClass().getClassLoader());
	    for (Iterator i = xmlin.iterator(); i.hasNext(); ) {
		Object bean = i.next();
		if (bean instanceof Bookmark) 
		    add((Bookmark) bean);
	    }
	}
	catch (XMLException e) {
	    // REMIND: decide what to do about this
	}
    }

    /**
     * Save the bookmarks.
     *
     **/
    protected void save() throws IOException {
	try {
	    XMLBeanWriter xmlout = new XMLBeanWriter();
	    
	    for (Iterator i = bookmarks.iterator(); i.hasNext(); ) {
		Bookmark bookmark = (Bookmark) i.next();
		
		xmlout.addToRoot(bookmark);
	    }
	    
	    xmlout.save(bookmarkFile);
	}
	catch (XMLException e) {
	    // REMIND: decide what to do about this
	}
    }

    protected Iterator iterator() {
	return bookmarks.iterator();
    }
}
