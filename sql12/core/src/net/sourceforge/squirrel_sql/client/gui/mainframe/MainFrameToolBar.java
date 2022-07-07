package net.sourceforge.squirrel_sql.client.gui.mainframe;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasPopUpMenuAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CascadeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.GlobalPreferencesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.MaximizeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.NewSessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.SessionPopUpMenuAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileHorizontalAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileVerticalAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.findprefs.FindInPreferencesAction;
import net.sourceforge.squirrel_sql.client.session.action.CommitAction;
import net.sourceforge.squirrel_sql.client.session.action.NewAliasConnectionAction;
import net.sourceforge.squirrel_sql.client.session.action.NewObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.RollbackAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleAutoCommitAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileSaveAllAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.GitCommitSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionManageAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionOpenAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionSaveAction;
import net.sourceforge.squirrel_sql.client.session.action.worksheettypechoice.NewSQLWorksheetAction;
import net.sourceforge.squirrel_sql.client.session.action.worksheettypechoice.SQLWorksheetTypeChooser;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * Toolbar for <CODE>MainFrame</CODE>.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MainFrameToolBar extends ToolBar
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MainFrameToolBar.class);

   MainFrameToolBar()
   {
      setUseRolloverButtons(true);
      setFloatable(true);

      ActionCollection actions = Main.getApplication().getActionCollection();
		add(actions.get(AliasPopUpMenuAction.class));
      addSeparator();
      add(actions.get(GlobalPreferencesAction.class));
      add(actions.get(NewSessionPropertiesAction.class));
      add(actions.get(FindInPreferencesAction.class));
      if (Main.getApplication().getDesktopStyle().isInternalFrameStyle())
      {
         addSeparator();
         add(actions.get(TileAction.class));
         add(actions.get(TileHorizontalAction.class));
         add(actions.get(TileVerticalAction.class));
         add(actions.get(CascadeAction.class));
         add(actions.get(MaximizeAction.class));
         addSeparator();
      }

      addSeparator();
      add(actions.get(SessionPopUpMenuAction.class));

      addSeparator();
      addToggleAction((IToggleAction) actions.get(ToggleAutoCommitAction.class));
      add(actions.get(CommitAction.class));
      add(actions.get(RollbackAction.class));

      addSeparator();
		//add(actions.get(NewSQLWorksheetAction.class));
		add(new SQLWorksheetTypeChooser((NewSQLWorksheetAction) actions.get(NewSQLWorksheetAction.class)).getComponent());

		add(actions.get(NewObjectTreeAction.class));
      add(actions.get(NewAliasConnectionAction.class));
      add(actions.get(FileSaveAllAction.class));

		addSeparator();
		add(actions.get(SessionSaveAction.class));
		add(actions.get(GitCommitSessionAction.class));
		add(actions.get(SessionOpenAction.class));
		add(actions.get(SessionManageAction.class));
	}
}

