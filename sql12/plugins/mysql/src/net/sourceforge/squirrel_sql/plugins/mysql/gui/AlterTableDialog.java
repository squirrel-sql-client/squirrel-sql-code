package net.sourceforge.squirrel_sql.plugins.mysql.gui;
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
import java.sql.SQLException;

import javax.swing.JDialog;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;
/**
 * Dialog that allows user to alter the dtructure of a table
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AlterTableDialog extends JDialog
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(AlterTableDialog.class);

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AlterTableDialog.class);

	/**
	 * Ctor.
	 *
	 * @param	app		Appliocation API.
	 * @param	plugin	This plugin.
	 * @param	ti		Points to table to be modified.
	 */
	public AlterTableDialog(ISession session, MysqlPlugin plugin,
								ITableInfo ti)
		throws SQLException
	{
		super(session.getApplication().getMainFrame(), true);

		setTitle(s_stringMgr.getString("AlterTableDialog.title",
										ti.getQualifiedName()));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setContentPane(new AlterTablePanelBuilder().buildPanel(session, ti));
	}

}
