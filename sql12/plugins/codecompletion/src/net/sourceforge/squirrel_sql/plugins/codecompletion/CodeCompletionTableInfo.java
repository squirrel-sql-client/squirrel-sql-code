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

import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.client.session.SchemaInfo;

import java.sql.SQLException;
import java.util.Vector;

public class CodeCompletionTableInfo extends CodeCompletionInfo
{
   private String _tableName;
   private String _tableType;
   private CodeCompletionColumnInfo[] _colInfos;


   public CodeCompletionTableInfo(String tableName, String tableType)
   {
      _tableName = tableName;
      _tableType = tableType;
   }

   public String getCompareString()
   {
      return _tableName;
   }

   public CodeCompletionInfo[] getColumns(SchemaInfo schemaInfo, String colNamePattern)
      throws SQLException
   {
      if(null == _colInfos)
      {
         ExtendedColumnInfo[] schnemColInfos = schemaInfo.getExtendedColumnInfos(_tableName);

         _colInfos = new CodeCompletionColumnInfo[schnemColInfos.length];
         for (int i = 0; i < schnemColInfos.length; i++)
         {
            String columnName = schnemColInfos[i].getColumnName();
            String columnType = schnemColInfos[i].getColumnType();
            int columnSize = schnemColInfos[i].getColumnSize();
            int decimalDigits = schnemColInfos[i].getDecimalDigits();
            boolean nullable = schnemColInfos[i].isNullable();
            CodeCompletionColumnInfo buf = new CodeCompletionColumnInfo(columnName, columnType, columnSize, decimalDigits, nullable);
            _colInfos[i] = buf;

         }
      }

      String upperCaseColNamePattern = colNamePattern.toUpperCase().trim();

      if("".equals(upperCaseColNamePattern))
      {
         return _colInfos;
      }

      Vector ret = new Vector();
      for(int i=0; i < _colInfos.length; ++i)
      {
         if(_colInfos[i].upperCaseCompletionStringStartsWith(upperCaseColNamePattern))
         {
            ret.add(_colInfos[i]);
         }
      }

      return (CodeCompletionInfo[])ret.toArray(new CodeCompletionInfo[0]);
   }

   public boolean hasColumns()
   {
      return true;
   }


   public String toString()
   {
      if(null != _tableType && !"TABLE".equals(_tableType))
      {
         return _tableName  + " (" + _tableType + ")";
      }
      else
      {
         return _tableName;
      }
   }
}
