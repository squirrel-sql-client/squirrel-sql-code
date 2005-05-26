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


import java.sql.Types;


public class CodeCompletionColumnInfo extends CodeCompletionInfo
{
   private String _columnName;
   private String _columnType;
   private int _columnSize;
   private boolean _nullable;

   private String _toString;
   private int _decimalDigits;


   public CodeCompletionColumnInfo(String columnName, String columnType, int columnSize, int decimalDigits, boolean nullable)
   {
      _columnName = columnName;
      _columnType = columnType;
      _columnSize = columnSize;
      _decimalDigits = decimalDigits;
      _nullable = nullable;

      String decimalDigitsString = 0 == _decimalDigits ? "" : "," + _decimalDigits;
      _toString = _columnName + "  " + _columnType + "(" + _columnSize + decimalDigitsString + ") " + (_nullable? "NULL": "NOT NULL");
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
