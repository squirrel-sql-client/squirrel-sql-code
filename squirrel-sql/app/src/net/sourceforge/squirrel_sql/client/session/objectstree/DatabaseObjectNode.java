package net.sourceforge.squirrel_sql.client.session.objectstree;
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
import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class DatabaseObjectNode extends BaseNode implements IDatabaseObjectInfo {
	private final IDatabaseObjectInfo _objInfo;

	public DatabaseObjectNode(ISession session, ObjectsTreeModel treeModel, IDatabaseObjectInfo objInfo) {
		super(session, treeModel, getNodeText(objInfo));
		_objInfo = objInfo;
	}

	public String getCatalogName() {
		return _objInfo.getCatalogName();
	}

	public String getSchemaName() {
		return _objInfo.getSchemaName();
	}

	public String getSimpleName() {
		return _objInfo.getSimpleName();
	}

	public String getQualifiedName() {
		return _objInfo.getQualifiedName();
	}

	private static String getNodeText(IDatabaseObjectInfo objInfo) {
		if (objInfo == null) {
			throw new IllegalArgumentException("Null IDatabaseObjectInfo passed");
		}
		return objInfo.getSimpleName();
	}

	public boolean equals(Object obj) {
		if(obj instanceof DatabaseObjectNode) {
			return ((DatabaseObjectNode)obj)._objInfo.equals(_objInfo);
		}
		return false;
	}

	public int compareTo(Object o) {
		DatabaseObjectNode other = (DatabaseObjectNode) o;	
		return _objInfo.compareTo(other._objInfo);
     }

}
