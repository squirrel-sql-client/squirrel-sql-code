package net.sourceforge.squirrel_sql.client.gui;

import java.awt.Dimension;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack;

/*
 * Copyright (C) 2010 Rob Manning
 * manningr@users.sourceforge.net
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

public class ProgressCallBackDialogTestUI
{

	private static IProgressCallBackFactory progressCallBackFactory = new ProgressCallBackFactory(); 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		ApplicationArguments.initialize(new String[] {});
		String[] tables = new String[] { "table_a", "table_b", "table_c", "table_d", "table_e", };
		JFrame parent = new JFrame();
		GUIUtils.centerWithinScreen(parent);
		parent.setSize(new Dimension(200, 200));
		parent.setVisible(true);
		ProgressCallBack pcb = progressCallBackFactory.create(parent, "test", 5);

		pcb.setVisible(true);
		for (int i = 0; i < 5; i++)
		{
			pcb.currentlyLoading(tables[i]);
			Thread.sleep(1000);
		}
		System.exit(0);
	}

}
