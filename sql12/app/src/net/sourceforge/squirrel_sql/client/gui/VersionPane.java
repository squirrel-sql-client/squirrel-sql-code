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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;

import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A class that encapsulates the work of rendering the version and copyright.
 * This is used in both the splash screen and the about dialog.
 */
public class VersionPane extends JTextPane implements MouseMotionListener,MouseListener{

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
        setContentType("text/html");
        StyledDocument doc = getStyledDocument();
        SimpleAttributeSet s = new SimpleAttributeSet();
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        StyleConstants.setBold(s, true);
        try {
            doc.setParagraphAttributes(0,content.length(), s, false);
            doc.insertString(0, content, s);
            if (_showWebsite) {
            	String webContent = Version.getWebSite();
            	SimpleAttributeSet w = new SimpleAttributeSet();
                StyleConstants.setAlignment(w, StyleConstants.ALIGN_CENTER);
                StyleConstants.setUnderline(w, true);
                SimpleAttributeSet hrefAttr = new SimpleAttributeSet();
                hrefAttr.addAttribute(HTML.Attribute.HREF, Version.getWebSite());
                w.addAttribute(HTML.Tag.A, hrefAttr);
                doc.setParagraphAttributes(content.length(),webContent.length(), w, false);
                doc.insertString(content.length(), webContent, w);
                if (Desktop.isDesktopSupported()){
                	addMouseListener(this);
                	addMouseMotionListener(this);
                }
            }
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
        	 if (!Desktop.isDesktopSupported()) {
        		 text.append(Version.getWebSite());
        	 }
         }
        return text.toString();
    }

	public void mouseMoved(MouseEvent ev) {
        JTextPane editor = (JTextPane) ev.getSource();
        editor.setEditable(false);
          Point pt = new Point(ev.getX(), ev.getY());
          int pos = editor.viewToModel(pt);
          if (pos >= 0) {
            Document eDoc = editor.getDocument();
            if (eDoc instanceof DefaultStyledDocument) {
              DefaultStyledDocument hdoc =
                (DefaultStyledDocument) eDoc;
              Element e = hdoc.getCharacterElement(pos);
              AttributeSet a = e.getAttributes();
              AttributeSet tagA = (AttributeSet) a.getAttribute(HTML.Tag.A);
              String href = null;
              if (tagA!=null){
                  href = (String)tagA.getAttribute(HTML.Attribute.HREF);
              }
              if (href != null) {
                  editor.setToolTipText(href);
                  if (editor.getCursor().getType() != Cursor.HAND_CURSOR) {
                      editor.setCursor(new Cursor(Cursor.HAND_CURSOR));
                  }
              }
              else {
                  editor.setToolTipText(null);
                  if (editor.getCursor().getType() != Cursor.DEFAULT_CURSOR) {
                      editor.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
              }
            }
          }
          else {
              editor.setToolTipText(null);
          }
        }

	public void mouseClicked(MouseEvent ev) {
	    JTextPane editor = (JTextPane) ev.getSource();
	     editor.setEditable(false);
	      Point pt = new Point(ev.getX(), ev.getY());
	      int pos = editor.viewToModel(pt);
	      if (pos >= 0) {
	        Document eDoc = editor.getDocument();
	        if (eDoc instanceof DefaultStyledDocument) {
	          DefaultStyledDocument hdoc =
	            (DefaultStyledDocument) eDoc;
	          Element e = hdoc.getCharacterElement(pos);
	          AttributeSet a = e.getAttributes();
	          AttributeSet tagA = (AttributeSet) a.getAttribute(HTML.Tag.A);
	          String href = null;
	          if (tagA!=null){
	        	  href = (String)tagA.getAttribute(HTML.Attribute.HREF);
	          }
	          if (href != null) {
	        	Desktop desktop = Desktop.getDesktop();
	        	try {
					desktop.browse(new URI(href));
				} catch (IOException e1) {
					 s_log.error("mouseClicked: Unexpected exception "+e1.getMessage());
				} catch (URISyntaxException e1) {
					 s_log.error("mouseClicked: Unexpected exception "+e1.getMessage());
				}

	        }
	      }

	    }
	}

	@SuppressWarnings("all")
	public void mouseEntered(MouseEvent arg0) {
	}
	@SuppressWarnings("all")
	public void mouseExited(MouseEvent arg0) {
	}

	@SuppressWarnings("all")
	public void mousePressed(MouseEvent arg0) {
	}

	@SuppressWarnings("all")
	public void mouseReleased(MouseEvent arg0) {
	}

	@SuppressWarnings("all")
	public void mouseDragged(MouseEvent arg0) {
	}
}