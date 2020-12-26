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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

/**
 * This window shows all the database aliases defined in the system.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AliasesListInternalFrame extends BaseListInternalFrame
{

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasesListInternalFrame.class);

	private IApplication _app;

	/** User Interface facory. */
	private AliasUserInterfaceFactory _uiFactory;

	public AliasesListInternalFrame(IApplication app, AliasesList list)
	{
		super(new AliasUserInterfaceFactory(list));
		_app = app;
		_uiFactory = (AliasUserInterfaceFactory)getUserInterfaceFactory();


      addVetoableChangeListener(new VetoableChangeListener()
      {
         public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException
         {
            if(JInternalFrame.IS_CLOSED_PROPERTY.equals(evt.getPropertyName()) && Boolean.TRUE.equals(evt.getNewValue()))
            {
               nowVisible(true);
                // i18n[AliasesListInternalFrame.error.ctrlF4key=Probably closed by the ctrl F4 key. See BasicDesktopPaneUi.CloseAction]
               throw new PropertyVetoException(s_stringMgr.getString("AliasesListInternalFrame.error.ctrlF4key"), evt);
            }
         }
      });

      addWidgetListener(new WidgetAdapter()
      {
         @Override
         public void widgetOpened(WidgetEvent evt)
         {
            nowVisible(true);
         }

         @Override
         public boolean widgetClosing(WidgetEvent evt)
         {
            nowVisible(false);
            return true;
         }

         @Override
         public void widgetClosed(WidgetEvent evt)
         {
            nowVisible(false);
         }
      });


      _app.getSquirrelPreferences().addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				final String propName = evt != null ? evt.getPropertyName() : null;
				if (propName == null
					|| propName.equals(SquirrelPreferences.IPropertyNames.SHOW_ALIASES_TOOL_BAR))
				{
					boolean show = _app.getSquirrelPreferences().getShowAliasesToolBar();
					if (show)
					{
						_uiFactory.createToolBar();
					}
					else
					{
						_uiFactory.removeToolbar();
					}
					setToolBar(_uiFactory.getToolBar());
				}
			}
		});


      addFocusListener(new FocusAdapter()
      {
         public void focusGained(FocusEvent e)
         {
            _uiFactory.getAliasesList().requestFocus();
         }

      });

   }

   public AliasesList getAliasesList()
	{
		return _uiFactory.getAliasesList();
	}

   public void nowVisible(final boolean b)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _app.getMainFrame().setEnabledAliasesMenu(b);
            _uiFactory.getAliasesList().requestFocus();
         }
      });
   }

   public void enableDisableActions()
   {
      _uiFactory.enableDisableActions();
   }

   public boolean isEmpty()
   {
      return _uiFactory.getAliasesList().isEmpty();
   }


}
