/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A utility class that can be used to add the ability to drag a file from the 
 * desktop to a session sql editor panel.
 * 
 * @author manningr
 *
 */
public class FileEditorDropTargetListener extends DropTargetAdapter 
                                          implements DropTargetListener {

    /** Logger for this class. */
    private static final ILogger s_log = 
        LoggerController.createLogger(FileEditorDropTargetListener.class);    
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(FileEditorDropTargetListener.class); 
    
    private static interface i18n {
        //i18n[FileEditorDropTargetListener.oneFileDropMessage=Only one file 
        //may be dropped onto the editor at a time.]
        String ONE_FILE_DROP_MESSAGE = 
            s_stringMgr.getString("FileEditorDropTargetListener.oneFileDropMessage");
    }
    
    /** the session we are listening for drops into */
    private ISession _session;
    
    public FileEditorDropTargetListener(ISession session) {
        this._session = session;
    }
    
    /**
     * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
     */
    @SuppressWarnings("unchecked")
    public void drop(DropTargetDropEvent dtde) {
        try {
            DropTargetContext context = dtde.getDropTargetContext();
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            Transferable t = dtde.getTransferable();
            List<File> transferData = 
                (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
            if (transferData.size() > 1) {
                _session.showErrorMessage("Only one file may be dropped onto the editor at a time.");
            } else {
                File f = transferData.get(0);
                if (s_log.isInfoEnabled()) {
                    s_log.info("drop: path="+f.getAbsolutePath());
                }
                _session.getSQLPanelAPIOfActiveSessionWindow().fileOpen(f);
            }
            context.dropComplete(true);
        } catch (Exception e) {
            s_log.error("drop: Unexpected exception "+e.getMessage(),e);
        }

    }

}
