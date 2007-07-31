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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;

abstract class BaseFavouriteAction extends SquirrelAction {
	private QueryTree _tree;
	private TreePath _path;

	protected BaseFavouriteAction(IApplication app, Resources rsrc) {
		super(app, rsrc);
	}
//?? Split these 4 functions off into a subclass of BaseFavouriteAction
	void setQueryTree(QueryTree value) {
		_tree = value;
	}

//?? Split these 4 functions off into a subclass of BaseFavouriteAction
	void setTreePath(TreePath value) {
		_path = value;
	}

//?? Split these 4 functions off into a subclass of BaseFavouriteAction
	QueryTree getQueryTree() {
		return _tree;
	}

//?? Split these 4 functions off into a subclass of BaseFavouriteAction
	TreePath getTreePath() {
		return _path;
	}
}