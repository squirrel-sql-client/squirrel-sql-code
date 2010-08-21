package net.sourceforge.squirrel_sql.client.session.sqlfilter;
/*
 * Copyright (C) 2003 Maury Hammel
 * mjhammel@users.sourceforge.net
 *
 * Adapted from ISessionPropertiesPanel.java by Colin Bell.
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
import net.sourceforge.squirrel_sql.client.util.IOptionPanel;
/**
 * This interface defines the behaviour expected of a SQL Filter panel.
 *
 * @author  <A HREF="mailto:mjhammel@users.sourceforge.net">Maury Hammel</A>
 */
public interface ISQLFilterPanel extends IOptionPanel
{
	/**
	 * Initialize panel for the specified sesion.
	 *
	 * @param	sqlFilterClauses	Session whose SQL Filter Information are
	 * 								being maintained.
	 */
	void initialize(SQLFilterClauses sqlFilterClauses);
}