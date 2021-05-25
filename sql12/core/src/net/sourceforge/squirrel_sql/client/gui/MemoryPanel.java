package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2001-2003 Gerd Wagner
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class MemoryPanel extends JPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MemoryPanel.class);

	private JProgressBar _bar;
	private JButton _btnGarbage;
	private JButton _btnSessionGCStatus;
	transient private IApplication _app;
	private HashMap<IIdentifier, MemorySessionInfo> _sessionInfosBySessionIDs =  new HashMap<IIdentifier, MemorySessionInfo>();

	public MemoryPanel(IApplication app)
	{
		_app = app;

		_bar = new JProgressBar();

		_bar.setStringPainted(true);

		_btnGarbage = new JButton();
		// i18n[MemoryPanel.runGC=Run garbage collection]
		_btnGarbage.setToolTipText(s_stringMgr.getString("MemoryPanel.runGC"));
		_btnGarbage.setBorder(null);

		ImageIcon trashIcon = _app.getResources().getIcon(SquirrelResources.IImageNames.TRASH);
		_btnGarbage.setIcon(trashIcon);

		Dimension prefButtonSize = new Dimension(3 * trashIcon.getIconWidth() / 2, trashIcon.getIconHeight());

		_btnGarbage.setPreferredSize(prefButtonSize);

		_btnGarbage.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Utilities.garbageCollect();
			}
		});

		_btnSessionGCStatus = new JButton()
		{
         public void paint(Graphics g)
			{
				super.paint(g);
//				paintNumWaitingGC(g);
			}
		};

		_btnSessionGCStatus.setBorder(null);

		updateGcStatus();

		_btnSessionGCStatus.setBorder(null);
		_btnSessionGCStatus.setPreferredSize(prefButtonSize);


		_btnSessionGCStatus.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showSessionGCStatus();
			}
		});


		JPanel pnlButtons = new JPanel(new GridLayout(1,2,3,0));
		pnlButtons.add(_btnSessionGCStatus);
		pnlButtons.add(_btnGarbage);


		this.setLayout(new BorderLayout(5,0));
		this.add(pnlButtons, BorderLayout.EAST);
		GUIUtils.setMinimumWidth(_bar, 150);
		this.add(_bar, BorderLayout.CENTER);

		this.setBorder(null);

		_app.getSessionManager().addSessionListener(new SessionAdapter()
		{
			public void sessionClosed(SessionEvent evt)
			{
				IIdentifier id = evt.getSession().getIdentifier();
				MemorySessionInfo msi = _sessionInfosBySessionIDs.get(id);
				if(null == msi)
				{
					throw new IllegalStateException("A session with ID " + id + " has not been created");
				}
				msi.closed = new Date();
				updateGcStatus();
			}

			public void sessionConnected(SessionEvent evt)
			{
				IIdentifier id = evt.getSession().getIdentifier();
				if(null != _sessionInfosBySessionIDs.get(id))
				{
					throw new IllegalStateException("A session with ID " + id + " has already been created");
				}
				MemorySessionInfo msi = new MemorySessionInfo(id, evt.getSession().getAlias().getName());
				_sessionInfosBySessionIDs.put(id, msi);

			}

			public void sessionFinalized(IIdentifier sessionId)
			{
				MemorySessionInfo msi = _sessionInfosBySessionIDs.get(sessionId);
				if(null == msi)
				{
					throw new IllegalStateException("A session with ID " + sessionId + " has not been created");
				}
				msi.finalized = new Date();
				updateGcStatus();
			}
		});

		Timer t = new Timer(500, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateLabel();
			}
		});
		t.start();

	}

	private void updateGcStatus()
	{
		SessionGCStatus gcStat = getSessionGCStatus();
		_btnSessionGCStatus.setToolTipText(gcStat.tooltip);
		_btnSessionGCStatus.setBackground(gcStat.color);
		_btnSessionGCStatus.setText(gcStat.numSessAwaitingGC);
	}



	private SessionGCStatus getSessionGCStatus()
	{
		SessionGCStatus ret = new SessionGCStatus();

		int numSessAwaitingGC = 0;
		for(Iterator<MemorySessionInfo> i = 
            _sessionInfosBySessionIDs.values().iterator(); i.hasNext();)
		{
			MemorySessionInfo msi =  i.next();
			if(null != msi.closed && null == msi.finalized)
			{
				++numSessAwaitingGC;
			}
		}

		ret.numSessAwaitingGC = "" + numSessAwaitingGC;

		// i18n [MemoryPanel.gcStatusToolTip={0} Sessions waiting for garbage collection]
		ret.tooltip = s_stringMgr.getString("MemoryPanel.gcStatusToolTip", new Integer(ret.numSessAwaitingGC));

		ret.color = Color.yellow;
		if(numSessAwaitingGC < 2)
		{
			ret.color = Color.green;
		}
		else if(numSessAwaitingGC > 4)
		{
			ret.color = Color.red;
		}

		return ret;


	}

	private void updateLabel()
	{
		long total = Runtime.getRuntime().totalMemory() >> 10 >> 10;
		long free = Runtime.getRuntime().freeMemory() >> 10 >> 10;
		long just = total-free;

		_bar.setMinimum(0);
		_bar.setMaximum((int)total);
		_bar.setValue((int)just);

		Object[] params = new Long[]
			{
				Long.valueOf(just),
				Long.valueOf(total)
			};

		// i18n[MemoryPanel.memSize={0} of {1} MB];
		String msg = s_stringMgr.getString("MemoryPanel.memSize", params);
		_bar.setString(msg);
	}

	private void showSessionGCStatus()
	{
		StringBuffer[] params = new StringBuffer[]
			{
				new StringBuffer(getSessionGCStatus().tooltip),
				new StringBuffer(),
				new StringBuffer(),
				new StringBuffer()
			};


		MemorySessionInfo[] msis = _sessionInfosBySessionIDs.values().toArray(new MemorySessionInfo[0]);

		Arrays.sort(msis);

		for (int i = 0; i < msis.length; i++)
		{
			if(null != msis[i].closed && null == msis[i].finalized)
			{
				params[1].append(msis[i].toString()).append('\n');
			}
			else if(null == msis[i].closed)
			{
				params[2].append(msis[i].toString()).append('\n');
			}
			else if(null != msis[i].finalized)
			{
				params[3].append(msis[i].toString()).append('\n');
			}
		}


		// i18n [MemoryPanel.gcStatus={0}\n\n
		//Sessions waiting for garbage collection:\n
		//==================================================\n
		//{1}\n
		//Sessions open:\n
		//==================================================\n
		//{2}\n
		//Sessions garbage collected:\n
		//==================================================\n
		//{3}\n]
		String msg = s_stringMgr.getString("MemoryPanel.gcStatus", (Object[])params);
		ErrorDialog errorDialog = new ErrorDialog(_app.getMainFrame(), msg);


		// i18n[MemoryPanel.statusDialogTitle=Session garbage collection status]
		errorDialog.setTitle(s_stringMgr.getString("MemoryPanel.statusDialogTitle"));
		errorDialog.setVisible(true);
	}


	private static class SessionGCStatus
	{
		String tooltip;
		Color color;
		String numSessAwaitingGC;
	}

}