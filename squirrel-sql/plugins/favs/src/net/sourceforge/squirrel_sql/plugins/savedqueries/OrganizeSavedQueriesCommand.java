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

import net.sourceforge.squirrel_sql.client.IApplication;

public class OrganizeSavedQueriesCommand {
    private IApplication _app;
    private FoldersCache _cache;
    private Frame _frame;

    public OrganizeSavedQueriesCommand(IApplication app, FoldersCache cache,
                                        Frame frame)
            throws IllegalArgumentException {
        super();
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        if (cache == null) {
            throw new IllegalArgumentException("Null FoldersCache passed");
        }

        _app = app;
        _cache = cache;
        _frame = frame;
    }

    public void execute() {
        new OrganizeSavedQueriesDialog(_app, _cache, _frame).show();
    }
}