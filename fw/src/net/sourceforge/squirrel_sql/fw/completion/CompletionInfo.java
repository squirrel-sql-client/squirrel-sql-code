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



public abstract class CompletionInfo implements Comparable
{
   private String _upperCaseCompletionString;

   public abstract String getCompareString();

   public String getCompletionString()
   {
      return getCompareString();
   }

   public int compareTo(Object obj)
   {
      CompletionInfo other = (CompletionInfo)obj;

      if(null == _upperCaseCompletionString)
      {
         _upperCaseCompletionString = getCompareString().toUpperCase();
      }

      if(null == other._upperCaseCompletionString)
      {
         other._upperCaseCompletionString = other.getCompareString().toUpperCase();
      }

      return _upperCaseCompletionString.compareTo(other._upperCaseCompletionString);
   }

   /**
    * Param must be an upper case string if not the result will always be false
    */
   public boolean upperCaseCompletionStringStartsWith(String testString)
   {
      if(null == _upperCaseCompletionString)
      {
         _upperCaseCompletionString = getCompareString().toUpperCase();
      }

      return _upperCaseCompletionString.startsWith(testString);
   }

   /**
    * Param must be an upper case string if not the result will always be false
    */
   public boolean upperCaseCompletionStringEquals(String testString)
   {
      if(null == _upperCaseCompletionString)
      {
         _upperCaseCompletionString = getCompareString().toUpperCase();
      }

      return _upperCaseCompletionString.equals(testString);
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
