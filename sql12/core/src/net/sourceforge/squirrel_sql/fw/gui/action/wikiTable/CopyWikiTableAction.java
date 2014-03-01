/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.gui.action.wikiTable;

import java.awt.event.ActionEvent;

import javax.swing.JTable;

import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.gui.action.TableCopyWikiCommand;

/**
 * Action, for copying the selection of a {@link JTable} as WIKI text into the clipboard.
 * @see IWikiTableConfiguration
 * @see IWikiTableTransformer
 * @see ITableActionCallback
 * @author Stefan Willinger
 *
 */
public class CopyWikiTableAction extends BaseAction{
	private static final long serialVersionUID = -6527002300678829186L;
	/**
	 * WIKI specific configuration for this action.
	 */
	private IWikiTableConfiguration config;
	
	
	/**
	 * A callback which provides the {@link JTable}
	 */
	private ITableActionCallback callback;

	
	/**
	 * Constructor for the Action.
	 * @param titel String representing the name of the action
	 * @param config WIKI specific configuration to use.
	 * @param callback Callback, which provides the JTable.
	 */
	public CopyWikiTableAction(String titel, IWikiTableConfiguration config, ITableActionCallback callback)
	{
		super(titel);
		this.config = config;
		this.callback = callback;
	}

	/**
	 * Performs the copy-work.
	 * Asks the callback for the JTable and creates the WIKI text. 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt)
	{
		JTable table = callback.getJTable();
		if (table != null){
			new TableCopyWikiCommand(table,config ).execute();
		}
	}
}