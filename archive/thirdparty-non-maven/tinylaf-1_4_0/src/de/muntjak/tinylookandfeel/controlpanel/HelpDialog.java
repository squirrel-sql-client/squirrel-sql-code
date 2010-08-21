/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.controlpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.io.IOException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


public class HelpDialog extends JDialog implements HyperlinkListener {

	private static HelpDialog instance;
	private static JEditorPane editor;
	private static URL helpURL;
	
	private HelpDialog(Frame owner) {
		super(owner, "Control Panel Help", false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		helpURL = getClass().getResource("/help/help.html");
		
		if(helpURL != null) {
			try {
				editor = new JEditorPane(helpURL);
			}
			catch(IOException ex) {
				System.err.println(ex.toString());
				helpURL = null;
			}
		}
		
		if(helpURL == null) {
			String html = "<html><body><center><h2><font color=\"#FF0000\">" +
				"Couldn't set up the online help." +
				"</h2></body></html>";
			editor = new JEditorPane("text/html", html);
		}
		
		editor.setBackground(new Color(237, 237, 237));
		editor.setEditable(false);
		editor.addHyperlinkListener(this);
		
		setupUI(owner);
	}
	
	public static void showDialog(Frame owner) {
		if(instance == null) {
			instance = new HelpDialog(owner);
		}
		
		instance.setVisible(true);
	}
	
	public static void updateUI() {
		if(instance == null) return;
		
		SwingUtilities.updateComponentTreeUI(instance);
	}
	
	public void setupUI(Frame frame) {
		JScrollPane sp = new JScrollPane(editor);
		getContentPane().add(sp);
		
		pack();
		
		setSize(800, 600);
		setLocation(frame.getLocationOnScreen().x + 
			(frame.getWidth() - getSize().width) / 2,
			frame.getLocationOnScreen().y + 
			(frame.getHeight() - getSize().height) / 2);
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		if(e.getEventType() != HyperlinkEvent.EventType.ACTIVATED) return;
		
		try {
			editor.setPage(e.getURL());
		}
		catch(IOException ex) {
			System.err.println(ex.toString());
		}
	}
}
