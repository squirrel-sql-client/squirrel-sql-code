package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
/*
 * Copyright (C) 2003-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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
import javax.swing.BorderFactory;
import javax.swing.JDialog;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
/**
 * This dialog allows filtering of the onjects displayed in the object tree.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreeFilterDialog extends JDialog
{
	public ObjectTreeFilterDialog(IApplication app, IObjectTreeAPI tree)
	{
		super(app.getMainFrame(), "Filtering", true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		createGUI(app, tree);
	}

	private void createGUI(IApplication app, IObjectTreeAPI tree)
	{
		final ObjectTreeFilterPanel contentPane = new ObjectTreeFilterPanel(tree);
		setContentPane(contentPane);
		contentPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(true);
	}
}
