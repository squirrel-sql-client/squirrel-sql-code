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

import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionColumnInfo;
import net.sourceforge.squirrel_sql.plugins.codecompletion.CodeCompletionInfo;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;
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

   public String getCompletionString()
   {
      return _tableName;
   }

   public CodeCompletionInfo[] getColumns(DatabaseMetaData jdbcMetaData, String colNamePattern)
      throws SQLException
   {
      if(null == _colInfos)
      {
         Vector infos = new Vector();
         ResultSet res = jdbcMetaData.getColumns(null, null, _tableName, "%");
         while(res.next())
         {
            String columnName = res.getString("COLUMN_NAME");
            String columnType = res.getString("TYPE_NAME");
            int columnSize = res.getInt("COLUMN_SIZE");
            boolean nullable = "YES".equals(res.getString("IS_NULLABLE"));
            CodeCompletionColumnInfo buf = new CodeCompletionColumnInfo(columnName, columnType, columnSize, nullable);
            infos.add(buf);
         }
         _colInfos = (CodeCompletionColumnInfo[])infos.toArray(new CodeCompletionColumnInfo[0]);
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
