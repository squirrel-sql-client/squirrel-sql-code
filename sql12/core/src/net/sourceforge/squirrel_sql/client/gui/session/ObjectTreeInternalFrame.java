package net.sourceforge.squirrel_sql.client.gui.session;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Copyright (C) 2003-2004 Jason Height
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
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionTabWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ObjectTreePosition;
import net.sourceforge.squirrel_sql.client.session.action.FindColumnsAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshSchemaInfoAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.fw.gui.SQLCatalogsComboBox;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.sql.SQLException;

/* Object Tree frame class*/
public class ObjectTreeInternalFrame extends SessionTabWidget implements IObjectTreeInternalFrame
{
	private ObjectTreePanel _objTreePanel;

	private ObjectTreeToolBar _toolBar;

	private StatusBar _statusBar = new StatusBar();


	private boolean _hasBeenVisible = false;

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ObjectTreeInternalFrame.class);
    
	public ObjectTreeInternalFrame(ISession session)
	{
		super(session.getTitle(), true, true, true, true, session);
		setVisible(false);
		createGUI(session);
	}

	public void addNotify()
	{
		super.addNotify();
		if (!_hasBeenVisible)
		{
			_hasBeenVisible = true;
			// Done this late so that plugins have time to register expanders
			// with the object tree prior to it being built.
			_objTreePanel.refreshTree();
		}
	}

	public ObjectTreePanel getObjectTreePanel()
	{
		return _objTreePanel;
	}

	public IObjectTreeAPI getObjectTreeAPI()
	{
		return _objTreePanel;
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
				Window window = SwingUtilities.windowForComponent(ObjectTreeInternalFrame.this.getObjectTreePanel());
				Component focusOwner = (window != null) ? window.getFocusOwner() : null;
				if (focusOwner != null)
				{
					FocusEvent lost = new FocusEvent(focusOwner, FocusEvent.FOCUS_LOST);
					FocusEvent gained = new FocusEvent(focusOwner, FocusEvent.FOCUS_GAINED);
					window.dispatchEvent(lost);
					window.dispatchEvent(gained);
					window.dispatchEvent(lost);
					focusOwner.requestFocus();
				}
			}
		});

		_objTreePanel = new ObjectTreePanel(getSession(), ObjectTreePosition.OBJECT_TREE_INTERNAL_FRAME);
		_toolBar = new ObjectTreeToolBar(getSession(), _objTreePanel);
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(_toolBar, BorderLayout.NORTH);
		contentPanel.add(_objTreePanel, BorderLayout.CENTER);
		setContentPane(contentPanel);

		app.getFontInfoStore().setUpStatusBarFont(_statusBar);
		contentPanel.add(_statusBar, BorderLayout.SOUTH);

		_statusBar.addJComponent(new SchemaPanel(session));

		SessionColoringUtil.colorStatusbar(session, _statusBar);

		validate();
	}

	public boolean hasSQLPanelAPI()
   {
      return false; 
   }

   /** The class representing the toolbar at the top of a sql internal frame*/
	private static class ObjectTreeToolBar extends ToolBar
	{
      private CatalogsPanel _catalogsPanel;

      ObjectTreeToolBar(ISession session, ObjectTreePanel panel)
      {
         createGUI(session, panel);
			SessionColoringUtil.colorToolbar(session, this);
		}

		private void createGUI(ISession session, ObjectTreePanel panel)
		{
         _catalogsPanel = new CatalogsPanel(session, this);
         _catalogsPanel.addActionListener(new CatalogsComboListener(session));
         add(_catalogsPanel);

         ActionCollection actions = session.getApplication()
					.getActionCollection();
			setUseRolloverButtons(true);
			setFloatable(false);
			add(actions.get(RefreshSchemaInfoAction.class));
			add(actions.get(FindColumnsAction.class));
		}
	}

	private static final class CatalogsComboListener implements ActionListener
	{
		private ISession _session;

		public CatalogsComboListener(ISession session)
		{
			_session = session;
		}

		public void actionPerformed(ActionEvent evt)
		{
			Object src = evt.getSource();
			if (src instanceof SQLCatalogsComboBox)
			{
				SQLCatalogsComboBox cmb = (SQLCatalogsComboBox)src;
				String catalog = cmb.getSelectedCatalog();
				if (catalog != null)
				{
					try
					{
						_session.getSQLConnection().setCatalog(catalog);
					}
					catch (SQLException ex)
					{
						_session.showErrorMessage(ex);
					}
				}
			}
		}
	}

}
