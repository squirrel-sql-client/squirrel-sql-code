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
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class ErrorDialog extends JDialog
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface ErrorDialog_i18n
	{
		String ERROR = "Error";
		String CLOSE = "Close";
		String UNKNOWN_ERROR = "Unknown error";
		String MORE = "More";
		String LESS = "Less";
	}

	/** Preferred width of the message area. TODO: remove magic number*/
	private static final int PREFERRED_WIDTH = 400;

	/** Close button. */
	private JButton _closeBtn;

	/** Handler for Close button. */
	private ActionListener _closeHandler = new CloseButtonHandler();

	/** More button. */
	private JButton _moreBtn;

	/** Handler for More button. */
	private ActionListener _moreHandler = null;

	/** Panel to display the stack trace in. */
	private JScrollPane _stackTraceScroller;

	public ErrorDialog(Throwable th)
	{
		this((Frame) null, th);
	}

	public ErrorDialog(Frame owner, Throwable th)
	{
		super(owner, ErrorDialog_i18n.ERROR, true);
		createUserInterface(null, th);
	}

	public ErrorDialog(Dialog owner, Throwable th)
	{
		super(owner, ErrorDialog_i18n.ERROR, true);
		createUserInterface(null, th);
	}

	public ErrorDialog(Frame owner, String msg)
	{
		super(owner, ErrorDialog_i18n.ERROR, true);
		createUserInterface(msg, null);
	}

	public ErrorDialog(Frame owner, String msg, Throwable th)
	{
		super(owner, ErrorDialog_i18n.ERROR, true);
		createUserInterface(msg, th);
	}

	public ErrorDialog(Dialog owner, String msg)
	{
		super(owner, ErrorDialog_i18n.ERROR, true);
		createUserInterface(msg, null);
	}

	/**
	 * Dispose of this dialog after cleaning up all listeners.
	 */
	public void dispose()
	{
		if (_closeBtn != null && _closeHandler != null)
		{
			_closeBtn.removeActionListener(_closeHandler);
		}
		if (_moreBtn != null && _moreHandler != null)
		{
			_moreBtn.removeActionListener(_moreHandler);
		}
		super.dispose();
	}

	/**
	 * Create user intnerface.
	 * 
	 * @param	msg		Message to be displayed. Can be null.
	 * @param	th		Exception to be shown. Can be null.
	 */
	private void createUserInterface(String msg, Throwable th)
	{
		if (msg == null || msg.length() == 0)
		{
			if (th != null)
			{
				msg = th.getMessage();
				if (msg == null || msg.length() == 0)
				{
					msg = th.toString();
				}
			}
		}
		if (msg == null || msg.length() == 0)
		{
			msg = ErrorDialog_i18n.UNKNOWN_ERROR;
		}

		// Message panel.
		MessagePanel msgPnl = new MessagePanel(msg);

		_stackTraceScroller = new JScrollPane(new StackTracePanel(th));
		_stackTraceScroller.setVisible(false);

		JPanel btnsPnl = new JPanel();
		if (th != null)
		{
			_moreBtn = new JButton(ErrorDialog_i18n.MORE);
			btnsPnl.add(_moreBtn);
			_moreBtn.addActionListener(new MoreButtonHandler());
		}
		_closeBtn = new JButton(ErrorDialog_i18n.CLOSE);
		_closeBtn.addActionListener(new CloseButtonHandler());
		btnsPnl.add(_closeBtn);

		Container content = getContentPane();
		content.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = gbc.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		content.add(new JScrollPane(msgPnl), gbc);
		++gbc.gridy;
		content.add(btnsPnl, gbc);
		++gbc.gridy;
		content.add(_stackTraceScroller, gbc);

		getRootPane().setDefaultButton(_closeBtn);
		setResizable(false);

		//SwingUtilities.invokeLater(new Runnable()
		//{
		//	public void run()
		//	{
				pack();
				GUIUtils.centerWithinParent(ErrorDialog.this);
		//	}
		//});
	}

	private static Color getTextAreaBackgroundColor()
	{
		return (Color)UIManager.get("TextArea.background");
	}

	/**
	 * Panel to display the message in.
	 */
	private final class MessagePanel extends MultipleLineLabel
	{
		MessagePanel(String msg)
		{
			super();
			setText(msg);
			setBackground(ErrorDialog.this.getTextAreaBackgroundColor());
			setRows(3);
			Dimension dim = getPreferredSize();
			dim.width = PREFERRED_WIDTH;
			setPreferredSize(dim);
		}
	}

	/**
	 * Panel to display the stack trace in.
	 */
	private final class StackTracePanel extends MultipleLineLabel
	{
		StackTracePanel(Throwable th)
		{
			super();
			setBackground(ErrorDialog.this.getTextAreaBackgroundColor());
			if (th != null)
			{
				setText(Utilities.getStackTrace(th));
				setRows(10);
			}
		}
	}

	/**
	 * Handler for Close button. Disposes of this dialog.
	 */
	private final class CloseButtonHandler implements ActionListener
	{
		/**
		 * Disposes of this dialog.
		 */
		public void actionPerformed(ActionEvent evt)
		{
			ErrorDialog.this.dispose();
		}

	}

	/**
	 * Handler for More button. Shows/hides the stack trace.
	 */
	private final class MoreButtonHandler implements ActionListener
	{
		/**
		 * Show/hide the stack trace.
		 */
		public void actionPerformed(ActionEvent evt)
		{
			_stackTraceScroller.setVisible(!_stackTraceScroller.isVisible());
			_moreBtn.setText(
				_stackTraceScroller.isVisible()
					? ErrorDialog_i18n.LESS
					: ErrorDialog_i18n.MORE);
			ErrorDialog.this.pack();
		}
	}
}
