package net.sourceforge.squirrel_sql.client.gui;

/*
 * Copyright (C) 2006 Rob Manning
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
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A class that encapsulates the work of rendering the version and copyright.
 * This is used in both the splash screen and the about dialog.
 */
public class VersionPane extends JTextPane {

    private boolean _showWebsite = false;

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(VersionPane.class);
    
    /**
     * Constructor
     * @param showWebsite whether or not to display the website.  This is done
     *                    in the about dialog but not in the splash screen.
     */
    public VersionPane(boolean showWebsite) {
        _showWebsite = showWebsite;
        init();
    }
    
    /**
     * Renders the content.
     */
    private void init() {
        String content = getContent();
        StyledDocument doc = getStyledDocument();
        SimpleAttributeSet s = new SimpleAttributeSet();
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        StyleConstants.setBold(s, true);
        
        try {          
            doc.setParagraphAttributes(0,content.length(), s, false);
            doc.insertString(0, content, s);
        } catch (Exception e) {
            s_log.error("init: Unexpected exception "+e.getMessage());
        }
        setOpaque(false);        
    }
    
    /**
     * Constructs the text that gets rendered.
     * 
     * @return version and copyright info ( and possibly website url ) 
     */
    private String getContent() {
        StringBuffer text = new StringBuffer();
        text.append(Version.getVersion());
        text.append("\n");
        text.append(Version.getCopyrightStatement());
        if (_showWebsite) {
            text.append("\n");
            text.append(Version.getWebSite());            
        }
        return text.toString();
    }
}
