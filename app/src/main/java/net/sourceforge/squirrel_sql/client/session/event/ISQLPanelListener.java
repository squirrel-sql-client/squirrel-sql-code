package net.sourceforge.squirrel_sql.client.session.event;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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
import java.util.EventListener;
/**
 * This listener is called whenever an event occurs in the SQL panel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface ISQLPanelListener extends EventListener
{
	/**
	 * The SQL entry area control has been installed.
	 * 
	 * @param evt	Object describing this event.
	 */
	void sqlEntryAreaInstalled(SQLPanelEvent evt);

   void sqlEntryAreaClosed(SQLPanelEvent evt);
}