package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2004 Colin Bell
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
 import javax.swing.JComboBox;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
 
public class ReadTypeCombo extends JComboBox
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ReadTypeCombo.class);

	static final int READ_ALL_IDX = 1;
	static final int READ_PARTIAL_IDX = 0;

	public ReadTypeCombo()
	{
		addItem(s_stringMgr.getString("ReadTypeCombo.onlyFirst"));
		addItem(s_stringMgr.getString("ReadTypeCombo.all"));
	}
}