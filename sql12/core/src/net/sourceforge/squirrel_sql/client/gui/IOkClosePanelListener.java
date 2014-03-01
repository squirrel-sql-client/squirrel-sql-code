package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
 * Listener for events in the <TT>OkClosepanel</TT> class.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IOkClosePanelListener extends EventListener
{
	/**
	 * The OK button was pressed.
	 *
	 * @param	evt		Describes this event.
	 */
	void okPressed(OkClosePanelEvent evt);

	/**
	 * The Close button was pressed.
	 *
	 * @param	evt		Describes this event.
	 */
	void closePressed(OkClosePanelEvent evt);

	/**
	 * The Cancel button was pressed.
	 *
	 * @param	evt		Describes this event.
	 */
	void cancelPressed(OkClosePanelEvent evt);
}
