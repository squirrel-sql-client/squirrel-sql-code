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
package net.sourceforge.squirrel_sql.client.gui.dnd;

import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
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
 */
public class FileEditorDropTargetListener extends DropTargetAdapter 
                                          implements DropTargetListener {

   static final ILogger s_log =
         LoggerController.createLogger(DropedFileExtractor.class);
   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(DropedFileExtractor.class);


   /** the session we are listening for drops into */
    private ISession _session;
    
    public FileEditorDropTargetListener(ISession session) {
        this._session = session;
    }
    
    /**
     * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
     */
    public void drop(DropTargetDropEvent dtde) {
        try {
           File fileToOpen = DropedFileExtractor.getFile(dtde, _session.getApplication());

           if (fileToOpen != null) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("drop: path="+fileToOpen.getAbsolutePath());
                }            
                ISQLPanelAPI api = 
                    _session.getSQLPanelAPIOfActiveSessionWindow(); 
                api.fileOpen(fileToOpen);
            }            
        } catch (Exception e) {
            s_log.error("drop: Unexpected exception "+e.getMessage(),e);
        }

    }


}
