package net.sourceforge.squirrel_sql.plugins.syntax.oster;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.syntax.IConstants;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;
/**
 * Factory for creating SQL entry area objects.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class OsterSQLEntryAreaFactory implements ISQLEntryPanelFactory
{
	private SyntaxPugin _plugin;

	/** The original Squirrel SQL CLient factory for creating SQL entry panels. */
	private ISQLEntryPanelFactory _originalFactory;

	public OsterSQLEntryAreaFactory(SyntaxPugin plugin, ISQLEntryPanelFactory originalFactory)
	{
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null OsterPlugin passed");
		}

		if (originalFactory == null)
		{
			throw new IllegalArgumentException("Null originalFactory passed");
		}

		_plugin = plugin;
		_originalFactory = originalFactory;
	}

	/**
	 * @see ISQLEntryPanelFactory#createSQLEntryPanel()
	 */
	public ISQLEntryPanel createSQLEntryPanel(ISession session)
		throws IllegalArgumentException
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		final SyntaxPreferences prefs = getPreferences(session);

		if (prefs.getUseOsterTextControl())
		{
			OsterSQLEntryPanel pnl = getPanel(session);

			if (pnl == null)
			{
				pnl = new OsterSQLEntryPanel(session, prefs);
				savePanel(session, pnl);
			}

			return pnl;
		}

		removePanel(session);

		return _originalFactory.createSQLEntryPanel(session);
	}

	private SyntaxPreferences getPreferences(ISession session)
	{
		return (SyntaxPreferences)session.getPluginObject(_plugin,
			IConstants.ISessionKeys.PREFS);
	}

	private OsterSQLEntryPanel getPanel(ISession session)
	{
		return (OsterSQLEntryPanel)session.getPluginObject(_plugin,
			IConstants.ISessionKeys.SQL_ENTRY_CONTROL);
	}

	private void savePanel(ISession session, OsterSQLEntryPanel pnl)
	{
		session.putPluginObject(_plugin,
			IConstants.ISessionKeys.SQL_ENTRY_CONTROL, pnl);
	}

	private void removePanel(ISession session)
	{
		session.removePluginObject(_plugin,
			IConstants.ISessionKeys.SQL_ENTRY_CONTROL);
	}
}
