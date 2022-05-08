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

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Point;
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
public class VersionPane extends JTextPane implements MouseMotionListener, MouseListener
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
            String content = text.toString();
            putClientProperty(HONOR_DISPLAY_PROPERTIES, true);
            setContentType("text/html");
            StyledDocument doc = getStyledDocument();
            SimpleAttributeSet s = new SimpleAttributeSet();
            StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
            StyleConstants.setBold(s, true);
            doc.setParagraphAttributes(0, content.length(), s, false);
            doc.insertString(0, content, null);

            appendWebsiteLink(doc, Version.getWebSite());
            doc.insertString(doc.getLength(), "\n", null);
            appendWebsiteLink(doc, Version.getWebSite2());
            doc.insertString(doc.getLength(), "\n", null);
            appendWebsiteLink(doc, Version.getWebSite3());

            addMouseListener(this);
            addMouseMotionListener(this);
         }
         else
         {
            text.append(Version.getWebSite() + "\n");
            text.append(Version.getWebSite2() + "\n");
            text.append(Version.getWebSite3());

            String content = text.toString();
            putClientProperty(HONOR_DISPLAY_PROPERTIES, true);
            setContentType("text/html");
            StyledDocument doc = getStyledDocument();
            SimpleAttributeSet s = new SimpleAttributeSet();
            StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
            StyleConstants.setBold(s, true);
            doc.setParagraphAttributes(0, content.length(), s, false);
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

   private void appendWebsiteLink(StyledDocument doc, String webContent) throws BadLocationException
   {
      SimpleAttributeSet w = new SimpleAttributeSet();
      StyleConstants.setAlignment(w, StyleConstants.ALIGN_CENTER);
      StyleConstants.setUnderline(w, true);
      SimpleAttributeSet hrefAttr = new SimpleAttributeSet();
      hrefAttr.addAttribute(HTML.Attribute.HREF, webContent);
      w.addAttribute(HTML.Tag.A, hrefAttr);
      doc.setParagraphAttributes(doc.getLength(), webContent.length(), w, false);
      doc.insertString(doc.getLength(), webContent, null);
   }

   public void mouseMoved(MouseEvent ev)
   {
      JTextPane editor = (JTextPane) ev.getSource();
      editor.setEditable(false);
      Point pt = new Point(ev.getX(), ev.getY());
      int pos = editor.viewToModel2D(pt);
      if (pos >= 0)
      {
         Document eDoc = editor.getDocument();
         if (eDoc instanceof DefaultStyledDocument)
         {
            DefaultStyledDocument hdoc =
                  (DefaultStyledDocument) eDoc;
            Element e = hdoc.getCharacterElement(pos);
            AttributeSet a = e.getAttributes();
            AttributeSet tagA = (AttributeSet) a.getAttribute(HTML.Tag.A);
            String href = null;
            if (tagA != null)
            {
               href = (String) tagA.getAttribute(HTML.Attribute.HREF);
            }
            if (href != null)
            {
               editor.setToolTipText(href);
               if (editor.getCursor().getType() != Cursor.HAND_CURSOR)
               {
                  editor.setCursor(new Cursor(Cursor.HAND_CURSOR));
               }
            }
            else
            {
               editor.setToolTipText(null);
               if (editor.getCursor().getType() != Cursor.DEFAULT_CURSOR)
               {
                  editor.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
               }
            }
         }
      }
      else
      {
         editor.setToolTipText(null);
      }
   }

   public void mouseClicked(MouseEvent ev)
   {
      JTextPane editor = (JTextPane) ev.getSource();
      editor.setEditable(false);
      Point pt = new Point(ev.getX(), ev.getY());
      int pos = editor.viewToModel2D(pt);
      if (pos >= 0)
      {
         Document eDoc = editor.getDocument();
         if (eDoc instanceof DefaultStyledDocument)
         {
            DefaultStyledDocument hdoc =
                  (DefaultStyledDocument) eDoc;
            Element e = hdoc.getCharacterElement(pos);
            AttributeSet a = e.getAttributes();
            AttributeSet tagA = (AttributeSet) a.getAttribute(HTML.Tag.A);
            String href = null;
            if (tagA != null)
            {
               href = (String) tagA.getAttribute(HTML.Attribute.HREF);
            }
            if (href != null)
            {
               Desktop desktop = Desktop.getDesktop();
               try
               {
                  desktop.browse(new URI(href));
               }
               catch (IOException e1)
               {
                  s_log.error("mouseClicked: Unexpected exception " + e1.getMessage());
               }
               catch (URISyntaxException e1)
               {
                  s_log.error("mouseClicked: Unexpected exception " + e1.getMessage());
               }

            }
         }

      }
   }

   public void mouseEntered(MouseEvent arg0)
   {
   }

   public void mouseExited(MouseEvent arg0)
   {
   }

   public void mousePressed(MouseEvent arg0)
   {
   }

   public void mouseReleased(MouseEvent arg0)
   {
   }

   public void mouseDragged(MouseEvent arg0)
   {
   }
}