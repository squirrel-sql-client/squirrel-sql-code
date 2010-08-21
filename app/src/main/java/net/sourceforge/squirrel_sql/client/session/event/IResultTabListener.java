package net.sourceforge.squirrel_sql.client.session.event;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.util.EventListener;

/**
 * This listener is called for events to do with the SQL result tabs.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IResultTabListener extends EventListener
{
	/**
	 * An SQL results tab has been added to the tabbed folder.
	 * 
	 * @param	evt		The event.
	 */
	void resultTabAdded(ResultTabEvent evt);

	/**
	 * An SQL results tab has been removed from the tabbed folder.
	 * 
	 * @param	evt		The event.
	 */
	void resultTabRemoved(ResultTabEvent evt);

	/**
	 * An SQL results tab has been "torn off" from the tabbed folder into
	 * its own frame.
	 * 
	 * @param	evt		The event.
	 */
	void resultTabTornOff(ResultTabEvent evt);

	/**
	 * A "torn off" SQL results tab has been returned to the tabbed folder.
	 * 
	 * @param	evt		The event.
	 */
	void tornOffResultTabReturned(ResultTabEvent evt);
}
