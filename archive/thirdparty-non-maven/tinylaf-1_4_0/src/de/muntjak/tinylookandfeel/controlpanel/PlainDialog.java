/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.controlpanel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * PlainDialog is a non-modal dialog for showing
 * dialog decoration.
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class PlainDialog extends JDialog {
	
	private static int count = 1;

	PlainDialog(Frame owner) {
		super(owner, "JDialog " + (count++), false);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));		
		JLabel l = new JLabel("<html><center>" +
			"A <font color=\"#0000ff\">JDialog</font> for testing<br>" +
			"dialog decoration.");
			
		p.add(l);
		
		getContentPane().add(p, BorderLayout.CENTER);
		
		p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
		JButton b = new JButton("Close");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlainDialog.this.dispose();
			}
		});
		
		p.add(b);
		
		getContentPane().add(p, BorderLayout.SOUTH);
		
		pack();

		int w = Math.max(240, getWidth() + 32), h = getHeight();		
		Point loc = new Point(
			owner.getLocationOnScreen().x + (owner.getWidth() - w) / 2,
			owner.getLocationOnScreen().y + (owner.getHeight() - w) * 2 / 3);
			
		setSize(w, h);
		setLocation(loc);
		setVisible(true);
	}
}
