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

package net.sourceforge.squirrel_sql.plugins.dbdiff.actions;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider;
import net.sourceforge.squirrel_sql.plugins.dbdiff.commands.CompareCommand;
import net.sourceforge.squirrel_sql.plugins.dbdiff.gui.DiffPresentationFactoryImpl;
import net.sourceforge.squirrel_sql.plugins.dbdiff.gui.IDiffPresentationFactory;

import java.awt.event.ActionEvent;

public class CompareAction extends AbstractDiffAction implements ISessionAction
{

	/** Current plugin. */
	private final SessionInfoProvider sessionInfoProv;

	/** Logger for this class. */
	private final static ILogger log = LoggerController.createLogger(CompareAction.class);

	/**
	 * Creates a new SQuirreL action that gets fired whenever the user chooses the compare operation.
	 * 
	 * @param app
	 * @param rsrc
	 * @param plugin
	 */
	public CompareAction(IApplication app, Resources rsrc, SessionInfoProvider prov)
	{
		super(app, rsrc);
		sessionInfoProv = prov;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt)
	{
		final ISession sourceSession = sessionInfoProv.getSourceSession();
		final ISession destSession = sessionInfoProv.getDestSession();
		final IObjectTreeAPI api = destSession.getObjectTreeAPIOfActiveSessionWindow();
		if (api == null)
		{
			return;
		}
		final IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();


      boolean clearSource = false;

		// sourceSession can be null in the case where two tables are selected in the same schema for direct
		// compare.
		if (  sourceExists() )
		{
			sessionInfoProv.setDestSelectedDatabaseObjects(dbObjs);
         clearSource = true;
		}
		else
		{
			// User didn't 'select' any objects in a different session to compare. If there are two objects
			// that are selected in this session, then compare them. Otherwise, we need to inform the user
			// how object selection in the diff plugin works.
			if (dbObjs.length == 2)
			{
				sessionInfoProv.setSourceSession(destSession);
				sessionInfoProv.setSourceSelectedDatabaseObjects(new IDatabaseObjectInfo[] { dbObjs[0] });
				sessionInfoProv.setDestSession(destSession);
				sessionInfoProv.setDestSelectedDatabaseObjects(new IDatabaseObjectInfo[] { dbObjs[1] });
            clearSource = true;
			}
			else
			{
				destSession.showErrorMessage("To compare, you must have exactly two tables selected in the "
					+ "object tree");
			}
		}

		if (sessionInfoProv.getSourceSession() == null)
		{
			log.error("actionPerformed: Source session was null - A source session must be selected to "
				+ "perform a comparison.");
			return;
		}
		if (sessionInfoProv.getDestSession() == null)
		{
			log.error("actionPerformed: Source session was null - A destination session must be selected to "
				+ "perform a comparison.");
			return;
		}
		final CompareCommand command = new CompareCommand(sessionInfoProv);

		final IDiffPresentationFactory diffPresentationFactory = new DiffPresentationFactoryImpl();
		command.setDiffPresentationFactory(diffPresentationFactory);
		command.setPluginPreferencesManager(pluginPreferencesManager);
		command.execute();

      if(clearSource)
      {
         sessionInfoProv.setSourceSelectedDatabaseObjects(new IDatabaseObjectInfo[0]);
      }

	}

   private boolean sourceExists()
   {
      return sessionInfoProv.getSourceSession() != null &&
            null != sessionInfoProv.getSourceSelectedDatabaseObjects()
            && 0 < sessionInfoProv.getSourceSelectedDatabaseObjects().length;
   }

   /**
	 * Set the current session.
	 * 
	 * @param session
	 *           The current session.
	 */
	public void setSession(ISession session)
	{
		sessionInfoProv.setDestSession(session);
	}

}
