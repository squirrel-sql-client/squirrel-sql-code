package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.util.SortedSet;
import java.util.TreeSet;

public class TableInfo extends DatabaseObjectInfo implements ITableInfo
{
    /** Table Type. */
	private final String _tableType;

	/** Table remarks. */
	private final String _remarks;

	private SortedSet<ITableInfo> _childList; // build up datastructure.
	private ITableInfo[] _childs; // final cache.

    ForeignKeyInfo[] exportedKeys = null;
    ForeignKeyInfo[] importedKeys = null;
    
	public TableInfo(String catalog, String schema, String simpleName,
					 String tableType, String remarks,
					 ISQLDatabaseMetaData md)
	{
		super(catalog, schema, simpleName, getTableType(tableType), md);
		_remarks = remarks;
		_tableType = tableType;
	}

   private static DatabaseObjectType getTableType(String tableType)
   {
      if(null == tableType)
      {
         return DatabaseObjectType.TABLE;
      }
      else if(false == tableType.equalsIgnoreCase("TABLE") && false == tableType.equalsIgnoreCase("VIEW"))
      {
         return DatabaseObjectType.TABLE;
      }
      else
      {
         return tableType.equalsIgnoreCase("VIEW") ? DatabaseObjectType.VIEW : DatabaseObjectType.TABLE;
      }
   }

   public void replaceDatabaseObjectTypeConstantObjectsByConstantObjectsOfThisVM()
   {
      if(DatabaseObjectType.TABLE.getKeyForSerializationReplace().equals(super.getDatabaseObjectType().getKeyForSerializationReplace()))
      {
         super.replaceDatabaseObjectTypeConstantObjectsByConstantObjectsOfThisVM(DatabaseObjectType.TABLE);
      }
      else if (DatabaseObjectType.TABLE.getKeyForSerializationReplace().equals(super.getDatabaseObjectType().getKeyForSerializationReplace()))
      {
         super.replaceDatabaseObjectTypeConstantObjectsByConstantObjectsOfThisVM(DatabaseObjectType.VIEW);
      }
   }


   // TODO: Rename this to getTableType.
   public String getType()
   {
      return _tableType;
   }

	public String getRemarks()
	{
		return _remarks;
	}

	public boolean equals(Object obj)
	{
		if (super.equals(obj) && obj instanceof TableInfo)
		{
			TableInfo info = (TableInfo) obj;
			if ((info._tableType == null && _tableType == null)
				|| ((info._tableType != null && _tableType != null)
					&& info._tableType.equals(_tableType)))
			{
				return (
					(info._remarks == null && _remarks == null)
						|| ((info._remarks != null && _remarks != null)
							&& info._remarks.equals(_remarks)));
			}
		}
		return false;
	}

	void addChild(ITableInfo tab)
	{
		if (_childList == null)
		{
			_childList = new TreeSet<ITableInfo>();
		}
		_childList.add(tab);
	}

	public ITableInfo[] getChildTables()
	{
		if (_childs == null && _childList != null)
		{
			_childs = _childList.toArray(new ITableInfo[_childList.size()]);
			_childList = null;
		}
		return _childs;
	}


    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ITableInfo#getExportedKeys()
     */
    public ForeignKeyInfo[] getExportedKeys() {
        return exportedKeys;
    }

    public void setExportedKeys(ForeignKeyInfo[] foreignKeys) {
        exportedKeys = foreignKeys;
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.ITableInfo#getImportedKeys()
     */
    public ForeignKeyInfo[] getImportedKeys() {
        return importedKeys;
    }

    public void setImportedKeys(ForeignKeyInfo[] foreignKeys) {
        importedKeys = foreignKeys;
    }



}
