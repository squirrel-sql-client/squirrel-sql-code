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

import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A class that encapsulates the work of rendering the version and copyright.
 * This is used in both the splash screen and the about dialog.
 */
public class VersionPane extends JTextPane
{
   /**
    * Logger for this class.
    */
   private final static ILogger s_log = LoggerController.createLogger(VersionPane.class);

   /**
    * Constructor
    *
    */
   public VersionPane()
   {
      try
      {
         StringBuilder text = new StringBuilder();
         text.append(Version.getVersion());
         text.append("\n");
         text.append(Version.getCopyrightStatement());
         text.append("\n");

         if( Desktop.isDesktopSupported() )
         {
            String content;

            content = text.toString();
            putClientProperty(HONOR_DISPLAY_PROPERTIES, true);
            setContentType("text/html");
            StyledDocument doc = getStyledDocument();
            doc.setParagraphAttributes(0, content.length(), getDefaultStyleAttributeSet(), true);
            doc.insertString(0, content, null);
         }
         else
         {
            String content = text.toString();
            putClientProperty(HONOR_DISPLAY_PROPERTIES, true);
            setContentType("text/html");
            StyledDocument doc = getStyledDocument();
            doc.setParagraphAttributes(0, content.length(), getDefaultStyleAttributeSet(), true);
            doc.insertString(0, content, null);

         }

         setOpaque(false);

         //setFocusable(false);
         setFocusCycleRoot(false);
      }
      catch (Exception e)
      {
         s_log.error(e);
      }
   }

   private static SimpleAttributeSet getDefaultStyleAttributeSet()
   {
      SimpleAttributeSet s = new SimpleAttributeSet();
      StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
      StyleConstants.setBold(s, true);
      return s;
   }


}