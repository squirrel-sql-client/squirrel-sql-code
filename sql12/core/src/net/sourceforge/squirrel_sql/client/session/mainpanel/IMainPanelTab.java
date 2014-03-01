package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.awt.Component;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * This interface defines the behaviour for a tab in the main tabbed panel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IMainPanelTab {
	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	String getTitle();

   /**
    *
    * @return if != null the return component is displayed instead of the title
    */
   Component getTabComponent();


	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	String getHint();

	/**
	 * Return the component to be displayed in the panel.
	 *
	 * @return	The component to be displayed in the panel.
	 */
	Component getComponent();

	/**
	 * Set the current session.
	 *
	 * @param	session		Current session.
	 */
	void setSession(ISession session);

	/**
	 * The current session is closing.
	 *
	 * @param	session		Current session.
	 */
	void sessionClosing(ISession session);

	/**
	 * This tab has been selected.
	 */
	void select();
}

