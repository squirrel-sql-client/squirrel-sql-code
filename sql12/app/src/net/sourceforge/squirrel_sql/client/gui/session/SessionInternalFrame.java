package net.sourceforge.squirrel_sql.client.gui.session;
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
import java.awt.Component;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.beans.PropertyVetoException;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.Version;

public class SessionInternalFrame extends BaseSessionInternalFrame
					implements ISQLInternalFrame, IObjectTreeInternalFrame
{
    static final long serialVersionUID = 6961615570741567740L;
    
    /** Application API. */
	private final IApplication _app;

	private SessionPanel _sessionPanel;
    
	public SessionInternalFrame(ISession session)
	{
		super(session, session.getTitle(), true, true, true, true);
		_app = session.getApplication();
		setVisible(false);
		createGUI(session);
	}

	public SessionPanel getSessionPanel()
	{
		return _sessionPanel;
	}

	public ISQLPanelAPI getSQLPanelAPI()
	{
		return _sessionPanel.getSQLPaneAPI();
	}

	public IObjectTreeAPI getObjectTreeAPI()
	{
		return _sessionPanel.getObjectTreePanel();
	}

   /**
	 * Add the passed action to the toolbar of the sessions main window.
	 *
	 * @param	action	Action to be added.
	 */
	void addToToolbar(Action action)
   {
      _sessionPanel.addToToolbar(action);
   }

   public void addSeparatorToToolbar()
   {
      _sessionPanel.addSeparatorToToolbar();
   }

   public void addToToolsPopUp(String selectionString, Action action)
   {
      _sessionPanel.addToToolsPopUp(selectionString, action);
   }


	public void setSelected(boolean selected)
			throws PropertyVetoException
	{
		super.setSelected(selected);

		// Without this when using alt left/right to move
		// between sessions the focus is left in the SQL
		// entry area of the previous session.
		// TODO: Once Java 5 is minimum supported
		// we don't need this,
        if (Version.isJDK14())
        {
        	if (selected)
        	{
        		SwingUtilities.invokeLater(new Runnable()
        		{
        			public void run()
        			{
        				_sessionPanel.getSQLEntryPanel().requestFocus();
        			}
        		});
        	}
		}
	}

	private void createGUI(final ISession session)
	{
		setVisible(false);
		setDefaultCloseOperation(SessionInternalFrame.DO_NOTHING_ON_CLOSE);
		final IApplication app = session.getApplication();
		Icon icon = app.getResources().getIcon(getClass(), "frameIcon"); //i18n
		if (icon != null)
		{
			setFrameIcon(icon);
		}

		addInternalFrameListener(new InternalFrameAdapter()
		{
			public void internalFrameClosing(InternalFrameEvent evt)
			{
                if (!session.isfinishedLoading()) {                         
                    return;
                }
				final ISession mySession = getSession();
				if (mySession != null)
				{
               _sessionPanel.sessionWindowClosing();
					_app.getSessionManager().closeSession(mySession);
				}
			}
		});

		_sessionPanel = new SessionPanel(session);
		setContentPane(_sessionPanel);
		validate();
	}

   public void requestFocus()
   {
      _sessionPanel.getSQLEntryPanel().requestFocus();
   }

   public boolean hasSQLPanelAPI()
   {
      return true;
   }

}
