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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.ICompletorModel;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

/**
 * Manages the users bookmarks. Including loading and saving to
 * an XML file.
 *
 * @author Joseph Mocker
 */
public class BookmarkManager implements ICompletorModel
{

   /**
    * The file to save/load bookmarks to/from
    */
   private File bookmarkFile;

   /**
    * List of all the loaded bookmarks
    */
   private ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();

   /**
    * Index of bookmark names to indexes in the bookmarks array
    */
   private HashMap<String, Integer> bookmarkIdx = new HashMap<String, Integer>();
   private SQLBookmarkPlugin _plugin;

   public BookmarkManager(SQLBookmarkPlugin plugin)
   {
      try
      {
         _plugin = plugin;
         bookmarkFile = new File(_plugin.getPluginUserSettingsFolder(), "bookmarks.xml");
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Add a new bookmark, or replace an existing bookmark.
    *
    * @param bookmark bookmark to add/change.
    * @return true if a replacement, false if a new bookmark.
    */
   protected boolean add(Bookmark bookmark)
   {
      Integer idxInt = bookmarkIdx.get(bookmark.getName());
      if (idxInt != null)
      {
         bookmarks.set(idxInt.intValue(), bookmark);
         return true;
      }
      else
      {
         bookmarks.add(bookmark);
         idxInt = bookmarks.size() - 1;
         bookmarkIdx.put(bookmark.getName(), idxInt);
         return false;
      }
   }

   /**
    * Retrieve a bookmark by name.
    *
    * @param name Name of the bookmark.
    * @return the bookmark.
    */
   protected Bookmark get(String name)
   {
      Integer idxInt = bookmarkIdx.get(name);
      if (idxInt != null) {
         return bookmarks.get(idxInt.intValue());
      }
      return null;
   }

   /**
    * Load the stored bookmarks.
    */
   protected void load() throws IOException
   {

      try
      {
         XMLBeanReader xmlin = new XMLBeanReader();

         if (bookmarkFile.exists())
         {
            xmlin.load(bookmarkFile, getClass().getClassLoader());
            for (Iterator<?> i = xmlin.iterator(); i.hasNext();)
            {
               Object bean = i.next();
               if (bean instanceof Bookmark)
               {
                  add((Bookmark) bean);
               }
            }
         }
      }
      catch (XMLException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Save the bookmarks.
    */
   protected void save()
   {
      try
      {
         XMLBeanWriter xmlout = new XMLBeanWriter();

         for (Iterator<Bookmark> i = bookmarks.iterator(); i.hasNext();)
         {
            Bookmark bookmark = i.next();

            xmlout.addToRoot(bookmark);
         }

         xmlout.save(bookmarkFile);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   protected Iterator<Bookmark> iterator()
   {
      return bookmarks.iterator();
   }

   public CompletionCandidates getCompletionCandidates(String bookmarkNameBegin)
   {
      Vector<BookmarkCompletionInfo> ret = new Vector<BookmarkCompletionInfo>();

      int maxNameLen = 0;
      for (int i = 0; i < bookmarks.size(); i++)
      {
         Bookmark bookmark = bookmarks.get(i);
         if (bookmark.getName().startsWith(bookmarkNameBegin))
         {
            ret.add(new BookmarkCompletionInfo(bookmark));
            maxNameLen = Math.max(maxNameLen, bookmark.getName().length());
         }
      }

      String defaultMarksInPopup =
         _plugin.getBookmarkProperties().getProperty(SQLBookmarkPlugin.BOOKMARK_PROP_DEFAULT_MARKS_IN_POPUP, "" + false);

      if(Boolean.valueOf(defaultMarksInPopup).booleanValue())
      {
         Bookmark[] defaultBookmarks = DefaultBookmarksFactory.getDefaultBookmarks();

         for (int i = 0; i < defaultBookmarks.length; i++)
         {
            if (defaultBookmarks[i].getName().startsWith(bookmarkNameBegin))
            {
               ret.add(new BookmarkCompletionInfo(defaultBookmarks[i]));
               maxNameLen = Math.max(maxNameLen, defaultBookmarks[i].getName().length());
            }
         }
      }





      BookmarkCompletionInfo[] candidates = ret.toArray(new BookmarkCompletionInfo[ret.size()]);

      for (int i = 0; i < candidates.length; i++)
      {
         candidates[i].setMaxCandidateNameLen(maxNameLen);
      }

      return new CompletionCandidates(candidates);
   }

   public void removeAll()
   {
      bookmarks = new ArrayList<Bookmark>();
      bookmarkIdx = new HashMap<String, Integer>();
   }
}
