/*
 * Copyright (C) 2003 Gerd Wagner
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
package net.sourceforge.squirrel_sql.fw.completion;


import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;

public abstract class CompletionInfo implements Comparable<CompletionInfo>
{
   private String _upperCaseCompletionString;
   private String _completionString;

   public abstract String getCompareString();


   /**
    * @param completionParser Provides information about the text position where the completion is executed. E.g. can be asked if the completion takes place for a qualified column.
    */
   public String getCompletionString(CompletionParser completionParser)
   {
      return getCompletionString();
   }

   public String getCompletionString()
   {
      return getCompareString();
   }

   public int compareTo(CompletionInfo other)
   {
      initCache();
      other.initCache();

      return _upperCaseCompletionString.compareTo(other._upperCaseCompletionString);
   }

   public boolean matchesCompletionStringStart(String testString, CompletionMatchType completionMatchType)
   {
      initCache();
      return _upperCaseCompletionString.startsWith(testString.toUpperCase()) || (completionMatchType.match(testString, _completionString));
   }

   private boolean matchesCamelCase(String testString)
   {
      return CamelCaseMatcher.matchesCamelCase(testString, _completionString);
   }


   public boolean matchesCompletionString(String testString)
   {
      initCache();
      return _upperCaseCompletionString.equals(testString.toUpperCase());
   }

   private void initCache()
   {
      if(null == _upperCaseCompletionString)
      {
         _completionString = getCompareString();
         _upperCaseCompletionString = _completionString.toUpperCase();
      }
   }


   /**
    * Default implementation
    */
   public boolean hasColumns()
   {
      return false;
   }


   public String toString()
   {
      return getCompletionString();
   }
}
