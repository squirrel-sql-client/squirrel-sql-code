package net.sourceforge.squirrel_sql.plugins.sqlscript;
/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;

import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class LoadScriptCommand implements ICommand {
    /** Parent frame. */
    private final Frame _frame;

    /** The session that we are saving a script for. */
    private final ISession _session;

    /** The current plugin. */
    private SQLScriptPlugin _plugin;

    /**
     * Ctor.
     *
     * @param   frame   Parent Frame.
     * @param   session The session that we are loading a script for.
     * @param   plugin  The current plugin.
     *
     * @throws  IllegalArgumentException
     *              Thrown if a <TT>null</TT> <TT>ISession</TT> or <TT>IPlugin</TT>
     *              passed.
     */
    public LoadScriptCommand(Frame frame, ISession session, SQLScriptPlugin plugin) {
        super();
        if (session == null) {
            throw new IllegalArgumentException("Null ISession passed");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Null IPlugin passed");
        }

        _frame = frame;
        _session = session;
        _plugin = plugin;
    }

    /**
     * Display the properties dialog.
     */
    public void execute() {
        if (_session != null) {
            JFileChooser chooser = new JFileChooser();
            chooser.addChoosableFileFilter(new FileExtensionFilter("Text files", new String[] {".txt"}));
            chooser.addChoosableFileFilter(new FileExtensionFilter("SQL files", new String[] {".sql"}));

            SQLScriptPreferences prefs = _plugin.getPreferences();

            if (prefs.getOpenInPreviousDirectory()) {
                String fileName = (String)_session.getPluginObject(_plugin, ISessionKeys.SAVE_SCRIPT_FILE_PATH_KEY);
                if (fileName != null) {
                    chooser.setCurrentDirectory(new File(fileName).getParentFile());
                }
            } else {
                String dirName = prefs.getSpecifiedDirectory();
                if (dirName != null) {
                    chooser.setCurrentDirectory(new File(dirName));
                }
            }
             _session.selectMainTab(ISession.IMainTabIndexes.SQL_TAB);
            if(chooser.showOpenDialog(_frame) == chooser.APPROVE_OPTION) {
                loadScript(chooser.getSelectedFile());
            }
        }
    }

    private void loadScript(File file) {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            StringBuffer sb = new StringBuffer();
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            byte[] bytes = new byte[2048];
            int iRead = bis.read(bytes);
            while(iRead != -1) {
                sb.append(new String(bytes,0,iRead));
                iRead = bis.read(bytes);
            }
            _session.setSQLScript(sb.toString());
            _session.putPluginObject(_plugin, ISessionKeys.SAVE_SCRIPT_FILE_PATH_KEY, file.getAbsolutePath());
        } catch(java.io.IOException io) {
            _session.getMessageHandler().showMessage(io);
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch(IOException ignore){
            }
            try {
                fis.close();
            } catch(IOException io){
            }
        }
    }
}
