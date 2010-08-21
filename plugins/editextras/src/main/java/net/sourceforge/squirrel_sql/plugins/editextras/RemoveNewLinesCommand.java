package net.sourceforge.squirrel_sql.plugins.editextras;

/*
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
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

/**
 * This command will remove &quot;newline&quot; characters from an SQL string.
 * 
 */
class RemoveNewLinesCommand implements ICommand {
    private final ISQLPanelAPI _api;

    RemoveNewLinesCommand(ISQLPanelAPI api) {
        super();
        _api = api;
    }

    public void execute() throws BaseException {
        int[] bounds = _api.getSQLEntryPanel().getBoundsOfSQLToBeExecuted();

        if (bounds[0] == bounds[1]) {
            return;
        }

        String textToChange = _api.getSQLEntryPanel().getSQLToBeExecuted();

        if (null == textToChange) {
            return;
        }

        String[] parts = textToChange.split("\n");
        String newText = StringUtilities.join(parts, null);

        _api.getSQLEntryPanel().setSelectionStart(bounds[0]);
        _api.getSQLEntryPanel().setSelectionEnd(bounds[1]);
        _api.getSQLEntryPanel().replaceSelection(newText);
    }
}
