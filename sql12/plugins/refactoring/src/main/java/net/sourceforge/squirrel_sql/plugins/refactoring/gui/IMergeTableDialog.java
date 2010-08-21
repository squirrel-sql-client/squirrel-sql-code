/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Vector;

import net.sourceforge.squirrel_sql.client.gui.db.IDisposableDialog;

public interface IMergeTableDialog extends IDisposableDialog
{

	String getReferencedTable();

	Vector<String[]> getWhereDataColumns();

	Vector<String> getMergeColumns();

	boolean isMergeData();
	
	void setVisible(boolean val);

	void addShowSQLListener(ActionListener listener);
	
	void addEditSQLListener(ActionListener listener);
	
	void addExecuteListener(ActionListener listener);
	
	void setLocationRelativeTo(Component c);
}