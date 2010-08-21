package net.sourceforge.squirrel_sql.client.session.event;
/*
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
import java.util.EventListener;
/**
 * This listener is called for events to do with the SQL executer tabs.
 *
 */
public interface ISQLResultExecuterTabListener extends EventListener
{
	/**
	 * An SQL executer tab has been added to the tabbed folder.
	 *
	 * @param	evt		The event.
	 */
	public void executerTabAdded(SQLResultExecuterTabEvent evt);

	/**
	 * An SQL executer tab has been removed from the tabbed folder.
	 *
	 * @param	evt		The event.
	 */
	public void executerTabRemoved(SQLResultExecuterTabEvent evt);

	/**
	 * An SQL executer tab has been selected causing it to become the active tab.
	 *
	 * @param	evt		The event.
	 */
	public void executerTabActivated(SQLResultExecuterTabEvent evt);
}