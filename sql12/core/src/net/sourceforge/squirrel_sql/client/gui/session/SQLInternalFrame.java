package net.sourceforge.squirrel_sql.client.gui.session;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Copyright (C) 2003 Jason Height
 * jmheight@users.sourceforge.net
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionTabWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.gui.session.rowcolumnlabel.RowColumnLabel;
import net.sourceforge.squirrel_sql.client.session.ISQLInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanelPosition;
import net.sourceforge.squirrel_sql.fw.gui.statusbar.SessionStatusBar;

import javax.swing.*;
import java.awt.*;

public class SQLInternalFrame extends SessionTabWidget implements ISQLInternalFrame
{

	private SQLPanel _sqlPanel;

	private SQLInternalFrameToolBar _toolBar;

	private SessionStatusBar _sessionStatusBar = new SessionStatusBar();

	public SQLInternalFrame(ISession session)
	{
		super(session.getTitleModificationAware(), true, true, true, true, session);
		setVisible(false);
		createGUI(session);
	}

	public SQLPanel getSQLPanel()
	{
		return _sqlPanel;
	}

	public ISQLPanelAPI getMainSQLPanelAPI()
	{
		return _sqlPanel.getSQLPanelAPI();
	}

	private void createGUI(ISession session)
	{
      _sqlPanel = new SQLPanel(session, SQLPanelPosition.IN_SQL_WORKSHEET, super.getTitleFileHandler());

		setVisible(false);
		final IApplication app = Main.getApplication();
		Icon icon = app.getResources().getIcon(getClass(), "frameIcon"); //i18n
		if (icon != null)
		{
			setFrameIcon(icon);
		}

		// This is to fix a problem with the JDK (up to version 1.3)
		// where focus events were not generated correctly. The sympton
		// is being unable to key into the text entry field unless you click
		// elsewhere after focus is gained by the internal frame.
		// See bug ID 4309079 on the JavaSoft bug parade (plus others).
		addWidgetListener(new WidgetAdapter()
		{
			public void widgetActivated(WidgetEvent evt)
			{
				// Removing this fixed the following bug: When a "SQL Worksheet" was moved to an extra window
				// several functions (first and foremost editing) in SQuirreL's main window didn't work anymore.
            //SwingUtilities.invokeLater(new Runnable()
            //{
            //   public void run()
            //   {
            //      _sqlPanel.getSQLEntryPanel().getTextComponent().requestFocus();
            //   }
            //});
			}

         public boolean widgetClosing(WidgetEvent e)
         {
				// When the Session itself is closing confirms are handled by SessionManager.confirmClose()
				if (false == Main.getApplication().getSessionManager().isInCloseSession(session))
				{

					if(false == _sqlPanel.getSQLPanelAPI().confirmClose())
					{
						return false;
					}
				}

				_sqlPanel.sessionWorksheetOrTabClosing();
            return true;
         }
		});

      _sqlPanel.storeSplitPanePositionOnSessionClose(true);

		_toolBar = new SQLInternalFrameToolBar(getSession(), _sqlPanel.getSQLPanelAPI());
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(_toolBar, BorderLayout.NORTH);
		contentPanel.add(_sqlPanel.getSqlPanelSplitter(), BorderLayout.CENTER);

		app.getFontInfoStore().setUpStatusBarFont(_sessionStatusBar);
		contentPanel.add(_sessionStatusBar, BorderLayout.SOUTH);

		_sessionStatusBar.addJComponent(new SchemaPanel(session));
		_sessionStatusBar.addJComponent(new RowColumnLabel(_sqlPanel.getSQLEntryPanel()));


		SessionColoringUtil.colorStatusbar(session, _sessionStatusBar);

		setContentPane(contentPanel);
		validate();
	}


   public void requestFocus()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _sqlPanel.getSQLEntryPanel().requestFocus();
         }
      });

   }

   public void addSeparatorToToolbar()
   {
      if (null != _toolBar)
      {
         _toolBar.addSeparator();
      }
   }

   public void addToToolbar(Action action)
   {
      if (null != _toolBar)
      {
         _toolBar.add(action);
      }
   }

   public void addToToolsPopUp(String selectionString, Action action)
   {
      getMainSQLPanelAPI().addToToolsPopUp(selectionString, action);
   }

   public boolean hasSQLPanelAPI()
   {
      return true;
   }


	@Override
	public void dispose()
	{
		super.dispose();
	}

	@Override
	public void moveToFront()
	{
		super.moveToFront();
		_sqlPanel.getSQLEntryPanel().requestFocus();
	}

}