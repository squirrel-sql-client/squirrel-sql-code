package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class ErrorDialog extends JDialog {
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		String ERROR = "Error";
		String OK = "OK";
	}

	public ErrorDialog(Throwable th) {
		this((Frame)null, th);
	}

	public ErrorDialog(Frame owner, Throwable th) {
		super(owner, i18n.ERROR,true);
		createUserInterface(generateMessage(th), th);
	}

	public ErrorDialog(Dialog owner, Throwable th) {
		super(owner, i18n.ERROR,true);
		createUserInterface(generateMessage(th), th);
	}

	public ErrorDialog(Frame owner, String msg) {
		super(owner, i18n.ERROR,true);
		createUserInterface(msg, null);
	}

	public ErrorDialog(Dialog owner, String msg) {
		super(owner, i18n.ERROR, true);
		createUserInterface(msg, null);
	}

	public ErrorDialog(Frame owner, String msg, Throwable th) {
		super(owner, i18n.ERROR, true);
		createUserInterface(msg, th);
	}

	public ErrorDialog(Dialog owner, String msg, Throwable th) {
		super(owner, i18n.ERROR, true);
		createUserInterface(msg, th);
	}

//	private void commonCtor(String msg) {
//		createUserInterface(msg);
//	}

	/**
	 * Generate a message from the exception.
	 * 
	 * @param	th	The exception to get the message from.
	 * 
	 * @return	The message.
	 */
	private static String generateMessage(Throwable th) {
		String msg = th.getMessage();
		if (msg == null || msg.length() == 0) {
			msg = th.toString();
		}
		return msg;
	}

	private void createUserInterface(String msg, Throwable th) {
		StringBuffer buf = new StringBuffer();
		buf/*.append("<html><body>")*/.append(msg);
		if (th != null) {
			StringWriter  sw = new StringWriter();
			th.printStackTrace(new PrintWriter(sw));
			buf/*.append("<BR>")*/.append("\n")
			.append(sw.toString());
		}
//		buf.append("</body></html>");

		int iDialogWidth = 350;
		int iDialogHeight = 150;

//		JPanel mainPnl = new JPanel();
//		mainPnl.setLayout(new GridLayout(0, 1));
		/*JLabel*/MultipleLineLabel ta = new /*JLabel*/MultipleLineLabel(buf.toString());
//		ta.setVerticalTextPosition(SwingConstants.TOP);
//		Dimension dim = ta.getPreferredSize();
//		if (dim.width > iDialogWidth) {
//			int widthMinScrollbar = (iDialogWidth-20); // 20 should not be guessed
//			dim.height = dim.height * (dim.width / widthMinScrollbar);
//			dim.width = widthMinScrollbar;
//		}
//		ta.setPreferredSize(dim);
		ta.setLineWrap(true);
		final JScrollPane scroller = new JScrollPane(ta,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		//mainPnl.add(scroller);
		JPanel btnsPnl = new JPanel();
		JButton okBtn = new JButton(i18n.OK);
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dispose();
			}
		});
		btnsPnl.add(okBtn);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(/*mainPnl*/scroller, BorderLayout.CENTER);
		getContentPane().add(btnsPnl, BorderLayout.SOUTH);
		getRootPane().setDefaultButton(okBtn);
		//pack();
		setSize(iDialogWidth, iDialogHeight);
		GUIUtils.centerWithinParent(this);
		setResizable(false);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				scroller.getViewport().setViewPosition(new Point(0,0));
			}
		});
	}
}
