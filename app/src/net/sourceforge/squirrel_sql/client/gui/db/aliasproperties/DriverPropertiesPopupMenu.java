/*
 * Copyright (C) 2009 Rob Manning
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
package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * Popup menu which is displayed when the user right-clicks on the driver properties table, giving the choice
 * to remove a property or add a new one.
 */
public class DriverPropertiesPopupMenu extends JPopupMenu
{
	private static final long serialVersionUID = -8109748449852223185L;

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DriverPropertiesPopupMenu.class);

	private static interface i18n
	{
		// i18n[DriverPropertiesPopupMenu.addPropertyLabel=Add Property]
		String addPropertyLabel = s_stringMgr.getString("DriverPropertiesPopupMenu.addPropertyLabel");
		
		// i18n[DriverPropertiesPopupMenu.removePropertyLabel=Remove Property]
		String removePropertyLabel = s_stringMgr.getString("DriverPropertiesPopupMenu.removePropertyLabel");
	}

	private final DriverPropertiesTable driverPropertiesTable;

	public DriverPropertiesPopupMenu(DriverPropertiesTable table)
	{
		this.driverPropertiesTable = table;
		JMenuItem addPropertyMenuItem = new JMenuItem(i18n.addPropertyLabel);
		addPropertyMenuItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				AddDriverPropertyDialog dialog = new AddDriverPropertyDialog(driverPropertiesTable);
				GUIUtils.centerWithinParent(dialog);
				dialog.setVisible(true);
			}

		});
		JMenuItem removePropertyMenuItem = new JMenuItem(i18n.removePropertyLabel);
		removePropertyMenuItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				int selectedRowIndex = driverPropertiesTable.getSelectedRow();
				String propertyName = (String) driverPropertiesTable.getValueAt(selectedRowIndex, 0);
				driverPropertiesTable.removeProperty(propertyName);
			}

		});
		add(addPropertyMenuItem);
		add(removePropertyMenuItem);
	}

	/**
	 * Show the menu.
	 */
	public void show(Component invoker, int x, int y)
	{
		super.show(invoker, x, y);
	}
}
