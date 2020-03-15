package net.sourceforge.squirrel_sql.client.gui.db;
/*
 * Copyright (C) 2001-2004 Colin Bell
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

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.HashMap;
import java.util.Map;

public class AliasWindowFactory implements AliasInternalFrame.IMaintenanceType
{
	private static final ILogger s_log = LoggerController.createLogger(AliasWindowFactory.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasWindowFactory.class);
    
	/**
	 * Collection of <TT>AliasMaintDialog</TT> that are currently visible modifying
	 * an existing aliss. Keyed by <TT>ISQLAlias.getIdentifier()</TT>.
	 */
	private static Map<IIdentifier, AliasInternalFrame> _modifySheets = new HashMap<IIdentifier, AliasInternalFrame>();


	/**
	 * Get a maintenance sheet for the passed alias. If a maintenance sheet already
	 * exists it will be brought to the front. If one doesn't exist it will be
	 * created.
	 *
	 * @param	alias	The alias that user has requested to modify.
	 *
	 * @return	The maintenance sheet for the passed alias.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> passed.
	 */
	public static AliasInternalFrame getModifySheet(ISQLAlias alias)
	{
		AliasInternalFrame sheet = _modifySheets.get(alias.getIdentifier());
		if (sheet == null)
		{
			sheet = new AliasInternalFrame(Main.getApplication(), alias, MODIFY);
			_modifySheets.put(alias.getIdentifier(), sheet);
			Main.getApplication().getMainFrame().addWidget(sheet);

			sheet.addWidgetListener(new WidgetAdapter()
			{
				public void widgetClosed(WidgetEvent evt)
				{
					AliasInternalFrame frame = (AliasInternalFrame)evt.getWidget();
					_modifySheets.remove(frame.getSQLAlias().getIdentifier());
				}
         });

			initAliasWidgetListener(sheet);

         DialogWidget.centerWithinDesktop(sheet);
		}

		return sheet;
	}

	private static void initAliasWidgetListener(AliasInternalFrame sheet)
	{
		sheet.addWidgetListener(new WidgetAdapter()
		{
			@Override
			public void widgetClosed(WidgetEvent evt)
			{
				Main.getApplication().getWindowManager().getAliasesListInternalFrame().getAliasesList().aliasChanged(sheet.getSQLAlias());
			}
		});
	}


	/**
	 * Create and show a new maintenance sheet to allow the user to create a new
	 * alias.
	 *
	 * @return	The new maintenance sheet.
	 */
	public static AliasInternalFrame getCreateSheet()
	{
		final net.sourceforge.squirrel_sql.client.gui.db.DataCache cache = Main.getApplication().getDataCache();
		final IIdentifierFactory factory = IdentifierFactory.getInstance();
		final ISQLAlias alias = cache.createAlias(factory.createIdentifier());
		final AliasInternalFrame sheet = new AliasInternalFrame(Main.getApplication(), alias, NEW);
		Main.getApplication().getMainFrame().addWidget(sheet);
      DialogWidget.centerWithinDesktop(sheet);

		initAliasWidgetListener(sheet);

		return sheet;
	}


	/**
	 * Create and show a new maintenance sheet that will allow the user to create a
	 * new alias that is a copy of the passed one.
	 *
	 * @return	The new maintenance sheet.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> passed.
	 */
	public static AliasInternalFrame getCopySheet(SQLAlias alias)
	{
		final DataCache cache = Main.getApplication().getDataCache();
		final IIdentifierFactory factory = IdentifierFactory.getInstance();
		SQLAlias newAlias = cache.createAlias(factory.createIdentifier());

		newAlias.assignFrom(alias, false);

		if(SQLAliasSchemaProperties.GLOBAL_STATE_SPECIFY_SCHEMAS == newAlias.getSchemaProperties().getGlobalState())
		{
			// i18n[AliasWindowFactory.schemaPropsCopiedWarning=Warning: Your target Alias contains database specific Schema properties copied from the source Alias.\n
			// Schema loading of the target Alias may be errorneous. Please check your target Alias's Schema properties.]
			Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("AliasWindowFactory.schemaPropsCopiedWarning"));
		}

		Main.getApplication().getPluginManager().aliasCopied(alias, newAlias);
		final AliasInternalFrame sheet = new AliasInternalFrame(Main.getApplication(), newAlias, COPY);
		Main.getApplication().getMainFrame().addWidget(sheet);
		DialogWidget.centerWithinDesktop(sheet);

		initAliasWidgetListener(sheet);

		return sheet;
	}

}
