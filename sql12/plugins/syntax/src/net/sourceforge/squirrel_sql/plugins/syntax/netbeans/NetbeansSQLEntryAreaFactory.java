package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;
/*
 * Copyright (C) 2004 Gerd Wagner
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
import java.util.HashMap;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;
import net.sourceforge.squirrel_sql.plugins.syntax.IConstants;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;

import org.netbeans.editor.DialogSupport;
import org.netbeans.editor.ImplementationProvider;
import org.netbeans.modules.editor.NbImplementationProvider;

/**
 * Factory for creating Netbeans SQL entry area objects.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class NetbeansSQLEntryAreaFactory
{
	private SyntaxPugin _plugin;
   private SyntaxFactory _syntaxFactory;

   public NetbeansSQLEntryAreaFactory(SyntaxPugin plugin)
	{
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null NetbeansPlugin passed");
		}

		_plugin = plugin;
      _syntaxFactory = new SyntaxFactory();

      //DialogSupport.setDialogFactory(new NbDialogSupport());
      DialogSupport.setDialogFactory(new SquirrelNBDialogFactory(_plugin));
      ImplementationProvider.registerDefault(new NbImplementationProvider());
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory#createSQLEntryPanel()
	 */
	public ISQLEntryPanel createSQLEntryPanel(ISession session, 
                                              HashMap<String, Object> props)
		throws IllegalArgumentException
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		SyntaxPreferences prefs = getPreferences(session);
      return new NetbeansSQLEntryPanel(session, prefs, _syntaxFactory, _plugin, props);
	}

	private SyntaxPreferences getPreferences(ISession session)
	{
		return (SyntaxPreferences)session.getPluginObject(_plugin, IConstants.ISessionKeys.PREFS);
	}


   public void sessionEnding(ISession sess)
   {
      _syntaxFactory.sessionEnding(sess);
   }
}
