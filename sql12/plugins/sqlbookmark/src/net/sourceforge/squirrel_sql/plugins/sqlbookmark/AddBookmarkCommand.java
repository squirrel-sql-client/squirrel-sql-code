/*
 * Copyright (C) 2003 Joseph Mocker
 * mock-sf@misfit.dhs.org
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

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.awt.Frame;
import java.io.IOException;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Initiate the addition of a new SQL Bookmark.
 *
 * The SQL for the bookmark is taken from the current SQL Edit buffer.
 * The user is prompted for a name for the bookmark.
 *
 * @author      Joseph Mocker
 **/
public class AddBookmarkCommand implements ICommand {
    private interface IAddKeys {
	String BM_TITLE = "dialog.add.title";
    }

    private static ILogger logger = 
	LoggerController.createLogger(AddBookmarkCommand.class);

    /** Parent frame. */
    private final Frame frame;

    /** The session that we are saving a script for. */
    private final ISession session;

    /** The current plugin. */
    private SQLBookmarkPlugin plugin;

    /**
     * Ctor.
     *
     * @param   frame   Parent Frame.
     * @param   session The session that we are saving a script for.
     * @param   plugin  The current plugin.
     *
     * @throws  IllegalArgumentException
     *          Thrown if a <TT>null</TT> <TT>ISession</TT> or <TT>IPlugin</TT>
     *          passed.
     */
    public AddBookmarkCommand(Frame frame, ISession session, SQLBookmarkPlugin plugin)
        throws IllegalArgumentException {
        super();
        if (session == null) {
            throw new IllegalArgumentException("Null ISession passed");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Null IPlugin passed");
        }
        this.frame = frame;
        this.session = session;
        this.plugin = plugin;
    }

    /**
     * Execute the addition of the bookmark.
     */
    public void execute() {
        if (session != null) {
	    String name = 
		JOptionPane.showInputDialog(frame, plugin.getResourceString(IAddKeys.BM_TITLE));
	    
	    if (name == null || name.length() == 0)
		return;
	    
	    String sql = session.getSQLPanelAPI(plugin).getEntireSQLScript();
	    
	    logger.info("bookmark name: " + name);
	    logger.info("bookmark sql: " + sql);

	    Bookmark bookmark = new Bookmark(name, sql);
	    
	    if (!plugin.getBookmarkManager().add(bookmark))
		plugin.addBookmarkItem(bookmark);

	    try {
		plugin.getBookmarkManager().save();
	    }
	    catch (IOException e) {
		logger.error("Problem saving bookmarks", e);
	    }
        }
    }

}
