package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2003 Colin Bell
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
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableModel;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.gui.action.MakeEditableCommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.gui.session.MainPanel;
/**
 * A popup menu useful for a editable text area.
 * 
 * @author	Gerd Wagner
 */
public class SessionTextEditPopupMenu extends TextPopupMenu
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MainPanel.class);

	private MakeEditableAction _makeEditable = new MakeEditableAction();

	// The following pointer is needed to allow the "Make Editable button
	// to tell the application to set up an editable display panel
	private IDataSetUpdateableModel _updateableModel = null;

	/**
	 * Default ctor.
	 */
	public SessionTextEditPopupMenu()
	{
		super();
		addMenuEntries(false);
	}

	/**
	 * Constructor used when caller wants to be able to make data editable.
	 * We need both parameters because there is at least one case where the
	 * underlying data model is updateable, but we do not want to allow the
	 * user to enter editing mode because they are already in edit mode.
	 * While that case only applys to the TablePopupMenu, we use the same interface
	 * for both table and text for consistancy.
	 * The caller needs to determine whether or not to allow a request for edit mode.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public SessionTextEditPopupMenu(boolean allowEditing,
									IDataSetUpdateableModel updateableModel)
	{
		super();

		// save the pointer needed to enable editing of data on-demand
		_updateableModel = updateableModel;

		addMenuEntries(allowEditing);
	}

	private void addMenuEntries(boolean allowEditing)
	{
		if (allowEditing)
		{
			addSeparator();
			add(_makeEditable);
			addSeparator();
		}
	}

	private class MakeEditableAction extends BaseAction
	{
		MakeEditableAction()
		{
			super(s_stringMgr.getString("SessionTextEditPopupMenu.makeeditable"));
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_updateableModel != null)
			{
				new MakeEditableCommand(_updateableModel).execute();
			}
		}
	}
}
