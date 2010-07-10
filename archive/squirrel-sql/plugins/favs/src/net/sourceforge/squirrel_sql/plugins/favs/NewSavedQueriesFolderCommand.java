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
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;

public class NewSavedQueriesFolderCommand {
	private IApplication _app;
	private QueryTree _tree;
	private TreePath _path;
	public NewSavedQueriesFolderCommand(IApplication app, QueryTree tree,
											TreePath path)
			throws IllegalArgumentException {
		super();
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (tree == null) {
			throw new IllegalArgumentException("Null QueryTree passed");
		}
		//if (path == null) {
		//  throw new IllegalArgumentException("Null TreePath passed");
		//}

		_app = app;
		_tree = tree;
		_path = path;
	}
	public void execute() {
		FolderNode rootNode = (FolderNode)_tree.getModel().getRoot();
		FolderNode parentNode = null;
		if (_path == null) {
			parentNode = rootNode;
		} else {
			Object obj = _path.getLastPathComponent();
			if (obj == null) {
				parentNode = rootNode;
			} else if (obj instanceof FolderNode) {
				parentNode = (FolderNode)obj;
			}
		}
		if (parentNode != null) {
			final Folder folder = new Folder(null, "New Folder"); // ?? i18n
			final FolderNode newNode = new FolderNode(folder);
			parentNode.getFolder().addSubFolder(folder);
			parentNode.add(newNode);
			_tree.getTypedModel().nodeStructureChanged(parentNode);

			TreePath newNodePath = null;
			if (_path != null) {
				newNodePath = _path.pathByAddingChild(newNode);
			} else {
				newNodePath = new TreePath(new FolderNode[] {rootNode, newNode});
			}
			if (newNodePath != null) {
				_tree.makeVisible(newNodePath);
				_tree.expandPath(newNodePath);
				_tree.startEditingAtPath(newNodePath);
			}
		}
	}
}