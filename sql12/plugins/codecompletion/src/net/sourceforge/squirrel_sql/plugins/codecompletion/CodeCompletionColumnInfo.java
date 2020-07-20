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
package net.sourceforge.squirrel_sql.plugins.codecompletion;


import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferences;

public class CodeCompletionColumnInfo extends CodeCompletionInfo
{
   private String _tableName;
   private String _columnName;
   private String _columnType;
   private int _columnSize;
   private boolean _nullable;
   private CodeCompletionPreferences _prefs;

   private String _toString;
   private int _decimalDigits;

   private String _remarks;


   public CodeCompletionColumnInfo(String tableName, String columnName, String remarks, String columnType, int columnSize, int decimalDigits, boolean nullable, CodeCompletionPreferences prefs)
   {
      _tableName = tableName;
      _columnName = columnName;
      _columnType = columnType;
      _columnSize = columnSize;
      _decimalDigits = decimalDigits;
      _nullable = nullable;
      _prefs = prefs;
      String decimalDigitsString = 0 == _decimalDigits ? "" : "," + _decimalDigits;
      _remarks = remarks;
      //_toString = getTableName() + _columnName + getRemarksString() + _columnType + "(" + _columnSize + decimalDigitsString + ") " + (_nullable? "NULL": "NOT NULL");
      _toString = _columnName + getTableName() + getRemarksString() + _columnType + "(" + _columnSize + decimalDigitsString + ") " + (_nullable? "NULL": "NOT NULL");
   }

   private String getTableName()
   {
      if(false == _prefs.isShowTableNameOfColumnsInCompletion() || StringUtilities.isEmpty(_tableName, true))
      {
         return "";
      }

      return " (" + _tableName + ")";
   }

   private String getTableQualifier()
   {
      if(StringUtilities.isEmpty(_tableName, true))
      {
         return "";
      }

      //return _tableName.trim() + ".";
      return _tableName + ".";
   }

   private String getRemarksString()
   {
      String ret = " ";
      if (_prefs.isShowRemarksInColumnCompletion() && null != _remarks && 0 < _remarks.trim().length())
      {
         ret = " (" + _remarks + ")  ";
      }
      return ret;
   }

   @Override
   public String getCompletionString(CompletionParser completionParser)
   {
      final String ret;

      if ( false == completionParser.isQualified() && _prefs.isCompleteColumnsQualified())
      {
         ret = getTableQualifier() + getCompareString();
      }
      else
      {
         ret = super.getCompletionString();
      }

      return CompletionCaseSpelling.valueOf(_prefs.getColumnCaseSpelling()).adjustCaseSpelling(ret);

   }

   public String getCompareString()
   {
      return _columnName;
   }

   public String toString()
   {
      return _toString;
   }
}
