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

package net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.commands.DBDiffCompareCommand;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.DiffPresentationFactoryImpl;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.IDiffPresentationFactory;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.event.ActionEvent;

public class DBDiffCompareAction extends SquirrelAction implements ISessionAction
{

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DBDiffCompareAction.class);

	private final static ILogger log = LoggerController.createLogger(DBDiffCompareAction.class);

	/**
	 * Creates a new SQuirreL action that gets fired whenever the user chooses the compare operation.
	 * 
	 * @param app
	 * @param rsrc
	 * @param plugin
	 */
	public DBDiffCompareAction()
	{
		super(Main.getApplication());
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt)
	{
		final ISession sourceSession = Main.getApplication().getDBDiffState().getSourceSession();
		final ISession destSession = Main.getApplication().getDBDiffState().getDestSession();
		final IObjectTreeAPI api = destSession.getObjectTreeAPIOfActiveSessionWindow();
		if (api == null)
		{
			return;
		}
		final IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();


      boolean clearSource = false;

		// sourceSession can be null in the case where two tables are selected in the same schema for direct
		// compare.
		if ( sourceExists() )
		{
			Main.getApplication().getDBDiffState().setDestSelectedDatabaseObjects(dbObjs);
         clearSource = true;
		}
		else
		{
			// User didn't 'select' any objects in a different session to compare. If there are two objects
			// that are selected in this session, then compare them. Otherwise, we need to inform the user
			// how object selection in the diff plugin works.
			if (dbObjs.length == 2)
			{
				Main.getApplication().getDBDiffState().setSourceSession(Main.getApplication().getDBDiffState().getDestSession());
				Main.getApplication().getDBDiffState().setSourceSelectedDatabaseObjects(new IDatabaseObjectInfo[] { dbObjs[0] });
				Main.getApplication().getDBDiffState().setDestSession(Main.getApplication().getDBDiffState().getDestSession());
				Main.getApplication().getDBDiffState().setDestSelectedDatabaseObjects(new IDatabaseObjectInfo[] { dbObjs[1] });
            clearSource = true;
			}
			else
			{
				Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("CompareAction.exactly.two.tables.when.no.source"));
			}
		}

		if (Main.getApplication().getDBDiffState().getSourceSession() == null)
		{
			log.error("actionPerformed: Source session was null - A source session must be selected to "
				+ "perform a comparison.");
			return;
		}
		if (Main.getApplication().getDBDiffState().getDestSession() == null)
		{
			log.error("actionPerformed: Source session was null - A destination session must be selected to "
				+ "perform a comparison.");
			return;
		}
		final DBDiffCompareCommand command = new DBDiffCompareCommand();

		final IDiffPresentationFactory diffPresentationFactory = new DiffPresentationFactoryImpl();
		command.setDiffPresentationFactory(diffPresentationFactory);
		command.execute();

      if(clearSource)
      {
			Main.getApplication().getDBDiffState().setSourceSelectedDatabaseObjects(new IDatabaseObjectInfo[0]);
      }

	}

   private boolean sourceExists()
   {
      return Main.getApplication().getDBDiffState().getSourceSession() != null &&
            null != Main.getApplication().getDBDiffState().getSourceSelectedDatabaseObjects()
            && 0 < Main.getApplication().getDBDiffState().getSourceSelectedDatabaseObjects().length;
   }

   /**
	 * Set the current session.
	 * 
	 * @param session
	 *           The current session.
	 */
	public void setSession(ISession session)
	{
		Main.getApplication().getDBDiffState().setDestSession(session);
	}

}
