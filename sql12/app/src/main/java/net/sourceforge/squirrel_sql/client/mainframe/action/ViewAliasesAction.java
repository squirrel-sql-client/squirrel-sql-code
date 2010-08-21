package net.sourceforge.squirrel_sql.client.mainframe.action;
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
import net.sourceforge.squirrel_sql.client.gui.db.AliasesListInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SelectWidgetAction;

import java.awt.event.ActionEvent;

/**
 * This <CODE>Action</CODE> displays the Aliases Tool Window.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ViewAliasesAction extends SelectWidgetAction
{
   private AliasesListInternalFrame m_window;

   /**
	 * Ctor specifying the Aliases Tool Window.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if <TT>null</TT> <TT>AliasesToolWindow</TT> passed.
	 */
	public ViewAliasesAction(IApplication app, AliasesListInternalFrame window)
	{
		super(window);
		if (window == null)
		{
			throw new IllegalArgumentException("null AliasesToolWindow passed");
		}
      m_window = window;

      app.getResources().setupAction(this, app.getSquirrelPreferences().getShowColoriconsInToolbar());
	}


   public void actionPerformed(ActionEvent evt)
   {
      super.actionPerformed(evt);

      if(null != m_window)
      {
         m_window.nowVisible(true);
      }
   }
}
