package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.event.EventListenerList;

import com.jgoodies.forms.factories.ButtonBarFactory;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class OkClosePanel extends JPanel
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(OkClosePanel.class);

	private boolean _executingMode;

	/** Listeners for this object. */
	private EventListenerList _listenerList = new EventListenerList();

	private JButton _okBtn;
	private JButton _closeBtn = new JButton(s_stringMgr.getString("OkClosePanel.close"));

	public OkClosePanel()
	{
		super();
		createGUI(s_stringMgr.getString("OkClosePanel.ok"));
	}

	public OkClosePanel(String okButtonText)
	{
		super();
		createGUI(okButtonText != null ? okButtonText : s_stringMgr.getString("OkClosePanel.ok"));
	}

	/**
	 * When in <EM>executing mode</EM> the OK button is disabled
	 * and the Close button is retitled as Cancel.
	 */
	public void setExecuting(boolean executingMode)
	{
		if (executingMode != _executingMode)
		{
			_executingMode = executingMode;
			_okBtn.setEnabled(!executingMode);
			_closeBtn.setText(executingMode ? s_stringMgr.getString("OkClosePanel.cancel") : s_stringMgr.getString("OkClosePanel.close"));
			if (!executingMode)
			{
				_closeBtn.setEnabled(true);
			}
		}
	}

	/**
	 * Enable/disable the Close/Cancel button.
	 *
	 * @param	enable	<TT>true</TT> to enable else <TT>false</TT> to disable.
	 */
	public void enableCloseButton(boolean enable)
	{
		_closeBtn.setEnabled(enable);
	}

	/**
	 * Adds a listener for actions in this panel.
	 *
	 * @param	lis		<TT>OkClosePanelListener</TT> that will be notified when
	 *					actions are performed in this panel.
	 */
	public synchronized void addListener(IOkClosePanelListener lis)
	{
		_listenerList.add(IOkClosePanelListener.class, lis);
	}

	/**
	 * Make the OK button the default. This should be called
	 * <EM>after</EM> you add this panel to a dialog/frame, not
	 * before otherwise you will get an <TT>IllegalStateException</TT>
	 * exception.
	 *
	 * @param	IllegalStateException
	 * 			Thrown if <TT>null</TT> <TT>JRootPane</TT>. I.E. component
	 * 			hasn't been added to a frame, dialog etc.
	 */
	public synchronized void makeOKButtonDefault()
			throws IllegalStateException
	{
		JRootPane root = getRootPane();
		if (root == null)
		{
			throw new IllegalStateException("Null RootPane so cannot set default button");
		}
//		root.setDefaultButton(_okBtn);
	}

	public JButton getCloseButton()
	{
		return _closeBtn;
	}

	public JButton getOKButton()
	{
		return _okBtn;
	}

	private void fireButtonPressed(JButton btn)
	{
		// Guaranteed to be non-null.
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		OkClosePanelEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 )
		{
			if (listeners[i] == IOkClosePanelListener.class)
			{
				// Lazily create the event:
				if (evt == null)
				{
					evt = new OkClosePanelEvent(this);
				}
				IOkClosePanelListener lis = (IOkClosePanelListener)listeners[i + 1];
				if (btn == _okBtn)
				{
					lis.okPressed(evt);
				}
				else if (_executingMode)
				{
					lis.cancelPressed(evt);
				}
				else
				{
					lis.closePressed(evt);
				}
			}
		}
	}

	private void createGUI(String okButtonText)
	{
		_okBtn = new JButton(okButtonText);

		JPanel pnl = ButtonBarFactory.buildOKCancelBar(_okBtn, _closeBtn);
		add(pnl);
//		add(_okBtn);
//		add(_closeBtn);
		_okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				fireButtonPressed(_okBtn);
			}
		});
		_closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				fireButtonPressed(_closeBtn);
			}
		});
//
//		GUIUtils.setJButtonSizesTheSame(new JButton[] {_okBtn, _closeBtn, new JButton(i18n.CANCEL)});
	}
}
