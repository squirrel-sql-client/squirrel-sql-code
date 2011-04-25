/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.gui.action.wikiTable;

import javax.swing.JTable;

/**
 * A <tt>IWikiTableTransformer</tt> transforms the selected area of a {@link JTable} into a WIKI table.
 * An IWikiTableTransformer should always be created by a {@link IWikiTableConfiguration}.
 * @see IWikiTableConfiguration
 * @see IWikiTableConfiguration#createTransformer()
 * @author Stefan Willinger
 *
 */
public interface IWikiTableTransformer {
	
	/**
	 * Transform the selected area of the table into a WIKI table.
	 * @param table Table, which should be transformed into a WIKI table
	 * @return String, which represents a WIKI table.
	 */
	String transform(JTable table);
}
