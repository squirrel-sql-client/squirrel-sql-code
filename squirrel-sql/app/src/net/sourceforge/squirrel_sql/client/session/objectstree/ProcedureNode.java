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

import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class ProcedureNode extends BaseNode implements IProcedureInfo {

	private final IProcedureInfo _procInfo;

	public ProcedureNode(ISession session, ObjectsTreeModel treeModel,
							IProcedureInfo procInfo) {
		super(session, treeModel, getNodeText(procInfo));
		_procInfo = procInfo;
	}

	public String getCatalogName() {
		return _procInfo.getCatalogName();
	}

	public String getSchemaName() {
		return _procInfo.getSchemaName();
	}

	public String getSimpleName() {
		return _procInfo.getSimpleName();
	}

	public String getQualifiedName() {
		return _procInfo.getQualifiedName();
	}

	public String getRemarks() {
		return _procInfo.getRemarks();
	}

	public int getType() {
		return _procInfo.getType();
	}

	public String getTypeDescription() {
		return _procInfo.getTypeDescription();
	}

	public JComponent getDetailsPanel() {
		final ISession session = getSession();
		final IPlugin plugin = session.getApplication().getDummyAppPlugin();
		ProcedurePanel pnl = (ProcedurePanel)session.getPluginObject(plugin, ISession.ISessionKeys.PROCEDURE_DETAIL_PANEL_KEY);
		//if (pnl == null) {
		//	pnl = new ProcedurePanel(session);
		//	session.putPluginObject(plugin, ISession.ISessionKeys.PROCEDURE_DETAIL_PANEL_KEY, pnl);
		//}
		pnl.setProcedureInfo(this);
		return pnl;
	}

	public boolean isLeaf() {
		return true;
	}

	private static String getNodeText(IProcedureInfo procInfo)
			throws IllegalArgumentException {
		if (procInfo == null) {
			throw new IllegalArgumentException("Null IProcedureInfo passed");
		}
		return procInfo.getSimpleName();
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof ProcedureNode)
		{
			return ((ProcedureNode)obj)._procInfo.equals(_procInfo);
		}
		return false;
	}

	public int compareTo(Object o) {
		ProcedureNode other = (ProcedureNode) o;
		return _procInfo.compareTo(other._procInfo);
    }

}
