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

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;

final class FolderNode extends BaseNode {
    private Folder _folder;
    private String _identifier;
    private String _name;

    FolderNode(Folder folder) throws IllegalArgumentException {
        super(folder != null ? folder.getName() : "?", true);
        if (folder == null) {
            throw new IllegalArgumentException("Null Folder passed");
        }
        _folder = folder;
    }

    public boolean isLeaf() {
        return false;
    }

    Folder getFolder() {
        return _folder;
    }

    String getName() {
        return _folder.getName();
    }

    void setName(String name) throws ValidationException {
        _folder.setName(name);
    }
}
