package net.sourceforge.squirrel_sql.plugins.savedqueries;
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
import java.awt.Frame;
import java.text.MessageFormat;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Logger;

import net.sourceforge.squirrel_sql.client.db.AliasMaintDialog;
import net.sourceforge.squirrel_sql.client.db.DataCache;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

public class DeleteSavedQueriesFolderCommand {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String MSG_CONFIRM = "Are you sure to want to delete the folder \"{0}\" and all of its contents?";
    }

    private Frame _frame;
    private QueryTree _tree;
    private TreePath _path;

    public DeleteSavedQueriesFolderCommand(Frame frame, QueryTree tree, TreePath path) {
        super();
        _frame = frame;
        _tree = tree;
        _path = path;
    }

    public void execute() {
        if (_path != null) {
            Object obj = _path.getLastPathComponent();
            if (obj instanceof FolderNode) {
                FolderNode nodeToDelete = (FolderNode)obj;
                Object[] args = {nodeToDelete.getName()};
                String msg = MessageFormat.format(i18n.MSG_CONFIRM, args);
                if (Dialogs.showYesNo(_frame, msg)) {
                    TreeNode parentNode = nodeToDelete.getParent();
                    if (parentNode instanceof FolderNode) {
                        FolderNode parentFolder = (FolderNode)parentNode;
                        parentFolder.remove(nodeToDelete);
                        parentFolder.getFolder().removeSubFolder(nodeToDelete.getFolder());
                        _tree.getTypedModel().nodeStructureChanged(parentFolder);
                    }
                }
            }
        }
    }
}