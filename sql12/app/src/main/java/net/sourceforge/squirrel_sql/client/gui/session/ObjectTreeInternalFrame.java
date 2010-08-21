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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionTabWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.RefreshSchemaInfoAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.fw.gui.SQLCatalogsComboBox;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/* Object Tree frame class*/
public class ObjectTreeInternalFrame extends SessionTabWidget
										implements IObjectTreeInternalFrame
{
	/** Application API. */
	private final IApplication _app;

	private ObjectTreePanel _objTreePanel;

	/** Toolbar for window. */
	private ObjectTreeToolBar _toolBar;

	private boolean _hasBeenVisible = false;

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ObjectTreeInternalFrame.class);            
    
	public ObjectTreeInternalFrame(ISession session)
	{
		super(session.getTitle(), true, true, true, true, session);
		_app = session.getApplication();
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

		_objTreePanel = new ObjectTreePanel(getSession());
		_objTreePanel.addTreeSelectionListener(new ObjectTreeSelectionListener());
		_toolBar = new ObjectTreeToolBar(getSession(), _objTreePanel);
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(_toolBar, BorderLayout.NORTH);
		contentPanel.add(_objTreePanel, BorderLayout.CENTER);
		setContentPane(contentPanel);
		validate();
	}

   public boolean hasSQLPanelAPI()
   {
      return false; 
   }

   /** The class representing the toolbar at the top of a sql internal frame*/
	private class ObjectTreeToolBar extends ToolBar
	{
		/** Internationalized strings for this class. */
		private final StringManager s_stringMgr = StringManagerFactory.getStringManager(ObjectTreeToolBar.class);
		private ILogger s_log = LoggerController.createLogger(ObjectTreeToolBar.class);
      private CatalogsPanel _catalogsPanel;

      ObjectTreeToolBar(ISession session, ObjectTreePanel panel)
      {
         super();
         createGUI(session, panel);
      }

		private void createGUI(ISession session, ObjectTreePanel panel)
		{
         _catalogsPanel = new CatalogsPanel(session, this);
         _catalogsPanel.addActionListener(new CatalogsComboListener());
         add(_catalogsPanel);

         ActionCollection actions = session.getApplication()
					.getActionCollection();
			setUseRolloverButtons(true);
			setFloatable(false);
			add(actions.get(RefreshSchemaInfoAction.class));
		}
	}

	private final class CatalogsComboListener implements ActionListener
	{
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
						getSession().getSQLConnection().setCatalog(catalog);
					}
					catch (SQLException ex)
					{
						getSession().showErrorMessage(ex);
					}
				}
			}
		}
	}

   /** JASON: this could be added to the objecttreepanel if the status bar was attached
    *  to the application
    */
   private final class ObjectTreeSelectionListener
         implements
            TreeSelectionListener
   {
      public void valueChanged(TreeSelectionEvent evt)
      {
         final TreePath selPath = evt.getNewLeadSelectionPath();
         if (selPath != null)
         {
            StringBuffer buf = new StringBuffer();
            Object[] fullPath = selPath.getPath();
            for (int i = 0; i < fullPath.length; ++i)
            {
               if (fullPath[i] instanceof ObjectTreeNode)
               {
                  ObjectTreeNode node = (ObjectTreeNode)fullPath[i];
                  buf.append('/').append(node.toString());
               }
            }
            //JASON: have a main application status bar setStatusBarMessage(buf.toString());
         }
      }
   }
}
