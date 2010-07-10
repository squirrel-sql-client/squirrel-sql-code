package net.sourceforge.squirrel_sql.plugins.favs;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.client.IApplication;

class OrganizeSavedQueriesDialog extends JDialog {
	private IApplication _app;
	private FoldersCache _cache;

	private JSplitPane _mainSplitPane = new JSplitPane();
	private QueryTree _queryTree;// = new QueryTree();
	private static interface i18n {
		String TITLE = "Saved Queries";
	}

	public OrganizeSavedQueriesDialog(IApplication app, FoldersCache cache, Frame owner)
			throws IllegalArgumentException {
		super(owner, i18n.TITLE, true);
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (cache == null) {
			throw new IllegalArgumentException("Null FoldersCache passed");
		}
		_app = app;
		_cache = cache;
		createUserInterface();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private void createUserInterface() {
		final Container contentPane = getContentPane();
		setVisible(false);

		_queryTree = new QueryTree(_app, _cache);
		_mainSplitPane.setOneTouchExpandable(true);
		_mainSplitPane.setContinuousLayout(true);

		_queryTree.setPreferredSize(new Dimension(200, 200));
		_mainSplitPane.add(new JScrollPane(_queryTree), JSplitPane.LEFT);
//	  _mainSplitPane.add(getDesktopPane(), JSplitPane.RIGHT);
		contentPane.setLayout(new BorderLayout());
		contentPane.add(_mainSplitPane, BorderLayout.CENTER);

		//setBounds(new Rectangle(600, 400));
		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(false);
	}
}

