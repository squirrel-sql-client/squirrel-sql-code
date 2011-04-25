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
package net.sourceforge.squirrel_sql.fw.gui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import javax.swing.JTable;

import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableTransformer;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.WikiTableSelection;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This command gets the current selected text from a <TT>JTable</TT>, formats
 * it as WIKI table and places it on the system clipboard.
 * @see IWikiTableConfiguration
 * @see IWikiTableTransformer
 * @author Stefan Willinger
 * 
 */
public class TableCopyWikiCommand implements ICommand {

	private static ILogger s_log = LoggerController.createLogger(TableCopyWikiCommand.class);

	/**
	 * Configuration to use.
	 */
	private IWikiTableConfiguration config = null;

	/**
	 * The table, where the data will get from.
	 */
	private JTable table = null;

	private WikiTableSelection selection = null;

	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(TableCopyWikiCommand.class);

	/**
	 * Create a new Command.
	 * 
	 * @param _table
	 */
	public TableCopyWikiCommand(JTable aTable, IWikiTableConfiguration wikiConfig) {
		super();
		if (aTable == null) {
			throw new IllegalArgumentException("JTable == null");
		}
		if (wikiConfig == null) {
			throw new IllegalArgumentException("wikiConfig == null");
		}

		this.table = aTable;
		this.config = wikiConfig;
		try {
			this.selection = new WikiTableSelection();
		} catch (ClassNotFoundException e) {
			String msg = s_stringMgr.getString("TableCopyWikiCommand.error.flavors");
			s_log.error(msg, e);
		}

	}

	/**
	 * Performs the action.
	 * 
	 * @see net.sourceforge.squirrel_sql.fw.util.ICommand#execute()
	 */
	@Override
	public void execute() {

		String result = this.config.createTransformer().transform(table);
		if (result != null) {
			selection.setData(result);
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			cb.setContents(selection, null);
		}
	}

}
