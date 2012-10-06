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
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionTabWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.*;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class SQLInternalFrame extends SessionTabWidget
								implements ISQLInternalFrame
{
	/** Application API. */
	private final IApplication _app;

	private SQLPanel _sqlPanel;
	/** Toolbar for window. */
	private SQLToolBar _toolBar;

	private StatusBar _statusBar = new StatusBar();

	public SQLInternalFrame(ISession session)
	{
		super(session.getTitle(), true, true, true, true, session);
		_app = session.getApplication();
		setVisible(false);
		createGUI(session);
	}

	public SQLPanel getSQLPanel()
	{
		return _sqlPanel;
	}

	public ISQLPanelAPI getSQLPanelAPI()
	{
		return _sqlPanel.getSQLPanelAPI();
	}

	private void createGUI(ISession session)
	{
		setVisible(false);
		final IApplication app = session.getApplication();
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
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  _sqlPanel.getSQLEntryPanel().getTextComponent().requestFocus();
               }
            });
			}

         public void widgetClosing(WidgetEvent e)
         {
            _sqlPanel.sessionWindowClosing();
         }
		});

		_sqlPanel = new SQLPanel(getSession(), false);


		// Needed to make the panel set the divider location from preferences
      _sqlPanel.setVisible(true);

		_toolBar = new SQLToolBar(getSession(), _sqlPanel.getSQLPanelAPI());
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(_toolBar, BorderLayout.NORTH);
		contentPanel.add(_sqlPanel, BorderLayout.CENTER);

		Font fn = app.getFontInfoStore().getStatusBarFontInfo().createFont();
		_statusBar.setFont(fn);
		contentPanel.add(_statusBar, BorderLayout.SOUTH);

		RowColumnLabel lblRowCol = new RowColumnLabel(_sqlPanel.getSQLEntryPanel());
		_statusBar.addJComponent(lblRowCol);

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
      getSQLPanelAPI().addToToolsPopUp(selectionString, action);
   }

   public boolean hasSQLPanelAPI()
   {
      return true;
   }


   /** The class representing the toolbar at the top of a sql internal frame*/
	private class SQLToolBar extends ToolBar
	{
		SQLToolBar(ISession session, ISQLPanelAPI panel)
		{
			super();
			createGUI(session, panel);
		}

		private void createGUI(ISession session, ISQLPanelAPI panel)
		{
			ActionCollection actions = session.getApplication().getActionCollection();
			setUseRolloverButtons(true);
			setFloatable(false);
			add(actions.get(ExecuteSqlAction.class));
			addSeparator();
			add(actions.get(FileNewAction.class));
			add(actions.get(FileOpenAction.class));
			add(actions.get(FileAppendAction.class));
			add(actions.get(FileSaveAction.class));
			add(actions.get(FileSaveAsAction.class));
            add(actions.get(FilePrintAction.class));
			add(actions.get(FileCloseAction.class));
			addSeparator();
			add(actions.get(PreviousSqlAction.class));
			add(actions.get(NextSqlAction.class));
			add(actions.get(SelectSqlAction.class));
		}
	}


   @Override
   public void dispose()
   {
      super.dispose();    //To change body of overridden methods use File | Settings | File Templates.
   }

   @Override
   public void moveToFront()
   {
      super.moveToFront();
      _sqlPanel.getSQLEntryPanel().requestFocus();
   }
}