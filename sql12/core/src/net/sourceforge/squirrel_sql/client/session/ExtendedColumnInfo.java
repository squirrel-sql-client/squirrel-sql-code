/*
 * Copyright (C) 2004 Gerd Wagner
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import java.io.Serializable;
import java.sql.Types;

public class ExtendedColumnInfo implements Serializable
{
   private static final long serialVersionUID = 1L;
   private String _columnName;
   private String _columnType;
   private int _columnSize;
   private int _decimalDigits;
   private boolean _nullable;
   private String _cat;
   private String _schem;
   private String _simpleTableName;
   private String _qualifiedName;
   private String _remarks;
   private int _columnTypeID;

   public ExtendedColumnInfo(TableColumnInfo info, String simpleTableName)
   {
      _columnName = info.getColumnName();
      _columnType = info.getTypeName();
      _columnTypeID = info.getDataType();
      _columnSize = info.getColumnSize();
      _decimalDigits = info.getDecimalDigits();
      _remarks = info.getRemarks();
      if ("YES".equals(info.isNullable()))
      {
         _nullable = true;
      }
      else
      {
         _nullable = false;
      }
      _cat = info.getCatalogName();
      _schem = info.getSchemaName();
      _simpleTableName = simpleTableName;

      _qualifiedName = _cat + "." + _schem + "." + _simpleTableName + "." +_columnName;
   }

   public String getColumnName()
   {
      return _columnName;
   }

   public String getColumnType()
   {
      return _columnType;
   }

   public int getColumnSize()
   {
      return _columnSize;
   }

   public int getDecimalDigits()
   {
      return _decimalDigits;
   }

   public boolean isNullable()
   {
      return _nullable;
   }

   public String getCatalog()
   {
      return _cat;
   }

   public String getSchema()
   {
      return _schem;
   }

   public String getRemarks()
   {
      return _remarks;
   }

   public String getSimpleTableName()
   {
      return _simpleTableName;
   }

   public int getColumnTypeID()
   {
      return _columnTypeID;
   }

   public boolean isCharacterType()
   {
      return   Types.VARCHAR == _columnTypeID
            || Types.LONGVARCHAR == _columnTypeID
            || Types.CHAR  == _columnTypeID
            || Types.NVARCHAR  == _columnTypeID;
   }


   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
       final int prime = 31;
       int result = 1;
       result = prime * result
       + ((_qualifiedName == null) ? 0 : _qualifiedName.hashCode());
       return result;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
       if (this == obj)
           return true;
       if (obj == null)
           return false;
       if (getClass() != obj.getClass())
           return false;
       final ExtendedColumnInfo other = (ExtendedColumnInfo) obj;
       if (_qualifiedName == null) {
           if (other._qualifiedName != null)
               return false;
       } else if (!_qualifiedName.equals(other._qualifiedName))
           return false;
       return true;
   }
}
