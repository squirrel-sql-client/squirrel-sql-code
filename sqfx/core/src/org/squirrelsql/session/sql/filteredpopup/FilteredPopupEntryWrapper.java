package org.squirrelsql.session.sql.filteredpopup;

import java.util.ArrayList;
import java.util.List;

public class FilteredPopupEntryWrapper<T extends FilteredPopupEntry> implements FilteredPopupEntry
{
   private T _entry;
   private String _distString;

   public FilteredPopupEntryWrapper(T entry)
   {
      _entry = entry;
   }

   @Override
   public String getSelShortcut()
   {
      return _entry.getSelShortcut();
   }

   @Override
   public String getDescription()
   {
      return _entry.getDescription();
   }

   public T getEntry()
   {
      return _entry;
   }


   public void setDisplaySpace(int distLen)
   {
      StringBuffer buf = new StringBuffer();

      for (int j = 0; j < distLen; j++)
      {
         buf.append(" ");
      }

      _distString = buf.toString();

   }

   @Override
   public String toString()
   {
      return getSelShortcut() + _distString + getDescription();
   }

   public static <T extends FilteredPopupEntry> ArrayList<FilteredPopupEntryWrapper<T>> wrap(List<T> entries)
   {
      ArrayList<FilteredPopupEntryWrapper<T>> ret = new ArrayList<>();

      for (T entry : entries)
      {
         ret.add(new FilteredPopupEntryWrapper<T>(entry));
      }

      return ret;
   }
}
